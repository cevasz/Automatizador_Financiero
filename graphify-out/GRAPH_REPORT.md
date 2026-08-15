# Graph Report - .  (2026-08-15)

## Corpus Check
- 4 files · ~128,038 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1311 nodes · 2048 edges · 134 communities (58 shown, 76 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 82 edges (avg confidence: 0.81)
- Token cost: 71,786 input · 0 output

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
- InvoiceScreen.InvoiceItem
- LoginScreen (Boolean)
- MovementsListScreen.Category
- MovementsListScreen.Movement
- NotificationCenterScreen (androidx)
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

## God Nodes (most connected - your core abstractions)
1. `MovementDao` - 34 edges
2. `SettingsViewModel` - 33 edges
3. `docs/SDD.md` - 32 edges
4. `MovementRepositoryImpl` - 28 edges
5. `BankParserTest` - 21 edges
6. `AgendaEntryEntity` - 20 edges
7. `InvoiceScreen()` - 18 edges
8. `BudgetDao` - 18 edges
9. `DashboardScreen()` - 17 edges
10. `docs/guia.md` - 17 edges

## Surprising Connections (you probably didn't know these)
- `Crear/editar presupuesto desde la UI (AddEditBudgetScreen)` --references--> `BudgetDao`  [AMBIGUOUS]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/data/local/dao/BudgetDao.kt
- `Cifrado en reposo de la base de datos (pendiente)` --references--> `FinanzasDatabase`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/data/local/FinanzasDatabase.kt
- `Botón Abonar en Metas de ahorro conectado` --references--> `SavingsGoalDao`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/data/local/dao/SavingsGoalDao.kt
- `UI para reglas de clasificación (diferido)` --references--> `ClassificationRuleEntity`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/data/local/entity/ClassificationRuleEntity.kt
- `Escaneo de comprobantes por OCR con ML Kit` --references--> `ParserRegistry`  [EXTRACTED]
  docs/PENDIENTES.md → kivo-android/app/src/main/java/com/finanzas/automatica/domain/parser/ParserRegistry.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Sesión permisos graduales: POST_NOTIFICATIONS contextual + revisión cámara/biometría** — docs_pendientes_permisos_graduales, docs_pendientes_post_notifications_permission, docs_pendientes_permisos_camara_biometria_revisados [INFERRED 0.85]
- **Flujos que comparten el mismo BiometricLockGate (PDF, OCR de facturas, captura de pantalla)** — docs_pendientes_biometria_reset_navegacion, docs_pendientes_pdf_password, docs_pendientes_escaneo_ocr [EXTRACTED 1.00]
- **Rediseño visual con el skill mobile-app-ui-design (FinanceCard, ilustraciones, celebración de meta)** — docs_pendientes_financecard_iconbadge_rediseno, docs_pendientes_ilustraciones_marca_conectadas, docs_pendientes_celebracion_meta_peak_end [EXTRACTED 1.00]
- **Kivo CI Build Pipeline (GitHub Actions)** — github_workflows_build_workflow, github_workflows_build_run_unit_tests_step, github_workflows_build_assembledebug_step, github_workflows_build_upload_apk_step [EXTRACTED 1.00]
- **SDD Data Model Entities (§7)** — graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_usuario, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_movimiento, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_agenda, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_categoria, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_presupuesto, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_meta_ahorro, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_regla_clasificacion, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_aporte_voluntario [EXTRACTED 1.00]

## Communities (134 total, 76 thin omitted)

### Community 0 - "Dashboard & Database Core"
Cohesion: 0.06
Nodes (33): UI para reglas de clasificación (diferido), CategoryEntity, ClassificationRuleEntity, MovementEntity, AgendaRepositoryImpl, CategoryRepositoryImpl, Flow, List (+25 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.06
Nodes (63): Composable, DebtSummary, Total/deudas de Facturas: fix con derivedStateOf, ImageVector, Invoice, InvoiceItem, EmptyState(), FinanceCard() (+55 more)

### Community 2 - "Android/Kotlin Common Types"
Cohesion: 0.05
Nodes (39): Converters, Instant, Long, String, AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL (+31 more)

### Community 3 - "Agenda Data Access"
Cohesion: 0.06
Nodes (47): Bundle, Class, Bug biometría: BiometricLockGate reseteaba la navegación, Botón Abonar en Metas de ahorro conectado, Celebración pantalla completa al lograr meta (peak-end rule), Escaneo de comprobantes por OCR con ML Kit, Rediseño FinanceCard/IconBadge, Fondo de splash: curvas topográficas en Canvas (+39 more)

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.06
Nodes (31): AgendaEntryEntity, CategoryEntity, BudgetEntity, EnrichmentPipeline, Boolean, Category, toDomain(), toDomain() (+23 more)

### Community 5 - "Movement Data Access (DAO)"
Cohesion: 0.06
Nodes (30): Bug crítico: paquetes de banco incorrectos (pagos no se registraban), Permisos de cámara y biometría revisados, sin cambios, Permisos solicitados gradualmente (POST_NOTIFICATIONS contextual + revisión cámara/biometría), Permiso POST_NOTIFICATIONS nunca se pedía, BancolombiaParser, BaseBankParser, ParseResult, String (+22 more)

### Community 6 - "In-App Notification DAO"
Cohesion: 0.10
Nodes (21): AppThemePalette, Color, JSONArray, JSONObject, Boolean, String, NotificationAccessRow(), palettePreviewColors() (+13 more)

### Community 7 - "Budget Data Access"
Cohesion: 0.07
Nodes (26): AgendaEntry, BankParser, Double, BaseBankParser, DatePattern, DatePatternType, DAY_MONTH_YEAR, DMY (+18 more)

### Community 8 - "Movement Repository"
Cohesion: 0.06
Nodes (18): AppNotificationDao, Flow, Int, List, Long, AppNotificationEntity, AppNotificationRepository, Flow (+10 more)

### Community 9 - "Bank Statement Importer"
Cohesion: 0.08
Nodes (20): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+12 more)

### Community 10 - "UI Motion & Animation"
Cohesion: 0.14
Nodes (7): Flow, Int, List, Long, String, MovementDao, MovementEntity

### Community 11 - "Model Mappers"
Cohesion: 0.14
Nodes (9): CategoryTotal, MonthlyTotal, Flow, Int, List, Long, MovementEntity, String (+1 more)

### Community 12 - "Movement Entity & Classification Rule Repo"
Cohesion: 0.08
Nodes (27): android, Boolean, Context, InputImage, ImageTextRecognizer, Bitmap, String, Uri (+19 more)

### Community 13 - "Category Data Access"
Cohesion: 0.06
Nodes (35): Exportación a Excel y PDF (Fase 2, pendiente), Prohibición de usar Accessibility API para acciones autónomas, Agenda financiera, Alertas y detección de patrones (§6.7), Aportes voluntarios con recompensa (§9.1), Aprendizaje comunitario opt-in de la agenda financiera (§6.2 refuerzo), Principio de captura multi-fuente (§5.2), Confirmación ligera de movimientos (patrón swipe) (+27 more)

### Community 14 - "Classification Rule DAO"
Cohesion: 0.10
Nodes (27): AddEditBudgetScreen(), BudgetCard(), BudgetsScreen(), Budget, Category, List, Long, Map (+19 more)

### Community 15 - "Enrichment Pipeline & Agenda/Category Models"
Cohesion: 0.11
Nodes (12): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+4 more)

### Community 16 - "Budgets ViewModel"
Cohesion: 0.11
Nodes (11): Cifrado en reposo de la base de datos (pendiente), Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity, FinanzasDatabase (+3 more)

### Community 17 - "Movements List Screen"
Cohesion: 0.13
Nodes (28): Confirmación ligera con gesto swipe en Movimientos, cleanEnum(), fromRoute(), ImportStatementDialog(), Boolean, Category, List, Long (+20 more)

### Community 18 - "Docs: Brand & Core Concepts Overview"
Cohesion: 0.17
Nodes (11): BudgetEntity, Agenda: botón Agregar contacto conectado, Movimientos: botón Detalle conectado, BudgetDetailScreen legacy reemplazada, Crear/editar presupuesto desde la UI (AddEditBudgetScreen), Reactividad Flow en Agenda, Presupuestos y Metas, BudgetDao, Flow (+3 more)

### Community 19 - "Savings Goal Data Access"
Cohesion: 0.15
Nodes (14): InvoiceViewModel, AgendaEntry, Bitmap, Category, DebtSummary, Invoice, InvoiceItem, List (+6 more)

### Community 21 - "Repo Structure & CI/Graphify Integration"
Cohesion: 0.20
Nodes (7): CategoryDao, CategoryEntity, Flow, Int, List, Long, String

### Community 22 - "SDD: Módulos Pendientes (Alertas, Confirmación)"
Cohesion: 0.20
Nodes (8): ClassificationRuleEntity, ClassificationRuleDao, Boolean, Flow, Int, List, Long, String

### Community 23 - "Notification Center Screen & Entity"
Cohesion: 0.16
Nodes (13): BankEntity, Category, Int, Boolean, ByteArray, String, MovementViewModel, List (+5 more)

### Community 24 - "Bank Entity Enum"
Cohesion: 0.12
Nodes (18): docs/brand/ (material de marca), Agenda, app_notifications (centro de notificaciones in-app), BankParser, BiometricAccess, BiometricLockGate, BiometricPrompt, BootReceiver (+10 more)

### Community 25 - "Movement Processor Service"
Cohesion: 0.12
Nodes (19): Alertas y detección de patrones (Fase 2, sin código), Instrumentar métricas de éxito del MVP (§13 SDD), Sugerencia proactiva de agenda (pendiente), Alertas y detección de patrones (6.7), SDD_App_Finanzas.docx (fuente original), Aplicación móvil (Android), Backend / API, Confirmación ligera de movimientos (6.10) (+11 more)

### Community 26 - "Room Type Converters"
Cohesion: 0.12
Nodes (16): Graphify Integration Rule, Graphify Workflow, backend/ (módulo, pendiente), docs/ (documentación viva), Modelo de negocio: núcleo gratuito e ilimitado, graphify-out/ (mapa de dependencias), Flujo de contextualización vía graphify query, kivo-android/ (módulo) (+8 more)

### Community 27 - "App Navigation Routes"
Cohesion: 0.19
Nodes (14): androidx, AppNotificationEntity, ImageVector, Int, List, Long, Modifier, String (+6 more)

### Community 28 - "Notification Capture Service"
Cohesion: 0.22
Nodes (13): Dp, FontWeight, AnimatedAmountText(), appearFromBelow(), Color, Int, Long, Modifier (+5 more)

### Community 29 - "Web Session ViewModel"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 30 - "SDD: Módulos Core (Agenda, Captura, Clasificación)"
Cohesion: 0.29
Nodes (7): Instant, ImportSummary, BankEntity, Long, String, StatementImporter, MovementSource

### Community 31 - "Classification Engine Support"
Cohesion: 0.18
Nodes (12): NotificationListenerService, iOS vía Share Extension (Fase 3-4), Agenda financiera (6.2), Aprendizaje comunitario opt-in de la agenda, Captura automática de movimientos (6.1), Clasificación automática (6.3), Movimiento crudo (objeto estandarizado), Fuente 1 — NotificationListenerService (+4 more)

### Community 32 - "Classification Engine & Result"
Cohesion: 0.21
Nodes (7): Boolean, List, Long, SavingsGoal, StateFlow, ViewModel, SavingsGoalsViewModel

### Community 33 - "Enrichment Pipeline Logic"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 34 - "Payment Method Enum"
Cohesion: 0.25
Nodes (7): Fix categorías duplicadas (seed/dedupe), Pantalla de gestión de categorías (diferido), DefaultCategories, CategoryEntity, FinanzasDatabase, List, Long

### Community 35 - "Contexto Regulatorio y Roadmap"
Cohesion: 0.22
Nodes (8): FinanzasDatabase, agendaEditRoute(), AppNavHost(), budgetEditRoute(), databaseViewModel(), savingsEditRoute(), Long, VM

### Community 36 - "Notification Access Permission"
Cohesion: 0.35
Nodes (10): Agenda, Budgets, Dashboard, Invoices, Login, Movements, Notifications, Savings (+2 more)

### Community 37 - "Bank Entities & Test Convention"
Cohesion: 0.24
Nodes (10): Decreto 0368 de 2026 (Finanzas Abiertas), Decreto 0368 de 2026 (en guía), Integración oficial con Open Finance (Fase 3-4), Decreto 0368 de 2026 (Finanzas Abiertas), Fase 0 — Validación, Fase 1 (MVP), Fase 2 — Robustecimiento, Fase 3 — Multiplataforma (+2 more)

### Community 38 - "Modelo de Sostenibilidad"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 39 - "Colombian Amount Parser Tests"
Cohesion: 0.25
Nodes (8): Bancolombia, BankParser (convención), Convención: tests unitarios de BankParser con fixtures reales, Daviplata, Kivo, Lulo Bank, Nequi, Nu

### Community 40 - "Confirmation State Enum"
Cohesion: 0.25
Nodes (8): Aportes voluntarios con recompensa (9.1), Capas premium no esenciales (9.2), Huella financiera generativa, Modelo de sostenibilidad y financiación (9), Número de fundador, Resumen del año enriquecido, Temas exclusivos, Voto de roadmap

### Community 41 - "Movement Type Enum"
Cohesion: 0.36
Nodes (5): InvoiceItem, List, Pair, String, ReceiptOcrParser

### Community 43 - "Boot Receiver"
Cohesion: 0.33
Nodes (4): Boolean, ByteArray, String, PdfStatementExtractor

### Community 44 - "Agenda Origin Enum"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 45 - "Notification Access Tests"
Cohesion: 0.40
Nodes (3): Application, FinanzasApplication, FinanzasDatabase

### Community 46 - "Cumplimiento Normativo y Seguridad"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 47 - "PDF Statement Extractor"
Cohesion: 0.40
Nodes (6): Ley 1581 de 2012 (Habeas Data), Ley 1581 de 2012 (en guía), Registro formal como responsable del tratamiento de datos, Cumplimiento normativo (8.2), Ley 1581 de 2012 (Habeas Data), Seguridad y privacidad (8.1)

### Community 50 - "Kivo Brand Identity & Colors"
Cohesion: 0.40
Nodes (3): ColombianAmountParser, Long, String

### Community 51 - "Instant/Long Conversions"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 53 - "Gradle Wrapper Script"
Cohesion: 0.67
Nodes (3): Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity

### Community 55 - "Notification Access State Composable"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 56 - "Empty State UX Assets"
Cohesion: 1.00
Nodes (3): Kivo Backend (pendiente de desarrollo), Política: no construir backend/web/sync hasta validar MVP local, Kivo Web Panel (pendiente de desarrollo)

### Community 58 - "Simple Smoke Test"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 60 - "Bancolombia Fixtures & PDFBox Data"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

### Community 61 - "App Build Config"
Cohesion: 0.67
Nodes (3): PDFBox Glyph List (vendored resource tables), Bancolombia Notification Fixtures, Bancolombia Statement PDF Fixture

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown
- `BudgetDao` → `Crear/editar presupuesto desde la UI (AddEditBudgetScreen)`  [AMBIGUOUS]
  docs/PENDIENTES.md · relation: references
- `Selector de formato de exportación arreglado` → `gradlew test agregado al CI`  [AMBIGUOUS]
  docs/PENDIENTES.md · relation: conceptually_related_to
- `Paleta "Barro & Ocre" reemplaza "Kivo Coral"` → `Navegación redistribuida (barra inferior / menú lateral)`  [AMBIGUOUS]
  docs/PENDIENTES.md · relation: conceptually_related_to

## Knowledge Gaps
- **176 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+171 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **76 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **What is the exact relationship between `BudgetDao` and `Crear/editar presupuesto desde la UI (AddEditBudgetScreen)`?**
  _Edge tagged AMBIGUOUS (relation: references) - confidence is low._
- **What is the exact relationship between `Selector de formato de exportación arreglado` and `gradlew test agregado al CI`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **What is the exact relationship between `Paleta "Barro & Ocre" reemplaza "Kivo Coral"` and `Navegación redistribuida (barra inferior / menú lateral)`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **Why does `FinanzasDatabase` connect `Budgets ViewModel` to `Movement Repository`, `Bank Statement Importer`?**
  _High betweenness centrality (0.325) - this node is a cross-community bridge._
- **Why does `SavingsGoalDao` connect `Budgets ViewModel` to `Agenda Data Access`?**
  _High betweenness centrality (0.227) - this node is a cross-community bridge._
- **Why does `Botón Abonar en Metas de ahorro conectado` connect `Agenda Data Access` to `Budgets ViewModel`?**
  _High betweenness centrality (0.198) - this node is a cross-community bridge._