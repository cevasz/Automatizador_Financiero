# Guía del proyecto

## Alcance
- Android nativo en Kotlin.
- Captura de movimientos sólo desde notificaciones bancarias autorizadas.
- Sin credenciales bancarias ni scraping.
- Motor de clasificación por reglas, regex e histórico.

## Arquitectura
1. `NotificationListenerService` captura el texto crudo.
2. Cada banco tiene su propio `BankParser`.
3. El resultado se normaliza en `RawMovement`.
4. Se cruza con `Agenda` para reconocer comercios/cuentas.
5. La clasificación final se guarda en Room.

## Sincronizacion web
- El login es para la cuenta del panel web, no para bancos.
- Se usa para preparar la sincronizacion con backend y web.
- La app sigue funcionando localmente si no hay sesion web.
- Datos de entrada: correo, URL del backend y token de acceso.

## Regla de limpieza
- Mantener sólo documentación viva y artefactos útiles del grafo.
- Borrar caches, builds y conversiones regenerables.
