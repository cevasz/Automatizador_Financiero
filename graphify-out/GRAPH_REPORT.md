# Graph Report - .  (2026-08-14)

## Corpus Check
- 31 files · ~132,945 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1085 nodes · 1849 edges · 95 communities (62 shown, 33 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 155 edges (avg confidence: 0.82)
- Token cost: 0 input · 30,000 output

## Community Hubs (Navigation)
- Dashboard & Database Core
- Bank Parsers & Raw Movement Model
- Classification Rule DAO
- Cumplimiento Normativo y Exportación
- Invoice/Debt Data Access
- In-App Notification DAO
- Movement Entity Core Types
- Movement Data Access (DAO)
- Room Persistence & Budget DAO
- Bank Statement Importer
- Budget UI & Motion
- Savings Goal Screen
- Agenda Repository Impl
- Model Mappers (Agenda/Category)
- Biometric Lock Gate
- Agenda Data Access
- Category Data Access
- Budgets ViewModel
- Movements List Screen
- Room Type Converters
- Savings Goal Data Access
- Bank Parser Tests
- Notification Center Screen & Entity
- Movement Processor Service
- App Navigation Routes
- Agenda Repository Interface
- Movement Enrichment Pipeline
- Web Session ViewModel
- Notification Capture Service
- SDD Roadmap: Fases 2-3 y Alcance
- Boot Receiver & Email Capture Source
- Payment Method Enum
- Agenda Financiera (Concept)
- SDD Módulos: Captura y Alertas
- Notification Access Permission
- Contexto Regulatorio y Roadmap
- Modelo de Sostenibilidad (Aportes)
- Raw Movement Data Model
- Clasificación Automática y Categorías
- Bank Entity Enum
- Colombian Amount Parser Tests
- BankParser Interface
- Onboarding Security Illustration
- Biometric Availability Check
- Notification Access Tests
- Restricciones de Plataforma y Riesgos
- Movement Source Enum
- PDF Statement Extractor
- Date Pattern Types
- Colombian Amount Parser
- Graphify Workflow Integration
- Backend/Web Sync (Pendiente)
- Instant/Long Conversions
- Statement Importer Tests
- Gradle Wrapper Script
- CI Build & Android Module
- Backend/Web Pendientes de Desarrollo
- Documentos Vivos del Proyecto
- Kivo Brand Identity Assets
- Notification Access State Composable
- Empty State UX Assets
- PDF Extractor Tests
- Simple Smoke Test
- PDFBox Vendored Resources
- Bancolombia Fixtures & PDFBox Data
- In-App Notifications Table
- BankParser Test Convention
- Capas Premium (Modelo de Negocio)
- Bancolombia Bank Entity
- Biometric Access (Doc Ref)
- Daviplata Bank Entity
- Living Documentation Concept
- Lulo Bank Entity
- Nequi Bank Entity
- Nu Bank Entity
- BiometricPrompt API
- Cleanup Rule (Docs)
- Kivo Brand Name
- pdfbox-android Library
- MVP Success Metrics
- Próximos Pasos (SDD)
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
4. `CategoryEntity` - 26 edges
5. `AgendaEntryEntity` - 23 edges
6. `FinanceCard()` - 23 edges
7. `IconBadge()` - 21 edges
8. `AppNavHost()` - 18 edges
9. `DashboardScreen()` - 18 edges
10. `BudgetsViewModel` - 18 edges

## Surprising Connections (you probably didn't know these)
- `Persistencia en Room` --conceptually_related_to--> `FinanzasDatabase`  [INFERRED]
  docs/guia.md → kivo-android/app/src/main/java/com/finanzas/automatica/data/local/FinanzasDatabase.kt
- `Seguridad y privacidad por diseño` --rationale_for--> `FinanzasDatabase`  [EXTRACTED]
  docs/SDD.md → kivo-android/app/src/main/java/com/finanzas/automatica/data/local/FinanzasDatabase.kt
- `Fuente 5 — Integración oficial Open Finance` --references--> `OPEN_FINANCE`  [EXTRACTED]
  docs/SDD.md → kivo-android/app/src/main/java/com/finanzas/automatica/domain/model/Movement.kt
- `Bancolombia Notification Fixtures` --semantically_similar_to--> `Bancolombia Statement PDF Fixture`  [INFERRED] [semantically similar]
  kivo-android/app/src/test/resources/fixtures/bancolombia_notifications.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `Agenda financiera` --references--> `COMMUNITY_SUGGESTED`  [EXTRACTED]
  docs/SDD.md → kivo-android/app/src/main/java/com/finanzas/automatica/domain/model/AgendaEntry.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Arquitectura de captura multi-fuente (5 fuentes de datos)** — docs_sdd_notificationlistenerservice, docs_sdd_share_extension, docs_sdd_ocr, docs_sdd_parseo_de_correos, docs_sdd_open_finance [EXTRACTED 1.00]
- **Roadmap por fases (Fase 0-4)** — docs_sdd_fase_0, docs_sdd_fase_1_mvp, docs_sdd_fase_2, docs_sdd_fase_3, docs_sdd_fase_4_open_finance [EXTRACTED 1.00]
- **Recompensas simbólicas para aportantes voluntarios** — docs_sdd_aportes_voluntarios_con_recompensa, docs_sdd_huella_financiera_generativa, docs_sdd_numero_de_fundador, docs_sdd_voto_de_roadmap, docs_sdd_temas_exclusivos, docs_sdd_resumen_del_ano_enriquecido [INFERRED 0.85]

## Communities (95 total, 33 thin omitted)

### Community 0 - "Dashboard & Database Core"
Cohesion: 0.05
Nodes (79): Composable, DebtSummary, Dashboard y cronología, FinanzasDatabase, Invoice, InvoiceItem, AppNavHost(), EmptyState() (+71 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.06
Nodes (36): BankParser, Double, AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, Failure (+28 more)

### Community 2 - "Classification Rule DAO"
Cohesion: 0.06
Nodes (32): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity (+24 more)

### Community 3 - "Cumplimiento Normativo y Exportación"
Cohesion: 0.07
Nodes (29): Ley 1581 de 2012 (habeas data), Never request bank credentials or scrape accounts, Context, Cumplimiento normativo, Exportación (CSV, Excel, PDF), Ley 1581 de 2012 (habeas data), Seguridad y privacidad por diseño, JSONArray (+21 more)

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.06
Nodes (26): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+18 more)

### Community 5 - "In-App Notification DAO"
Cohesion: 0.06
Nodes (18): AppNotificationDao, Flow, Int, List, Long, AppNotificationEntity, AppNotificationRepository, Flow (+10 more)

### Community 6 - "Movement Entity Core Types"
Cohesion: 0.15
Nodes (7): Flow, Int, List, Long, MovementEntity, String, MovementDao

### Community 7 - "Movement Data Access (DAO)"
Cohesion: 0.14
Nodes (9): CategoryTotal, MonthlyTotal, Flow, Int, List, Long, MovementEntity, String (+1 more)

### Community 8 - "Room Persistence & Budget DAO"
Cohesion: 0.10
Nodes (13): Application, Persistencia en Room, BudgetDao, Flow, Int, List, Long, BudgetEntity (+5 more)

### Community 9 - "Bank Statement Importer"
Cohesion: 0.10
Nodes (18): ByteArray, ImportSummary, BankEntity, Instant, Long, PaymentMethod, String, StatementImporter (+10 more)

### Community 10 - "Budget UI & Motion"
Cohesion: 0.11
Nodes (29): Presupuestos por categoría, Dp, Float, FontWeight, AnimatedAmountText(), appearFromBelow(), Color, Int (+21 more)

### Community 11 - "Savings Goal Screen"
Cohesion: 0.13
Nodes (18): Metas de ahorro, SavingsGoal, List, Long, Modifier, NumberFormat, String, Unit (+10 more)

### Community 12 - "Agenda Repository Impl"
Cohesion: 0.18
Nodes (6): AgendaRepositoryImpl, CategoryRepositoryImpl, Flow, List, Long, String

### Community 13 - "Model Mappers (Agenda/Category)"
Cohesion: 0.13
Nodes (12): toDomain(), toEntity(), AgendaEntry, Category, Budget, AgendaViewModel, Boolean, List (+4 more)

### Community 14 - "Biometric Lock Gate"
Cohesion: 0.11
Nodes (16): Bundle, Class, BiometricLockGate, BiometricLockGate, BiometricLockGate(), BiometricLockPrompt(), FragmentActivity, showPrompt() (+8 more)

### Community 15 - "Agenda Data Access"
Cohesion: 0.18
Nodes (7): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity

### Community 16 - "Category Data Access"
Cohesion: 0.20
Nodes (7): CategoryDao, Flow, Int, List, Long, String, CategoryEntity

### Community 17 - "Budgets ViewModel"
Cohesion: 0.15
Nodes (13): budgetKey(), BudgetsViewModel, Boolean, Budget, Category, Int, List, Long (+5 more)

### Community 18 - "Movements List Screen"
Cohesion: 0.17
Nodes (19): cleanEnum(), fromRoute(), ImportStatementDialog(), List, Long, Modifier, Movement, NumberFormat (+11 more)

### Community 19 - "Room Type Converters"
Cohesion: 0.16
Nodes (10): Converters, String, ConfirmationState, AUTO_CONFIRMED, CONFIRMED, PENDING, REJECTED, MovementType (+2 more)

### Community 20 - "Savings Goal Data Access"
Cohesion: 0.22
Nodes (6): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity

### Community 21 - "Bank Parser Tests"
Cohesion: 0.14
Nodes (3): BankParserTest, List, String

### Community 22 - "Notification Center Screen & Entity"
Cohesion: 0.19
Nodes (14): androidx, AppNotificationEntity, ImageVector, Int, List, Long, Modifier, String (+6 more)

### Community 23 - "Movement Processor Service"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 24 - "App Navigation Routes"
Cohesion: 0.27
Nodes (12): Agenda, Budgets, Dashboard, databaseViewModel(), Invoices, Login, Movements, Notifications (+4 more)

### Community 25 - "Agenda Repository Interface"
Cohesion: 0.20
Nodes (5): AgendaRepository, Flow, List, Long, String

### Community 26 - "Movement Enrichment Pipeline"
Cohesion: 0.30
Nodes (6): EnrichmentPipeline, Boolean, Double, toDomain(), EnrichedMovement, RawMovement

### Community 27 - "Web Session ViewModel"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 28 - "Notification Capture Service"
Cohesion: 0.23
Nodes (5): Notification, String, NotificationCaptureService, NotificationListenerService, StatusBarNotification

### Community 29 - "SDD Roadmap: Fases 2-3 y Alcance"
Cohesion: 0.18
Nodes (11): Local-first, rules-only classification (no LLM) scope decision, web (panel module), Importación de extractos bancarios (CSV/texto/PDF), Aprendizaje comunitario opt-in de la agenda (efecto de red), Fase 2 — Robustecimiento, Fase 3 — Multiplataforma, Fuente 3 — OCR de comprobantes, Panel web (componente) (+3 more)

### Community 30 - "Boot Receiver & Email Capture Source"
Cohesion: 0.20
Nodes (7): BroadcastReceiver, BootReceiver, Permiso de notificaciones (habilitación manual), Fuente 4 — Parseo de correos de confirmación bancaria, BootReceiver, Context, Intent

### Community 31 - "Payment Method Enum"
Cohesion: 0.20
Nodes (10): PaymentMethod, BANCOLOMBIA, CASH, DAVIPLATA, LULO, NEQUI, NU, OTHER (+2 more)

### Community 32 - "Agenda Financiera (Concept)"
Cohesion: 0.25
Nodes (7): Agenda (table), Agenda, Agenda financiera, AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL

### Community 33 - "SDD Módulos: Captura y Alertas"
Cohesion: 0.25
Nodes (7): BankParser (por banco), NotificationListenerService, Alertas y detección de patrones, Captura automática de movimientos, Confirmación ligera de movimientos (patrón swipe), Fuente 1 — NotificationListenerService, DatePattern

### Community 34 - "Notification Access Permission"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 35 - "Contexto Regulatorio y Roadmap"
Cohesion: 0.32
Nodes (8): Decreto 0368 de 2026, Kivo (app), Decreto 0368 de 2026, Fase 0 — Validación, Fase 1 — MVP (Android, reglas+regex, gratis), Fase 4 — Open Finance, Fuente 5 — Integración oficial Open Finance, OPEN_FINANCE

### Community 36 - "Modelo de Sostenibilidad (Aportes)"
Cohesion: 0.32
Nodes (7): Free, unlimited core features (no paywall), Aportes voluntarios con recompensa, Huella financiera generativa, Número de fundador, Resumen del año enriquecido, Temas exclusivos (tema fundador terracota-ocre), Voto de roadmap

### Community 37 - "Raw Movement Data Model"
Cohesion: 0.29
Nodes (5): RawMovement, RawMovement, Modelo de datos (entidades principales), Movimiento crudo (objeto estandarizado multi-fuente), Movement

### Community 38 - "Clasificación Automática y Categorías"
Cohesion: 0.29
Nodes (4): Clasificación automática, DefaultCategories, List, Long

### Community 39 - "Bank Entity Enum"
Cohesion: 0.25
Nodes (7): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN

### Community 41 - "BankParser Interface"
Cohesion: 0.38
Nodes (4): BankParser, Boolean, List, String

### Community 42 - "Onboarding Security Illustration"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 43 - "Biometric Availability Check"
Cohesion: 0.33
Nodes (4): BiometricAccess, BiometricAccess, Boolean, Context

### Community 45 - "Restricciones de Plataforma y Riesgos"
Cohesion: 0.50
Nodes (5): No Accessibility API for automated actions (Google Play policy), NotificationListenerService, Restricción de plataforma Android (permiso de alto riesgo), Restricción de plataforma iOS (sin lectura de notificaciones), Riesgos y mitigaciones

### Community 46 - "Movement Source Enum"
Cohesion: 0.40
Nodes (3): MovementSource, MANUAL, NOTIFICATION

### Community 47 - "PDF Statement Extractor"
Cohesion: 0.40
Nodes (3): ByteArray, String, PdfStatementExtractor

### Community 48 - "Date Pattern Types"
Cohesion: 0.40
Nodes (5): DatePatternType, DAY_MONTH_YEAR, DMY, TODAY, YESTERDAY

### Community 49 - "Colombian Amount Parser"
Cohesion: 0.40
Nodes (3): ColombianAmountParser, Long, String

### Community 50 - "Graphify Workflow Integration"
Cohesion: 0.50
Nodes (4): Graphify Integration Rule, Graphify Workflow, graphify-out (dependency graph output), graphify query-first contextualization workflow

### Community 51 - "Backend/Web Sync (Pendiente)"
Cohesion: 0.50
Nodes (4): backend (sync API module), Web/backend synchronization (account login), Sincronización web (login de panel, no bancario), Backend / API (componente)

### Community 54 - "Gradle Wrapper Script"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 55 - "CI Build & Android Module"
Cohesion: 0.67
Nodes (3): Build Debug APK Workflow (GitHub Actions), kivo-android (Android app module), Aplicación móvil (componente)

### Community 56 - "Backend/Web Pendientes de Desarrollo"
Cohesion: 1.00
Nodes (3): Kivo Backend (pendiente de desarrollo), Defer backend/web/multi-device sync until local MVP is validated, Kivo Web Panel (pendiente de desarrollo)

### Community 57 - "Documentos Vivos del Proyecto"
Cohesion: 1.00
Nodes (3): CLAUDE.md — Kivo Project Context, Guía del proyecto — Kivo, SDD — Aplicación de Contabilidad Financiera Personal Automática

### Community 58 - "Kivo Brand Identity Assets"
Cohesion: 1.00
Nodes (3): Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity

### Community 60 - "Empty State UX Assets"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 63 - "PDFBox Vendored Resources"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

### Community 64 - "Bancolombia Fixtures & PDFBox Data"
Cohesion: 0.67
Nodes (3): PDFBox Glyph List (vendored resource tables), Bancolombia Notification Fixtures, Bancolombia Statement PDF Fixture

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown
- `Permiso de notificaciones (habilitación manual)` → `Fuente 4 — Parseo de correos de confirmación bancaria`  [AMBIGUOUS]
  docs/SDD.md · relation: conceptually_related_to

## Knowledge Gaps
- **109 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+104 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **33 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **What is the exact relationship between `Permiso de notificaciones (habilitación manual)` and `Fuente 4 — Parseo de correos de confirmación bancaria`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **Why does `FinanzasDatabase` connect `Room Persistence & Budget DAO` to `Cumplimiento Normativo y Exportación`, `Invoice/Debt Data Access`, `In-App Notification DAO`, `Clasificación Automática y Categorías`, `Agenda Data Access`?**
  _High betweenness centrality (0.288) - this node is a cross-community bridge._
- **Why does `AppNavHost()` connect `Dashboard & Database Core` to `Cumplimiento Normativo y Exportación`, `Bank Statement Importer`, `Budget UI & Motion`, `Biometric Lock Gate`, `Budgets ViewModel`, `Movements List Screen`, `Notification Center Screen & Entity`, `App Navigation Routes`?**
  _High betweenness centrality (0.225) - this node is a cross-community bridge._
- **Why does `SettingsViewModel` connect `Cumplimiento Normativo y Exportación` to `Dashboard & Database Core`, `Raw Movement Data Model`, `Biometric Lock Gate`?**
  _High betweenness centrality (0.218) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `SettingsViewModel` (e.g. with `AppNavHost()` and `.create()`) actually correct?**
  _`SettingsViewModel` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _109 weakly-connected nodes found - possible documentation gaps or missing edges._