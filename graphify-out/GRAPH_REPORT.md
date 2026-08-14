# Graph Report - .  (2026-08-14)

## Corpus Check
- 4 files · ~132,798 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1083 nodes · 1869 edges · 75 communities (58 shown, 17 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 138 edges (avg confidence: 0.81)
- Token cost: 0 input · 55,000 output

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
- Double Type
- androidx Package Ref
- ByteArray Type
- Biometric Lock Illustration
- Savings Goal Illustration
- Splash Background Illustration
- Daviplata Notification Fixtures
- Lulo Notification Fixtures
- Nequi Notification Fixtures
- Nu Notification Fixtures

## God Nodes (most connected - your core abstractions)
1. `MovementDao` - 33 edges
2. `SettingsViewModel` - 31 edges
3. `MovementRepositoryImpl` - 28 edges
4. `docs/SDD.md` - 28 edges
5. `CategoryEntity` - 26 edges
6. `AgendaEntryEntity` - 23 edges
7. `FinanceCard()` - 23 edges
8. `IconBadge()` - 21 edges
9. `AppNavHost()` - 18 edges
10. `DashboardScreen()` - 18 edges

## Surprising Connections (you probably didn't know these)
- `Bancolombia Notification Fixtures` --semantically_similar_to--> `Bancolombia Statement PDF Fixture`  [INFERRED] [semantically similar]
  kivo-android/app/src/test/resources/fixtures/bancolombia_notifications.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `docs/guia.md` --references--> `PENDIENTES.md`  [EXTRACTED]
  docs/guia.md → CLAUDE.md
- `graphify-out/ (mapa de dependencias)` --conceptually_related_to--> `Graphify Integration Rule`  [INFERRED]
  CLAUDE.md → .agents/rules/graphify.md
- `Kivo Backend (pendiente de desarrollo)` --references--> `Política: no construir backend/web/sync hasta validar MVP local`  [INFERRED]
  backend/README.md → CLAUDE.md
- `Kivo Web Panel (pendiente de desarrollo)` --references--> `Kivo Backend (pendiente de desarrollo)`  [INFERRED]
  web/README.md → backend/README.md

## Import Cycles
- None detected.

## Communities (75 total, 17 thin omitted)

### Community 0 - "Dashboard & Database Core"
Cohesion: 0.05
Nodes (78): Composable, DebtSummary, FinanzasDatabase, Invoice, InvoiceItem, AppNavHost(), EmptyState(), FinanceCard() (+70 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.05
Nodes (42): BankParser, Double, AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, Failure (+34 more)

### Community 2 - "Android/Kotlin Common Types"
Cohesion: 0.05
Nodes (37): Bundle, Class, Context, JSONArray, JSONObject, BiometricLockGate(), BiometricLockPrompt(), FragmentActivity (+29 more)

### Community 3 - "Agenda Data Access"
Cohesion: 0.07
Nodes (18): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepositoryImpl (+10 more)

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.06
Nodes (26): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+18 more)

### Community 5 - "Movement Data Access (DAO)"
Cohesion: 0.13
Nodes (9): CategoryTotal, Flow, Int, List, Long, MovementEntity, String, MonthlyTotal (+1 more)

### Community 6 - "In-App Notification DAO"
Cohesion: 0.06
Nodes (18): AppNotificationDao, Flow, Int, List, Long, AppNotificationEntity, AppNotificationRepository, Flow (+10 more)

### Community 7 - "Budget Data Access"
Cohesion: 0.09
Nodes (15): Application, BudgetDao, Flow, Int, List, Long, BudgetEntity, FinanzasDatabase (+7 more)

### Community 8 - "Movement Repository"
Cohesion: 0.16
Nodes (7): Flow, Int, List, Long, MovementEntity, String, MovementRepositoryImpl

### Community 9 - "Bank Statement Importer"
Cohesion: 0.10
Nodes (18): ByteArray, ImportSummary, BankEntity, Instant, Long, PaymentMethod, String, StatementImporter (+10 more)

### Community 10 - "UI Motion & Animation"
Cohesion: 0.12
Nodes (28): Dp, Float, FontWeight, AnimatedAmountText(), appearFromBelow(), Color, Int, Long (+20 more)

### Community 11 - "Model Mappers"
Cohesion: 0.11
Nodes (20): toDomain(), toEntity(), Budget, SavingsGoal, List, Long, Modifier, NumberFormat (+12 more)

### Community 12 - "Movement Entity & Classification Rule Repo"
Cohesion: 0.17
Nodes (10): MovementEntity, ClassificationRuleRepository, DefaultClassificationEngine, DefaultKeywordRepository, KeywordMatch, KeywordRepository, Flow, List (+2 more)

### Community 13 - "Category Data Access"
Cohesion: 0.20
Nodes (7): CategoryDao, Flow, Int, List, Long, String, CategoryEntity

### Community 14 - "Classification Rule DAO"
Cohesion: 0.18
Nodes (8): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity

### Community 15 - "Enrichment Pipeline & Agenda/Category Models"
Cohesion: 0.13
Nodes (10): toDomain(), AgendaEntry, Category, AgendaViewModel, Boolean, List, Long, StateFlow (+2 more)

### Community 16 - "Budgets ViewModel"
Cohesion: 0.15
Nodes (13): budgetKey(), BudgetsViewModel, Boolean, Budget, Category, Int, List, Long (+5 more)

### Community 17 - "Movements List Screen"
Cohesion: 0.17
Nodes (19): cleanEnum(), fromRoute(), ImportStatementDialog(), List, Long, Modifier, Movement, NumberFormat (+11 more)

### Community 18 - "Docs: Brand & Core Concepts Overview"
Cohesion: 0.12
Nodes (18): docs/brand/ (material de marca), Agenda, app_notifications (centro de notificaciones in-app), BankParser, BiometricAccess, BiometricLockGate, BiometricPrompt, BootReceiver (+10 more)

### Community 19 - "Savings Goal Data Access"
Cohesion: 0.20
Nodes (6): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity

### Community 20 - "Bank Parser Tests"
Cohesion: 0.14
Nodes (3): BankParserTest, List, String

### Community 21 - "Repo Structure & CI/Graphify Integration"
Cohesion: 0.12
Nodes (15): Build Debug APK Workflow (GitHub Actions), Graphify Integration Rule, Graphify Workflow, backend/ (módulo, pendiente), docs/ (documentación viva), Modelo de negocio: núcleo gratuito e ilimitado, graphify-out/ (mapa de dependencias), Flujo de contextualización vía graphify query (+7 more)

### Community 22 - "SDD: Módulos Pendientes (Alertas, Confirmación)"
Cohesion: 0.14
Nodes (16): Alertas y detección de patrones (6.7), SDD_App_Finanzas.docx (fuente original), Aplicación móvil (Android), Backend / API, Confirmación ligera de movimientos (6.10), Dashboard y cronología (6.4), docs/SDD.md, Exportación (6.9) (+8 more)

### Community 23 - "Notification Center Screen & Entity"
Cohesion: 0.19
Nodes (14): androidx, AppNotificationEntity, ImageVector, Int, List, Long, Modifier, String (+6 more)

### Community 24 - "Bank Entity Enum"
Cohesion: 0.15
Nodes (11): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN, BankParser (+3 more)

### Community 25 - "Movement Processor Service"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 26 - "Room Type Converters"
Cohesion: 0.22
Nodes (7): Converters, String, MovementSource, MANUAL, NOTIFICATION, OCR, OPEN_FINANCE

### Community 27 - "App Navigation Routes"
Cohesion: 0.27
Nodes (12): Agenda, Budgets, Dashboard, databaseViewModel(), Invoices, Login, Movements, Notifications (+4 more)

### Community 28 - "Notification Capture Service"
Cohesion: 0.21
Nodes (5): Notification, String, NotificationCaptureService, NotificationListenerService, StatusBarNotification

### Community 29 - "Web Session ViewModel"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 30 - "SDD: Módulos Core (Agenda, Captura, Clasificación)"
Cohesion: 0.22
Nodes (10): Agenda financiera (6.2), Aprendizaje comunitario opt-in de la agenda, Captura automática de movimientos (6.1), Clasificación automática (6.3), Movimiento crudo (objeto estandarizado), Fuente 1 — NotificationListenerService, Fuente 3 — OCR, Fuente 4 — Parseo de correos (+2 more)

### Community 31 - "Classification Engine Support"
Cohesion: 0.33
Nodes (6): CategoryLookupRepository, ClassificationRepositoryProvider, Long, MovementHistoryRepository, RoomCategoryLookupRepository, RoomMovementHistoryRepository

### Community 32 - "Classification Engine & Result"
Cohesion: 0.20
Nodes (8): ClassificationEngine, ClassificationResult, ClassificationSource, AGENDA, HISTORY, KEYWORDS, RULES, UNKNOWN

### Community 33 - "Enrichment Pipeline Logic"
Cohesion: 0.38
Nodes (5): EnrichmentPipeline, Boolean, Double, EnrichedMovement, RawMovement

### Community 34 - "Payment Method Enum"
Cohesion: 0.20
Nodes (10): PaymentMethod, BANCOLOMBIA, CASH, DAVIPLATA, LULO, NEQUI, NU, OTHER (+2 more)

### Community 35 - "Contexto Regulatorio y Roadmap"
Cohesion: 0.28
Nodes (9): Decreto 0368 de 2026 (Finanzas Abiertas), Decreto 0368 de 2026 (en guía), Decreto 0368 de 2026 (Finanzas Abiertas), Fase 0 — Validación, Fase 1 (MVP), Fase 2 — Robustecimiento, Fase 3 — Multiplataforma, Fase 4 — Open Finance (+1 more)

### Community 36 - "Notification Access Permission"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 37 - "Bank Entities & Test Convention"
Cohesion: 0.25
Nodes (8): Bancolombia, BankParser (convención), Convención: tests unitarios de BankParser con fixtures reales, Daviplata, Kivo, Lulo Bank, Nequi, Nu

### Community 38 - "Modelo de Sostenibilidad"
Cohesion: 0.25
Nodes (8): Aportes voluntarios con recompensa (9.1), Capas premium no esenciales (9.2), Huella financiera generativa, Modelo de sostenibilidad y financiación (9), Número de fundador, Resumen del año enriquecido, Temas exclusivos, Voto de roadmap

### Community 40 - "Confirmation State Enum"
Cohesion: 0.29
Nodes (5): ConfirmationState, AUTO_CONFIRMED, CONFIRMED, PENDING, REJECTED

### Community 41 - "Movement Type Enum"
Cohesion: 0.29
Nodes (4): Movement, MovementType, EXPENSE, INCOME

### Community 42 - "Onboarding Security Illustration"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 43 - "Boot Receiver"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 44 - "Agenda Origin Enum"
Cohesion: 0.33
Nodes (4): AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL

### Community 46 - "Cumplimiento Normativo y Seguridad"
Cohesion: 0.50
Nodes (5): Ley 1581 de 2012 (Habeas Data), Ley 1581 de 2012 (en guía), Cumplimiento normativo (8.2), Ley 1581 de 2012 (Habeas Data), Seguridad y privacidad (8.1)

### Community 47 - "PDF Statement Extractor"
Cohesion: 0.40
Nodes (3): ByteArray, String, PdfStatementExtractor

### Community 48 - "Colombian Amount Parser"
Cohesion: 0.40
Nodes (3): ColombianAmountParser, Long, String

### Community 49 - "Biometric Availability Check"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 50 - "Kivo Brand Identity & Colors"
Cohesion: 0.67
Nodes (3): Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity

### Community 53 - "Gradle Wrapper Script"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 54 - "Backend/Web Pendientes (Política)"
Cohesion: 1.00
Nodes (3): Kivo Backend (pendiente de desarrollo), Política: no construir backend/web/sync hasta validar MVP local, Kivo Web Panel (pendiente de desarrollo)

### Community 56 - "Empty State UX Assets"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 59 - "PDFBox Vendored Resources"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

### Community 60 - "Bancolombia Fixtures & PDFBox Data"
Cohesion: 0.67
Nodes (3): PDFBox Glyph List (vendored resource tables), Bancolombia Notification Fixtures, Bancolombia Statement PDF Fixture

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown

## Knowledge Gaps
- **119 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+114 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **17 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **Why does `SavingsGoal` connect `Model Mappers` to `Bank Parsers & Raw Movement Model`?**
  _High betweenness centrality (0.285) - this node is a cross-community bridge._
- **Why does `SavingsGoalCard()` connect `Model Mappers` to `Dashboard & Database Core`, `UI Motion & Animation`?**
  _High betweenness centrality (0.226) - this node is a cross-community bridge._
- **Why does `FinanzasDatabase` connect `Budget Data Access` to `Agenda Data Access`, `Invoice/Debt Data Access`, `In-App Notification DAO`, `Classification Rule DAO`, `Savings Goal Data Access`?**
  _High betweenness centrality (0.222) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `SettingsViewModel` (e.g. with `AppNavHost()` and `.create()`) actually correct?**
  _`SettingsViewModel` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _119 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Dashboard & Database Core` be split into smaller, more focused modules?**
  _Cohesion score 0.05364314400458979 - nodes in this community are weakly interconnected._