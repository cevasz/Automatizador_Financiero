# Kivo Web — panel

Panel de consulta y correccion del historial financiero que la app Android
sincroniza a Supabase. **No captura movimientos**: la lectura de notificaciones
bancarias solo existe en el telefono (ver `ARQUITECTURA.md`). Aqui se revisa con calma
lo que ya se capturo, en una pantalla grande.

## Stack

- **Next.js (App Router) + React + TypeScript**. Render en el servidor para que
  cada pantalla llegue con sus datos y no haya un parpadeo de "cargando".
- **Supabase** como base de datos y autenticacion (`backend/README.md`).
- **CSS propio**, sin framework de UI: la paleta "Barro & Ocre" viene de la app
  Android y un framework la taparia con sus propios valores por defecto. Sin
  fuentes remotas — el panel se construye y se sirve sin salir a Internet.

## Puesta en marcha

```bash
cd web
cp .env.example .env.local     # y rellenar con los datos del proyecto Supabase
npm install
npm run dev                    # http://localhost:3000
```

Requisito previo: haber ejecutado las migraciones de
`backend/supabase/migrations/` en el proyecto de Supabase.

## Estructura

```
src/
  middleware.ts              refresca la sesion y protege /panel
  app/
    entrar/                  registro e inicio de sesion
    panel/
      layout.tsx             navegacion + guarda de sesion
      page.tsx               resumen del mes, grafica y ultimos movimientos
      movimientos/           listado filtrable + edicion en linea
      presupuestos/          limites del mes con gasto real
      metas/                 metas de ahorro y abonos
      agenda/                contactos y su categoria por defecto
      cuenta/                que hay sincronizado, exportar, borrar
      actions.ts             TODAS las escrituras (Server Actions)
    api/exportar/            descarga CSV / JSON
  lib/
    supabase/{client,server,env}.ts
    types.ts                 espejo TS de las tablas
    format.ts                pesos, fechas, porcentajes
  components/                piezas compartidas
```

## Reglas al tocar este codigo

- **Toda escritura pasa por `app/panel/actions.ts`** y pone `updated_at` en la
  hora actual. Sin eso, el "gana el mas reciente" del servidor ve un valor viejo
  y el proximo push del telefono revierte en silencio la correccion.
- **Los borrados son logicos** (`deleted = true`), nunca `DELETE`. Un borrado
  fisico reaparece en la siguiente subida del telefono, que no sabe que se
  borro. La unica excepcion es "borrar todos mis datos" (habeas data), donde el
  borrado fisico es justamente lo que se pide.
- **Los montos son centavos** en la base y pesos en la interfaz. La conversion
  vive en `lib/format.ts` (mostrar) y en las Server Actions (guardar).
- **El aislamiento entre usuarios no se programa aqui**: lo hace Row Level
  Security en Postgres. No hace falta (ni sirve) filtrar por `user_id` en cada
  consulta.

## Despliegue

Pensado para Vercel (plan gratuito): importar el repo, poner *Root Directory* en
`web/`, y definir `NEXT_PUBLIC_SUPABASE_URL` y `NEXT_PUBLIC_SUPABASE_ANON_KEY`
como variables de entorno. Cualquier host con Node 20+ y `npm run build && npm
run start` tambien funciona.
