# Graph Report - .  (2026-08-14)

## Corpus Check
- 13 files · ~124,323 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1269 nodes · 2061 edges · 119 communities (60 shown, 59 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 127 edges (avg confidence: 0.8)
- Token cost: 83,778 input · 0 output

## Community Hubs (Navigation)
- Dashboard & Database Core
- Bank Parsers & Raw Movement Model
- Android/Kotlin Common Types
- Agenda Data Access
- Invoice/Debt Data Access
- Movement Data Access (DAO)
- In-App Notification DAO
- Budget Data Access
- Movement Repository
- Bank Statement Importer
- UI Motion & Animation
- Model Mappers
- Movement Entity & Classification Rule Repo
- Category Data Access
- Classification Rule DAO
- Enrichment Pipeline & Agenda/Category Models
- Budgets ViewModel
- Movements List Screen
- Docs: Brand & Core Concepts Overview
- Savings Goal Data Access
- Bank Parser Tests
- Repo Structure & CI/Graphify Integration
- SDD: Módulos Pendientes (Alertas, Confirmación)
- Notification Center Screen & Entity
- Bank Entity Enum
- Movement Processor Service
- Room Type Converters
- App Navigation Routes
- Notification Capture Service
- Web Session ViewModel
- SDD: Módulos Core (Agenda, Captura, Clasificación)
- Classification Engine Support
- Classification Engine & Result
- Enrichment Pipeline Logic
- Payment Method Enum
- Contexto Regulatorio y Roadmap
- Notification Access Permission
- Bank Entities & Test Convention
- Modelo de Sostenibilidad
- Colombian Amount Parser Tests
- Confirmation State Enum
- Movement Type Enum
- Onboarding Security Illustration
- Boot Receiver
- Agenda Origin Enum
- Notification Access Tests
- Cumplimiento Normativo y Seguridad
- PDF Statement Extractor
- Colombian Amount Parser
- Biometric Availability Check
- Kivo Brand Identity & Colors
- Instant/Long Conversions
- Statement Importer Tests
- Gradle Wrapper Script
- Backend/Web Pendientes (Política)
- Notification Access State Composable
- Empty State UX Assets
- PDF Extractor Tests
- Simple Smoke Test
- PDFBox Vendored Resources
- Bancolombia Fixtures & PDFBox Data
- App Build Config
- Double Type
- androidx Package Ref
- Theme Typography Definitions
- ByteArray Type
- Biometric Lock Illustration
- Savings Goal Illustration
- Splash Background Illustration
- Daviplata Notification Fixtures
- Lulo Notification Fixtures
- Nequi Notification Fixtures
- Nu Notification Fixtures
- Root Build Config
- Gradle Settings Config
- Fuentes PDFBox (glyphs)
- Fixtures Bancolombia (PDF + notificaciones)
- Metricas de exito del MVP
- Config release pendiente
- CI: build del APK
- CI: publicar APK
- Modelo de datos: aporte voluntario
- Riesgo: cambio de formato de notificaciones
- Gradle: modulo app
- BudgetDao.getAllFlow
- BaseBankParser (montos)
- NotificationCenterScreen (androidx)
- MovementViewModel.BankEntity
- MovementViewModel (ByteArray)
- Ilustracion: bloqueo biometrico
- Ilustracion: meta de ahorro
- Ilustracion: splash
- Fixtures Daviplata
- Fixtures Lulo Bank
- Fixtures Nequi
- Fixtures Nu
- Gradle: proyecto raiz
- Gradle: settings
- Ilustracion: bloqueo biometrico
- Ilustracion: meta de ahorro
- Ilustracion: splash
- Fixtures Daviplata
- Fixtures Lulo Bank
- Fixtures Nequi
- Fixtures Nu
- Gradle: proyecto raiz
- Gradle: settings
- InvoiceScreen.Invoice
- InvoiceScreen.InvoiceItem
- LoginScreen (Boolean)
- MovementsListScreen.Category
- MovementsListScreen.Movement
- NotificationCenterScreen (androidx)
- SavingsGoalsScreen.SavingsGoal
- Tema: tipografia

## God Nodes (most connected - your core abstractions)
1. `MovementDao` - 35 edges
2. `SettingsViewModel` - 34 edges
3. `MovementRepositoryImpl` - 28 edges
4. `docs/SDD.md` - 28 edges
5. `AgendaEntryEntity` - 20 edges
6. `FinanceCard()` - 19 edges
7. `BudgetDao` - 19 edges
8. `InvoiceScreen()` - 18 edges
9. `DashboardScreen()` - 17 edges
10. `docs/guia.md` - 17 edges

## Surprising Connections (you probably didn't know these)
- `Selector de formato de exportación (CSV real, Excel/PDF con aviso honesto)` --references--> `SettingsViewModel`  [INFERRED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/presentation/viewmodel/SettingsViewModel.kt
- `UI para reglas de clasificación (diferido)` --references--> `ClassificationRuleEntity`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/data/local/entity/ClassificationRuleEntity.kt
- `Movimientos: botón "Detalle" (recategorizar vía correctMovement())` --references--> `MovementViewModel`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/presentation/viewmodel/MovementViewModel.kt
- `Confirmación ligera con gesto swipe en Movimientos (§6.10)` --references--> `MovementsListScreen()`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/presentation/ui/screen/MovementsListScreen.kt
- `Botón "Abonar" en Metas de ahorro (conectado, bug SET vs incremento corregido)` --references--> `SavingsGoalsScreen()`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/presentation/ui/screen/SavingsGoalsScreen.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Auditoría completa de AppNavHost (botones huérfanos)** — docs_pendientes_boton_agenda_agregar_contacto, docs_pendientes_boton_movimientos_detalle, docs_pendientes_reactividad_agenda_presupuestos_metas, kivo_android_app_src_main_java_com_finanzas_automatica_presentation_navigation_appnavhost_appnavhost [EXTRACTED 1.00]
- **Sesión de rediseño visual con skill mobile-app-ui-design** — docs_pendientes_skill_mobile_app_ui_design, docs_pendientes_financecard_iconbadge_rediseno, docs_pendientes_ilustraciones_marca_conectadas, docs_pendientes_celebracion_meta_peak_end [EXTRACTED 1.00]
- **Pipeline de escaneo OCR de comprobantes** — docs_pendientes_escaneo_ocr, kivo_android_app_src_main_java_com_finanzas_automatica_domain_importer_imagetextrecognizer_imagetextrecognizer, kivo_android_app_src_main_java_com_finanzas_automatica_domain_importer_receiptocrparser_receiptocrparser, kivo_android_app_src_main_java_com_finanzas_automatica_domain_importer_statementimporter_statementimporter [EXTRACTED 1.00]
- **Kivo CI Build Pipeline (GitHub Actions)** — github_workflows_build_workflow, github_workflows_build_run_unit_tests_step, github_workflows_build_assembledebug_step, github_workflows_build_upload_apk_step [EXTRACTED 1.00]
- **SDD Data Model Entities (§7)** — graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_usuario, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_movimiento, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_agenda, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_categoria, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_presupuesto, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_meta_ahorro, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_regla_clasificacion, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_aporte_voluntario [EXTRACTED 1.00]

## Communities (119 total, 59 thin omitted)

### Community 0 - "Dashboard & Database Core"
Cohesion: 0.06
Nodes (66): Boolean, Color, Composable, DebtSummary, FinanceCard/IconBadge rediseño (esquinas, sombra, zona de toque), Total/deudas de Facturas no se actualizaban (fix derivedStateOf), ImageVector, Invoice (+58 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.05
Nodes (41): BankParser, Double, AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, Failure (+33 more)

### Community 2 - "Android/Kotlin Common Types"
Cohesion: 0.06
Nodes (32): CategoryEntity, ClassificationRuleEntity, MovementEntity, AgendaRepositoryImpl, CategoryRepositoryImpl, Flow, List, Long (+24 more)

### Community 3 - "Agenda Data Access"
Cohesion: 0.05
Nodes (34): AgendaEntryEntity, ByteArray, CategoryEntity, Instant, Int, EnrichmentPipeline, Boolean, Category (+26 more)

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.08
Nodes (37): Bundle, Class, Celebración a pantalla completa al completar Meta de ahorro (peak-end rule), Fondo del splash: curvas topográficas dibujadas en Canvas (reemplaza JPG generado), 5 ilustraciones de marca conectadas a composables (antes sin uso), Skill mobile-app-ui-design instalado (rediseño visual + animaciones), FragmentActivity, BiometricLockGate() (+29 more)

### Community 5 - "Movement Data Access (DAO)"
Cohesion: 0.09
Nodes (20): Application, BudgetEntity, BudgetDetailScreen (legacy, reemplazado por AddEditBudgetScreen), Categorías duplicadas: seed() ya no reinserta + dedupe() fusiona duplicados existentes, Crear/editar presupuesto desde la UI (AddEditBudgetScreen), Pantalla de gestión de categorías propias (diferido), Reactividad Agenda/Presupuestos/Metas (Flow reactivo sobre Room), UI para reglas de clasificación (diferido) (+12 more)

### Community 6 - "In-App Notification DAO"
Cohesion: 0.13
Nodes (9): CategoryTotal, Flow, Int, List, Long, String, MonthlyTotal, MovementDao (+1 more)

### Community 7 - "Budget Data Access"
Cohesion: 0.06
Nodes (18): AppNotificationDao, Flow, Int, List, Long, AppNotificationEntity, AppNotificationRepository, Flow (+10 more)

### Community 8 - "Movement Repository"
Cohesion: 0.08
Nodes (20): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+12 more)

### Community 9 - "Bank Statement Importer"
Cohesion: 0.08
Nodes (31): Context, Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity, Paleta "Barro & Ocre" reemplaza "Kivo Coral" (Color.kt/Theme.kt), Temas exclusivos — tema "fundador" ya no puede ser terracota-ocre a secas (paleta base cambió), Boolean, Color (+23 more)

### Community 10 - "UI Motion & Animation"
Cohesion: 0.06
Nodes (26): Alertas y detección de patrones de gasto (§6.7), Aprendizaje comunitario opt-in de la agenda financiera (§6.2), Cifrado en reposo de la base de datos (§8.1), Confirmación ligera con gesto swipe en Movimientos (§6.10), Escaneo de comprobantes por OCR con ML Kit (§6.8), Huella financiera generativa (arte único de patrones de gasto), Instrumentar métricas de éxito del MVP (§13 SDD), Número de fundador (+18 more)

### Community 11 - "Model Mappers"
Cohesion: 0.06
Nodes (36): Exportación a Excel y PDF (§6.9), Selector de formato de exportación (CSV real, Excel/PDF con aviso honesto), Prohibición de usar Accessibility API para acciones autónomas, Agenda financiera, Alertas y detección de patrones (§6.7), Aportes voluntarios con recompensa (§9.1), Aprendizaje comunitario opt-in de la agenda financiera (§6.2 refuerzo), Principio de captura multi-fuente (§5.2) (+28 more)

### Community 12 - "Movement Entity & Classification Rule Repo"
Cohesion: 0.13
Nodes (10): AppThemePalette, JSONArray, JSONObject, Boolean, Int, String, ViewModel, SettingsViewModel (+2 more)

### Community 13 - "Category Data Access"
Cohesion: 0.10
Nodes (27): AddEditBudgetScreen(), BudgetCard(), BudgetsScreen(), Budget, Category, List, Long, Map (+19 more)

### Community 14 - "Classification Rule DAO"
Cohesion: 0.16
Nodes (7): Flow, Int, List, Long, MovementEntity, String, MovementRepositoryImpl

### Community 15 - "Enrichment Pipeline & Agenda/Category Models"
Cohesion: 0.11
Nodes (12): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+4 more)

### Community 16 - "Budgets ViewModel"
Cohesion: 0.15
Nodes (25): Category, cleanEnum(), fromRoute(), ImportStatementDialog(), List, Long, Modifier, NumberFormat (+17 more)

### Community 17 - "Movements List Screen"
Cohesion: 0.15
Nodes (9): Botón "Abonar" en Metas de ahorro (conectado, bug SET vs incremento corregido), Flow, Int, List, Long, SavingsGoalDao, BudgetEntity, SavingsGoalEntity (+1 more)

### Community 18 - "Docs: Brand & Core Concepts Overview"
Cohesion: 0.15
Nodes (14): InvoiceViewModel, AgendaEntry, Bitmap, Category, DebtSummary, Invoice, InvoiceItem, List (+6 more)

### Community 19 - "Savings Goal Data Access"
Cohesion: 0.16
Nodes (21): Agenda: botones "Agregar contacto" y tocar contacto (ruta huérfana conectada), Movimientos: botón "Detalle" (recategorizar vía correctMovement()), Navegación redistribuida: Presupuestos/Metas a la barra inferior, Facturas/Agenda al menú lateral, Agenda, agendaEditRoute(), AppNavHost(), budgetEditRoute(), Budgets (+13 more)

### Community 20 - "Bank Parser Tests"
Cohesion: 0.19
Nodes (8): ClassificationRuleEntity, ClassificationRuleDao, Boolean, Flow, Int, List, Long, String

### Community 21 - "Repo Structure & CI/Graphify Integration"
Cohesion: 0.20
Nodes (7): CategoryDao, CategoryEntity, Flow, Int, List, Long, String

### Community 22 - "SDD: Módulos Pendientes (Alertas, Confirmación)"
Cohesion: 0.13
Nodes (18): docs/brand/ (material de marca), Agenda, app_notifications (centro de notificaciones in-app), BankParser, BiometricAccess, BiometricLockGate, BiometricPrompt, Regla de limpieza del repo (+10 more)

### Community 23 - "Notification Center Screen & Entity"
Cohesion: 0.13
Nodes (18): Alertas y detección de patrones (6.7), SDD_App_Finanzas.docx (fuente original), Aplicación móvil (Android), Backend / API, Confirmación ligera de movimientos (6.10), Dashboard y cronología (6.4), docs/SDD.md, Exportación (6.9) (+10 more)

### Community 24 - "Bank Entity Enum"
Cohesion: 0.14
Nodes (3): BankParserTest, List, String

### Community 25 - "Movement Processor Service"
Cohesion: 0.15
Nodes (10): AgendaEntry, AgendaViewModel, Boolean, Category, List, Long, StateFlow, String (+2 more)

### Community 26 - "Room Type Converters"
Cohesion: 0.12
Nodes (16): Graphify Integration Rule, Graphify Workflow, Kivo Backend (pendiente de desarrollo), backend/ (módulo, pendiente), Política: no construir backend/web/sync hasta validar MVP local, docs/ (documentación viva), Modelo de negocio: núcleo gratuito e ilimitado, graphify-out/ (mapa de dependencias) (+8 more)

### Community 27 - "App Navigation Routes"
Cohesion: 0.19
Nodes (14): androidx, AppNotificationEntity, ImageVector, Int, List, Long, Modifier, String (+6 more)

### Community 28 - "Notification Capture Service"
Cohesion: 0.20
Nodes (14): Dp, Float, FontWeight, AnimatedAmountText(), appearFromBelow(), Color, Int, Long (+6 more)

### Community 29 - "Web Session ViewModel"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 30 - "SDD: Módulos Core (Agenda, Captura, Clasificación)"
Cohesion: 0.21
Nodes (7): Boolean, List, Long, SavingsGoal, StateFlow, ViewModel, SavingsGoalsViewModel

### Community 31 - "Classification Engine Support"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 32 - "Classification Engine & Result"
Cohesion: 0.27
Nodes (5): Converters, String, MovementType, EXPENSE, INCOME

### Community 33 - "Enrichment Pipeline Logic"
Cohesion: 0.20
Nodes (10): PaymentMethod, BANCOLOMBIA, CASH, DAVIPLATA, LULO, NEQUI, NU, OTHER (+2 more)

### Community 34 - "Payment Method Enum"
Cohesion: 0.28
Nodes (9): Decreto 0368 de 2026 (Finanzas Abiertas), Decreto 0368 de 2026 (en guía), Decreto 0368 de 2026 (Finanzas Abiertas), Fase 0 — Validación, Fase 1 (MVP), Fase 2 — Robustecimiento, Fase 3 — Multiplataforma, Fase 4 — Open Finance (+1 more)

### Community 35 - "Contexto Regulatorio y Roadmap"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 36 - "Notification Access Permission"
Cohesion: 0.25
Nodes (8): CLAUDE.md, docs/guia.md, Backend + panel web (Fase 3-4, bloqueado hasta validar MVP), Integración oficial con el Sistema de Finanzas Abiertas (Open Finance), iOS vía Share Extension, Panel web completo (Fase 2, requiere backend), Política de tratamiento de datos + consentimiento diferenciado, Registro formal como responsable del tratamiento de datos (Ley 1581 de 2012)

### Community 37 - "Bank Entities & Test Convention"
Cohesion: 0.25
Nodes (8): Bancolombia, BankParser (convención), Convención: tests unitarios de BankParser con fixtures reales, Daviplata, Kivo, Lulo Bank, Nequi, Nu

### Community 38 - "Modelo de Sostenibilidad"
Cohesion: 0.25
Nodes (8): Aportes voluntarios con recompensa (9.1), Capas premium no esenciales (9.2), Huella financiera generativa, Modelo de sostenibilidad y financiación (9), Número de fundador, Resumen del año enriquecido, Temas exclusivos, Voto de roadmap

### Community 39 - "Colombian Amount Parser Tests"
Cohesion: 0.25
Nodes (7): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN

### Community 41 - "Movement Type Enum"
Cohesion: 0.29
Nodes (7): Agenda financiera (6.2), Aprendizaje comunitario opt-in de la agenda, Captura automática de movimientos (6.1), Clasificación automática (6.3), Movimiento crudo (objeto estandarizado), Fuente 3 — OCR, Fuente 4 — Parseo de correos

### Community 42 - "Onboarding Security Illustration"
Cohesion: 0.29
Nodes (5): ConfirmationState, AUTO_CONFIRMED, CONFIRMED, PENDING, REJECTED

### Community 43 - "Boot Receiver"
Cohesion: 0.29
Nodes (5): MovementSource, MANUAL, NOTIFICATION, OCR, OPEN_FINANCE

### Community 44 - "Agenda Origin Enum"
Cohesion: 0.29
Nodes (5): toDomain(), AgendaEntry, Movement, Budget, SavingsGoal

### Community 45 - "Notification Access Tests"
Cohesion: 0.38
Nodes (4): BankParser, Boolean, List, String

### Community 46 - "Cumplimiento Normativo y Seguridad"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 47 - "PDF Statement Extractor"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 48 - "Colombian Amount Parser"
Cohesion: 0.33
Nodes (4): AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL

### Community 50 - "Kivo Brand Identity & Colors"
Cohesion: 0.50
Nodes (5): Ley 1581 de 2012 (Habeas Data), Ley 1581 de 2012 (en guía), Cumplimiento normativo (8.2), Ley 1581 de 2012 (Habeas Data), Seguridad y privacidad (8.1)

### Community 51 - "Instant/Long Conversions"
Cohesion: 0.40
Nodes (3): ByteArray, String, PdfStatementExtractor

### Community 52 - "Statement Importer Tests"
Cohesion: 0.40
Nodes (3): ColombianAmountParser, Long, String

### Community 53 - "Gradle Wrapper Script"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 57 - "PDF Extractor Tests"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 60 - "Bancolombia Fixtures & PDFBox Data"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 63 - "androidx Package Ref"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

### Community 64 - "Theme Typography Definitions"
Cohesion: 0.67
Nodes (3): PDFBox Glyph List (vendored resource tables), Bancolombia Notification Fixtures, Bancolombia Statement PDF Fixture

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown

## Knowledge Gaps
- **159 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+154 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **59 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **Why does `FinanzasDatabase` connect `UI Motion & Animation` to `Movement Repository`, `Bank Parser Tests`, `Budget Data Access`?**
  _High betweenness centrality (0.190) - this node is a cross-community bridge._
- **Why does `docs/SDD.md` connect `UI Motion & Animation` to `Bank Statement Importer`, `Model Mappers`?**
  _High betweenness centrality (0.142) - this node is a cross-community bridge._
- **Why does `Escaneo de comprobantes por OCR con ML Kit (§6.8)` connect `UI Motion & Animation` to `Dashboard & Database Core`, `Agenda Data Access`?**
  _High betweenness centrality (0.111) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `SettingsViewModel` (e.g. with `Selector de formato de exportación (CSV real, Excel/PDF con aviso honesto)` and `.create()`) actually correct?**
  _`SettingsViewModel` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _159 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Dashboard & Database Core` be split into smaller, more focused modules?**
  _Cohesion score 0.06116700201207243 - nodes in this community are weakly interconnected._