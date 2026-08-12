# Guía del proyecto

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

## Sincronizacion web
- El login es para la cuenta del panel web, no para bancos.
- Se usa para preparar la sincronizacion con backend y web.
- La app sigue funcionando localmente si no hay sesion web.
- Datos de entrada: correo, URL del backend y token de acceso.

## Regla de limpieza
- Mantener sólo documentación viva y artefactos útiles del grafo.
- Borrar caches, builds y conversiones regenerables.
