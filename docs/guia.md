# Guía del proyecto — Kivo

> Reglas y convenciones de trabajo: `CLAUDE.md`. Especificación completa (visión,
> arquitectura objetivo, modelo de datos, roadmap, riesgos): `docs/SDD.md`.
> Lista de trabajo pendiente: `docs/PENDIENTES.md`.

## Identidad
- Nombre: **Kivo**, tagline "Tu dinero, en orden".
- Paleta: coral `#F56565`/`#FC8181`, pizarra `#2D3748`, crema `#FEFCF5`;
  complementarios teal `#2C7A7B` (ingresos), ámbar `#D69E2E` (avisos), azul `#3182CE`
  (información). Definición en código:
  `kivo-android/app/src/main/java/.../presentation/ui/theme/Color.kt`.
- Ícono adaptativo vectorial (monograma K crema + moneda teal sobre fondo coral) en
  `kivo-android/app/src/main/res/` — sin PNGs de lanzador. Referencia de diseño original
  en `docs/brand/`.

## Estructura (monorepo)
- `kivo-android/`: proyecto Gradle autocontenido (compilar/testear desde ahí).
- `web/` y `backend/`: pendientes de desarrollo (solo README).
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

## Sincronización con la web
- El inicio de sesión no es bancario: sirve para vincular la app Android con la cuenta
  del panel web y preparar la sincronización de movimientos, agenda y configuraciones —
  el backend y la web ya aparecen en el SDD como parte de la arquitectura objetivo.
- Si la sesión web no existe, la app sigue funcionando localmente sin bloquear el uso
  principal.
- No guardar ni pedir credenciales de bancos. Solo correo, URL del backend y token de
  acceso de la cuenta web.

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
