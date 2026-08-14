---
tags: [kivo, pendientes]
proyecto: Kivo
actualizado: 2026-08-14
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

## 🔴 Rápidos (bajo esfuerzo, alto impacto)

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

## 🟡 Deuda técnica / funcionalidad incompleta

- [x] Crear/editar presupuesto desde la UI — `AddEditBudgetScreen` nuevo, reemplaza el `BudgetDetailScreen` que existía pero no tenía ni botones de editar/eliminar. #pendiente/deuda-tecnica ✅ 2026-08-14
- [ ] Pantalla de gestión de categorías (crear/editar/eliminar categorías propias) — hoy solo existen las categorías sembradas por `DefaultCategories`, sin UI de administración. **Diferido** (feature nueva, no un botón roto). #pendiente/deuda-tecnica
- [ ] UI para reglas de clasificación — `ClassificationRuleEntity` existe en Room pero ninguna pantalla la expone; el usuario no puede ver ni editar sus propias reglas. **Diferido** (feature nueva, no un botón roto). #pendiente/deuda-tecnica
- [ ] Configurar variante *release* (firma, ofuscación/R8) — hoy el proyecto solo tiene variante debug lista. **Diferido** (config de build, no un botón). #pendiente/deuda-tecnica

### Botones que además se arreglaron en esta pasada (auditoría completa de `AppNavHost`)
No estaban en la lista original pero aparecieron al auditar cada callback: siete botones
en `AppNavHost` estaban literalmente cableados a `{}` (no hacían nada al tocarlos).
- [x] Agenda: "Agregar contacto" y tocar un contacto — ya existía `AddEditAgendaEntryScreen` completo pero sin ninguna ruta de navegación (pantalla huérfana). #pendiente/rapido ✅ 2026-08-14
- [x] Movimientos: botón "Detalle" — ahora abre un diálogo para recategorizar el movimiento (`MovementViewModel.correctMovement()` ya existía, nada lo llamaba). #pendiente/rapido ✅ 2026-08-14
- [x] Reactividad: Agenda, Presupuestos y Metas de ahorro leían con consultas de una sola vez (mismo patrón de bug que Movimientos, corregido antes) — crear/editar desde la pantalla nueva no se veía en la lista hasta reiniciar la app. Se convirtieron a `Flow` reactivo sobre Room (se agregó `BudgetDao.getAllFlow()`, no existía). #pendiente/rapido ✅ 2026-08-14

## 🟢 Fase 1 (MVP) — entregables del propio alcance aún sin terminar

Ver [[docs/SDD]] § 6 (módulos funcionales) y § 11 (roadmap).

- [x] Confirmación ligera con gesto swipe (§6.10) — deslizar a la derecha confirma, a la izquierda rechaza, en [[MovementsListScreen]] (solo movimientos PENDING). Los botones "Confirmar"/"Rechazar" se mantienen debajo como vía explícita (accesibilidad/descubribilidad); el swipe es un atajo adicional, no un reemplazo. #pendiente/fase1 ✅ 2026-08-14
- [ ] Cifrado en reposo de la base de datos (§8.1) — [[FinanzasDatabase]] (Room) no usa SQLCipher ni cifrado a nivel de archivo. Es el hueco de seguridad más señalado por el propio documento. #pendiente/fase1 #seguridad
- [ ] Sugerencia proactiva de agenda cuando un número desconocido se repite en la misma categoría (§6.2) — la parte 100% local (sin comunidad) es viable ya; el enum `AgendaSource.COMMUNITY_SUGGESTED`/`AUTO_DETECTED` existe en el modelo pero nada lo asigna todavía. #pendiente/fase1

## 🔵 Fase 2 (Robustecimiento) — sin iniciar

- [ ] Alertas y detección de patrones: gasto inusual, rachas de gasto, movimientos recurrentes (§6.7) — confirmado sin código: cero nodos coincidentes al consultar el grafo. #pendiente/fase2
- [x] Escaneo de comprobantes por OCR con ML Kit (§6.8) — implementado con `ImageTextRecognizer` (ML Kit Text Recognition, 100% local) + `ReceiptOcrParser` (regex, sin LLM). Facturas: botones reales "Tomar foto"/"Galería" en [[InvoiceScreen]] (antes solo insertaban una plantilla fija de 3 productos falsos vía "Simular Escaneo"). Movimientos: nueva opción "Escanear Captura de Pantalla" en el diálogo de importar extracto — reusa `ParserRegistry` (los mismos `BankParser` de las notificaciones) sobre el texto OCR, con `StatementImporter` como respaldo; `MovementSource.OCR` ya se guarda de verdad (antes `EnrichmentPipeline` fijaba `NOTIFICATION` siempre, sin importar el origen real). #pendiente/fase2 ✅ 2026-08-14
- [ ] Exportación a Excel y PDF (§6.9) — hoy solo hay export a JSON local. #pendiente/fase2
- [ ] Aprendizaje comunitario opt-in de la agenda financiera (§6.2, refuerzo) — requiere backend. #pendiente/fase2
- [ ] Panel web completo — requiere backend. #pendiente/fase2

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

- [ ] Backend + panel web — bloqueado explícitamente hasta validar el MVP local en un dispositivo real.
- [ ] iOS vía Share Extension.
- [ ] Integración oficial con el Sistema de Finanzas Abiertas (Open Finance) — depende del cronograma de la Superintendencia Financiera (Decreto 0368 de 2026).

## 📊 Métricas (sin instrumentar)

- [ ] Instrumentar las métricas de éxito del MVP del §13 del SDD (% de movimientos clasificados correctamente sin intervención, retención a 30/90 días, tasa de corrección en confirmación ligera) — hoy no hay ninguna telemetría/analítica en el proyecto. Diseñar con cuidado dado el enfoque de privacidad del producto (opt-in, anonimizado, nunca obligatorio). #pendiente/metricas
