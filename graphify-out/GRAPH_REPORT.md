# Graph Report - .  (2026-08-15)

## Corpus Check
- 7 files · ~128,881 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1319 nodes · 2037 edges · 154 communities (69 shown, 85 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 90 edges (avg confidence: 0.79)
- Token cost: 60,005 input · 0 output

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
- Tema: tipografia
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
- InvoiceScreen.DebtSummary
- InvoiceScreen.Invoice
- LoginScreen (Boolean)
- MovementsListScreen.Category
- MovementsListScreen.Movement
- NotificationCenterScreen (androidx)
- SavingsGoalsScreen.SavingsGoal
- Tema: tipografia
- MovementViewModel (ByteArray)
- MovementViewModel (Int)
- Ilustracion: bloqueo biometrico
- Ilustracion: meta de ahorro
- Ilustracion: splash
- Fixtures Daviplata
- Fixtures Lulo Bank
- Fixtures Nequi
- Fixtures Nu
- Gradle: proyecto raiz
- Gradle: settings
- NotificationCenterScreen (androidx)
- SavingsGoalsScreen.SavingsGoal
- SettingsScreen.Color
- SettingsScreen.ImageVector
- SettingsScreen.Triple
- Tema: tipografia
- MovementViewModel.BankEntity
- MovementViewModel (Int)
- MovementViewModel.StateFlow
- MovementViewModel.ViewModel
- Ilustracion: bloqueo biometrico
- Ilustracion: meta de ahorro
- Fixtures Daviplata
- Fixtures Lulo Bank
- Fixtures Nequi
- Fixtures Nu
- Gradle: proyecto raiz
- Gradle: settings
- Movement (tipo generico)
- Unit (tipo generico)
- Fixtures Lulo Bank
- Fixtures Nequi
- Fixtures Nu

## God Nodes (most connected - your core abstractions)
1. `MovementDao` - 35 edges
2. `SettingsViewModel` - 33 edges
3. `MovementRepositoryImpl` - 28 edges
4. `docs/SDD.md` - 28 edges
5. `AgendaEntryEntity` - 20 edges
6. `BankParserTest` - 20 edges
7. `DashboardScreen()` - 17 edges
8. `docs/guia.md` - 17 edges
9. `InvoiceViewModel` - 17 edges
10. `BudgetsViewModel` - 16 edges

## Surprising Connections (you probably didn't know these)
- `Bancolombia Notification Fixtures` --semantically_similar_to--> `Bancolombia Statement PDF Fixture`  [INFERRED] [semantically similar]
  kivo-android/app/src/test/resources/fixtures/bancolombia_notifications.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `graphify-out/ (mapa de dependencias)` --conceptually_related_to--> `Graphify Integration Rule`  [INFERRED]
  CLAUDE.md → .agents/rules/graphify.md
- `Kivo Backend (pendiente de desarrollo)` --references--> `Política: no construir backend/web/sync hasta validar MVP local`  [INFERRED]
  backend/README.md → CLAUDE.md
- `Kivo Web Panel (pendiente de desarrollo)` --references--> `Kivo Backend (pendiente de desarrollo)`  [INFERRED]
  web/README.md → backend/README.md
- `PDFBox Glyph List (vendored resource tables)` --shares_data_with--> `Bancolombia Statement PDF Fixture`  [INFERRED]
  kivo-android/app/src/test/resources/com/tom_roush/pdfbox/resources/glyphlist/glyphlist.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Sesión 2026-08-15: duplicados multicanal + crashes de CoroutineScope** — docs_pendientes_movimientos_duplicados_multicanal, docs_pendientes_crashes_coroutinescope_sin_manejador, kivo_android_app_src_main_java_com_finanzas_automatica_domain_enrichment_enrichmentpipeline_enrichmentpipeline_process [INFERRED 0.85]
- **Auditoría de AppNavHost: botones huérfanos y reactividad** — docs_pendientes_boton_agenda_agregar_contacto, docs_pendientes_boton_movimientos_detalle, docs_pendientes_reactividad_agenda_presupuestos_metas [EXTRACTED 1.00]
- **Sesión 2026-08-15: PDF con contraseña + bug de biometría + versión en app** — docs_pendientes_pdf_password, docs_pendientes_biometria_reset_navegacion, docs_pendientes_version_en_app [EXTRACTED 1.00]
- **Kivo CI Build Pipeline (GitHub Actions)** — github_workflows_build_workflow, github_workflows_build_run_unit_tests_step, github_workflows_build_assembledebug_step, github_workflows_build_upload_apk_step [EXTRACTED 1.00]
- **SDD Data Model Entities (§7)** — graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_usuario, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_movimiento, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_agenda, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_categoria, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_presupuesto, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_meta_ahorro, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_regla_clasificacion, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_aporte_voluntario [EXTRACTED 1.00]

## Communities (154 total, 85 thin omitted)

### Community 0 - "Dashboard & Database Core"
Cohesion: 0.05
Nodes (78): Color, Composable, DebtSummary, ImageVector, Int, Invoice, InvoiceItem, EmptyState() (+70 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.07
Nodes (20): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, CategoryEntity (+12 more)

### Community 2 - "Android/Kotlin Common Types"
Cohesion: 0.10
Nodes (20): AppThemePalette, JSONArray, JSONObject, Boolean, String, NotificationAccessRow(), palettePreviewColors(), PostNotificationsPermissionRow() (+12 more)

### Community 3 - "Agenda Data Access"
Cohesion: 0.06
Nodes (26): BancolombiaParser, BaseBankParser, ParseResult, String, DaviplataParser, BaseBankParser, ParseResult, String (+18 more)

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.07
Nodes (27): AgendaEntry, BankParser, Double, BaseBankParser, DatePattern, DatePatternType, DAY_MONTH_YEAR, DMY (+19 more)

### Community 5 - "Movement Data Access (DAO)"
Cohesion: 0.09
Nodes (25): ClassificationRuleEntity, MovementEntity, CategoryLookupRepository, ClassificationEngine, ClassificationRepositoryProvider, ClassificationRuleRepository, DefaultClassificationEngine, DefaultKeywordRepository (+17 more)

### Community 6 - "In-App Notification DAO"
Cohesion: 0.14
Nodes (7): Flow, Int, List, Long, String, MovementDao, MovementEntity

### Community 7 - "Budget Data Access"
Cohesion: 0.07
Nodes (33): Bundle, Class, Dp, Float, FontWeight, FragmentActivity, BiometricLockGate(), BiometricLockPrompt() (+25 more)

### Community 8 - "Movement Repository"
Cohesion: 0.06
Nodes (18): AppNotificationDao, Flow, Int, List, Long, AppNotificationEntity, AppNotificationRepository, Flow (+10 more)

### Community 9 - "Bank Statement Importer"
Cohesion: 0.08
Nodes (20): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+12 more)

### Community 10 - "UI Motion & Animation"
Cohesion: 0.08
Nodes (27): android, Boolean, Context, InputImage, ImageTextRecognizer, Bitmap, String, Uri (+19 more)

### Community 11 - "Model Mappers"
Cohesion: 0.06
Nodes (35): Exportación a Excel y PDF (pendiente, hoy solo JSON), Prohibición de usar Accessibility API para acciones autónomas, Agenda financiera, Alertas y detección de patrones (§6.7), Aportes voluntarios con recompensa (§9.1), Aprendizaje comunitario opt-in de la agenda financiera (§6.2 refuerzo), Principio de captura multi-fuente (§5.2), Confirmación ligera de movimientos (patrón swipe) (+27 more)

### Community 12 - "Movement Entity & Classification Rule Repo"
Cohesion: 0.15
Nodes (8): MonthlyTotal, Flow, Int, List, Long, MovementEntity, String, MovementRepositoryImpl

### Community 13 - "Category Data Access"
Cohesion: 0.10
Nodes (27): AddEditBudgetScreen(), BudgetCard(), BudgetsScreen(), Budget, Category, List, Long, Map (+19 more)

### Community 14 - "Classification Rule DAO"
Cohesion: 0.12
Nodes (10): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity, FinanzasDatabase, getInstance() (+2 more)

### Community 15 - "Enrichment Pipeline & Agenda/Category Models"
Cohesion: 0.14
Nodes (27): cleanEnum(), fromRoute(), ImportStatementDialog(), Boolean, Category, List, Long, Modifier (+19 more)

### Community 16 - "Budgets ViewModel"
Cohesion: 0.14
Nodes (20): Application, FinanzasDatabase, FinanzasApplication, Agenda, agendaEditRoute(), AppNavHost(), budgetEditRoute(), Budgets (+12 more)

### Community 17 - "Movements List Screen"
Cohesion: 0.15
Nodes (14): InvoiceViewModel, AgendaEntry, Bitmap, Category, DebtSummary, Invoice, InvoiceItem, List (+6 more)

### Community 18 - "Docs: Brand & Core Concepts Overview"
Cohesion: 0.11
Nodes (3): BankParserTest, String, List

### Community 19 - "Savings Goal Data Access"
Cohesion: 0.23
Nodes (6): BudgetEntity, BudgetDao, Flow, Int, List, Long

### Community 20 - "Bank Parser Tests"
Cohesion: 0.19
Nodes (8): ClassificationRuleEntity, ClassificationRuleDao, Boolean, Flow, Int, List, Long, String

### Community 21 - "Repo Structure & CI/Graphify Integration"
Cohesion: 0.20
Nodes (7): CategoryDao, CategoryEntity, Flow, Int, List, Long, String

### Community 22 - "SDD: Módulos Pendientes (Alertas, Confirmación)"
Cohesion: 0.16
Nodes (13): BankEntity, ByteArray, Boolean, Category, Int, List, RawMovement, String (+5 more)

### Community 23 - "Notification Center Screen & Entity"
Cohesion: 0.13
Nodes (18): docs/brand/ (material de marca), Agenda, app_notifications (centro de notificaciones in-app), BankParser, BiometricAccess, BiometricLockGate, BiometricPrompt, Regla de limpieza del repo (+10 more)

### Community 24 - "Bank Entity Enum"
Cohesion: 0.13
Nodes (18): Alertas y detección de patrones (6.7), SDD_App_Finanzas.docx (fuente original), Aplicación móvil (Android), Backend / API, Confirmación ligera de movimientos (6.10), Dashboard y cronología (6.4), docs/SDD.md, Exportación (6.9) (+10 more)

### Community 25 - "Movement Processor Service"
Cohesion: 0.16
Nodes (10): Converters, String, ConfirmationState, AUTO_CONFIRMED, CONFIRMED, PENDING, REJECTED, MovementType (+2 more)

### Community 26 - "Room Type Converters"
Cohesion: 0.12
Nodes (16): Graphify Integration Rule, Graphify Workflow, Kivo Backend (pendiente de desarrollo), backend/ (módulo, pendiente), Política: no construir backend/web/sync hasta validar MVP local, docs/ (documentación viva), Modelo de negocio: núcleo gratuito e ilimitado, graphify-out/ (mapa de dependencias) (+8 more)

### Community 27 - "App Navigation Routes"
Cohesion: 0.19
Nodes (14): androidx, AppNotificationEntity, ImageVector, Int, List, Long, Modifier, String (+6 more)

### Community 28 - "Notification Capture Service"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 29 - "Web Session ViewModel"
Cohesion: 0.29
Nodes (7): Instant, ImportSummary, BankEntity, Long, String, StatementImporter, MovementSource

### Community 30 - "SDD: Módulos Core (Agenda, Captura, Clasificación)"
Cohesion: 0.21
Nodes (5): String, NotificationCaptureService, Notification, NotificationListenerService, StatusBarNotification

### Community 31 - "Classification Engine Support"
Cohesion: 0.39
Nodes (6): AgendaEntryEntity, CategoryEntity, EnrichedMovement, EnrichmentPipeline, Boolean, RawMovement

### Community 32 - "Classification Engine & Result"
Cohesion: 0.17
Nodes (11): Bug: biometría reseteaba navegación (BiometricLockGate overlay fix), Fix: excepciones sin atrapar en CoroutineScopes tumbaban toda la app al abrir, Fix: movimientos duplicados por SMS+correo (mismo banco/tipo/monto en 5 min), Bug: paquetes de banco declarados no coincidían con apps reales, Crash al ingresar contraseña correcta de PDF (catch ampliado a Throwable), Extractos PDF protegidos con contraseña (requiresPassword/extractText), Cámara y biometría revisados: consentimiento ya correcto, sin cambios, Permisos solicitados gradualmente (POST_NOTIFICATIONS en el momento justo) (+3 more)

### Community 33 - "Enrichment Pipeline Logic"
Cohesion: 0.21
Nodes (7): Boolean, List, Long, SavingsGoal, StateFlow, ViewModel, SavingsGoalsViewModel

### Community 34 - "Payment Method Enum"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 35 - "Contexto Regulatorio y Roadmap"
Cohesion: 0.20
Nodes (10): PaymentMethod, BANCOLOMBIA, CASH, DAVIPLATA, LULO, NEQUI, NU, OTHER (+2 more)

### Community 36 - "Notification Access Permission"
Cohesion: 0.24
Nodes (9): AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, EnrichedMovement, Failure, ParseResult (+1 more)

### Community 37 - "Bank Entities & Test Convention"
Cohesion: 0.28
Nodes (9): Decreto 0368 de 2026 (Finanzas Abiertas), Decreto 0368 de 2026 (en guía), Decreto 0368 de 2026 (Finanzas Abiertas), Fase 0 — Validación, Fase 1 (MVP), Fase 2 — Robustecimiento, Fase 3 — Multiplataforma, Fase 4 — Open Finance (+1 more)

### Community 38 - "Modelo de Sostenibilidad"
Cohesion: 0.22
Nodes (6): BudgetEntity, toDomain(), toEntity(), Movement, Budget, SavingsGoal

### Community 39 - "Colombian Amount Parser Tests"
Cohesion: 0.31
Nodes (5): DefaultCategories, CategoryEntity, FinanzasDatabase, List, Long

### Community 40 - "Confirmation State Enum"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 41 - "Movement Type Enum"
Cohesion: 0.25
Nodes (8): Bancolombia, BankParser (convención), Convención: tests unitarios de BankParser con fixtures reales, Daviplata, Kivo, Lulo Bank, Nequi, Nu

### Community 42 - "Onboarding Security Illustration"
Cohesion: 0.25
Nodes (8): Aportes voluntarios con recompensa (9.1), Capas premium no esenciales (9.2), Huella financiera generativa, Modelo de sostenibilidad y financiación (9), Número de fundador, Resumen del año enriquecido, Temas exclusivos, Voto de roadmap

### Community 43 - "Boot Receiver"
Cohesion: 0.25
Nodes (5): AgendaEntry, AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL

### Community 44 - "Agenda Origin Enum"
Cohesion: 0.25
Nodes (7): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN

### Community 45 - "Notification Access Tests"
Cohesion: 0.36
Nodes (5): InvoiceItem, List, Pair, String, ReceiptOcrParser

### Community 47 - "PDF Statement Extractor"
Cohesion: 0.29
Nodes (4): Botón Agenda "Agregar contacto" conectado a AddEditAgendaEntryScreen, Botón Movimientos "Detalle" conectado a correctMovement(), Reactividad Agenda/Presupuestos/Metas convertida a Flow, Long

### Community 48 - "Colombian Amount Parser"
Cohesion: 0.29
Nodes (7): Agenda financiera (6.2), Aprendizaje comunitario opt-in de la agenda, Captura automática de movimientos (6.1), Clasificación automática (6.3), Movimiento crudo (objeto estandarizado), Fuente 3 — OCR, Fuente 4 — Parseo de correos

### Community 49 - "Biometric Availability Check"
Cohesion: 0.29
Nodes (5): MovementSource, MANUAL, NOTIFICATION, OCR, OPEN_FINANCE

### Community 50 - "Kivo Brand Identity & Colors"
Cohesion: 0.33
Nodes (4): Boolean, ByteArray, String, PdfStatementExtractor

### Community 51 - "Instant/Long Conversions"
Cohesion: 0.38
Nodes (4): BankParser, Boolean, List, String

### Community 52 - "Statement Importer Tests"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 53 - "Gradle Wrapper Script"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 56 - "Empty State UX Assets"
Cohesion: 0.50
Nodes (5): Ley 1581 de 2012 (Habeas Data), Ley 1581 de 2012 (en guía), Cumplimiento normativo (8.2), Ley 1581 de 2012 (Habeas Data), Seguridad y privacidad (8.1)

### Community 57 - "PDF Extractor Tests"
Cohesion: 0.40
Nodes (4): Celebración peak-end al completar meta de ahorro, Rediseño FinanceCard (esquinas/sombra) e IconBadge (44dp), 5 ilustraciones de marca conectadas (antes sin usar), Skill mobile-app-ui-design instalado y adaptado a Compose

### Community 58 - "Simple Smoke Test"
Cohesion: 0.40
Nodes (4): Fondo del splash: curvas topográficas en Canvas (reemplaza JPG generado), Navegación redistribuida (barra inferior vs menú lateral), Paleta "Barro & Ocre" reemplaza "Kivo Coral", Temas exclusivos (tema "fundador" diferenciado de Barro & Ocre)

### Community 59 - "PDFBox Vendored Resources"
Cohesion: 0.40
Nodes (3): ColombianAmountParser, Long, String

### Community 60 - "Bancolombia Fixtures & PDFBox Data"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 62 - "Double Type"
Cohesion: 0.67
Nodes (3): Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity

### Community 63 - "androidx Package Ref"
Cohesion: 0.50
Nodes (4): Backend + panel web (bloqueado hasta validar MVP en dispositivo real), Integración oficial con Sistema de Finanzas Abiertas (Decreto 0368), iOS vía Share Extension, Panel web completo (requiere backend)

### Community 64 - "Theme Typography Definitions"
Cohesion: 0.50
Nodes (3): Escaneo de comprobantes por OCR con ML Kit (implementado), Category, toDomain()

### Community 67 - "Savings Goal Illustration"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 70 - "Lulo Notification Fixtures"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 72 - "Nu Notification Fixtures"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

### Community 73 - "Root Build Config"
Cohesion: 0.67
Nodes (3): PDFBox Glyph List (vendored resource tables), Bancolombia Notification Fixtures, Bancolombia Statement PDF Fixture

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown

## Knowledge Gaps
- **182 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+177 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **85 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **Why does `FinanzasDatabase` connect `Classification Rule DAO` to `Movement Repository`, `Bank Statement Importer`, `Bank Parser Tests`?**
  _High betweenness centrality (0.241) - this node is a cross-community bridge._
- **Why does `MovementDao` connect `In-App Notification DAO` to `Classification Engine & Result`, `Classification Rule DAO`?**
  _High betweenness centrality (0.200) - this node is a cross-community bridge._
- **Why does `Fix: movimientos duplicados por SMS+correo (mismo banco/tipo/monto en 5 min)` connect `Classification Engine & Result` to `Classification Engine Support`?**
  _High betweenness centrality (0.167) - this node is a cross-community bridge._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _182 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Dashboard & Database Core` be split into smaller, more focused modules?**
  _Cohesion score 0.052782558806655194 - nodes in this community are weakly interconnected._
- **Should `Bank Parsers & Raw Movement Model` be split into smaller, more focused modules?**
  _Cohesion score 0.06610169491525424 - nodes in this community are weakly interconnected._