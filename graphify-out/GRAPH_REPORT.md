# Graph Report - .  (2026-08-13)

## Corpus Check
- 110 files · ~123,389 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 960 nodes · 1747 edges · 56 communities (47 shown, 9 thin omitted)
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 151 edges (avg confidence: 0.81)
- Token cost: 0 input · 15,000 output

## Community Hubs (Navigation)
- Movement Data Access (DAO)
- Bank Parsers & Raw Movement Model
- Classification Rule DAO
- Agenda & Category UI
- Invoice/Debt Data Access
- Category Data Access
- Room Type Converters
- Project Docs & Architecture
- Model Mappers & UI Motion
- Brand Theme & Color Palette
- Bank Statement Importer
- Budget Data Access
- Agenda Data Access
- Movement Enrichment Pipeline
- Dashboard Screen UI
- Movements List Screen
- Biometric Lock Gate
- Savings Goal Data Access
- Notification Processor Service
- Invoice/Debt Screen UI
- App Navigation Routes
- In-App Notification DAO
- Settings Screen UI
- Web Session ViewModel
- Shared Finance UI Components
- In-App Notification Repository
- Navigation & Login Screens
- Bank Parser Tests
- Notification Center Screen
- Notification Center ViewModel
- Notification Access Permission
- In-App Notification Entity
- Onboarding Security Illustration
- Boot Receiver
- Notification Access Tests
- PDF Statement Extractor
- Notification Center Composable
- Biometric Availability Check
- Dashboard Period Enum
- Invoice Tab Enum
- Gradle Wrapper Script
- Kivo Brand Icon Asset
- Empty State Wallet Illustration
- PDF Extractor Tests
- Statement Importer Tests
- Simple Smoke Test
- PDFBox Vendored Glyph Resources
- In-App Notifications Table
- Biometric Lock Illustration
- Savings Goal Illustration
- Splash Background Illustration

## God Nodes (most connected - your core abstractions)
1. `MovementEntity` - 37 edges
2. `MovementDao` - 31 edges
3. `SettingsViewModel` - 31 edges
4. `CategoryEntity` - 26 edges
5. `MovementRepositoryImpl` - 26 edges
6. `AppNavHost()` - 25 edges
7. `AgendaEntryEntity` - 23 edges
8. `FinanceCard()` - 22 edges
9. `IconBadge()` - 21 edges
10. `DashboardScreen()` - 18 edges

## Surprising Connections (you probably didn't know these)
- `Importación de extractos bancarios (CSV/texto/PDF)` --references--> `Bancolombia Statement PDF Fixture`  [INFERRED]
  docs/guia.md → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `Bancolombia Notification Fixtures` --semantically_similar_to--> `Bancolombia Statement PDF Fixture`  [INFERRED] [semantically similar]
  kivo-android/app/src/test/resources/fixtures/bancolombia_notifications.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `BankParser unit test convention (real-text fixtures required)` --references--> `Daviplata Notification Fixtures`  [INFERRED]
  CLAUDE.md → kivo-android/app/src/test/resources/fixtures/daviplata_notifications.txt
- `BankParser unit test convention (real-text fixtures required)` --references--> `Lulo Bank Notification Fixtures`  [INFERRED]
  CLAUDE.md → kivo-android/app/src/test/resources/fixtures/lulo_notifications.txt
- `BankParser unit test convention (real-text fixtures required)` --references--> `Nequi Notification Fixtures`  [INFERRED]
  CLAUDE.md → kivo-android/app/src/test/resources/fixtures/nequi_notifications.txt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **MVP Supported Bank Entities** — claude_bancolombia, claude_nequi, claude_daviplata, claude_nu, claude_lulo_bank [EXTRACTED 1.00]
- **BankParser Real-Text Test Fixtures** — claude_bankparser_test_convention, kivo_android_app_src_test_resources_fixtures_bancolombia_notifications_fixture, kivo_android_app_src_test_resources_fixtures_daviplata_notifications_fixture, kivo_android_app_src_test_resources_fixtures_lulo_notifications_fixture, kivo_android_app_src_test_resources_fixtures_nequi_notifications_fixture, kivo_android_app_src_test_resources_fixtures_nu_notifications_fixture [INFERRED 0.85]
- **Kivo Monorepo Structure** — claude_kivo_android_module, claude_web_module, claude_backend_module, claude_docs_module, claude_graphify_out_module [EXTRACTED 1.00]

## Communities (56 total, 9 thin omitted)

### Community 0 - "Movement Data Access (DAO)"
Cohesion: 0.08
Nodes (15): CategoryTotal, Flow, Int, List, Long, String, MonthlyTotal, MovementDao (+7 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.05
Nodes (40): AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, Failure, ParseResult, Success (+32 more)

### Community 2 - "Classification Rule DAO"
Cohesion: 0.06
Nodes (31): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity (+23 more)

### Community 3 - "Agenda & Category UI"
Cohesion: 0.07
Nodes (35): AgendaEntry, Category, Budget, AddEditAgendaEntryScreen(), AgendaEntryCard(), AgendaScreen(), cleanEnum(), List (+27 more)

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.06
Nodes (25): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+17 more)

### Community 5 - "Category Data Access"
Cohesion: 0.08
Nodes (16): CategoryDao, Flow, Int, List, Long, String, CategoryEntity, DefaultCategories (+8 more)

### Community 6 - "Room Type Converters"
Cohesion: 0.06
Nodes (31): Converters, Instant, Long, String, AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL (+23 more)

### Community 7 - "Project Docs & Architecture"
Cohesion: 0.06
Nodes (44): Build Debug APK Workflow (GitHub Actions), Graphify Integration Rule, Graphify Workflow, Kivo Backend (pendiente de desarrollo), Agenda (table), backend (sync API module), Bancolombia (bank entity), BankParser (+36 more)

### Community 8 - "Model Mappers & UI Motion"
Cohesion: 0.07
Nodes (33): Dp, Float, FontWeight, toDomain(), toEntity(), SavingsGoal, AnimatedAmountText(), appearFromBelow() (+25 more)

### Community 9 - "Brand Theme & Color Palette"
Cohesion: 0.10
Nodes (19): JSONArray, JSONObject, AppThemePalette, FOREST_GREEN, KIVO_CORAL, MIDNIGHT_BLUE, OCEAN_TEAL, SUNSET_AMBER (+11 more)

### Community 10 - "Bank Statement Importer"
Cohesion: 0.10
Nodes (21): ImportSummary, Instant, Long, String, StatementImporter, BankEntity, BANCOLOMBIA, DAVIPLATA (+13 more)

### Community 11 - "Budget Data Access"
Cohesion: 0.10
Nodes (12): Application, BudgetDao, Flow, Int, List, Long, BudgetEntity, FinanzasDatabase (+4 more)

### Community 12 - "Agenda Data Access"
Cohesion: 0.11
Nodes (12): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+4 more)

### Community 13 - "Movement Enrichment Pipeline"
Cohesion: 0.13
Nodes (11): EnrichmentPipeline, Boolean, Double, toDomain(), EnrichedMovement, RawMovement, Notification, String (+3 more)

### Community 14 - "Dashboard Screen UI"
Cohesion: 0.22
Nodes (19): Movement, cleanEnum(), DashboardScreen(), Boolean, Color, ImageVector, Int, List (+11 more)

### Community 15 - "Movements List Screen"
Cohesion: 0.18
Nodes (19): cleanEnum(), fromRoute(), ImportStatementDialog(), List, Long, Modifier, NumberFormat, String (+11 more)

### Community 16 - "Biometric Lock Gate"
Cohesion: 0.13
Nodes (14): Bundle, Class, BiometricLockGate(), BiometricLockPrompt(), FragmentActivity, showPrompt(), BiometricSettingsViewModelFactory, androidx (+6 more)

### Community 17 - "Savings Goal Data Access"
Cohesion: 0.22
Nodes (6): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity

### Community 18 - "Notification Processor Service"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 19 - "Invoice/Debt Screen UI"
Cohesion: 0.31
Nodes (11): DebtSummary, Invoice, FinanceCard(), DebtSummaryCard(), InvoiceItemEditorCard(), InvoiceScreen(), List, Modifier (+3 more)

### Community 20 - "App Navigation Routes"
Cohesion: 0.27
Nodes (12): Agenda, Budgets, Dashboard, databaseViewModel(), Invoices, Login, Movements, Notifications (+4 more)

### Community 21 - "In-App Notification DAO"
Cohesion: 0.23
Nodes (4): AppNotificationDao, Flow, Int, List

### Community 22 - "Settings Screen UI"
Cohesion: 0.29
Nodes (11): Boolean, Color, ImageVector, Modifier, String, Triple, Unit, NotificationAccessRow() (+3 more)

### Community 23 - "Web Session ViewModel"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 24 - "Shared Finance UI Components"
Cohesion: 0.36
Nodes (10): Composable, EmptyState(), FinanceTag(), IconBadge(), Color, ImageVector, Modifier, String (+2 more)

### Community 25 - "In-App Notification Repository"
Cohesion: 0.22
Nodes (5): AppNotificationRepository, Flow, Int, List, Long

### Community 26 - "Navigation & Login Screens"
Cohesion: 0.20
Nodes (8): AppNavHost(), Boolean, rememberNotificationAccessEnabled(), Boolean, Modifier, String, Unit, LoginScreen()

### Community 28 - "Notification Center Screen"
Cohesion: 0.31
Nodes (8): androidx, ImageVector, Long, String, Triple, NotificationItem(), notificationVisuals(), relativeTime()

### Community 29 - "Notification Center ViewModel"
Cohesion: 0.22
Nodes (6): Int, List, Long, StateFlow, ViewModel, NotificationCenterViewModel

### Community 30 - "Notification Access Permission"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 31 - "In-App Notification Entity"
Cohesion: 0.29
Nodes (3): Long, AppNotificationEntity, String

### Community 32 - "Onboarding Security Illustration"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 33 - "Boot Receiver"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 35 - "PDF Statement Extractor"
Cohesion: 0.40
Nodes (3): ByteArray, String, PdfStatementExtractor

### Community 36 - "Notification Center Composable"
Cohesion: 0.40
Nodes (5): Int, List, Modifier, Unit, NotificationCenterScreen()

### Community 37 - "Biometric Availability Check"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 38 - "Dashboard Period Enum"
Cohesion: 0.50
Nodes (4): DashboardPeriod, Day, Month, Week

### Community 39 - "Invoice Tab Enum"
Cohesion: 0.50
Nodes (4): InvoiceTab, DEBTS, INVOICES, UPLOAD

### Community 40 - "Gradle Wrapper Script"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 41 - "Kivo Brand Icon Asset"
Cohesion: 1.00
Nodes (3): Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity

### Community 42 - "Empty State Wallet Illustration"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 46 - "PDFBox Vendored Glyph Resources"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown

## Knowledge Gaps
- **75 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+70 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **Why does `AppNavHost()` connect `Navigation & Login Screens` to `Agenda & Category UI`, `Notification Center Composable`, `Invoice/Debt Data Access`, `Model Mappers & UI Motion`, `Brand Theme & Color Palette`, `Bank Statement Importer`, `Budget Data Access`, `Dashboard Screen UI`, `Movements List Screen`, `Biometric Lock Gate`, `Invoice/Debt Screen UI`, `App Navigation Routes`, `Settings Screen UI`, `Web Session ViewModel`, `Shared Finance UI Components`, `Notification Center ViewModel`?**
  _High betweenness centrality (0.354) - this node is a cross-community bridge._
- **Why does `FinanzasDatabase` connect `Budget Data Access` to `Category Data Access`, `Navigation & Login Screens`, `Invoice/Debt Data Access`, `In-App Notification DAO`?**
  _High betweenness centrality (0.236) - this node is a cross-community bridge._
- **Why does `MovementEntity` connect `Movement Data Access (DAO)` to `Model Mappers & UI Motion`, `Classification Rule DAO`, `Movement Enrichment Pipeline`?**
  _High betweenness centrality (0.105) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `SettingsViewModel` (e.g. with `AppNavHost()` and `.create()`) actually correct?**
  _`SettingsViewModel` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _75 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Movement Data Access (DAO)` be split into smaller, more focused modules?**
  _Cohesion score 0.07565392354124749 - nodes in this community are weakly interconnected._