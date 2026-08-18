# Guía del proyecto — Kivo

> Reglas y convenciones de trabajo: `CLAUDE.md`. Especificación completa (visión,
> arquitectura objetivo, modelo de datos, roadmap, riesgos): `docs/SDD.md`.
> Lista de trabajo pendiente: `docs/PENDIENTES.md`.

## Identidad
- Nombre: **Kivo**, tagline "Tu dinero, en orden".
- Paleta **"Barro & Ocre"** (tonos tierra, reemplazó a la coral/teal original en la
  sesión del 2026-08-14): barro `#9C4A3C` (primario y gastos), oliva `#6B7D4F`
  (secundario e ingresos), ocre `#C68A3D` (terciario y avisos), piedra cálida `#DED1B8`
  (fondo), azul piedra `#5E7A8C` (único acento frío, solo semántico). Definición en
  código: `kivo-android/app/src/main/java/.../presentation/ui/theme/Color.kt`, y
  duplicada como tokens CSS en `web/src/app/globals.css` (son dos plataformas sin
  pipeline de tokens compartido: si cambia una, hay que cambiar la otra).
- Ícono adaptativo vectorial (monograma K crema + moneda teal sobre fondo coral) en
  `kivo-android/app/src/main/res/` — sin PNGs de lanzador. Referencia de diseño original
  en `docs/brand/`.

## Estructura (monorepo)
- `kivo-android/`: proyecto Gradle autocontenido (compilar/testear desde ahí).
- `web/`: panel web en Next.js + TypeScript (`npm run dev` desde `web/`).
- `backend/`: no hay servidor propio — es el esquema SQL versionado de Supabase
  (`backend/supabase/migrations/`) más las funciones de sincronización.
- `docs/`: esta guía, el SDD, la lista de pendientes y el material de marca.
- `graphify-out/`: grafo de dependencias (regenerar con `graphify --update` tras cambios).

## Alcance
- Android nativo en Kotlin.
- Captura de movimientos sólo desde notificaciones bancarias autorizadas: Bancolombia,
  Nequi, Daviplata, Nu, Lulo Bank.
- Importación manual de extractos en CSV, texto plano o PDF (selector de archivos +
  `pdfbox-android` para extraer el texto; el mismo motor de parseo/clasificación
  procesa el resultado automáticamente).
- Sin credenciales bancarias ni scraping.
- Motor de clasificación por reglas, regex e histórico — sin LLM ni servicios de IA
  externos en el MVP.

## Contexto regulatorio (Colombia, 2026)
- **Decreto 0368 de 2026**: obliga el Sistema de Finanzas Abiertas (Open Finance) para
  entidades vigiladas por la Superintendencia Financiera. La lectura de notificaciones es
  una estrategia puente — el diseño anticipa migrar hacia APIs oficiales de Open Finance
  en un horizonte de 12-24 meses sin rediseñar clasificación ni agenda financiera
  (`Movement.kt` ya modela `MovementSource.OPEN_FINANCE` para ese momento). Detalle en
  `docs/SDD.md` § 2.2 y Fase 4 del roadmap.
- **Ley 1581 de 2012** (habeas data): exige poder exportar/eliminar toda la información
  del usuario en cualquier momento (implementado en Ajustes) y, a futuro, registro formal
  como responsable del tratamiento + consentimiento diferenciado por fuente de captura.
  Detalle en `docs/SDD.md` § 8.2.

## Arquitectura
1. `NotificationListenerService` captura el texto crudo.
2. Cada banco tiene su propio `BankParser` (regex propias, nunca mezcladas entre bancos).
3. El resultado se normaliza en `RawMovement`.
4. Se cruza con `Agenda` para reconocer comercios/cuentas.
5. La clasificación final se guarda en Room.
6. Centro de notificaciones in-app (tabla `app_notifications`): la app avisa de
   movimientos capturados, importaciones, presupuestos ajustados y metas logradas.
   Pantalla en el menú lateral con contador de no leídas.

## Panel web y sincronización con la nube

Kivo sigue siendo **local-first**: la captura y la edición viven en Room y la app
funciona entera sin red ni cuenta. La nube es una réplica opcional que existe para dos
cosas: ver el historial en una pantalla grande y no perderlo si se pierde el teléfono.

- **Dónde vive**: Supabase (Postgres gestionado + Auth + PostgREST). No hay servidor
  propio que mantener corriendo. Puesta en marcha y credenciales: `backend/README.md`.
- **Aislamiento entre usuarios**: Row Level Security en Postgres, no código de
  aplicación. La `anon key` es pública por diseño (viaja en el APK y en el bundle del
  navegador); sin un JWT de sesión válido, `auth.uid()` es `null` y ninguna política
  devuelve una sola fila.
- **Identidad de fila**: cada registro lleva un `syncId` (UUID) generado **en el
  dispositivo** al crearlo — el `id` de Room es un autoincremental que solo significa
  algo en ese teléfono. Generarlo en el cliente es lo que permite crear movimientos sin
  red.
- **Orden**: primero bajar, después subir. Un teléfono nuevo que entra a una cuenta ya
  poblada sembró sus propias categorías con UUID distintos; bajando primero, las
  reconcilia por llave natural y adopta el UUID de la nube en vez de chocar contra los
  índices únicos del servidor.
- **Conflictos**: gana la escritura más reciente (`updated_at`). Los dos lados casi
  nunca editan lo mismo — el teléfono captura, la web corrige.
- **Borrados**: lógicos (`deleted = true`) más lápidas locales (`sync_deletions`). Un
  borrado físico sin lápida reaparece en la siguiente bajada.
- **Qué NO se sube**: imágenes de comprobantes (solo su URI local) y, si el usuario
  apaga el interruptor en Cuenta, el texto crudo de la notificación bancaria.
- **Sin credenciales bancarias, nunca.** La cuenta es de Kivo, no del banco.

Piezas: `data/sync/` en Android (`SupabaseClient`, `SyncEngine`, `SyncMappers`,
`SyncStore`, `Tombstones`), `backend/supabase/migrations/` en SQL, `web/src/app/panel/`
en la web.

## Permiso de notificaciones (importante)
- Android exige que el "Acceso a notificaciones" se habilite MANUALMENTE en
  Ajustes del sistema; la app no puede pedirlo con un diálogo.
- La app detecta el estado real con `NotificationAccess.isEnabled()`
  (lee `Settings.Secure["enabled_notification_listeners"]`) y muestra avisos
  en Dashboard y en Ajustes con botón que abre la pantalla del sistema.
- La captura llega tanto de SMS (paquete de la app de mensajería) como de
  Gmail/correo (`com.google.android.gm`, `com.samsung.android.email`), según
  cómo llegue la notificación del banco.
- `BootReceiver` usa `NotificationListenerService.requestRebind` (el sistema
  re-enlaza solo los listeners habilitados tras reiniciar).

## Bloqueo biométrico opcional
- Ajuste "Bloqueo con biometría": al activarlo, la app pide huella/rostro/PIN
  (`BiometricPrompt`, respaldo a credencial del dispositivo) al abrirse y cada
  vez que vuelve de segundo plano.
- Sin permisos adicionales. Si el dispositivo no tiene ningún método de bloqueo
  configurado, el interruptor avisa y no se activa el bloqueo.
- Componentes: `BiometricLockGate` (pantalla de bloqueo) y `BiometricAccess`
  (detección de disponibilidad).

## Regla de limpieza
- Mantener sólo documentación viva y artefactos útiles del grafo.
- Borrar caches, builds y conversiones regenerables.
