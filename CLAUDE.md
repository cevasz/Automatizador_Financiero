# Contexto del proyecto — App de Contabilidad Financiera Automática (Colombia)

## Qué es
App Android que lee notificaciones bancarias autorizadas (Nequi, Bancolombia, Daviplata, Nu,
Lulo Bank) para construir un historial financiero automático, sin pedir nunca credenciales
ni acceder directamente a cuentas. Documento completo de diseño: `docs/SDD.md`.

## Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente)
- Plataforma: Android nativo (Kotlin) primero. iOS queda para una fase posterior vía
  Share Extension — no vía lectura de notificaciones (Apple no lo permite).
- Motor de clasificación: reglas + expresiones regulares. NADA de LLM ni servicios de IA
  externos en el MVP (evita costo variable y dependencia de red).
- MVP sin backend: la app es 100% local (Room/SQLite) hasta la Fase 2. No construir
  backend ni sincronización todavía aunque el SDD los mencione — se agregan después.
- Modelo de negocio: núcleo (registrar, ver, clasificar movimientos) siempre gratuito
  e ilimitado. No implementar ningún muro de pago sobre estas funciones.
- Nunca solicitar usuario/clave bancario ni scraping de credenciales. Solo notificaciones
  autorizadas explícitamente por el usuario, permiso por permiso.

## Arquitectura interna (dentro de la app Android)
1. `NotificationListenerService` captura el texto crudo de la notificación.
2. Capa de parseo: una implementación de `BankParser` por entidad bancaria, cada una
   con sus propias reglas regex. Nunca mezclar reglas de distintos bancos en una sola función.
3. Salida estandarizada: objeto `RawMovement` (tipo, valor, medio, contraparte, fecha, entidad).
4. Enriquecimiento: cruce de `contraparte` contra la tabla `Agenda` (número/cuenta → comercio).
5. Clasificación: reglas de categoría por comercio conocido, palabra clave, o histórico.
6. Persistencia local en Room.

## Convenciones de código
- Kotlin idiomático, sin dependencias innecesarias.
- Cada `BankParser` debe tener tests unitarios con ejemplos de texto REAL (ver
  `app/src/test/resources/fixtures/`) — nunca inventar el formato de una notificación.
- Módulos desacoplados: el motor de parseo/clasificación no debe saber nada de UI.
- Commits pequeños, un cambio funcional por commit, mensajes descriptivos en español.

## Entidades bancarias soportadas en el MVP
Bancolombia, Nequi, Daviplata, Nu, Lulo Bank (ver sección 5 y 6.1 del SDD para el detalle
de flujo). Agregar una entidad nueva = agregar un `BankParser` nuevo + sus fixtures,
sin tocar el resto del sistema.

## Qué NO hacer
- No usar la API de Accesibilidad para ejecutar acciones automáticas (prohibido por
  política de Google Play).
- No pedir permisos más amplios de los necesarios.
- No construir backend, web, ni sync multi-dispositivo hasta que el MVP local funcione
  y esté validado en un dispositivo real.
