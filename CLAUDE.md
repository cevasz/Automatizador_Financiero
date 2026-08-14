# Contexto del proyecto — Kivo, Contabilidad Financiera Automática (Colombia)

## Qué es
Kivo es una app Android que lee notificaciones bancarias autorizadas (Nequi, Bancolombia,
Daviplata, Nu, Lulo Bank) para construir un historial financiero automático, sin pedir nunca
credenciales ni acceder directamente a cuentas.

Este archivo son las **reglas y convenciones** del proyecto. Para identidad de marca,
arquitectura interna detallada, permisos y sincronización, ver `docs/guia.md` (resumen
operativo). Para visión de producto, contexto regulatorio completo, modelo de datos y
roadmap por fases, ver `docs/SDD.md`. Para la lista de trabajo pendiente, ver
`docs/PENDIENTES.md`.

## Estructura del repositorio (monorepo)
- `kivo-android/`: app Android nativa (Kotlin + Compose). Proyecto Gradle autocontenido.
- `web/`: panel web, **pendiente de desarrollo** (solo README).
- `backend/`: API de sincronización, **pendiente de desarrollo** (solo README).
- `docs/`: documentación viva del proyecto (guía, SDD, pendientes, fuente de marca).
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

## Contexto regulatorio (Colombia, 2026) — condiciona el diseño, no solo el MVP
El Decreto 0368 de 2026 (Finanzas Abiertas) y la Ley 1581 de 2012 (habeas data) condicionan
la arquitectura a mediano plazo — detalle completo en `docs/guia.md` § Contexto regulatorio
y `docs/SDD.md` § 2.2/8.2. En corto: la lectura de notificaciones es una estrategia puente
hacia Open Finance (12-24 meses), no rediseñar clasificación/agenda al migrar; y el usuario
debe poder exportar/eliminar su información en cualquier momento (ya implementado).

## Cómo contextualizarse en este repo (para agentes / sesiones nuevas)
`graphify-out/` ya existe y se mantiene actualizado (`/graphify --update` re-extrae solo lo
nuevo/cambiado). **Antes de leer archivos sueltos o explorar el árbol a mano, usa
`graphify query "<pregunta>"`** (o el skill `graphify`) para ubicar el código/doc relevante
con pocos tokens — cae directo a nodos y líneas concretas en vez de barrer directorios.
Recurre a Explore/Read completos solo cuando graphify no encuentre nodos coincidentes o
cuando ya sepas exactamente qué archivo necesitas editar.

## Convenciones de código
- Kotlin idiomático, sin dependencias innecesarias.
- Cada `BankParser` debe tener tests unitarios con ejemplos de texto REAL (ver
  `kivo-android/app/src/test/resources/fixtures/`) — nunca inventar el formato de una notificación.
- Agregar una entidad bancaria nueva = un `BankParser` nuevo + sus fixtures, sin tocar el
  resto del sistema. Nunca mezclar reglas de distintos bancos en una sola función.
- Módulos desacoplados: el motor de parseo/clasificación no debe saber nada de UI.
- Commits pequeños, un cambio funcional por commit, mensajes descriptivos en español.
- Compilar/testear desde `kivo-android/` (proyecto autocontenido).

## Qué NO hacer
- No usar la API de Accesibilidad para ejecutar acciones automáticas (prohibido por
  política de Google Play).
- No pedir permisos más amplios de los necesarios.
- No construir backend, web, ni sync multi-dispositivo hasta que el MVP local funcione
  y esté validado en un dispositivo real.
