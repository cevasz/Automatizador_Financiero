-- ============================================================================
-- Kivo — API de sincronizacion (push / pull) y utilidades de habeas data
-- ----------------------------------------------------------------------------
-- Por que RPC y no llamadas REST tabla por tabla:
--
--   1. Orden de llaves foraneas. Un movimiento referencia una categoria y un
--      contacto de agenda; una factura referencia un movimiento. Subir tabla
--      por tabla obliga al cliente a conocer y respetar ese orden, y cualquier
--      fallo a mitad deja el servidor en un estado incoherente. Aqui todo
--      ocurre dentro de UNA transaccion, en el orden correcto.
--   2. Resolucion de conflictos en un solo lugar. El "gana el mas reciente"
--      (last-write-wins por updated_at) vive en SQL, no duplicado en el cliente
--      Android y en el panel web.
--   3. Una sola ida y vuelta por sincronizacion en vez de nueve.
--
-- Ninguna funcion es SECURITY DEFINER: corren con los permisos de quien llama,
-- asi que RLS sigue aplicando. Aunque alguien llamara kivo_push_changes con
-- filas de otro usuario, el user_id se sobrescribe con auth.uid() y la politica
-- de RLS rechazaria lo demas.
-- ============================================================================

-- Orden de dependencias: cada tabla solo referencia tablas anteriores.
create or replace function public.kivo_sync_tables()
returns text[]
language sql
immutable
as $$
  select array[
    'categories',           -- no depende de nadie (salvo de si misma)
    'agenda_entries',       -- -> categories
    'movements',            -- -> categories, agenda_entries
    'budgets',              -- -> categories
    'savings_goals',        -- no depende de nadie
    'classification_rules', -- -> categories
    'invoices',             -- -> movements
    'invoice_items'         -- -> invoices, categories, agenda_entries
  ]::text[];
$$;

-- ---------------------------------------------------------------------------
-- PUSH: sube los cambios locales del cliente
-- ---------------------------------------------------------------------------
-- payload = {
--   "tablas":   { "categories": [ {...fila completa...} ], "movements": [ ... ] },
--   "borrados": { "movements": [ "uuid", "uuid" ], ... }
-- }
--
-- Los borrados van aparte y NO como filas con deleted = true. Si fueran filas,
-- el upsert intentaria INSERTARLAS cuando el servidor no las conoce (algo
-- creado y borrado en el telefono sin haber sincronizado en medio) y reventaria
-- contra las columnas NOT NULL, tumbando toda la transaccion. Como lista de
-- ids, una lapida de algo que el servidor no tiene simplemente no afecta filas.
--
-- Devuelve { "server_time": ..., "tablas": {tabla: {recibidas, aplicadas}} }
create or replace function public.kivo_push_changes(payload jsonb)
returns jsonb
language plpgsql
as $$
declare
  t             text;
  rows_in       jsonb;
  update_set    text;
  ids_borrar    jsonb;
  applied       jsonb := '{}'::jsonb;
  n_before      integer;
  n_applied     integer;
  v_uid         uuid := auth.uid();
begin
  if v_uid is null then
    raise exception 'kivo_push_changes requiere sesion autenticada';
  end if;

  foreach t in array public.kivo_sync_tables()
  loop
    rows_in := coalesce(payload -> 'tablas' -> t, '[]'::jsonb);
    if jsonb_typeof(rows_in) <> 'array' or jsonb_array_length(rows_in) = 0 then
      continue;
    end if;

    n_before := jsonb_array_length(rows_in);

    -- Lista de columnas a pisar en un conflicto. Se excluyen:
    --   id         (es la llave del conflicto)
    --   user_id    (inmutable, ya forzado a auth.uid())
    --   synced_at  (la pone el trigger del servidor)
    --   created_at (la fecha de creacion original no se reescribe nunca)
    select string_agg(format('%I = excluded.%I', column_name, column_name), ', ')
      into update_set
      from information_schema.columns
     where table_schema = 'public'
       and table_name = t
       and column_name not in ('id', 'user_id', 'synced_at', 'created_at');

    execute format(
      'with entrada as (
         select (jsonb_populate_record(
                   null::public.%I,
                   fila || jsonb_build_object(''user_id'', $2::text)
                 )).*
           from jsonb_array_elements($1) as fila
       )
       -- El alias `destino` no es cosmetico: sin el, la fila existente solo se
       -- puede nombrar con el nombre completo de la tabla, que dentro de un
       -- format() dinamico se vuelve fragil de leer y de escribir bien.
       insert into public.%I as destino select * from entrada
       on conflict (id) do update set %s
       -- Last-write-wins: solo pisa si lo que llega es igual o mas nuevo que lo
       -- que ya hay. Un dispositivo que estuvo offline y sube datos viejos no
       -- revierte una edicion mas reciente hecha desde la web.
       where destino.updated_at <= excluded.updated_at',
      t, t, update_set)
      using rows_in, v_uid;

    get diagnostics n_applied = row_count;
    applied := applied || jsonb_build_object(
      t, jsonb_build_object('recibidas', n_before, 'aplicadas', n_applied));
  end loop;

  -- Borrados logicos, despues de los upserts: si una fila se edito y se borro
  -- en la misma tanda, debe quedar borrada y no reaparecer.
  foreach t in array public.kivo_sync_tables()
  loop
    ids_borrar := coalesce(payload -> 'borrados' -> t, '[]'::jsonb);
    if jsonb_typeof(ids_borrar) <> 'array' or jsonb_array_length(ids_borrar) = 0 then
      continue;
    end if;

    execute format(
      'update public.%I
          set deleted = true,
              -- greatest(): si otro dispositivo ya escribio algo mas nuevo, el
              -- borrado no debe retroceder el reloj logico de la fila.
              updated_at = greatest(updated_at, now())
        where user_id = $2
          and deleted = false
          and id in (select valor::uuid from jsonb_array_elements_text($1) as valor)',
      t)
      using ids_borrar, v_uid;

    get diagnostics n_applied = row_count;
    if n_applied > 0 then
      applied := applied || jsonb_build_object(
        t || ' (borrados)', jsonb_build_object('aplicadas', n_applied));
    end if;
  end loop;

  return jsonb_build_object(
    'server_time', now(),
    'tablas', applied
  );
end;
$$;

-- ---------------------------------------------------------------------------
-- PULL: baja lo que cambio en el servidor desde el ultimo cursor
-- ---------------------------------------------------------------------------
-- El cursor es synced_at (reloj del SERVIDOR), no updated_at: el reloj de un
-- telefono puede estar atrasado y sus filas se saltarian el cursor para siempre.
--
-- IMPORTANTE para quien implemente el cliente: guarda como proximo cursor
-- server_time MENOS unos segundos de solape. Una transaccion que empezo antes
-- que esta pero confirmo despues puede quedar justo fuera del corte; como los
-- upserts son idempotentes, volver a bajar unas filas repetidas no cuesta nada,
-- pero perderlas si.
create or replace function public.kivo_pull_changes(since timestamptz default '-infinity')
returns jsonb
language plpgsql
stable
as $$
declare
  t       text;
  salida  jsonb := '{}'::jsonb;
  filas   jsonb;
  v_uid   uuid := auth.uid();
begin
  if v_uid is null then
    raise exception 'kivo_pull_changes requiere sesion autenticada';
  end if;

  foreach t in array public.kivo_sync_tables()
  loop
    execute format(
      'select coalesce(jsonb_agg(to_jsonb(x)), ''[]''::jsonb)
         from public.%I x
        where x.user_id = $1 and x.synced_at > $2',
      t)
      into filas
      using v_uid, since;

    salida := salida || jsonb_build_object(t, filas);
  end loop;

  return jsonb_build_object(
    'server_time', now(),
    'tablas', salida
  );
end;
$$;

-- ---------------------------------------------------------------------------
-- Habeas data (Ley 1581 de 2012): borrar todo lo del usuario
-- ---------------------------------------------------------------------------
-- Borrado FISICO, no logico: aqui el usuario ejerce su derecho de supresion, no
-- esta sincronizando un borrado entre dispositivos. Dejar lapidas seria
-- justamente lo contrario de lo que pide.
create or replace function public.kivo_delete_all_data()
returns jsonb
language plpgsql
as $$
declare
  t       text;
  tablas  text[] := public.kivo_sync_tables();
  i       integer;
  total   jsonb := '{}'::jsonb;
  n       integer;
  v_uid   uuid := auth.uid();
begin
  if v_uid is null then
    raise exception 'kivo_delete_all_data requiere sesion autenticada';
  end if;

  -- En orden inverso al de dependencias: primero los hijos.
  for i in reverse array_length(tablas, 1) .. 1
  loop
    t := tablas[i];
    execute format('delete from public.%I where user_id = $1', t) using v_uid;
    get diagnostics n = row_count;
    total := total || jsonb_build_object(t, n);
  end loop;

  return jsonb_build_object('borradas', total, 'server_time', now());
end;
$$;

-- ---------------------------------------------------------------------------
-- Vista de apoyo para el panel web: resumen mensual
-- ---------------------------------------------------------------------------
-- Existe para que el dashboard no tenga que bajar todos los movimientos del
-- historico solo para dibujar la grafica de los ultimos 12 meses.
-- security_invoker: la vista respeta la RLS de quien la consulta (sin esto, en
-- Postgres una vista corre con los permisos de su dueño y filtraria de mas).
create or replace view public.movement_monthly_summary
with (security_invoker = true)
as
select
  m.user_id,
  date_trunc('month', m.date) as mes,
  m.type,
  count(*)                    as movimientos,
  sum(m.amount)               as total
from public.movements m
where m.deleted = false
  and m.confirmation_state <> 'REJECTED'
group by m.user_id, date_trunc('month', m.date), m.type;

grant select on public.movement_monthly_summary to authenticated;
