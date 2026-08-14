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

- [ ] Confirmación ligera con gesto swipe (§6.10) — hoy son botones "Confirmar"/"Rechazar" en [[MovementsListScreen]], no el patrón swipe que especifica el SDD. #pendiente/fase1
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
- [ ] Temas exclusivos — tema "fundador" terracota-ocre real (los alias `Terracotta*`/`Ocre*` en `Color.kt` hoy solo apuntan a los valores coral/crema base; habría que definir una paleta terracota-ocre distinta). #pendiente/sostenibilidad
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
