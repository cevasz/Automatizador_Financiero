# Kivo — Backend (Supabase)

No hay servidor propio. La "API" es **Supabase**: Postgres gestionado + Auth +
PostgREST. Este directorio contiene el **esquema versionado** y las funciones de
sincronización; no hay código de aplicación que desplegar ni mantener corriendo.

## Por qué Supabase y no un backend propio

- El modelo de datos de Kivo ya es relacional (movimientos ↔ categorías ↔ agenda con
  llaves foráneas). Postgres lo recibe tal cual; una base de documentos obligaría a
  remodelarlo.
- El aislamiento entre usuarios queda **en la base** (Row Level Security), no en código
  de aplicación que haya que auditar en cada endpoint nuevo.
- La app Android habla HTTP plano contra PostgREST: no entra un SDK pesado ni Google
  Play Services al APK.

## Estado: desplegado y verificado

Las dos migraciones **ya están aplicadas** en el proyecto real. Comprobado de extremo a
extremo el 2026-08-18 contra la base en producción: alta de usuario, subida, bajada
incremental con cursor, resolución de conflictos en los dos sentidos, propagación de
borrados, aislamiento entre usuarios y borrado total.

```
Proyecto : etmudmitqszrawenimoi
URL      : https://etmudmitqszrawenimoi.supabase.co
```

### Dónde viven las credenciales

Solo en archivos que **no se versionan** (hay plantillas `.example` al lado de cada uno):

| Lado | Archivo (ignorado por git) | Claves |
|---|---|---|
| App Android | `kivo-android/local.properties` | `supabase.url`, `supabase.anonKey` |
| Panel web | `web/.env.local` | `NEXT_PUBLIC_SUPABASE_URL`, `NEXT_PUBLIC_SUPABASE_ANON_KEY` |

La `anon key` **es pública por diseño**: viaja dentro del APK y dentro del bundle JS del
panel, y no hay forma de esconderla en ninguno de los dos. Lo que protege los datos no es
el secreto de esa llave sino RLS — sin un JWT de sesión válido, `auth.uid()` es `null` y
toda política falla, así que la llave sola no devuelve ni una fila (comprobado: un
`select` con la anon key devuelve `[]` y un `insert` devuelve `42501`).

La **`service_role` key nunca debe salir del panel de Supabase** ni escribirse en ningún
archivo del repositorio: se salta RLS por completo y puede leer y borrar los datos de
cualquier usuario. Si alguna vez se comparte por error (chat, captura, correo), hay que
rotarla en *Project Settings → API → Reset service role key*.

### Configuración pendiente en el panel de Supabase

- **Confirmación de correo activada** (`mailer_autoconfirm: false`). Es lo correcto para
  producción, pero significa que al registrarse desde la app **no se abre sesión hasta
  confirmar el correo**; la app lo dice con ese mensaje exacto en vez de fallar en
  silencio. Para probar cómodamente: *Authentication → Providers → Email → Confirm
  email*, desactivar. Ojo también con el límite de correos del SMTP gratuito de Supabase
  (unos pocos por hora); para producción hay que configurar un SMTP propio.

## Archivos

```
supabase/migrations/
  0001_kivo_schema.sql     tablas, índices, triggers y políticas RLS
  0002_kivo_sync_api.sql   kivo_push_changes / kivo_pull_changes / kivo_delete_all_data
```

## Reaplicar el esquema desde cero

Si hay que rehacerlo (proyecto nuevo, entorno de pruebas):

1. Crear el proyecto en <https://supabase.com>. Región recomendada: `us-east-1`, la más
   cercana a Colombia con plan gratuito.
2. **SQL Editor → New query**, pegar y ejecutar en orden `0001_kivo_schema.sql` y luego
   `0002_kivo_sync_api.sql`.
3. Copiar `Project URL` y `anon public` de **Project Settings → API** a los dos archivos
   de la tabla de arriba.

Con la CLI de Supabase, como alternativa:

```bash
npm install -g supabase
supabase link --project-ref <ref-del-proyecto>
supabase db push          # aplica supabase/migrations/ en orden
```

## Cómo funciona la sincronización

Los porqués de cada decisión (identidad de fila, orden de las fases, conflictos,
borrados, cursor) están explicados una sola vez en **`docs/guia.md` § Panel web y
sincronización con la nube**, para no mantener dos versiones del mismo texto. El detalle
línea a línea está en los comentarios de las dos migraciones.

Resumen del flujo:

```
  Android (Room)                Supabase (Postgres)              Panel web
       │                                │                             │
       │ 1. kivo_pull_changes(cursor)   │                             │
       │<───────────────────────────────┤  todo lo que cambió después │
       │                                │  de cursor (reloj servidor) │
       │ 2. kivo_push_changes(payload)  │                             │
       ├───────────────────────────────>│  upsert con last-write-wins │
       │                                │<────────────────────────────┤
       │                                │   lee y edita vía PostgREST │
```

## Qué NO se sube

- Imágenes de facturas y capturas de pantalla (solo la URI local del teléfono).
- Contenido de notificaciones que el usuario no haya confirmado como movimiento.
- El texto crudo del banco, si el usuario apaga ese interruptor en la pantalla de Cuenta.
- Credenciales bancarias — Kivo nunca las pide (ver `CLAUDE.md`).
