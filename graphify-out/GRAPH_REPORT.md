# Graph Report - .  (2026-08-14)

## Corpus Check
- 6 files · ~126,835 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1292 nodes · 2041 edges · 140 communities (62 shown, 78 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 102 edges (avg confidence: 0.8)
- Token cost: 65,163 input · 0 output

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
- Ilustracion: splash
- Fixtures Daviplata
- Fixtures Lulo Bank
- Fixtures Nequi
- Fixtures Nu
- Gradle: proyecto raiz
- Gradle: settings
- InvoiceScreen.DebtSummary
- InvoiceScreen.Invoice
- InvoiceScreen.InvoiceItem
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
- MovementViewModel.Uri

## God Nodes (most connected - your core abstractions)
1. `docs/SDD.md` - 41 edges
2. `MovementDao` - 34 edges
3. `SettingsViewModel` - 33 edges
4. `MovementRepositoryImpl` - 28 edges
5. `AgendaEntryEntity` - 20 edges
6. `DashboardScreen()` - 17 edges
7. `docs/guia.md` - 17 edges
8. `InvoiceViewModel` - 17 edges
9. `MovementViewModel` - 17 edges
10. `BudgetsViewModel` - 16 edges

## Surprising Connections (you probably didn't know these)
- `Permiso POST_NOTIFICATIONS en Tiempo de Ejecución` --implements--> `SettingsScreen()`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/presentation/ui/screen/SettingsScreen.kt
- `Bancolombia Notification Fixtures` --semantically_similar_to--> `Bancolombia Statement PDF Fixture`  [INFERRED] [semantically similar]
  kivo-android/app/src/test/resources/fixtures/bancolombia_notifications.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `Bug: Biometría Reseteaba la Navegación` --implements--> `BiometricLockGate()`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/presentation/ui/components/BiometricLockGate.kt
- `Revisión de Permisos de Cámara y Biometría` --references--> `BiometricLockGate()`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/presentation/ui/components/BiometricLockGate.kt
- `Ilustraciones de Marca Conectadas` --references--> `BiometricLockGate()`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/presentation/ui/components/BiometricLockGate.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Importación de Extractos PDF Protegidos (implementación + fix de crash)** — docs_pendientes_pdf_password, docs_pendientes_pdf_crash_throwable, kivo_android_app_src_main_java_com_finanzas_automatica_domain_importer_pdfstatementextractor_pdfstatementextractor [INFERRED 0.85]
- **Revisión de Permisos en Tiempo de Ejecución (notificaciones, cámara, biometría)** — docs_pendientes_post_notifications_permission, docs_pendientes_permisos_camara_biometria_revisados, kivo_android_app_src_main_java_com_finanzas_automatica_presentation_ui_components_biometriclockgate_biometriclockgate [INFERRED 0.75]
- **Rediseño Visual con Skill mobile-app-ui-design** — docs_pendientes_financecard_iconbadge_rediseno, docs_pendientes_ilustraciones_marca_conectadas, docs_pendientes_celebracion_meta_peak_end [INFERRED 0.85]
- **Kivo CI Build Pipeline (GitHub Actions)** — github_workflows_build_workflow, github_workflows_build_run_unit_tests_step, github_workflows_build_assembledebug_step, github_workflows_build_upload_apk_step [EXTRACTED 1.00]
- **SDD Data Model Entities (§7)** — graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_usuario, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_movimiento, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_agenda, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_categoria, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_presupuesto, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_meta_ahorro, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_regla_clasificacion, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_aporte_voluntario [EXTRACTED 1.00]

## Communities (140 total, 78 thin omitted)

### Community 0 - "Dashboard & Database Core"
Cohesion: 0.05
Nodes (79): Boolean, Composable, DebtSummary, Botón Abonar en Metas de Ahorro, Total/Deudas de Facturas (fix derivedStateOf), ImageVector, Invoice, InvoiceItem (+71 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.07
Nodes (20): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, CategoryEntity (+12 more)

### Community 2 - "Android/Kotlin Common Types"
Cohesion: 0.05
Nodes (38): Converters, Instant, Long, String, AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL (+30 more)

### Community 3 - "Agenda Data Access"
Cohesion: 0.06
Nodes (41): Bundle, Class, Bug: Biometría Reseteaba la Navegación, Celebración de Meta (Peak-End Rule), Rediseño FinanceCard / IconBadge, Fondo del Splash (Curvas Topográficas), Ilustraciones de Marca Conectadas, Revisión de Permisos de Cámara y Biometría (+33 more)

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.10
Nodes (21): AppThemePalette, Color, JSONArray, JSONObject, Boolean, String, NotificationAccessRow(), palettePreviewColors() (+13 more)

### Community 5 - "Movement Data Access (DAO)"
Cohesion: 0.09
Nodes (24): ClassificationRuleEntity, MovementEntity, CategoryLookupRepository, ClassificationEngine, ClassificationRepositoryProvider, ClassificationRuleRepository, DefaultClassificationEngine, DefaultKeywordRepository (+16 more)

### Community 6 - "In-App Notification DAO"
Cohesion: 0.06
Nodes (18): AppNotificationDao, Flow, Int, List, Long, AppNotificationEntity, AppNotificationRepository, Flow (+10 more)

### Community 7 - "Budget Data Access"
Cohesion: 0.08
Nodes (20): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+12 more)

### Community 8 - "Movement Repository"
Cohesion: 0.14
Nodes (7): Flow, Int, List, Long, String, MovementDao, MovementEntity

### Community 9 - "Bank Statement Importer"
Cohesion: 0.14
Nodes (9): CategoryTotal, MonthlyTotal, Flow, Int, List, Long, MovementEntity, String (+1 more)

### Community 10 - "UI Motion & Animation"
Cohesion: 0.06
Nodes (35): Exportación a Excel y PDF, Prohibición de usar Accessibility API para acciones autónomas, Agenda financiera, Alertas y detección de patrones (§6.7), Aportes voluntarios con recompensa (§9.1), Aprendizaje comunitario opt-in de la agenda financiera (§6.2 refuerzo), Principio de captura multi-fuente (§5.2), Confirmación ligera de movimientos (patrón swipe) (+27 more)

### Community 11 - "Model Mappers"
Cohesion: 0.10
Nodes (27): AddEditBudgetScreen(), BudgetCard(), BudgetsScreen(), Budget, Category, List, Long, Map (+19 more)

### Community 12 - "Movement Entity & Classification Rule Repo"
Cohesion: 0.08
Nodes (22): BankEntity, Category, Botón Movimientos: Detalle, Layout Adaptativo para Tablet, Crash de PDF con Contraseña (catch Throwable), Extractos PDF Protegidos con Contraseña, Int, Boolean (+14 more)

### Community 13 - "Category Data Access"
Cohesion: 0.10
Nodes (13): ClassificationRuleEntity, Cifrado en Reposo de la Base de Datos, ClassificationRuleDao, Boolean, Flow, Int, List, Long (+5 more)

### Community 14 - "Classification Rule DAO"
Cohesion: 0.13
Nodes (28): Confirmación con Gesto Swipe, cleanEnum(), fromRoute(), ImportStatementDialog(), Boolean, Category, List, Long (+20 more)

### Community 15 - "Enrichment Pipeline & Agenda/Category Models"
Cohesion: 0.09
Nodes (20): Context, InputImage, ImageTextRecognizer, Bitmap, String, Uri, AppThemePalette, FOREST_GREEN (+12 more)

### Community 16 - "Budgets ViewModel"
Cohesion: 0.13
Nodes (13): AgendaEntryEntity, CategoryEntity, EnrichmentPipeline, Boolean, Category, toDomain(), EnrichedMovement, RawMovement (+5 more)

### Community 17 - "Movements List Screen"
Cohesion: 0.08
Nodes (27): Alertas y Detección de Patrones, Aprendizaje Comunitario Opt-in, Escaneo de Comprobantes por OCR, Huella Financiera Generativa, Métricas de Éxito del MVP, Número de Fundador, Paleta Barro & Ocre, Resumen del Año Enriquecido (+19 more)

### Community 18 - "Docs: Brand & Core Concepts Overview"
Cohesion: 0.15
Nodes (14): InvoiceViewModel, AgendaEntry, Bitmap, Category, DebtSummary, Invoice, InvoiceItem, List (+6 more)

### Community 19 - "Savings Goal Data Access"
Cohesion: 0.16
Nodes (8): Flow, Int, List, Long, SavingsGoalDao, BudgetEntity, SavingsGoalEntity, toEntity()

### Community 20 - "Bank Parser Tests"
Cohesion: 0.23
Nodes (6): BudgetEntity, BudgetDao, Flow, Int, List, Long

### Community 21 - "Repo Structure & CI/Graphify Integration"
Cohesion: 0.20
Nodes (7): CategoryDao, CategoryEntity, Flow, Int, List, Long, String

### Community 22 - "SDD: Módulos Pendientes (Alertas, Confirmación)"
Cohesion: 0.11
Nodes (18): Graphify Integration Rule, Graphify Workflow, backend/ (módulo, pendiente), docs/ (documentación viva), Modelo de negocio: núcleo gratuito e ilimitado, graphify-out/ (mapa de dependencias), Flujo de contextualización vía graphify query, kivo-android/ (módulo) (+10 more)

### Community 23 - "Notification Center Screen & Entity"
Cohesion: 0.19
Nodes (18): Agenda, agendaEditRoute(), AppNavHost(), budgetEditRoute(), Budgets, Dashboard, databaseViewModel(), Invoices (+10 more)

### Community 24 - "Bank Entity Enum"
Cohesion: 0.14
Nodes (3): BankParserTest, List, String

### Community 25 - "Movement Processor Service"
Cohesion: 0.15
Nodes (10): AgendaEntry, AgendaViewModel, Boolean, Category, List, Long, StateFlow, String (+2 more)

### Community 26 - "Room Type Converters"
Cohesion: 0.19
Nodes (14): androidx, AppNotificationEntity, ImageVector, Int, List, Long, Modifier, String (+6 more)

### Community 27 - "App Navigation Routes"
Cohesion: 0.13
Nodes (14): docs/brand/ (material de marca), app_notifications (centro de notificaciones in-app), BiometricAccess, BiometricLockGate, BiometricPrompt, BootReceiver, Regla de limpieza del repo, docs/guia.md (+6 more)

### Community 28 - "Notification Capture Service"
Cohesion: 0.16
Nodes (15): Agenda, BankParser, NotificationListenerService, RawMovement, Persistencia en Room, Agenda financiera (6.2), Aprendizaje comunitario opt-in de la agenda, Captura automática de movimientos (6.1) (+7 more)

### Community 29 - "Web Session ViewModel"
Cohesion: 0.16
Nodes (13): toDomain(), AgendaEntry, Movement, AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, Budget (+5 more)

### Community 30 - "SDD: Módulos Core (Agenda, Captura, Clasificación)"
Cohesion: 0.26
Nodes (8): Instant, ImportSummary, BankEntity, Long, String, StatementImporter, MovementSource, PaymentMethod

### Community 31 - "Classification Engine Support"
Cohesion: 0.26
Nodes (7): BankParser, BaseBankParser, DatePattern, Boolean, Instant, Int, String

### Community 32 - "Classification Engine & Result"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 33 - "Enrichment Pipeline Logic"
Cohesion: 0.21
Nodes (7): Double, Long, ParseResult, PaymentMethod, ParseResult, String, NuParser

### Community 34 - "Payment Method Enum"
Cohesion: 0.21
Nodes (7): Boolean, List, Long, SavingsGoal, StateFlow, ViewModel, SavingsGoalsViewModel

### Community 35 - "Contexto Regulatorio y Roadmap"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 36 - "Notification Access Permission"
Cohesion: 0.28
Nodes (9): Decreto 0368 de 2026 (Finanzas Abiertas), Decreto 0368 de 2026 (en guía), Decreto 0368 de 2026 (Finanzas Abiertas), Fase 0 — Validación, Fase 1 (MVP), Fase 2 — Robustecimiento, Fase 3 — Multiplataforma, Fase 4 — Open Finance (+1 more)

### Community 37 - "Bank Entities & Test Convention"
Cohesion: 0.31
Nodes (5): DefaultCategories, CategoryEntity, FinanzasDatabase, List, Long

### Community 38 - "Modelo de Sostenibilidad"
Cohesion: 0.31
Nodes (5): createDefault(), Boolean, List, String, ParserRegistry

### Community 39 - "Colombian Amount Parser Tests"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 40 - "Confirmation State Enum"
Cohesion: 0.25
Nodes (8): Bancolombia, BankParser (convención), Convención: tests unitarios de BankParser con fixtures reales, Daviplata, Kivo, Lulo Bank, Nequi, Nu

### Community 41 - "Movement Type Enum"
Cohesion: 0.25
Nodes (8): Aportes voluntarios con recompensa (9.1), Capas premium no esenciales (9.2), Huella financiera generativa, Modelo de sostenibilidad y financiación (9), Número de fundador, Resumen del año enriquecido, Temas exclusivos, Voto de roadmap

### Community 42 - "Onboarding Security Illustration"
Cohesion: 0.36
Nodes (5): InvoiceItem, List, Pair, String, ReceiptOcrParser

### Community 44 - "Agenda Origin Enum"
Cohesion: 0.38
Nodes (4): BankParser, Boolean, List, String

### Community 45 - "Notification Access Tests"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 46 - "Cumplimiento Normativo y Seguridad"
Cohesion: 0.40
Nodes (3): Application, FinanzasApplication, FinanzasDatabase

### Community 47 - "PDF Statement Extractor"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 51 - "Instant/Long Conversions"
Cohesion: 0.60
Nodes (4): android, isPostNotificationsGranted(), Boolean, rememberPostNotificationsGranted()

### Community 52 - "Statement Importer Tests"
Cohesion: 0.50
Nodes (5): Ley 1581 de 2012 (Habeas Data), Ley 1581 de 2012 (en guía), Cumplimiento normativo (8.2), Ley 1581 de 2012 (Habeas Data), Seguridad y privacidad (8.1)

### Community 53 - "Gradle Wrapper Script"
Cohesion: 0.40
Nodes (5): DatePatternType, DAY_MONTH_YEAR, DMY, TODAY, YESTERDAY

### Community 54 - "Backend/Web Pendientes (Política)"
Cohesion: 0.40
Nodes (3): ColombianAmountParser, Long, String

### Community 58 - "Simple Smoke Test"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 60 - "Bancolombia Fixtures & PDFBox Data"
Cohesion: 0.67
Nodes (3): Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity

### Community 62 - "Double Type"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 63 - "androidx Package Ref"
Cohesion: 1.00
Nodes (3): Kivo Backend (pendiente de desarrollo), Política: no construir backend/web/sync hasta validar MVP local, Kivo Web Panel (pendiente de desarrollo)

### Community 65 - "ByteArray Type"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 67 - "Savings Goal Illustration"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

### Community 68 - "Splash Background Illustration"
Cohesion: 0.67
Nodes (3): PDFBox Glyph List (vendored resource tables), Bancolombia Notification Fixtures, Bancolombia Statement PDF Fixture

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown

## Knowledge Gaps
- **180 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+175 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **78 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **Why does `FinanzasDatabase` connect `Category Data Access` to `In-App Notification DAO`, `Budget Data Access`?**
  _High betweenness centrality (0.295) - this node is a cross-community bridge._
- **Why does `docs/SDD.md` connect `Movements List Screen` to `Notification Access Permission`, `Movement Type Enum`, `UI Motion & Animation`, `Category Data Access`, `Classification Rule DAO`, `Statement Importer Tests`, `SDD: Módulos Pendientes (Alertas, Confirmación)`, `App Navigation Routes`, `Notification Capture Service`?**
  _High betweenness centrality (0.285) - this node is a cross-community bridge._
- **Why does `Cifrado en Reposo de la Base de Datos` connect `Category Data Access` to `Movements List Screen`?**
  _High betweenness centrality (0.195) - this node is a cross-community bridge._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _180 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Dashboard & Database Core` be split into smaller, more focused modules?**
  _Cohesion score 0.05220883534136546 - nodes in this community are weakly interconnected._
- **Should `Bank Parsers & Raw Movement Model` be split into smaller, more focused modules?**
  _Cohesion score 0.06610169491525424 - nodes in this community are weakly interconnected._