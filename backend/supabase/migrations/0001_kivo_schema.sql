-- ============================================================================
-- Kivo — esquema base en Supabase (Postgres)
-- ----------------------------------------------------------------------------
-- Espeja las entidades Room de kivo-android/ (ver
-- app/src/main/java/com/finanzas/automatica/data/local/entity/), con tres
-- diferencias deliberadas:
--
--   1. Llave primaria UUID en vez del Long autoincremental de Room. El id lo
--      genera el DISPOSITIVO al crear la fila, no el servidor: la app es
--      local-first y debe poder crear movimientos sin red. Un autoincremental
--      del servidor obligaria a esperar respuesta antes de tener id estable.
--   2. Toda fila lleva user_id. El aislamiento entre usuarios lo hace RLS
--      (abajo), no el codigo de la app: aunque alguien extraiga la anon key del
--      APK, Postgres no le entrega filas de otro usuario.
--   3. Borrado logico (deleted = true) en vez de DELETE fisico. Sin lapidas,
--      un dispositivo que estuvo offline vuelve a subir lo que otro ya borro.
--
-- Dos relojes distintos, a proposito:
--   - updated_at: hora LOGICA del cliente que hizo el cambio. Es la que decide
--     quien gana en un conflicto (last-write-wins).
--   - synced_at: hora del SERVIDOR, la pone un trigger. Es el cursor de la
--     descarga incremental ("dame todo lo que cambio despues de X"). No sirve
--     updated_at para esto: el reloj de un telefono puede estar atrasado y su
--     fila se saltaria el cursor.
-- ============================================================================

create extension if not exists "pgcrypto";

-- ---------------------------------------------------------------------------
-- Utilidades comunes
-- ---------------------------------------------------------------------------

-- Marca la hora del servidor en cada INSERT/UPDATE. El cliente nunca escribe
-- synced_at (y si lo intenta, este trigger lo pisa).
create or replace function public.kivo_touch_synced_at()
returns trigger
language plpgsql
as $$
begin
  new.synced_at := now();
  return new;
end;
$$;

-- Rechaza que una fila cambie de dueño. Sin esto, un usuario podria hacer
-- UPDATE ... SET user_id = <otro> y "regalarle" (o inyectarle) filas: la
-- politica de RLS de UPDATE valida la fila que se lee, y el WITH CHECK valida
-- la nueva, pero es mas claro y barato bloquearlo aqui de una vez.
create or replace function public.kivo_freeze_user_id()
returns trigger
language plpgsql
as $$
begin
  if new.user_id is distinct from old.user_id then
    raise exception 'user_id es inmutable';
  end if;
  return new;
end;
$$;

-- ---------------------------------------------------------------------------
-- profiles — una fila por usuario autenticado
-- ---------------------------------------------------------------------------
create table if not exists public.profiles (
  id           uuid primary key references auth.users(id) on delete cascade,
  email        text,
  display_name text,
  -- Preferencias que hoy viven en SharedPreferences y el panel web necesita
  -- leer para verse igual que la app (tema, moneda). No incluye nada sensible.
  theme_mode    text not null default 'SYSTEM',
  theme_palette text not null default 'KIVO_CORAL',
  created_at   timestamptz not null default now(),
  updated_at   timestamptz not null default now(),
  synced_at    timestamptz not null default now()
);

-- Crea el perfil en el momento del registro. Si se hiciera desde la app, un
-- usuario que se registra en la web no tendria perfil hasta abrir el movil.
create or replace function public.kivo_handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.profiles (id, email)
  values (new.id, new.email)
  on conflict (id) do nothing;
  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.kivo_handle_new_user();

-- ---------------------------------------------------------------------------
-- categories
-- ---------------------------------------------------------------------------
create table if not exists public.categories (
  id                 uuid primary key default gen_random_uuid(),
  user_id            uuid not null default auth.uid() references auth.users(id) on delete cascade,
  name               text not null,
  type               text not null check (type in ('INCOME', 'EXPENSE')),
  icon_name          text not null default 'category',
  is_custom          boolean not null default false,
  parent_category_id uuid references public.categories(id) on delete set null,
  sort_order         integer not null default 0,
  created_at         timestamptz not null default now(),
  updated_at         timestamptz not null default now(),
  deleted            boolean not null default false,
  synced_at          timestamptz not null default now()
);

-- Las 33 categorias sembradas por DefaultCategories son iguales en todos los
-- dispositivos del mismo usuario; sin esto, cada telefono subiria su propia
-- copia y el usuario terminaria con duplicados en la web (el mismo bug que ya
-- se corrigio localmente con DefaultCategories.dedupe()).
create unique index if not exists categories_user_name_type_uniq
  on public.categories (user_id, lower(name), type)
  where deleted = false;

-- ---------------------------------------------------------------------------
-- agenda_entries
-- ---------------------------------------------------------------------------
create table if not exists public.agenda_entries (
  id                  uuid primary key default gen_random_uuid(),
  user_id             uuid not null default auth.uid() references auth.users(id) on delete cascade,
  account_identifier  text not null,
  display_name        text not null,
  default_category_id uuid references public.categories(id) on delete set null,
  color               bigint not null default 4287458915,  -- 0xFF8D6E63, sin signo
  origin              text not null default 'MANUAL',
  created_at          timestamptz not null default now(),
  updated_at          timestamptz not null default now(),
  deleted             boolean not null default false,
  synced_at           timestamptz not null default now()
);

-- Equivalente al indice unico de Room sobre accountIdentifier, pero acotado al
-- usuario: dos personas distintas si pueden tener el mismo numero en su agenda.
create unique index if not exists agenda_entries_user_account_uniq
  on public.agenda_entries (user_id, account_identifier)
  where deleted = false;

-- ---------------------------------------------------------------------------
-- movements
-- ---------------------------------------------------------------------------
create table if not exists public.movements (
  id                 uuid primary key default gen_random_uuid(),
  user_id            uuid not null default auth.uid() references auth.users(id) on delete cascade,
  type               text not null check (type in ('INCOME', 'EXPENSE')),
  amount             bigint not null,                 -- centavos COP, igual que Room
  payment_method     text not null default 'UNKNOWN',
  counterparty_raw   text not null default '',
  counterparty_id    uuid references public.agenda_entries(id) on delete set null,
  category_id        uuid references public.categories(id) on delete set null,
  date               timestamptz not null,
  source             text not null check (source in ('NOTIFICATION', 'OCR', 'MANUAL', 'OPEN_FINANCE', 'IMPORT')),
  confirmation_state text not null default 'PENDING'
                     check (confirmation_state in ('PENDING', 'CONFIRMED', 'REJECTED', 'AUTO_CONFIRMED')),
  bank_entity        text not null,
  -- El texto crudo de la notificacion bancaria. Es el dato mas sensible de
  -- todo el esquema (nombres de terceros, saldos). Se sube porque es lo que
  -- permite re-clasificar y auditar por que se registro un movimiento, pero
  -- ver docs/SDD.md 8.2: el usuario puede desactivar su envio.
  raw_text           text not null default '',
  created_at         timestamptz not null default now(),
  updated_at         timestamptz not null default now(),
  deleted            boolean not null default false,
  synced_at          timestamptz not null default now()
);

create index if not exists movements_user_date_idx on public.movements (user_id, date desc);
create index if not exists movements_user_state_idx on public.movements (user_id, confirmation_state);
create index if not exists movements_user_category_idx on public.movements (user_id, category_id);

-- ---------------------------------------------------------------------------
-- budgets
-- ---------------------------------------------------------------------------
create table if not exists public.budgets (
  id            uuid primary key default gen_random_uuid(),
  user_id       uuid not null default auth.uid() references auth.users(id) on delete cascade,
  category_id   uuid not null references public.categories(id) on delete cascade,
  monthly_limit bigint not null,
  month         integer not null check (month between 1 and 12),
  year          integer not null check (year between 2000 and 2200),
  created_at    timestamptz not null default now(),
  updated_at    timestamptz not null default now(),
  deleted       boolean not null default false,
  synced_at     timestamptz not null default now()
);

create unique index if not exists budgets_user_category_period_uniq
  on public.budgets (user_id, category_id, month, year)
  where deleted = false;

-- ---------------------------------------------------------------------------
-- savings_goals
-- ---------------------------------------------------------------------------
create table if not exists public.savings_goals (
  id             uuid primary key default gen_random_uuid(),
  user_id        uuid not null default auth.uid() references auth.users(id) on delete cascade,
  name           text not null,
  target_amount  bigint not null,
  current_amount bigint not null default 0,
  target_date    timestamptz not null,
  created_at     timestamptz not null default now(),
  updated_at     timestamptz not null default now(),
  deleted        boolean not null default false,
  synced_at      timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- classification_rules
-- ---------------------------------------------------------------------------
create table if not exists public.classification_rules (
  id          uuid primary key default gen_random_uuid(),
  user_id     uuid not null default auth.uid() references auth.users(id) on delete cascade,
  pattern     text not null,
  bank_entity text not null,
  category_id uuid not null references public.categories(id) on delete cascade,
  priority    integer not null default 0,
  is_active   boolean not null default true,
  created_at  timestamptz not null default now(),
  updated_at  timestamptz not null default now(),
  deleted     boolean not null default false,
  synced_at   timestamptz not null default now()
);

create index if not exists classification_rules_user_bank_idx
  on public.classification_rules (user_id, bank_entity, priority desc);

-- ---------------------------------------------------------------------------
-- invoices / invoice_items
-- ---------------------------------------------------------------------------
create table if not exists public.invoices (
  id            uuid primary key default gen_random_uuid(),
  user_id       uuid not null default auth.uid() references auth.users(id) on delete cascade,
  merchant_name text not null,
  date          timestamptz not null default now(),
  total_amount  bigint not null default 0,
  -- Deliberadamente NO se sube la imagen del comprobante, solo su URI local.
  -- Subir fotos de facturas implica Storage, cuota y una superficie de datos
  -- personales bastante mayor; queda como decision aparte (ver PENDIENTES).
  image_uri     text,
  movement_id   uuid references public.movements(id) on delete set null,
  created_at    timestamptz not null default now(),
  updated_at    timestamptz not null default now(),
  deleted       boolean not null default false,
  synced_at     timestamptz not null default now()
);

create table if not exists public.invoice_items (
  id                uuid primary key default gen_random_uuid(),
  user_id           uuid not null default auth.uid() references auth.users(id) on delete cascade,
  invoice_id        uuid not null references public.invoices(id) on delete cascade,
  product_name      text not null,
  quantity          integer not null default 1,
  unit_price        bigint not null default 0,
  total_price       bigint not null default 0,
  category_id       uuid references public.categories(id) on delete set null,
  is_debt           boolean not null default false,
  debtor_contact_id uuid references public.agenda_entries(id) on delete set null,
  debtor_name       text,
  debt_status       text not null default 'PENDING' check (debt_status in ('PENDING', 'PAID')),
  notes             text,
  created_at        timestamptz not null default now(),
  updated_at        timestamptz not null default now(),
  deleted           boolean not null default false,
  synced_at         timestamptz not null default now()
);

create index if not exists invoice_items_user_invoice_idx on public.invoice_items (user_id, invoice_id);

-- ---------------------------------------------------------------------------
-- Triggers de synced_at e inmutabilidad de user_id
-- ---------------------------------------------------------------------------
do $$
declare
  t text;
begin
  foreach t in array array[
    'profiles', 'categories', 'agenda_entries', 'movements', 'budgets',
    'savings_goals', 'classification_rules', 'invoices', 'invoice_items'
  ]
  loop
    execute format(
      'drop trigger if exists %I on public.%I', t || '_touch_synced_at', t);
    execute format(
      'create trigger %I before insert or update on public.%I
         for each row execute function public.kivo_touch_synced_at()',
      t || '_touch_synced_at', t);

    if t <> 'profiles' then
      execute format(
        'drop trigger if exists %I on public.%I', t || '_freeze_user_id', t);
      execute format(
        'create trigger %I before update on public.%I
           for each row execute function public.kivo_freeze_user_id()',
        t || '_freeze_user_id', t);
    end if;
  end loop;
end;
$$;

-- ---------------------------------------------------------------------------
-- Row Level Security
-- ---------------------------------------------------------------------------
-- Sin esto, la anon key (que va dentro del APK y del bundle JS, es publica por
-- diseño) permitiria leer la tabla entera. RLS es la unica frontera real.
do $$
declare
  t text;
  owner_col text;
begin
  foreach t in array array[
    'profiles', 'categories', 'agenda_entries', 'movements', 'budgets',
    'savings_goals', 'classification_rules', 'invoices', 'invoice_items'
  ]
  loop
    owner_col := case when t = 'profiles' then 'id' else 'user_id' end;

    execute format('alter table public.%I enable row level security', t);
    -- FORCE: aplica RLS tambien al dueño de la tabla, no solo a roles ajenos.
    execute format('alter table public.%I force row level security', t);

    execute format('drop policy if exists %I on public.%I', t || '_select_own', t);
    execute format(
      'create policy %I on public.%I for select using (auth.uid() = %I)',
      t || '_select_own', t, owner_col);

    execute format('drop policy if exists %I on public.%I', t || '_insert_own', t);
    execute format(
      'create policy %I on public.%I for insert with check (auth.uid() = %I)',
      t || '_insert_own', t, owner_col);

    execute format('drop policy if exists %I on public.%I', t || '_update_own', t);
    execute format(
      'create policy %I on public.%I for update using (auth.uid() = %I) with check (auth.uid() = %I)',
      t || '_update_own', t, owner_col, owner_col);

    execute format('drop policy if exists %I on public.%I', t || '_delete_own', t);
    execute format(
      'create policy %I on public.%I for delete using (auth.uid() = %I)',
      t || '_delete_own', t, owner_col);
  end loop;
end;
$$;
