# Contexto del proyecto — Kivo, Contabilidad Financiera Automática (Colombia)

## Qué es
Kivo es una app Android que lee notificaciones bancarias autorizadas (Nequi, Bancolombia,
Daviplata, Nu, Lulo Bank) para construir un historial financiero automático, sin pedir nunca
credenciales ni acceder directamente a cuentas. Guía viva del proyecto: `docs/guia.md`.
Especificación completa (visión de producto, arquitectura objetivo, modelo de datos,
roadmap por fases, riesgos): `docs/SDD.md` (transcripción viva de `SDD_App_Finanzas.docx`).

## Contexto regulatorio (Colombia, 2026) — condiciona el diseño, no solo el MVP
- **Decreto 0368 de 2026**: obliga el Sistema de Finanzas Abiertas (Open Finance) para
  entidades vigiladas por la Superfinanciera. La lectura de notificaciones es una estrategia
  puente: el diseño debe anticipar migrar hacia APIs oficiales de Open Finance en 12-24 meses
  (ver sección 5.2 y Fase 4 del roadmap en `docs/SDD.md`) sin rediseñar clasificación ni agenda.
- **Ley 1581 de 2012** (habeas data): exige poder exportar/eliminar toda la información del
  usuario en cualquier momento (ya implementado en Ajustes) y, a futuro, registro formal como
  responsable del tratamiento + consentimiento diferenciado por fuente de captura.

## Estructura del repositorio (monorepo)
- `kivo-android/`: app Android nativa (Kotlin + Compose). Proyecto Gradle autocontenido.
- `web/`: panel web, **pendiente de desarrollo** (solo README).
- `backend/`: API de sincronización, **pendiente de desarrollo** (solo README).
- `docs/`: documentación viva del proyecto.
- `graphify-out/`: mapa de dependencias generado por `graphify`.

## Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente)
- Plataforma: Android nativo (Kotlin) primero. iOS queda para una fase posterior vía
  Share Extension — no vía lectura de notificaciones (Apple no lo permite).
- Motor de clasificación: reglas + expresiones regulares. NADA de LLM ni servicios de IA
  externos en el MVP (evita costo variable y dependencia de red).
- MVP local-first: la captura y la edición viven en Room/SQLite, pero ya existe una
  cuenta web para preparar la sincronizacion con el panel y el backend.
- Modelo de negocio: núcleo (registrar, ver, clasificar movimientos) siempre gratuito
  e ilimitado. No implementar ningún muro de pago sobre estas funciones.
- Nunca solicitar usuario/clave bancario ni scraping de credenciales. Solo notificaciones
  autorizadas explícitamente por el usuario, permiso por permiso.

## Identidad de marca
- Nombre: **Kivo**. Tagline: "Tu dinero, en orden".
- Paleta: coral `#F56565` y `#FC8181`, pizarra `#2D3748`, crema `#FEFCF5`.
  Complementarios: teal `#2C7A7B` (ingresos), ámbar `#D69E2E` (avisos), azul `#3182CE`
  (información). Ver `app/src/main/java/.../theme/Color.kt` (ruta dentro de `kivo-android/`).
- Ícono adaptativo vectorial (monograma K + moneda teal sobre fondo coral), sin PNG.

## Arquitectura interna (dentro de la app Android)
1. `NotificationListenerService` captura el texto crudo de la notificación.
2. Capa de parseo: una implementación de `BankParser` por entidad bancaria, cada una
   con sus propias reglas regex. Nunca mezclar reglas de distintos bancos en una sola función.
3. Salida estandarizada: objeto `RawMovement` (tipo, valor, medio, contraparte, fecha, entidad).
4. Enriquecimiento: cruce de `contraparte` contra la tabla `Agenda` (número/cuenta → comercio).
5. Clasificación: reglas de categoría por comercio conocido, palabra clave, o histórico.
6. Persistencia local en Room.
7. Centro de notificaciones in-app (tabla `app_notifications`): avisos de movimientos
   capturados, importaciones, presupuestos ajustados y metas logradas.
8. Bloqueo biométrico opcional (`BiometricLockGate` + `BiometricAccess`): pantalla de
   desbloqueo al abrir la app cuando la opción está activa. Sin permisos adicionales;
   usa `BiometricPrompt` con respaldo a PIN/patrón del dispositivo.

## Sincronizacion con la web
- El inicio de sesion no es bancario: sirve para vincular la app Android con la cuenta
  del panel web y preparar la sincronizacion de datos propios del usuario.
- El backend y la web ya aparecen en el SDD como parte de la arquitectura objetivo,
  incluyendo una sesion en la nube activa para sincronizar movimientos, agenda y
  configuraciones.
- Si la sesion web no existe, la app sigue funcionando localmente sin bloquear el uso
  principal.
- No guardar ni pedir credenciales de bancos. Solo correo, URL del backend y token de
  acceso de la cuenta web.

## Documentos vivos
- `docs/guia.md`: resumen operativo de arquitectura, sincronizacion y alcance.
- `docs/SDD.md`: especificación completa del producto (visión, arquitectura, modelo de
  datos, roadmap, riesgos, cumplimiento normativo). `SDD_App_Finanzas.docx` en la raíz es
  la fuente original; si cambia, actualizar `docs/SDD.md` a mano para no desincronizar.
- `graphify-out/GRAPH_REPORT.md`: mapa de dependencias generado por `graphify`.

## Cómo contextualizarse en este repo (para agentes / sesiones nuevas)
`graphify-out/` ya existe y se mantiene actualizado (`/graphify` re-extrae solo lo
nuevo/cambiado). **Antes de leer archivos sueltos o explorar el árbol a mano, usa
`graphify query "<pregunta>"`** (o el skill `graphify`) para ubicar el código/doc relevante
con pocos tokens — cae directo a nodos y líneas concretas en vez de barrer directorios.
Recurre a Explore/Read completos solo cuando graphify no encuentre nodos coincidentes o
cuando ya sepas exactamente qué archivo necesitas editar.

## Convenciones de código
- Kotlin idiomático, sin dependencias innecesarias.
- Cada `BankParser` debe tener tests unitarios con ejemplos de texto REAL (ver
  `kivo-android/app/src/test/resources/fixtures/`) — nunca inventar el formato de una notificación.
- Módulos desacoplados: el motor de parseo/clasificación no debe saber nada de UI.
- Commits pequeños, un cambio funcional por commit, mensajes descriptivos en español.
- Compilar/testear desde `kivo-android/` (proyecto autocontenido).

## Entidades bancarias soportadas en el MVP
Bancolombia, Nequi, Daviplata, Nu, Lulo Bank (ver secciones 5 y 6.1 de `docs/SDD.md` para
el detalle de flujo). Agregar una entidad nueva = agregar un `BankParser` nuevo + sus
fixtures, sin tocar el resto del sistema.

## Qué NO hacer
- No usar la API de Accesibilidad para ejecutar acciones automáticas (prohibido por
  política de Google Play).
- No pedir permisos más amplios de los necesarios.
- No construir backend, web, ni sync multi-dispositivo hasta que el MVP local funcione
  y esté validado en un dispositivo real.