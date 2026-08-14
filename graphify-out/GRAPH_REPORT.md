# Graph Report - .  (2026-08-14)

## Corpus Check
- 13 files · ~136,014 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1191 nodes · 1954 edges · 99 communities (70 shown, 29 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 124 edges (avg confidence: 0.81)
- Token cost: 84,514 input · 0 output

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

## God Nodes (most connected - your core abstractions)
1. `SettingsViewModel` - 34 edges
2. `MovementDao` - 33 edges
3. `MovementRepositoryImpl` - 28 edges
4. `docs/SDD.md` - 28 edges
5. `CategoryEntity` - 26 edges
6. `AgendaEntryEntity` - 23 edges
7. `FinanceCard()` - 17 edges
8. `IconBadge()` - 17 edges
9. `DashboardScreen()` - 17 edges
10. `docs/guia.md` - 17 edges

## Surprising Connections (you probably didn't know these)
- `Bancolombia Notification Fixtures` --semantically_similar_to--> `Bancolombia Statement PDF Fixture`  [INFERRED] [semantically similar]
  kivo-android/app/src/test/resources/fixtures/bancolombia_notifications.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `Motor de reglas / clasificación (reglas + regex)` --semantically_similar_to--> `ClassificationRuleEntity`  [INFERRED] [semantically similar]
  graphify-out/converted/SDD_App_Finanzas_94a76240.md → docs/PENDIENTES.md
- `Confirmación ligera de movimientos (patrón swipe)` --semantically_similar_to--> `MovementViewModel.correctMovement()`  [INFERRED] [semantically similar]
  graphify-out/converted/SDD_App_Finanzas_94a76240.md → docs/PENDIENTES.md
- `graphify-out/ (mapa de dependencias)` --conceptually_related_to--> `Graphify Integration Rule`  [INFERRED]
  CLAUDE.md → .agents/rules/graphify.md
- `Kivo Backend (pendiente de desarrollo)` --references--> `Política: no construir backend/web/sync hasta validar MVP local`  [INFERRED]
  backend/README.md → CLAUDE.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Kivo CI Build Pipeline (GitHub Actions)** — github_workflows_build_workflow, github_workflows_build_run_unit_tests_step, github_workflows_build_assembledebug_step, github_workflows_build_upload_apk_step [EXTRACTED 1.00]
- **SDD Data Model Entities (§7)** — graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_usuario, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_movimiento, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_agenda, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_categoria, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_presupuesto, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_meta_ahorro, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_regla_clasificacion, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_aporte_voluntario [EXTRACTED 1.00]
- **Fase 1 MVP Scope Tracking (SDD vs PENDIENTES)** — graphify_out_converted_sdd_app_finanzas_94a76240_fase1_mvp, docs_pendientes_swipe_confirmation_task, docs_pendientes_db_encryption_task, docs_pendientes_agenda_proactive_suggestion_task [INFERRED 0.85]

## Communities (99 total, 29 thin omitted)

### Community 0 - "Dashboard & Database Core"
Cohesion: 0.05
Nodes (76): Composable, DebtSummary, Invoice, InvoiceItem, EmptyState(), FinanceCard(), FinanceTag(), IconBadge() (+68 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.06
Nodes (31): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity (+23 more)

### Community 2 - "Android/Kotlin Common Types"
Cohesion: 0.06
Nodes (23): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+15 more)

### Community 3 - "Agenda Data Access"
Cohesion: 0.07
Nodes (17): CategoryDao, Flow, Int, List, Long, String, CategoryEntity, DefaultCategories (+9 more)

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.06
Nodes (27): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+19 more)

### Community 5 - "Movement Data Access (DAO)"
Cohesion: 0.08
Nodes (38): FinanzasDatabase, Agenda, agendaEditRoute(), AppNavHost(), budgetEditRoute(), Budgets, Dashboard, databaseViewModel() (+30 more)

### Community 6 - "In-App Notification DAO"
Cohesion: 0.06
Nodes (29): Bundle, Class, Context, BiometricLockGate(), BiometricLockPrompt(), FragmentActivity, showPrompt(), BiometricSettingsViewModelFactory (+21 more)

### Community 7 - "Budget Data Access"
Cohesion: 0.06
Nodes (18): AppNotificationDao, Flow, Int, List, Long, AppNotificationEntity, AppNotificationRepository, Flow (+10 more)

### Community 8 - "Movement Repository"
Cohesion: 0.15
Nodes (7): Flow, Int, List, Long, MovementEntity, String, MovementDao

### Community 9 - "Bank Statement Importer"
Cohesion: 0.14
Nodes (9): CategoryTotal, MonthlyTotal, Flow, Int, List, Long, MovementEntity, String (+1 more)

### Community 10 - "UI Motion & Animation"
Cohesion: 0.13
Nodes (10): AppThemePalette, JSONArray, JSONObject, Boolean, Int, String, ViewModel, SettingsViewModel (+2 more)

### Community 11 - "Model Mappers"
Cohesion: 0.10
Nodes (27): AddEditBudgetScreen(), BudgetCard(), BudgetsScreen(), Budget, Category, List, Long, Map (+19 more)

### Community 12 - "Movement Entity & Classification Rule Repo"
Cohesion: 0.12
Nodes (12): Application, BudgetEntity, Flow, BudgetDao, Int, List, Long, FinanzasDatabase (+4 more)

### Community 13 - "Category Data Access"
Cohesion: 0.15
Nodes (8): Flow, Int, List, Long, SavingsGoalDao, BudgetEntity, SavingsGoalEntity, toEntity()

### Community 14 - "Classification Rule DAO"
Cohesion: 0.17
Nodes (21): cleanEnum(), fromRoute(), ImportStatementDialog(), Category, List, Long, Modifier, Movement (+13 more)

### Community 15 - "Enrichment Pipeline & Agenda/Category Models"
Cohesion: 0.15
Nodes (12): BankEntity, ByteArray, Boolean, Category, Int, List, Long, Movement (+4 more)

### Community 16 - "Budgets ViewModel"
Cohesion: 0.12
Nodes (18): docs/brand/ (material de marca), Agenda, app_notifications (centro de notificaciones in-app), BankParser, BiometricAccess, BiometricLockGate, BiometricPrompt, BootReceiver (+10 more)

### Community 17 - "Movements List Screen"
Cohesion: 0.14
Nodes (3): BankParserTest, List, String

### Community 18 - "Docs: Brand & Core Concepts Overview"
Cohesion: 0.15
Nodes (10): AgendaEntry, AgendaViewModel, Boolean, Category, List, Long, StateFlow, String (+2 more)

### Community 19 - "Savings Goal Data Access"
Cohesion: 0.12
Nodes (16): Graphify Integration Rule, Graphify Workflow, Kivo Backend (pendiente de desarrollo), backend/ (módulo, pendiente), Política: no construir backend/web/sync hasta validar MVP local, docs/ (documentación viva), Modelo de negocio: núcleo gratuito e ilimitado, graphify-out/ (mapa de dependencias) (+8 more)

### Community 20 - "Bank Parser Tests"
Cohesion: 0.14
Nodes (16): Alertas y detección de patrones (6.7), SDD_App_Finanzas.docx (fuente original), Aplicación móvil (Android), Backend / API, Confirmación ligera de movimientos (6.10), Dashboard y cronología (6.4), docs/SDD.md, Exportación (6.9) (+8 more)

### Community 21 - "Repo Structure & CI/Graphify Integration"
Cohesion: 0.19
Nodes (14): androidx, AppNotificationEntity, ImageVector, Int, List, Long, Modifier, String (+6 more)

### Community 22 - "SDD: Módulos Pendientes (Alertas, Confirmación)"
Cohesion: 0.20
Nodes (14): Dp, Float, FontWeight, AnimatedAmountText(), appearFromBelow(), Color, Int, Long (+6 more)

### Community 23 - "Notification Center Screen & Entity"
Cohesion: 0.15
Nodes (11): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN, BankParser (+3 more)

### Community 24 - "Bank Entity Enum"
Cohesion: 0.14
Nodes (14): Color.kt (alias Terracotta*/Ocre* aún apuntan a coral/crema), Huella financiera generativa (pendiente, sostenibilidad), Número de fundador (pendiente, sostenibilidad), Resumen del año enriquecido (pendiente), SettingsViewModel (isContributor/contributionAmount como flags locales), Temas exclusivos / tema fundador terracota-ocre (pendiente), Voto de roadmap (pendiente, sostenibilidad), Aportes voluntarios con recompensa (§9.1) (+6 more)

### Community 25 - "Movement Processor Service"
Cohesion: 0.18
Nodes (12): toDomain(), AgendaEntry, AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, Budget, ClassificationRule (+4 more)

### Community 26 - "Room Type Converters"
Cohesion: 0.25
Nodes (8): ImportSummary, BankEntity, Instant, Long, PaymentMethod, String, StatementImporter, RawMovement

### Community 27 - "App Navigation Routes"
Cohesion: 0.17
Nodes (13): Sugerencia proactiva de agenda (pendiente), AgendaSource enum (COMMUNITY_SUGGESTED/AUTO_DETECTED), UI para reglas de clasificación (diferida), ClassificationRuleEntity, Prohibición de usar Accessibility API para acciones autónomas, Agenda financiera, Principio de captura multi-fuente (§5.2), Entidad de datos: Agenda financiera (+5 more)

### Community 28 - "Notification Capture Service"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 29 - "Web Session ViewModel"
Cohesion: 0.22
Nodes (7): Converters, String, MovementSource, MANUAL, NOTIFICATION, OCR, OPEN_FINANCE

### Community 30 - "SDD: Módulos Core (Agenda, Captura, Clasificación)"
Cohesion: 0.21
Nodes (7): Double, Instant, Long, ParseResult, PaymentMethod, DaviplataParser, String

### Community 31 - "Classification Engine Support"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 32 - "Classification Engine & Result"
Cohesion: 0.36
Nodes (5): BankParser, BaseBankParser, Boolean, Int, String

### Community 33 - "Enrichment Pipeline Logic"
Cohesion: 0.20
Nodes (10): Backend + panel web (Fase 3-4, bloqueado hasta validar MVP), Aprendizaje comunitario opt-in de agenda (Fase 2, requiere backend), Exportación a Excel y PDF (Fase 2, pendiente), Alertas y detección de patrones (Fase 2, sin iniciar), Panel web completo (Fase 2, requiere backend), Alertas y detección de patrones (§6.7), Aprendizaje comunitario opt-in de la agenda financiera (§6.2 refuerzo), Exportación a CSV/Excel/PDF (§6.9) (+2 more)

### Community 34 - "Payment Method Enum"
Cohesion: 0.22
Nodes (10): Agenda financiera (6.2), Aprendizaje comunitario opt-in de la agenda, Captura automática de movimientos (6.1), Clasificación automática (6.3), Movimiento crudo (objeto estandarizado), Fuente 1 — NotificationListenerService, Fuente 3 — OCR, Fuente 4 — Parseo de correos (+2 more)

### Community 35 - "Contexto Regulatorio y Roadmap"
Cohesion: 0.20
Nodes (10): PaymentMethod, BANCOLOMBIA, CASH, DAVIPLATA, LULO, NEQUI, NU, OTHER (+2 more)

### Community 36 - "Notification Access Permission"
Cohesion: 0.28
Nodes (9): Decreto 0368 de 2026 (Finanzas Abiertas), Decreto 0368 de 2026 (en guía), Decreto 0368 de 2026 (Finanzas Abiertas), Fase 0 — Validación, Fase 1 (MVP), Fase 2 — Robustecimiento, Fase 3 — Multiplataforma, Fase 4 — Open Finance (+1 more)

### Community 37 - "Bank Entities & Test Convention"
Cohesion: 0.25
Nodes (9): Cifrado en reposo de la base de datos (pendiente), PENDIENTES.md, FinanzasDatabase (Room), Cumplimiento legal: registro tratamiento datos, política, declaración Play Console, Cumplimiento normativo (§8.2), SDD — Aplicación de Contabilidad Financiera Personal Automática (v1.0), Ley 1581 de 2012 (habeas data / protección de datos personales), Entidad de datos: Movimiento (+1 more)

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
Nodes (8): iOS vía Share Extension (Fase 3-4), Fase 0: Validación, Fase 1 (MVP): Android, reglas + regex, gratis, Fase 3: Multiplataforma (iOS), Fase 4: Open Finance, Riesgo: iOS no permite leer notificaciones de terceros, Roadmap por fases (§11), Share Extension ("compartir hacia la app")

### Community 42 - "Onboarding Security Illustration"
Cohesion: 0.25
Nodes (8): Aportes voluntarios con recompensa (9.1), Capas premium no esenciales (9.2), Huella financiera generativa, Modelo de sostenibilidad y financiación (9), Número de fundador, Resumen del año enriquecido, Temas exclusivos, Voto de roadmap

### Community 44 - "Agenda Origin Enum"
Cohesion: 0.33
Nodes (6): AddEditAgendaEntryScreen, AppNavHost, MovementsListScreen, MovementViewModel.correctMovement(), Confirmación ligera con gesto swipe (pendiente), Confirmación ligera de movimientos (patrón swipe)

### Community 45 - "Notification Access Tests"
Cohesion: 0.29
Nodes (5): ConfirmationState, AUTO_CONFIRMED, CONFIRMED, PENDING, REJECTED

### Community 46 - "Cumplimiento Normativo y Seguridad"
Cohesion: 0.29
Nodes (4): Movement, MovementType, EXPENSE, INCOME

### Community 47 - "PDF Statement Extractor"
Cohesion: 0.29
Nodes (6): DatePattern, DatePatternType, DAY_MONTH_YEAR, DMY, TODAY, YESTERDAY

### Community 48 - "Colombian Amount Parser"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 49 - "Biometric Availability Check"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 50 - "Kivo Brand Identity & Colors"
Cohesion: 0.33
Nodes (4): AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL

### Community 52 - "Statement Importer Tests"
Cohesion: 0.47
Nodes (3): ParseResult, String, NuParser

### Community 54 - "Backend/Web Pendientes (Política)"
Cohesion: 0.50
Nodes (5): Ley 1581 de 2012 (Habeas Data), Ley 1581 de 2012 (en guía), Cumplimiento normativo (8.2), Ley 1581 de 2012 (Habeas Data), Seguridad y privacidad (8.1)

### Community 55 - "Notification Access State Composable"
Cohesion: 0.40
Nodes (3): ByteArray, String, PdfStatementExtractor

### Community 56 - "Empty State UX Assets"
Cohesion: 0.40
Nodes (3): ColombianAmountParser, Long, String

### Community 59 - "PDFBox Vendored Resources"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 60 - "Bancolombia Fixtures & PDFBox Data"
Cohesion: 0.67
Nodes (3): Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity

### Community 61 - "App Build Config"
Cohesion: 0.67
Nodes (4): MovementSource.OCR (enum, sin pipeline), Escaneo de comprobantes por OCR (Fase 2, pendiente), Escaneo OCR de comprobantes (ML Kit Text Recognition), Stack tecnológico propuesto (§10)

### Community 62 - "Double Type"
Cohesion: 0.83
Nodes (4): Integración oficial con Sistema de Finanzas Abiertas (Fase 3-4), Decreto 0368 de 2026 (Sistema de Finanzas Abiertas, Colombia), Sistema de Finanzas Abiertas (Open Finance, Colombia), Superintendencia Financiera de Colombia

### Community 65 - "ByteArray Type"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 66 - "Biometric Lock Illustration"
Cohesion: 0.67
Nodes (3): AddEditBudgetScreen, BudgetDetailScreen (reemplazada), Entidad de datos: Presupuesto

### Community 67 - "Savings Goal Illustration"
Cohesion: 0.67
Nodes (3): Pantalla de gestión de categorías (diferida), DefaultCategories, Entidad de datos: Categoría

### Community 69 - "Daviplata Notification Fixtures"
Cohesion: 0.67
Nodes (3): Agregar ./gradlew test al CI, Run unit tests step (./gradlew test), Build Debug APK Workflow (GitHub Actions)

### Community 70 - "Lulo Notification Fixtures"
Cohesion: 0.67
Nodes (3): Bug: SavingsGoalDao.updateProgress hacía SET en vez de incremento, SavingsGoalsScreen, Entidad de datos: Meta de ahorro

### Community 72 - "Nu Notification Fixtures"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 75 - "Fuentes PDFBox (glyphs)"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

### Community 76 - "Fixtures Bancolombia (PDF + notificaciones)"
Cohesion: 0.67
Nodes (3): PDFBox Glyph List (vendored resource tables), Bancolombia Notification Fixtures, Bancolombia Statement PDF Fixture

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown

## Knowledge Gaps
- **147 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+142 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **29 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **Why does `AppNavHost()` connect `Movement Data Access (DAO)` to `In-App Notification DAO`, `UI Motion & Animation`, `Model Mappers`, `Classification Rule DAO`, `Enrichment Pipeline & Agenda/Category Models`, `Docs: Brand & Core Concepts Overview`?**
  _High betweenness centrality (0.284) - this node is a cross-community bridge._
- **Why does `FinanzasDatabase` connect `Movement Entity & Classification Rule Repo` to `Android/Kotlin Common Types`, `Agenda Data Access`, `Invoice/Debt Data Access`, `Budget Data Access`, `Category Data Access`?**
  _High betweenness centrality (0.161) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `SettingsViewModel` (e.g. with `AppNavHost()` and `.create()`) actually correct?**
  _`SettingsViewModel` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _147 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Dashboard & Database Core` be split into smaller, more focused modules?**
  _Cohesion score 0.05359831376091539 - nodes in this community are weakly interconnected._
- **Should `Bank Parsers & Raw Movement Model` be split into smaller, more focused modules?**
  _Cohesion score 0.06440677966101695 - nodes in this community are weakly interconnected._