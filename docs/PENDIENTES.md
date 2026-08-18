---
tags: [kivo, pendientes]
proyecto: Kivo
actualizado: 2026-08-18
fuente: "[[docs/SDD]]"
---

# Pendientes — Kivo

> Lista viva de trabajo pendiente, derivada de comparar `docs/SDD.md` (la
> especificación) contra el código real (`kivo-android/`), verificado con
> `graphify query`. Reglas/convenciones: [[CLAUDE]]. Arquitectura y alcance:
> [[docs/guia]].
>
> Este archivo está escrito en Markdown compatible con Obsidian (frontmatter,
> `[[wikilinks]]`, `#tags`, checkboxes) — se puede copiar o enlazar
> directamente dentro de una vault sin modificar nada.

## Estado de la lista (2026-08-18)

Repaso completo tras la sesión de nube + panel web. Lo que **queda abierto** y por qué:

| Pendiente | Por qué sigue abierto |
|---|---|
| Cifrado en reposo (SQLCipher) | Migrar una base con datos reales sin un dispositivo donde probarlo es el riesgo que ya rompió la app tres veces. Plan concreto escrito abajo, para ejecutarlo con teléfono a mano. |
| Layout adaptativo para tablet | Feature de layout transversal (afecta todas las pantallas). No cabía junto a lo de esta sesión sin hacerlo a medias. |
| Exportación a Excel y PDF | Excel real (.xlsx) sin dependencias implica escribir el ZIP+XML a mano; PDF puede salir con `android.graphics.pdf`. Trabajo acotado pero independiente de todo lo demás. |
| Alertas y detección de patrones (§6.7) | Fase 2 sin empezar. Lógica de dominio pura, buena candidata para la próxima sesión. |
| Sugerencia proactiva de agenda (§6.2) | Fase 1 sin empezar, 100% local y viable ya. |
| Sostenibilidad (5 ítems) | Son decisiones de producto antes que de código (qué recompensa, qué la diferencia). |
| Legal (3 ítems) | No son código. La nube los vuelve **urgentes**: ver "Sugerencias nuevas". |
| Métricas del §13 | Requiere diseñar telemetría opt-in y anonimizada sin traicionar el enfoque de privacidad. |
| Aprendizaje comunitario | Ya no lo bloquea la infraestructura, sí el diseño de privacidad. |

Todo lo demás de la lista está cerrado.

## 🌐 Sesión 2026-08-18: base de datos en la nube (Supabase) + panel web

El usuario pidió explícitamente **reabrir** la decisión de alcance que bloqueaba esto
("no construir backend, web ni sync hasta validar el MVP local en un dispositivo real",
[[CLAUDE]] y Fase 3-4 más abajo). Queda anotado que se reabrió a petición suya, no por
descuido. La app **sigue siendo local-first**: sin cuenta, sin red y sin credenciales de
Supabase funciona entera, y una compilación sin configurar lo dice de frente en vez de
dejar botones que fallan.

Elecciones tomadas con el usuario antes de escribir código: **Supabase** (sobre Firebase
o un backend propio) y **panel que lee y edita lo seguro** (sobre solo-lectura o edición
total).

### Nube — `backend/supabase/migrations/`
- [x] Esquema completo espejando las 8 tablas sincronizables de Room, con tres
  diferencias deliberadas: llave primaria **UUID generada en el dispositivo** (Kivo tiene
  que poder registrar un movimiento sin red; esperar un id del servidor lo impediría),
  `user_id` en cada fila, y **borrado lógico** (`deleted = true`) porque un `DELETE`
  físico no se puede propagar — un teléfono que estuvo offline volvería a subir lo
  borrado. #pendiente/fase2 ✅ 2026-08-18
- [x] **Row Level Security** activada y forzada en las 9 tablas, con políticas por
  operación. Es la única frontera real entre usuarios: la `anon key` va dentro del APK y
  del bundle JS, es pública por diseño y no hay forma de esconderla. Además, un trigger
  hace `user_id` inmutable, para que nadie pueda reasignar filas a otra cuenta.
  #pendiente/fase2 #seguridad ✅ 2026-08-18
- [x] **Dos relojes distintos, a propósito**: `updated_at` (hora del cliente) decide los
  conflictos; `synced_at` (hora del servidor, puesta por trigger) es el cursor de la
  bajada incremental. Usar `updated_at` como cursor dejaría fuera **para siempre** las
  filas de un teléfono con la hora atrasada. ✅ 2026-08-18
- [x] `kivo_push_changes` / `kivo_pull_changes` como funciones RPC en vez de llamadas
  REST tabla por tabla: una sola transacción respeta el orden de llaves foráneas
  (categorías → agenda → movimientos → facturas), el "gana el más reciente" vive en un
  solo lugar y son 2 idas y vueltas por sincronización en vez de 16. Los borrados viajan
  como **lista de ids aparte**, no como filas con `deleted = true`: como filas, el upsert
  intentaría insertar algo que el servidor no conoce y reventaría contra las columnas
  NOT NULL, tumbando toda la transacción. ✅ 2026-08-18
- [x] `kivo_delete_all_data()` para habeas data (Ley 1581): borrado **físico**, no
  lógico — aquí el usuario ejerce su derecho de supresión, no está sincronizando. ✅ 2026-08-18

### Sincronización en Android — `kivo-android/.../data/sync/`
- [x] Columna `syncId` (UUID) en las 8 entidades sincronizables + tabla `sync_deletions`
  para las lápidas, con **`MIGRATION_3_4` real**. Hasta ahora la base solo tenía
  `fallbackToDestructiveMigration()`, que ante cualquier cambio de esquema **borra todos
  los movimientos del usuario**; con datos reales de meses eso ya no es aceptable. Se
  activó también la exportación del esquema (`app/schemas/`) y se comparó el DDL que Room
  espera contra el que escribe la migración, columna por columna e índice por índice —
  un desajuste mínimo ahí deja la app sin abrir, que es exactamente el bug que ya se
  sufrió tres veces. #pendiente/fase2 ✅ 2026-08-18
- [x] `SupabaseClient` con `HttpURLConnection` y `kotlinx-serialization` (ambos ya
  presentes): el SDK oficial de Supabase para Android arrastra Ktor entero, y son cuatro
  peticiones en toda la app. Atrapa `Throwable`, no `Exception` — un fallo de TLS puede
  llegar como `Error` y, en una corrutina sin manejador, tumbaría el proceso (mismo
  patrón ya corregido en PDF y notificaciones). ✅ 2026-08-18
- [x] `SyncEngine`: **primero baja, después sube**. No es indiferente — un teléfono que
  entra por primera vez a una cuenta ya poblada sembró sus propias 33 categorías con UUID
  distintos; si subiera primero chocaría contra los índices únicos del servidor y la
  transacción entera fallaría. Bajando primero, reconcilia por llave natural (categoría
  por nombre+tipo, contacto por identificador, presupuesto por periodo) y **adopta el
  UUID de la nube**, con lo que la subida ya no tiene con qué chocar. ✅ 2026-08-18
- [x] `Tombstones`: registra el `syncId` **antes** de cada borrado local (metas,
  presupuestos, contactos, facturas con sus productos, categorías, reglas y "borrar todos
  mis datos"). Sin esto, "Borrar todos mis datos" en Ajustes dejaría la app vacía y la
  siguiente sincronización la repoblaría entera desde la nube — es decir, el botón no
  borraría nada de verdad. ✅ 2026-08-18
- [x] Pantalla de Cuenta rehecha: antes pedía "URL del backend" y "token de acceso"
  escritos a mano (nadie fuera del proyecto podía rellenar eso) y el botón "Sincronizar"
  **solo escribía un JSON dentro del propio teléfono**, no salía nada a ninguna parte.
  Ahora es correo + contraseña contra Supabase Auth, con estado real de sincronización.
  ✅ 2026-08-18
- [x] Interruptor "subir el texto original del banco". Es el dato más sensible que maneja
  Kivo (nombres de terceros, saldos); por defecto se sube, porque sin él la web no puede
  explicar de dónde salió un movimiento, pero quien prefiera lo contrario lo apaga y el
  resto se sincroniza igual. #seguridad ✅ 2026-08-18
- [x] 11 tests unitarios de la traducción Room ↔ Postgres. Es el punto donde la
  sincronización puede corromper datos **en silencio**: un monto mal escalado, una zona
  horaria mal leída o una llave foránea con el id local en vez del UUID no fallan en
  ninguna parte, solo dejan datos mal. ✅ 2026-08-18

### Panel web — `web/`
- [x] Next.js (App Router) + TypeScript + Supabase, con sesión en cookies para que cada
  pantalla llegue del servidor ya con sus datos. Middleware que refresca el token y
  protege `/panel`, usando `getUser()` y no `getSession()` (en el servidor, `getSession`
  se cree lo que diga la cookie; una cookie manipulada pasaría). #pendiente/fase2 ✅ 2026-08-18
- [x] Pantallas: resumen del mes con gráfica de 12 meses, movimientos filtrables con
  corrección de categoría y confirmar/rechazar en línea, presupuestos con gasto real,
  metas con abonos, agenda, y cuenta (qué hay sincronizado, exportar CSV/JSON, borrar
  todo). ✅ 2026-08-18
- [x] CSS propio con la paleta "Barro & Ocre" de la app, sin framework de UI ni fuentes
  remotas — un framework taparía la identidad con sus valores por defecto, y las fuentes
  remotas atarían el build a tener red. ✅ 2026-08-18

- versionCode 10 -> 11, versionName 1.6.2 -> 1.7.0 (nube + panel web).
- versionCode 11 -> 12, versionName 1.7.0 -> 1.7.1 (integración con la base real y limpieza).

### Integración con la base real y verificación de extremo a extremo (2026-08-18, tarde)

El usuario creó el proyecto de Supabase y aplicó las migraciones. Con la base real ya
disponible se pudo comprobar lo único que la sesión anterior había dejado sin verificar
—el SQL nunca se había ejecutado contra un Postgres—, y **los 11 chequeos pasaron**:

- [x] Credenciales conectadas en `kivo-android/local.properties` y `web/.env.local`
  (ambos ignorados por git; confirmado que no aparecen en `git status`). La `anon key`
  llega a `BuildConfig` y el APK ya se compila con sincronización activa. ✅ 2026-08-18
- [x] **Verificado contra la base real**: push inicial de las 5 tablas; pull completo;
  pull incremental con cursor (devuelve solo lo cambiado); *last-write-wins* en los dos
  sentidos (una versión más vieja se ignora, una más nueva se aplica); propagación de
  borrados por lápida; lápida de una fila que el servidor no conoce **no revienta la
  transacción** (era el riesgo concreto por el que los borrados viajan como lista de ids
  y no como filas); el borrado viaja de vuelta en el pull como `deleted = true`;
  `kivo_delete_all_data` deja las 8 tablas en cero. ✅ 2026-08-18
- [x] **RLS comprobada, no asumida**: un segundo usuario no ve ninguna fila del primero,
  su pull devuelve 0 filas, y un intento de pisar un movimiento ajeno **conociendo su
  id exacto** es rechazado con `42501`. Con la `anon key` sola, `select` devuelve `[]` e
  `insert` es rechazado. #seguridad ✅ 2026-08-18
- [x] Los dos usuarios de prueba se eliminaron al terminar. ✅ 2026-08-18
- **Hallazgo operativo**: el proyecto tiene la **confirmación de correo activada**
  (`mailer_autoconfirm: false`), así que registrarse desde la app crea el usuario pero
  **no abre sesión** hasta confirmar el correo. La app ya lo dice con ese mensaje exacto
  en vez de fallar en silencio. Para probar cómodamente conviene desactivar *Confirm
  email* en el panel; para producción hay que dejarlo activo y configurar un SMTP propio
  (el gratuito de Supabase permite pocos correos por hora).
- [ ] **Rotar la `service_role` key.** Se compartió por chat para esta integración, así
  que debe considerarse comprometida: *Project Settings → API → Reset service role key*.
  No está escrita en ningún archivo del repositorio (verificado), y ni la app ni el panel
  la necesitan — ambos usan solo la `anon key`. #pendiente/rapido #seguridad

### Limpieza de archivos redundantes (2026-08-18, tarde)

- [x] **~140 líneas muertas en [[SettingsViewModel]]** (380 → 239). `prepareSyncSnapshot()`
  se quedó sin ningún llamador al conectar la sincronización real: solo escribía un JSON
  dentro del propio teléfono que nadie leía. Con él se fueron `buildExportSnapshot()`,
  `buildSettingsObject()`, los seis `build*Array()` y el helper `toJsonArray` — una
  serialización completa de todas las tablas que ahora hace `SyncMappers` de verdad y
  contra un esquema real. Sobrevive `buildMovementsCsv()`, que sí usa la exportación CSV.
  ✅ 2026-08-18
- [x] **16 carpetas de paquete vacías** en `kivo-android/app/src/`, restos de un layout
  anterior: `data/entity`, `data/dao`, `data/parser`, `domain/usecase`,
  `presentation/theme`, `util` (el código real vive en `data/local/entity`,
  `data/local/dao`, `domain/parser`, `presentation/ui/theme`), más los árboles vacíos
  `src/test/java/` y `src/androidTest/java/` — los tests reales están en `src/test/kotlin/`.
  Confundían a cualquiera que explorara el árbol buscando dónde está cada cosa. ✅ 2026-08-18
- [x] **`docs/SDD_App_Finanzas.docx` eliminado.** `docs/SDD.md` es un superset suyo y era
  el que se mantenía actualizado; el `.docx` se había quedado en la v1.0 de julio. El
  propio encabezado del `.md` pedía "si el `.docx` cambia, actualizar este archivo a
  mano": dos copias sincronizadas manualmente, una de ellas imposible de comparar en git.
  Ahora hay una sola fuente; el original sigue en el historial. ✅ 2026-08-18
- [x] **Instantáneas fechadas de graphify** (`graphify-out/2026-08-*`, 4,2 MB en 3
  carpetas) eliminadas y añadidas a `.gitignore`. Duplicaban `graph.json` de días
  anteriores, que es justo lo que el historial de git ya guarda; se acumulaba una carpeta
  por sesión. ✅ 2026-08-18
- [x] `.gitignore` sin duplicados (`*.iml`, `/.idea` y `.gradle` estaban dos veces) y
  `.codex/` vacío eliminado. ✅ 2026-08-18
- [x] `backend/README.md` reescrito: la versión con la documentación real se había
  perdido en un traslado de carpetas y el archivo había quedado con el texto de "pendiente
  de desarrollo". Ahora documenta el estado desplegado y **no repite** el "cómo funciona
  la sincronización" que ya está en [[docs/guia]] — apunta a él. ✅ 2026-08-18
- **Revisado y dejado como está**: `ic_launcher.xml` e `ic_launcher_round.xml` son
  idénticos, pero a propósito — el manifiesto referencia los dos y unificarlos rompería
  el icono redondo. `.agents/rules/` y `.agents/workflows/` son configuración de otros
  asistentes (Windsurf, Gemini), no de este proyecto: no se tocan.

### Cómo ponerlo en marcha

Ya está en marcha (ver arriba). Los pasos para reaplicar el esquema en un proyecto nuevo
—y la configuración pendiente del panel de Supabase— están en `backend/README.md`.

## 🎨 Sesión 2026-08-18 (noche): sistema de diseño

El usuario pidió "migrar a un framework para que se vea más atractiva". Se le explicó
que **la app ya está en un framework moderno** (Jetpack Compose es declarativo, como
React o SwiftUI) y que migrar a Flutter/React Native obligaría a reescribir en Kotlin
nativo justo lo que define al producto — `NotificationListenerService`, ML Kit y PDFBox
no existen ahí — además de tirar Room y toda la sincronización, **sin arreglar el
problema visual**, porque el problema no es la herramienta sino la falta de un sistema
de diseño encima. El usuario eligió construir ese sistema, con tipografía propia.

### Diagnóstico (medido sobre el código, no a ojo)

| Medición | Antes |
|---|---|
| Pesos tipográficos sueltos | 4 distintos; `SemiBold` **60 veces** |
| Familia | `FontFamily.Default` en todo |
| Cifras de dinero | sin `tnum`: los montos **se movían** al actualizarse |
| Espaciado | dominaban 8/12 (60 y 52 usos); `24.dp` solo 7, `32.dp` solo 3 → todo apretado |
| Fuera de retícula | `3, 5, 6, 10, 14, 18, 22, 42 dp` |
| Formateadores de moneda | **5 duplicados** en 5 pantallas + 19 divisiones sueltas |

- [x] **Tipografía propia** ([[Type.kt]] reescrito): **Manrope** para interfaz y dinero,
  **Fraunces** (serif) solo para títulos de pantalla — que es además lo que conecta
  visualmente con el panel web, cuyos encabezados ya son serif. Las dos en formato
  variable (un archivo por familia, 520 KB en total) y con licencia OFL, que permite
  empaquetarlas. `ManropeFamily` registra **solo dos pesos** (W400 y W600): eso hace que
  la regla de "máximo dos pesos" la imponga la fuente, no la disciplina — un
  `FontWeight.Bold` suelto se resuelve a W600 en vez de introducir un tercer peso.
  #pendiente/rapido ✅ 2026-08-18
- [x] **Cifras tabulares en el dinero** (`KivoText.amount` / `amountLarge` / `amountSmall`,
  con `fontFeatureSettings = "tnum"`). Se verificó en el archivo de la fuente que Manrope
  trae `tnum`, no se asumió. Sin esto cada dígito tiene un ancho distinto: un saldo que
  pasa de $1.111 a $8.888 cambia de ancho y empuja lo que tiene al lado, y una columna de
  cifras no alinea. En una app cuyo contenido principal son números, ese temblor es lo
  que más delata que no hay diseño detrás. ✅ 2026-08-18
- [x] **Un solo formateador de dinero** ([[Money]] nuevo, 7 tests). Reemplaza las 5
  definiciones duplicadas y las 19 divisiones sueltas — unas `/ 100` (división entera,
  que **trunca**) y otras `/ 100.0`. La API recibe **centavos**, que es como se guarda en
  Room y en Postgres: eso elimina de raíz la pregunta "¿esto ya venía dividido?", que era
  el origen de las dos variantes. De paso desaparece el parámetro `currencyFormat` que se
  iba pasando de composable en composable por cinco pantallas. ✅ 2026-08-18
- [x] **Retícula de espaciado** ([[KivoSpacing]]): escala en múltiplos de 4 más nombres
  semánticos (`card`, `screen`, `betweenSections`…) para que la decisión se tome una vez
  y no en cada pantalla. El relleno de tarjeta subió de 20 a **24 dp**. ✅ 2026-08-18
- [x] **13 `fontWeight` redundantes eliminados**, los que acompañaban a estilos que ya
  traen el peso de énfasis. Repetirlos era parte de por qué `SemiBold` aparecía 60 veces
  sin crear ninguna jerarquía. ✅ 2026-08-18
- [x] Componentes nuevos en [[FinanceUi]]: `AmountText` (para que ningún monto se pinte
  con un `Text` suelto y se salte las cifras tabulares) y `Eyebrow` (etiqueta de sección
  en mayúsculas, que da jerarquía por tracking en vez de gastar otro peso). ✅ 2026-08-18
- **Verificado**: debug, release con R8 y los 65 tests en verde; las dos fuentes viajan
  dentro de los dos APK y sobreviven al renombrado de recursos de R8. APK release 52 MB.
- versionCode 12 -> 13, versionName 1.7.1 -> 1.8.0.

### Lo que falta de esta línea de trabajo

- [ ] **Aplicar el sistema pantalla por pantalla.** Quedan ~42 `fontWeight` sueltos sobre
  estilos de cuerpo y los valores de espaciado fuera de retícula: la base está, falta
  pasarla por cada pantalla. No es mecánico — cada caso hay que decidirlo mirando la
  jerarquía real de esa pantalla. #pendiente/deuda-tecnica
- [ ] **Licencias de terceros dentro de la app.** La OFL exige que la licencia acompañe a
  la distribución; hoy está en `kivo-android/app/LICENSES-fonts.md` pero falta exponerla
  en Ajustes → Acerca de. #pendiente/rapido #legal
- [ ] **Gráfica de gastos por categoría y tendencia mensual en el Inicio.** Es el hueco
  funcional más grande: el panel web dibuja 12 meses de ingresos vs gastos y **la app no
  tiene ni una gráfica**. En una app de finanzas, ver a dónde se va la plata *es* el
  producto. #pendiente/fase1
- [ ] **Búsqueda por contraparte en Movimientos.** La web filtra por texto; la app solo
  tiene chips de banco/estado. #pendiente/rapido
- [ ] **Cierre de mes** (el momento "pico" que hoy no existe): resumen de lo gastado, en
  qué, y comparado con el mes anterior. #pendiente/fase2
- [ ] **Widget de pantalla de inicio** con el saldo del mes y los pendientes por
  confirmar. En una app de dinero es de lo que más se usa y no estaba en la lista.
  #pendiente/fase2
- [ ] **27 `contentDescription = null`** por revisar, y solo 9 usos de retroalimentación
  háptica en toda la app — en finanzas el tacto es señal de confianza.
  #pendiente/deuda-tecnica

## 💵 Sesión 2026-08-18 (cierre): correcciones de dinero + rectángulos del Inicio

El usuario reportó dos cosas: unos "rectángulos chiquitos" en los módulos de Ingresos y
Gastos del Inicio, y que **tenía $2.000 más de los que Kivo decía, sin ninguna forma de
cuadrarlo**.

### El rectángulo de Ingresos/Gastos

- [x] **Causa: color translúcido + sombra de elevación.** `FinanceCard` aplica
  `Modifier.shadow(elevation = 8.dp)` a todas las tarjetas, y **24 de ellas** usan un
  `containerColor` semitransparente. Android dibuja la sombra debajo de toda la silueta;
  si lo de encima deja pasar la luz, esa silueta se ve *a través* de la tarjeta como un
  rectángulo más oscuro dentro de ella. Las de Ingresos y Gastos son las más translúcidas
  de la app (10 % de opacidad), por eso ahí se notaba. La solución no fue quitar el color
  ni la sombra sino **aplanar** el color contra el fondo con `compositeOver`: mismo tono,
  pero opaco, y la sombra vuelve a quedar por fuera. Arreglado en `FinanceCard`, así que
  cubre las 24 tarjetas de una vez. #pendiente/rapido #bug ✅ 2026-08-18
- [x] **Descartado con datos, no por intuición**: se revisó si eran glifos faltantes
  (`tofu`) leyendo el `cmap` de la fuente nueva y comparándolo contra **todos** los
  caracteres no-ASCII que la app muestra. El signo menos `−` que se introdujo con `Money`
  sí está en Manrope. Pero aparecieron dos que **no**: `✓` y `✗` en el probador de reglas
  de clasificación, que sí se habrían visto como rectángulos vacíos. Reemplazados por
  texto con color. #bug ✅ 2026-08-18

### Bug serio encontrado de paso

- [x] **`correctMovement()` escribía un estado que no existe.** Guardaba `"CORRECTED"`,
  que no está en el enum `ConfirmationState`. Al releer la fila,
  `ConfirmationState.valueOf("CORRECTED")` lanzaba y `toDomainSafely` la descartaba:
  **recategorizar un movimiento lo hacía desaparecer de la lista, y para siempre**,
  porque el valor inválido quedaba guardado en la base. Y desde que existe la
  sincronización era peor: la columna equivalente en Postgres tiene un CHECK con los
  cuatro valores válidos, así que **una sola fila así habría hecho fallar el push
  entero**, no solo esa fila. Corregido a `CONFIRMED` (corregir la categoría es, en la
  práctica, revisar y aceptar) más `MIGRATION_4_5`, que repara las filas ya guardadas con
  cualquier valor inválido. #pendiente/rapido #bug #seguridad ✅ 2026-08-18

### Manejo manual del dinero (lo que faltaba)

- [x] **Registrar un movimiento a mano.** `MovementSource.MANUAL` existía en el modelo
  desde el principio pero **ninguna pantalla lo producía**: no había salida cuando algo
  no se capturaba (efectivo, un banco que no manda notificación). `MovementEditorDialog`
  nuevo, con botón flotante en Movimientos — en el tercio inferior, al alcance del pulgar,
  que es donde el skill de diseño pide la acción principal. ✅ 2026-08-18
- [x] **Editar un movimiento entero**, no solo su categoría. Tocar un movimiento abría un
  diálogo que solo dejaba recategorizar; poder cambiar la categoría pero no el monto
  dejaba sin salida el caso más común de todos: que la cifra capturada esté mal. Ahora se
  edita tipo, monto, contraparte, categoría y fecha. Se conservan `createdAt`, `source` y
  `rawText`: son el rastro de **cómo llegó** ese movimiento, y borrarlo al corregir un
  monto dejaría sin forma de comprobar después por qué Kivo registró esa cifra.
  ✅ 2026-08-18
- [x] **Eliminar un movimiento desde el móvil.** Ya existía `Tombstones.antesDeBorrarMovimiento`
  sin usarse: el borrado se propagaba bien pero faltaba el botón. ✅ 2026-08-18
- [x] **"Cuadrar saldo"** — lo que el usuario pedía. En la tarjeta de balance del Inicio,
  pegado al saldo, que es donde uno nota que la cifra no coincide con la realidad. Muestra
  lo que Kivo calculó, se escribe lo que se tiene de verdad y **dice exactamente qué va a
  registrar antes de tocar el botón**.
  **Por qué un movimiento y no un número escondido**: la tentación es guardar un "saldo
  inicial" aparte y sumarlo al total, pero entonces el balance deja de poder explicarse —
  dentro de seis meses nadie sabría de dónde salieron esos $2.000. Como movimiento, el
  ajuste se ve en la lista, dice cuándo se hizo, se puede editar o borrar, y se sincroniza
  como cualquier otro. Es la diferencia entre corregir y disimular. La aritmética vive en
  `BalanceAdjustment`, función pura y con 6 tests, porque si el signo se invierte el saldo
  se aleja **el doble** en vez de cuadrar. ✅ 2026-08-18
- versionCode 13 -> 14, versionName 1.8.0 -> 1.9.0.

**Sin verificar en pantalla**: no hay dispositivo en este entorno, así que el arreglo del
rectángulo está razonado y compilado, no visto. Si tras instalar 1.9.0 siguiera
apareciendo, hace falta una captura para descartar el resto.

## 🔴 Rápidos (bajo esfuerzo, alto impacto)

### Sesión 2026-08-15: bug crítico — ningún paquete de banco era el real (pagos no se registraban)
El usuario reportó: "acabo de recibir un pago y no se registró, ni leyendo la
notificación". Investigando se encontró que **ninguno de los 5 paquetes de Android
declarados en los `BankParser` coincidía con la app real en Google Play** — verificado
con búsquedas reales, no adivinado:

| Banco | Paquete declarado (viejo) | Paquete real (verificado 2026-08-15) |
|---|---|---|
| Nequi | `com.nequi.app` | `com.nequi.MobileApp` |
| Bancolombia | `com.bancolombia.certipersonas` / `com.bancolombia.personas` | `co.com.bancolombia.personas.superapp` ("Mi Bancolombia"; la app vieja `com.todo1.mobile` fue retirada de la tienda en 2025) |
| Daviplata | `com.daviplata.daviplata` | `com.davivienda.daviplataapp` |
| Nu | `co.nubank` / `br.com.nubank` | `com.nu.production` |
| Lulo Bank | `com.lulobank.app` / `co.lulobank` | `co.com.lulobank.production` |

- [x] **Causa raíz**: `notification_listener_config.xml` declara un
  `notification-listener-include-filter` — **el sistema operativo aplica ese filtro
  antes de que `NotificationCaptureService.onNotificationPosted()` se ejecute**. Con el
  paquete real ausente de esa lista, Android nunca entregaba la notificación al código
  de la app, sin importar qué tan bien funcionara `BankParser.canParse()` — coincide
  exactamente con "ni siquiera leyendo la notificación". Se corrigieron los 5 paquetes en
  `notification_listener_config.xml` y en cada `BankParser.supportedPackageNames`
  (manteniendo los nombres viejos como respaldo, por si alguien tiene una versión
  desactualizada de la app bancaria). Se agregaron 5 tests de regresión que confirman que
  `ParserRegistry` reconoce cada paquete real. #pendiente/rapido #bug ✅ 2026-08-15
- **Importante para el usuario**: tras instalar esta actualización, puede que Android no
  vuelva a leer la lista de paquetes permitidos hasta que se desactive y reactive el
  permiso "Acceso a notificaciones" de Kivo en Ajustes del sistema (Ajustes → Apps →
  Acceso especial → Acceso a notificaciones).

### Sesión 2026-08-15 (continuación): permisos solicitados gradualmente
- [x] **POST_NOTIFICATIONS ahora se pide en el momento en que hace falta**, no solo
  disponible escondido en Ajustes. `AutoRequestPostNotificationsWhenRelevant()` (nueva,
  en `PostNotificationsPermissionState.kt`) se dispara desde el Dashboard justo cuando
  `notificationAccessEnabled` pasa a verdadero — el momento en que la captura de
  notificaciones bancarias queda activa y en cualquier momento va a llegar una
  notificación local de Kivo ("movimientos capturados", meta lograda). Se pide **como
  máximo una vez en la vida de la instalación** (se guarda en `SharedPreferences`,
  `finanzas_settings`/`asked_post_notifications`) — se acepte o se rechace, no se vuelve
  a insistir sola; el usuario siempre puede activarlo después a mano desde Ajustes (la
  fila que ya existía). Revisión del resto de permisos, confirmados ya graduales por
  construcción: cámara/galería (se piden en el instante exacto en que se toca "Tomar
  foto"/"Escanear", nunca antes), acceso a notificaciones bancarias (botón "Habilitar" en
  Dashboard, solo cuando el usuario llega ahí), biometría (`BiometricPrompt` del sistema
  pide consentimiento en el momento de cada desbloqueo, después de que el usuario activó
  el switch en Ajustes). #pendiente/rapido ✅ 2026-08-15
- versionCode 7 -> 8, versionName 1.5.1 -> 1.6.0.

### Sesión 2026-08-15 (continuación): movimientos duplicados (SMS+correo) + app que no volvía a abrir
El usuario reportó dos bugs: (1) Bancolombia manda SMS **y** correo para la misma
transacción, y Kivo registraba el movimiento dos veces; (2) después de subir un extracto
la app se cerró y ya no la dejaba abrir de nuevo (crash en cada intento, no solo una vez).

- [x] **Movimientos duplicados por canal múltiple** — `EnrichmentPipeline.process()`
  ahora revisa, antes de guardar, si ya existe un movimiento del mismo banco + tipo +
  monto dentro de una ventana de 5 minutos (`MovementDao.findPossibleDuplicates()`,
  nueva) — sin importar si vino por SMS, Gmail o la app oficial, porque el texto exacto
  varía entre canales aunque describan la misma transacción real. **Trade-off asumido a
  propósito**: si el usuario hace dos pagos genuinos del mismo monto al mismo banco
  dentro de esos 5 minutos, el segundo se descartaría por error — se prefirió este riesgo
  (bajo, poco común) sobre seguir duplicando cada SMS+correo (que pasa en *cada*
  transacción de Bancolombia). #pendiente/rapido #bug ✅ 2026-08-15
- [x] **App que dejaba de abrir por completo** — encontrados y corregidos varios puntos
  donde una excepción sin atrapar en una `CoroutineScope` sin manejador propio podía
  tumbar el proceso entero, no solo la operación en curso:
  - `FinanzasApplication.onCreate()`: `DefaultCategories.seed()`/`dedupe()` corren en
    **cada arranque**, antes de cualquier pantalla — sin try/catch, un fallo ahí
    crashearía la app en todo intento de abrirla, no solo una vez. Ahora atrapa
    `Throwable`.
  - `EnrichmentPipeline.process()`: lo llaman tanto `NotificationCaptureService` (un
    servicio en segundo plano) como los tres flujos de importación — ahora todo el
    cuerpo está en un try/catch(Throwable), así un movimiento problemático nunca tumba
    quien lo esté llamando.
  - `NotificationCaptureService.processNotification()`: mismo problema, corre en un
    `CoroutineScope` propio sin manejador — ahora atrapa `Throwable` y solo registra el
    error en el log.
  - `MovementViewModel.importMovements()` (comparte los 3 flujos: texto pegado, PDF,
    captura de pantalla) e `importStatementText()`: el catch pasó de `Exception` a
    `Throwable`.
  #pendiente/rapido #bug ✅ 2026-08-15
- versionCode 8 -> 9, versionName 1.6.0 -> 1.6.1.

### Sesión 2026-08-16: la app seguía sin abrir (causa distinta a la de 1.6.1)
El usuario reportó que, pese a los try/catch de 1.6.1, la app **seguía sin abrir**. Los
arreglos anteriores cubrían las corrutinas de arranque e importación, pero no el camino
por el que la app realmente se cae al abrir: la lectura reactiva de la base de datos.

- [x] **Causa raíz encontrada**: `MovementEntity.toDomain()` (y los demás mappers) usan
  `MovementType.valueOf(...)`, `BankEntity.valueOf(...)`, etc. — si **una sola fila**
  quedó con un valor que no corresponde a ningún enum (dato corrupto, un `BankEntity`
  que ya no existe, una escritura a medias), `valueOf` lanza `IllegalArgumentException`.
  Eso ocurre **dentro del `Flow`** que alimenta cada `StateFlow` de los ViewModel
  (`repository.getAllFlow().map { it.toDomain() }.stateIn(...)`), que se suscribe apenas
  se abre el Dashboard — sin ningún try/catch en ese camino. Resultado: una fila mala
  guardada en la base = crash en **cada** apertura, para siempre, sin forma de
  recuperarse desde la app. Solución: `toDomainSafely()` nueva en `ModelMappers.kt`, que
  descarta (con log) solo las filas que no se puedan mapear en vez de tumbar la lista
  entera; aplicada a los 6 puntos donde se mapean listas desde Room
  ([[MovementViewModel]] ×2, [[BudgetsViewModel]] ×2, [[AgendaViewModel]] ×2,
  [[SavingsGoalsViewModel]], [[InvoiceViewModel]] ×2). 3 tests de regresión nuevos
  (`ModelMappersTest`) que reproducen exactamente el escenario. #pendiente/rapido #bug ✅ 2026-08-16
- [x] **Dos instancias de Room sobre el mismo archivo** — `FinanzasApplication.onCreate()`
  construía su propia base con `Room.databaseBuilder(...)` mientras el resto de la app
  usa `FinanzasDatabase.getInstance()`: dos instancias distintas apuntando a
  `finanzas.db`, lo que puede dejar la base en estado inconsistente (y además se saltaba
  la config de migración centralizada). Ahora `FinanzasApplication` usa la instancia
  compartida. De paso, `getInstance()` ganó doble verificación dentro del `synchronized`
  (sin ella, dos hilos simultáneos podían crear dos instancias) y
  `fallbackToDestructiveMigrationOnDowngrade()` — sin esto, reinstalar un APK anterior
  hace que Room lance al abrir y la app crashee en cada arranque sin poder recuperarse.
  #pendiente/rapido #bug ✅ 2026-08-16
- **Si la app sigue sin abrir tras esta versión**: la única forma de confirmarlo es leer
  el `logcat` real del dispositivo (`adb logcat --pid=$(adb shell pidof -s com.finanzas.automatica)`),
  porque el error concreto no es deducible solo del código. Como último recurso, borrar
  los datos de la app (Ajustes → Apps → Kivo → Almacenamiento → Borrar datos) elimina la
  base corrupta — se pierden los movimientos locales, pero la app vuelve a abrir.
- versionCode 9 -> 10, versionName 1.6.1 -> 1.6.2.

- [x] Conectar el botón "Abonar" en Metas de ahorro — se agregó el botón + diálogo en [[SavingsGoalsScreen]]. De paso se corrigió un bug real: `addProgress()` **reemplazaba** el ahorro en vez de sumarle (`SavingsGoalDao.updateProgress` hace un `SET`, no un incremento). #pendiente/rapido ✅ 2026-08-14
- [x] Arreglar el selector de formato de exportación — CSV ahora exporta un CSV real de movimientos; Excel/PDF avisan honestamente que aún no están disponibles (roadmap) en vez de entregar un JSON con el nombre equivocado. De paso se separó `exportData()` (formato elegido por el usuario) de `prepareSyncSnapshot()` (snapshot completo para "Sincronizar" en Login), que antes compartían la misma función sin relación. #pendiente/rapido #bug ✅ 2026-08-14
- [x] Agregar `./gradlew test` al CI (`.github/workflows/build.yml`). #pendiente/rapido #ci ✅ 2026-08-14

### Sesión 2026-08-14 (tarde): OCR real + versión de la app
- [x] Total/deudas de Facturas no se actualizaban al agregar un producto individual — `totalInvoiceAmount`/`totalDebtAmount` en [[InvoiceScreen]] usaban `remember(draftItems)`, pero `draftItems` es la misma instancia de `SnapshotStateList` durante toda la pantalla (su referencia nunca cambia), así que el cálculo solo corría una vez. Cambiado a `derivedStateOf`, que sí rastrea los cambios internos de la lista. #pendiente/rapido #bug ✅ 2026-08-14
- [x] Versionado: cada cambio funcional sube `versionCode`/`versionName` en `app/build.gradle.kts` (esta sesión: 1 → 2, `1.0.0` → `1.1.0`). #pendiente/rapido ✅ 2026-08-14

### Sesión 2026-08-14 (noche): rediseño visual + animaciones (skill `mobile-app-ui-design`)
Se instaló el skill [mobile-app-ui-design](https://github.com/ceorkm/mobile-app-ui-design)
en `~/.claude/skills/` (principios de diseño mobile de apps como Airbnb/Duolingo/Revolut,
adaptados aquí a Compose ya que el skill original apunta a React/Tailwind). Cambios:
- [x] `FinanceCard`: esquinas 8dp → 20dp y sombra suave tintada con el color de marca (antes
  `elevation = 0.dp` + solo borde de 1px, se veía plano). `IconBadge`: 40dp → 44dp (zona de
  toque mínima recomendada). #pendiente/rapido ✅ 2026-08-14
- [x] 5 ilustraciones de marca en `res/drawable-nodpi/*.jpg` que ya existían en el proyecto
  pero **ningún composable las usaba** (confirmado con grep, cero referencias) — conectadas:
  `empty_state_wallet` en Movimientos/Facturas vacías, `savings_goal_illustration` en Metas
  vacías y en la celebración de meta lograda, `biometric_lock_illustration` en
  [[BiometricLockGate]], `onboarding_security` como hero en [[LoginScreen]],
  `splash_background` de fondo en [[SplashScreen]]. `EmptyState` ganó un parámetro
  `illustrationRes` opcional (compatible hacia atrás). #pendiente/deuda-tecnica ✅ 2026-08-14
- [x] Momento "pico" (peak-end rule) en Metas de ahorro: al abonar y completar una meta
  aparece una celebración a pantalla completa (ilustración + destellos animados + rebote de
  entrada) en vez de solo la notificación silenciosa que ya existía. #pendiente/fase1 ✅ 2026-08-14
- [x] Confirmación con gesto swipe en Movimientos (ver ítem de Fase 1 arriba). ✅ 2026-08-14

### Sesión 2026-08-14 (madrugada): paleta "Barro & Ocre", fondo del splash y navegación
El usuario no le gustó el resultado de la sesión anterior: "se ve muy AI" — pidió tonos
tierra en vez de coral/teal, un fondo de splash que no fuera una ilustración generada, y
reorganizar la navegación. Se presentaron 3 direcciones de paleta + 3 conceptos de fondo
en un artifact interactivo; el usuario eligió **Barro & Ocre** y **curvas topográficas**.
- [x] Paleta completa reemplazada en `Color.kt`/`Theme.kt` (tema "Kivo Coral" → "Kivo
  Barro"): primario ladrillo apagado `#9C4A3C` (antes coral `#F56565`), secundario oliva
  `#6B7D4F` (antes teal), terciario ocre `#C68A3D` (antes ámbar — casi sin cambio), fondo
  piedra cálida `#DED1B8` (antes crema casi blanco `#FEFCF5`). `IncomeGreen`/`ExpenseRose`/
  `WarningAmber` se remapearon a oliva/barro/ocre para que los movimientos no choquen con
  la paleta nueva. El nombre guardado en `SharedPreferences` (enum `KIVO_CORAL`) no cambió
  para no perder la preferencia de quien ya tenga la app instalada — solo el
  `displayName` visible. #pendiente/rapido ✅ 2026-08-14
- [x] Fondo del splash: la ilustración `splash_background.jpg` (ola abstracta, generada)
  se reemplazó por un patrón de curvas topográficas dibujado a mano en `Canvas` (dos
  focos de anillos concéntricos, deriva de 90s casi imperceptible) — el asset JPG se
  eliminó del proyecto por quedar sin uso. #pendiente/rapido ✅ 2026-08-14
- [x] **Categorías duplicadas** (bug real, no de diseño): `DefaultCategories.seed()` se
  ejecutaba en cada arranque de la app (`FinanzasApplication.onCreate`) sin revisar si ya
  existían — el id autogenerado nunca choca, así que `OnConflictStrategy.IGNORE` no
  evitaba nada. Cada reinicio insertaba las 33 categorías de nuevo. Arreglado:
  `seed()` ahora no hace nada si la tabla ya tiene datos, y una función nueva
  `dedupe()` corre una vez al iniciar para fusionar duplicados ya existentes en el
  dispositivo — reasigna movimientos/reglas/presupuestos a la categoría más antigua de
  cada grupo antes de borrar las copias, para no perder clasificaciones. #pendiente/rapido #bug ✅ 2026-08-14
- [x] Navegación redistribuida: Presupuestos y Metas (se revisan seguido) subieron de la
  barra inferior; Facturas y Agenda (uso más ocasional) bajaron al menú lateral, agrupadas
  bajo "Gestión" junto con Notificaciones; Cuenta/Ajustes quedaron bajo "Cuenta". El menú
  ya no repite Inicio/Movimientos/Presupuestos/Metas (redundantes con la barra inferior).
  #pendiente/fase1 ✅ 2026-08-14

**Diferido, no resuelto en esta pasada:** las otras 4 ilustraciones conectadas en la
sesión anterior (`empty_state_wallet`, `savings_goal_illustration`,
`biometric_lock_illustration`, `onboarding_security`) no se tocaron — el usuario aclaró
que "el fondo" se refería específicamente al splash. Si esas ilustraciones también se
sienten "muy IA" en la práctica, valdría la pena reemplazarlas por algo más sobrio
(iconografía lineal propia, o nada) — no se hizo por no asumir de más. #pendiente/deuda-tecnica

### Sesión 2026-08-15: PDF con contraseña + bug de biometría que reseteaba la navegación
- [x] **Extractos PDF protegidos con contraseña** — los extractos bancarios colombianos
  casi siempre vienen cifrados (a veces con la cédula del titular como clave).
  `PdfStatementExtractor` no tenía forma de pasar contraseña; ahora `requiresPassword()`
  detecta si el PDF la necesita y `extractText(bytes, password)` la usa para descifrar.
  En [[MovementsListScreen]], si el PDF elegido requiere contraseña se abre un diálogo
  (`PdfPasswordDialog`, con ancho acotado para verse bien en tablet) antes de intentar
  importar; si la contraseña es incorrecta, se pide de nuevo con un mensaje claro en vez
  de reportar "0 movimientos importados" sin explicación. Con tests que cifran el fixture
  real en memoria con pdfbox (no se inventa un formato de banco). #pendiente/rapido #bug ✅ 2026-08-15
- [x] **Bug real, causa raíz encontrada**: al bloquearse por biometría, `BiometricLockGate`
  sacaba `AppNavHost` (y su `NavController`) completamente de la composición
  (`if (locked) lock else content()`) en vez de solo ocultarlo. Cualquier acción que abre
  un Activity externo (selector de archivos para importar extracto, cámara/galería para
  escanear factura o captura de pantalla) manda la app a segundo plano → dispara
  `ON_STOP` → bloquea la app → al volver, con biometría activada, `AppNavHost` se volvía
  a construir desde cero en el Inicio en vez de continuar donde el usuario estaba.
  Arreglado: el candado ahora se dibuja como overlay opaco ENCIMA del contenido (`Box`),
  que sigue compuesto debajo sin interrupción — el `NavController` sobrevive y el usuario
  vuelve exactamente a la pantalla/diálogo donde se quedó. Arregla el flujo de PDF, y de
  paso el de cámara/galería de Facturas y el de captura de pantalla de Movimientos (todos
  comparten el mismo `BiometricLockGate`). #pendiente/rapido #bug ✅ 2026-08-15
- [x] **Versión mostrada en la app** — Ajustes → "Acerca de" ya tenía una fila de
  versión, pero decía **"1.0.0" fijo en el código**, sin relación con la versión real
  (que ya iba en 1.4.0). Se activó `buildConfig = true` en `build.gradle.kts` para que
  `BuildConfig.VERSION_NAME`/`VERSION_CODE` (generados automáticamente desde
  `versionName`/`versionCode`) alimenten esa fila — de aquí en adelante se actualiza sola
  en cada build, sin volver a quedar desfasada. #pendiente/rapido #bug ✅ 2026-08-15

### Sesión 2026-08-15 (continuación): crash de PDF con contraseña + permiso de notificaciones
- [x] **Crash real al ingresar la contraseña correcta del PDF** — `PdfStatementExtractor`
  usa BouncyCastle (vía pdfbox-android) para descifrar; un extracto real con un
  algoritmo/estructura que la librería no maneja del todo bien puede lanzar un `Error`
  (no una `Exception`), que antes escapaba del `catch (e: Exception)` en
  `MovementViewModel.importStatementPdf` y tumbaba **todo el proceso** de la app (se veía
  como "se cierra y se reinicia"). Se amplió el catch a `Throwable` ahí y en
  `requiresPassword()`, para que nada de lo que pase leyendo un PDF de un usuario pueda
  crashear la app — como mucho, se reporta como importación fallida. #pendiente/rapido #bug ✅ 2026-08-15
- [x] **Permiso de notificaciones locales (POST_NOTIFICATIONS) nunca se pedía** — estaba
  declarado en el manifiesto pero ningún código lo solicitaba en tiempo de ejecución; en
  Android 13+ eso lo deja denegado por defecto **en silencio**, así que ninguna
  notificación local (meta lograda, resumen de importación) se mostraba nunca, sin que la
  app ni siquiera lo hubiera pedido. Se agregó `PostNotificationsPermissionRow` en
  Ajustes: el usuario ve el estado real y decide con un botón "Permitir" (dispara el
  diálogo real de Android, donde puede aceptar o rechazar) — no se asume el permiso.
  #pendiente/rapido #bug ✅ 2026-08-15
- **Revisado y confirmado correcto, sin cambios**: cámara (usa intents implícitos al
  selector del sistema, nunca declara `CAMERA` como permiso propio — el consentimiento lo
  maneja la app de cámara del sistema) y biometría (`USE_BIOMETRIC` es un permiso
  "normal" sin diálogo propio; el consentimiento real ocurre en el `BiometricPrompt` del
  sistema cada vez que se invoca, y el usuario ya opta explícitamente al activar el
  switch en Ajustes).

## 🟡 Deuda técnica / funcionalidad incompleta

- [x] Crear/editar presupuesto desde la UI — `AddEditBudgetScreen` nuevo, reemplaza el `BudgetDetailScreen` que existía pero no tenía ni botones de editar/eliminar. #pendiente/deuda-tecnica ✅ 2026-08-14
- [x] Pantalla de gestión de categorías — [[CategoriesScreen]] + [[CategoriesViewModel]] nuevos: crear, renombrar, cambiar icono y eliminar categorías propias, separadas por ingreso/gasto. Dos detalles que no son obvios: (1) el **tipo no se puede cambiar al editar** — una categoría de gasto con historial y presupuestos detrás no puede volverse de ingreso sin dejar todo eso contradiciéndose; (2) antes de eliminar se muestra **qué se va a perder** (cuántos movimientos quedan sin clasificar, cuántos presupuestos y reglas se borran en cascada por las llaves foráneas), porque borrar "Supermercado" también borra su presupuesto de mercado y sin el aviso nadie se enteraría. De paso, `iconName` **empezó a significar algo**: el campo existía desde el principio y `DefaultCategories` lo rellenaba con 33 nombres distintos, pero ningún composable lo leía (`CategoryIcons` nuevo lo resuelve, con mapa explícito y no reflexión, que R8 rompe). #pendiente/deuda-tecnica ✅ 2026-08-18
- [x] UI para reglas de clasificación — [[ClassificationRulesScreen]] + [[ClassificationRulesViewModel]] nuevos. `DefaultClassificationEngine` ya consultaba estas reglas en el paso 2 de la clasificación, pero como no había forma de crearlas la tabla **siempre estaba vacía** y ese paso nunca hacía nada: esta pantalla vuelve útil código que ya estaba escrito. Incluye un **probador**: se pega una notificación real y dice si la regla coincidiría. No es un adorno — una expresión mal escrita no falla al guardarla, falla mucho después dentro del motor, donde `runCatching { pattern.toRegex() }.getOrNull() ?: continue` la descarta **en silencio**; el usuario vería su regla en la lista, activa, sin clasificar nada nunca y sin ningún mensaje. Por eso además se valida al guardar (6 tests). #pendiente/deuda-tecnica ✅ 2026-08-18
- [x] Variante *release* configurada: R8 activado (`isMinifyEnabled` + `isShrinkResources`), `proguard-rules.pro` escrito caso por caso —Room resuelve sus `_Impl` por nombre, el sistema instancia los servicios del manifiesto por nombre, y los enums de dominio se guardan como texto en la base y se releen con `valueOf`: si R8 renombra cualquiera de esos, la app compila pero falla en producción sin síntoma claro. Firma opcional desde `local.properties`/variables de entorno (el keystore nunca se versiona); sin ella `assembleRelease` produce un APK sin firmar en vez de fallar, para poder probar la ofuscación. Se agregó `assembleRelease` al CI: sin eso, un fallo de R8 solo aparecería al publicar. APK release: 53 MB (debug: 70 MB). **A propósito NO se le puso `applicationIdSuffix` a debug** — cambiaría el applicationId de la app ya instalada en el teléfono de prueba y se perderían los movimientos capturados. #pendiente/deuda-tecnica ✅ 2026-08-18
- [ ] Layout adaptativo para tablet (nav rail permanente / dos paneles) — la dependencia `androidx.compose.material3:material3-window-size-class` está en `build.gradle.kts` pero **nunca se usa** en ningún composable (confirmado con grep). El 2026-08-15 el `PdfPasswordDialog` nuevo se acotó a `widthIn(max = 420.dp)` para que no se estire feo en pantallas grandes, pero eso es un parche puntual, no una estrategia de layout para tablet. **Diferido** (feature de layout grande, no cabía en el pedido puntual de esa sesión). #pendiente/deuda-tecnica

### Botones que además se arreglaron en esta pasada (auditoría completa de `AppNavHost`)
No estaban en la lista original pero aparecieron al auditar cada callback: siete botones
en `AppNavHost` estaban literalmente cableados a `{}` (no hacían nada al tocarlos).
- [x] Agenda: "Agregar contacto" y tocar un contacto — ya existía `AddEditAgendaEntryScreen` completo pero sin ninguna ruta de navegación (pantalla huérfana). #pendiente/rapido ✅ 2026-08-14
- [x] Movimientos: botón "Detalle" — ahora abre un diálogo para recategorizar el movimiento (`MovementViewModel.correctMovement()` ya existía, nada lo llamaba). #pendiente/rapido ✅ 2026-08-14
- [x] Reactividad: Agenda, Presupuestos y Metas de ahorro leían con consultas de una sola vez (mismo patrón de bug que Movimientos, corregido antes) — crear/editar desde la pantalla nueva no se veía en la lista hasta reiniciar la app. Se convirtieron a `Flow` reactivo sobre Room (se agregó `BudgetDao.getAllFlow()`, no existía). #pendiente/rapido ✅ 2026-08-14

## 🟢 Fase 1 (MVP) — entregables del propio alcance aún sin terminar

Ver [[docs/SDD]] § 6 (módulos funcionales) y § 11 (roadmap).

- [x] Confirmación ligera con gesto swipe (§6.10) — deslizar a la derecha confirma, a la izquierda rechaza, en [[MovementsListScreen]] (solo movimientos PENDING). Los botones "Confirmar"/"Rechazar" se mantienen debajo como vía explícita (accesibilidad/descubribilidad); el swipe es un atajo adicional, no un reemplazo. #pendiente/fase1 ✅ 2026-08-14
- [ ] Cifrado en reposo (§8.1) — [[FinanzasDatabase]] (Room) no usa SQLCipher ni cifrado a nivel de archivo, y desde 1.7.0 el hueco es un poco mayor: `SyncStore` guarda el *refresh token* de Supabase en `SharedPreferences` privadas (inaccesibles para otras apps en un dispositivo sin rootear, pero sin cifrar). Cuando se haga, las dos cosas van juntas.
  **Deliberadamente NO se hizo en esta sesión**: migrar una base existente a SQLCipher implica `sqlcipher_export` sobre datos reales del usuario, y sin un dispositivo donde probarlo el riesgo es exactamente el que ya mordió tres veces en este proyecto ("la app no abre"). Plan concreto para cuando haya dispositivo: agregar `net.zetetic:sqlcipher-android`, generar la passphrase con el Keystore de Android, migrar `finanzas.db` → `finanzas-cifrada.db` con `sqlcipher_export` dentro de un try/catch que, si falla, deja la base vieja intacta y sigue sin cifrar en vez de dejar al usuario sin app. #pendiente/fase1 #seguridad
- [ ] Sugerencia proactiva de agenda cuando un número desconocido se repite en la misma categoría (§6.2) — la parte 100% local (sin comunidad) es viable ya; el enum `AgendaSource.COMMUNITY_SUGGESTED`/`AUTO_DETECTED` existe en el modelo pero nada lo asigna todavía. #pendiente/fase1

## 🔵 Fase 2 (Robustecimiento) — sin iniciar

- [ ] Alertas y detección de patrones: gasto inusual, rachas de gasto, movimientos recurrentes (§6.7) — confirmado sin código: cero nodos coincidentes al consultar el grafo. #pendiente/fase2
- [x] Escaneo de comprobantes por OCR con ML Kit (§6.8) — implementado con `ImageTextRecognizer` (ML Kit Text Recognition, 100% local) + `ReceiptOcrParser` (regex, sin LLM). Facturas: botones reales "Tomar foto"/"Galería" en [[InvoiceScreen]] (antes solo insertaban una plantilla fija de 3 productos falsos vía "Simular Escaneo"). Movimientos: nueva opción "Escanear Captura de Pantalla" en el diálogo de importar extracto — reusa `ParserRegistry` (los mismos `BankParser` de las notificaciones) sobre el texto OCR, con `StatementImporter` como respaldo; `MovementSource.OCR` ya se guarda de verdad (antes `EnrichmentPipeline` fijaba `NOTIFICATION` siempre, sin importar el origen real). #pendiente/fase2 ✅ 2026-08-14
- [ ] Exportación a Excel y PDF (§6.9) — hoy solo hay export a JSON local. #pendiente/fase2
- [ ] Aprendizaje comunitario opt-in de la agenda financiera (§6.2, refuerzo). **Ya no está bloqueado por infraestructura** (Supabase existe desde 1.7.0), pero sí por diseño: compartir "este número es Rappi" entre usuarios significa sacar datos de un usuario del ámbito de su propio `auth.uid()`, que es justo lo que impide toda la política de RLS actual. Necesita una tabla aparte, anonimizada y con consentimiento explícito, antes de escribir una línea. #pendiente/fase2 #seguridad
- [x] Panel web completo — ver la sección "Sesión 2026-08-18" arriba. #pendiente/fase2 ✅ 2026-08-18

## 🟣 Sostenibilidad (cola de Fase 1 / Fase 2)

Ver [[docs/SDD]] § 9.1 — ninguna recompensa está implementada; `SettingsViewModel` solo
guarda `isContributor`/`contributionAmount` como flags locales, sin infraestructura real.

- [ ] Huella financiera generativa (arte único a partir de los patrones de gasto). #pendiente/sostenibilidad
- [ ] Número de fundador. #pendiente/sostenibilidad
- [ ] Voto de roadmap (encuesta mensual dentro de la app). #pendiente/sostenibilidad
- [ ] Temas exclusivos — tema "fundador" diferenciado. **Cambió el contexto**: desde la sesión del 2026-08-14 (madrugada) la paleta *base* de Kivo ya es terracota-ocre (`Kivo Barro`, ver arriba), así que los alias `Terracotta*`/`Ocre*` en `Color.kt` ahora sí describen tonos reales — pero un tema "fundador" ya no puede ser "terracota-ocre" a secas porque eso dejó de ser exclusivo. Habría que definir algo que se diferencie del tema base (p.ej. un acento metálico/dorado, o una variante más oscura y saturada). #pendiente/sostenibilidad
- [ ] Resumen del año enriquecido (versión animada/exportable para aportantes). #pendiente/sostenibilidad

## ⚫ Legal / cumplimiento (no-código)

- [ ] Registro formal como responsable del tratamiento de datos personales (Ley 1581 de 2012). #pendiente/legal
- [ ] Política de tratamiento de datos + flujo de consentimiento diferenciado por fuente de captura. #pendiente/legal
- [ ] Declaración de funciones financieras en Google Play Console (justificación del permiso de notificaciones). #pendiente/legal

## ⏳ Fase 3-4 (futuro explícito — no iniciar sin discutirlo, per [[CLAUDE]])

- [x] Backend + panel web — **el usuario reabrió esta decisión el 2026-08-18** y se construyó (Supabase + Next.js). Ver la sección de esa sesión arriba. ✅ 2026-08-18
- [ ] iOS vía Share Extension.
- [ ] Integración oficial con el Sistema de Finanzas Abiertas (Open Finance) — depende del cronograma de la Superintendencia Financiera (Decreto 0368 de 2026).

## 💡 Sugerencias nuevas (surgidas al construir la nube y el panel, 2026-08-18)

No estaban en la lista: aparecieron al escribir la sincronización y son consecuencia
directa de ella. Ordenadas por lo que costaría arreglarlas si se dejan crecer.

### Correcciones que conviene hacer pronto

- [ ] **El push sube TODAS las filas en cada sincronización.** Funciona y es idempotente
  (el servidor descarta lo que no es más nuevo), pero con dos años de historial son
  varios MB por cada toque de "Sincronizar", sobre datos móviles. Arreglo: una marca
  `dirty` por fila, o un cursor local de "última subida" comparado contra `updatedAt`.
  Se dejó así a propósito para la primera versión — subir todo es más difícil de
  romper mientras el mecanismo todavía no se ha probado en un teléfono real.
  #pendiente/fase2 #sync
- [ ] **La bajada tampoco pagina.** `kivo_pull_changes` devuelve en un solo JSON todo lo
  que cambió desde el cursor; el primer pull de una cuenta con historial largo puede ser
  muy grande. Agregar un límite por tanda y repetir hasta vaciar. #pendiente/fase2 #sync
- [ ] **Faltan `updatedAt` en `categories`, `budgets`, `classification_rules` e
  `invoices`.** Esas entidades solo tienen `createdAt`, así que la sincronización lo usa
  como reloj lógico — lo que significa que **editar** una de ellas no gana un conflicto
  contra una versión más vieja del otro lado. Para categorías y presupuestos, que ahora
  sí se pueden editar desde la app y desde la web, esto se va a notar. Es una migración
  de Room + una columna en Postgres. #pendiente/deuda-tecnica #sync
- [x] **Borrar movimientos desde el móvil** — hecho el 2026-08-18, ver la sesión de
  correcciones de dinero. #pendiente/rapido ✅ 2026-08-18
- [ ] **Carrera en la primera sincronización de dos dispositivos a la vez.** Si dos
  teléfonos recién instalados suben sus categorías sembradas simultáneamente, el segundo
  choca contra el índice único `(user_id, nombre, tipo)` y su push falla entero. La
  reconciliación por llave natural lo resuelve **en la siguiente pasada**, así que se
  cura solo, pero el usuario ve un error una vez. Arreglo limpio: reintentar una vez
  automáticamente tras un fallo de unicidad. #pendiente/fase2 #sync

### Funcionalidad que la nube habilita y todavía no está

- [ ] **Sincronización automática en segundo plano** (WorkManager, con restricción de red
  y batería). Hoy hay que abrir la app y tocar "Sincronizar": el panel web solo está al
  día si alguien se acordó de hacerlo. #pendiente/fase2 #sync
- [ ] **Realtime de Supabase en el panel**: hoy hay que recargar para ver lo que acaba de
  subir el teléfono. Es una suscripción, no una reescritura. #pendiente/fase2
- [ ] **Recuperación de contraseña y confirmación de correo** en el panel. Supabase ya lo
  ofrece; falta la pantalla y decidir si se exige confirmar el correo (hoy conviene
  desactivarlo para probar, pero para producción debería estar activo). #pendiente/fase2
- [ ] **Desplegar el panel** (Vercel, plan gratuito, *Root Directory* = `web/`) y elegir
  dominio. Mientras solo corra en `localhost` no le sirve a nadie más que a quien lo
  compila. #pendiente/fase2

### Riesgos y calidad

- [ ] **Test instrumentado de migración de Room.** Ya se activó la exportación del esquema
  (`app/schemas/`) y se comparó a mano el DDL de `MIGRATION_3_4` contra lo que Room
  espera, pero "a mano" no escala. `MigrationTestHelper` necesita un dispositivo o
  emulador, así que no se pudo correr en esta sesión. Es la red de seguridad para el bug
  que más ha dolido en este proyecto. #pendiente/deuda-tecnica
- [ ] **Política de tratamiento de datos publicada, ya no opcional.** Mientras todo era
  local se podía posponer; con datos personales saliendo del dispositivo hacia un
  servidor, la Ley 1581 aplica de lleno y Google Play exige una URL de política de
  privacidad para publicar. Se conecta con los pendientes legales de más abajo.
  #pendiente/legal #seguridad
- [ ] **Región del proyecto de Supabase y latencia desde Colombia.** `us-east-1` es lo
  más cercano con plan gratuito; conviene medirlo con datos reales antes de asumir que
  la experiencia es buena. #pendiente/fase2
- [ ] **Tests del panel web.** Hoy tiene `tsc --noEmit` y nada más. Al menos un par de
  pruebas de las Server Actions (que son donde se escribe) valdrían la pena.
  #pendiente/deuda-tecnica

## 📊 Métricas (sin instrumentar)

- [ ] Instrumentar las métricas de éxito del MVP del §13 del SDD (% de movimientos clasificados correctamente sin intervención, retención a 30/90 días, tasa de corrección en confirmación ligera) — hoy no hay ninguna telemetría/analítica en el proyecto. Diseñar con cuidado dado el enfoque de privacidad del producto (opt-in, anonimizado, nunca obligatorio). #pendiente/metricas
