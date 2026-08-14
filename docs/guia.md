# Guía del proyecto — Kivo

## Identidad
- Nombre: **Kivo**, tagline "Tu dinero, en orden".
- Paleta: coral `#F56565`/`#FC8181`, pizarra `#2D3748`, crema `#FEFCF5`;
  complementarios teal `#2C7A7B` (ingresos), ámbar `#D69E2E` (avisos), azul `#3182CE`.
- Ícono adaptativo vectorial (monograma K + moneda teal sobre coral) en
  `kivo-android/app/src/main/res/` — no hay PNGs de lanzador.

## Estructura (monorepo)
- `kivo-android/`: proyecto Gradle autocontenido (compilar/testear desde ahí).
- `web/` y `backend/`: pendientes de desarrollo (solo README).
- `docs/`: esta guía y documentación viva.
- `graphify-out/`: grafo de dependencias (regenerar con graphify tras cambios).

## Alcance
- Android nativo en Kotlin.
- Captura de movimientos sólo desde notificaciones bancarias autorizadas.
- Importación manual de extractos en CSV, texto plano o PDF (selector de archivos +
  `pdfbox-android` para extraer el texto; el mismo motor de parseo/clasificación
  procesa el resultado automáticamente).
- Sin credenciales bancarias ni scraping.
- Motor de clasificación por reglas, regex e histórico.

## Arquitectura
1. `NotificationListenerService` captura el texto crudo.
2. Cada banco tiene su propio `BankParser`.
3. El resultado se normaliza en `RawMovement`.
4. Se cruza con `Agenda` para reconocer comercios/cuentas.
5. La clasificación final se guarda en Room.
6. Centro de notificaciones in-app (tabla `app_notifications`): la app avisa de
   movimientos capturados, importaciones, presupuestos por debajo de lo gastado
   y metas logradas. Pantalla en el menú lateral con contador de no leídas.

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

## Sincronizacion web
- El login es para la cuenta del panel web, no para bancos.
- Se usa para preparar la sincronizacion con backend y web.
- La app sigue funcionando localmente si no hay sesion web.
- Datos de entrada: correo, URL del backend y token de acceso.

## Regla de limpieza
- Mantener sólo documentación viva y artefactos útiles del grafo.
- Borrar caches, builds y conversiones regenerables.
