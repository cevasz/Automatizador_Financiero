# Graph Report - New  (2026-08-12)

## Corpus Check
- 92 files · ~123,275 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 971 nodes · 1717 edges · 85 communities (49 shown, 36 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 134 edges (avg confidence: 0.81)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `5a5f9867`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- DAOs y consultas de movimientos
- Modelo RawMovement y fuentes
- Reglas de clasificacion (DAO)
- Agenda (DAO)
- Categorias (DAO)
- Herramientas y CI
- Convertidores Room
- Presupuestos y pantalla de planes
- Mapeos de dominio y utilidades UI
- Importador de extractos
- Facturas y deudas (repo)
- Facturas y deudas (DAO)
- Ajustes y exportacion JSON
- Dashboard y balance
- Lista de movimientos e importacion
- Presupuestos (DAO)
- Metas de ahorro (DAO)
- Bloqueo biometrico
- Facturas y deudas (UI)
- Navegacion y componentes base
- Servicio de procesamiento en segundo plano
- Destinos de navegacion
- Centro de notificaciones (DAO)
- Sesion y sincronizacion web
- Componentes UI reutilizables
- Centro de notificaciones (repo)
- Pantalla de agenda
- Tests de parsers bancarios
- Pantalla de notificaciones
- Centro de notificaciones (VM)
- Acceso a notificaciones del sistema
- Centro de notificaciones (entidad)
- Receptor de arranque
- Pantalla de cuenta y sincronizacion
- Tests de acceso a notificaciones
- Extraccion de texto PDF
- Pantalla de notificaciones (utilidades)
- Deteccion biometrica
- Periodos del dashboard
- Scripts de Gradle
- Tests de extraccion PDF
- Tests del importador
- Tests basicos
- BudgetsViewModel
- BudgetCard
- Contexto del proyecto — Kivo, Contabilidad Financiera Automática (Colombia)
- Guía del proyecto — Kivo
- InvoiceTab
- Workflow CI: Build Debug APK
- graphify.md
- graphify.md
- README.md
- README.md
- GRAPH_REPORT.md
- Graphify (grafo de conocimiento del código)
- Graphify skill (pipeline completo)
- Tabla Agenda (enriquecimiento)
- Plataforma Android nativa (Kotlin)
- Centro de notificaciones in-app (tabla app_notifications)
- BiometricAccess
- BiometricLockGate
- BiometricPrompt (API AndroidX)
- graphify-out/ (mapa de dependencias)
- docs/guia.md (guía viva del proyecto)
- MVP local-first
- Núcleo gratuito e ilimitado (modelo de negocio)
- Monorepo (kivo-android, web, backend, docs, graphify-out)
- Motor de clasificación por reglas + regex
- Prohibición de credenciales y scraping
- NotificationListenerService
- Room / SQLite (persistencia local)
- SDD (Software Design Document)
- Sesión web de sincronización (backend + panel)
- Centro de notificaciones in-app (app_notifications)
- BootReceiver
- Fuentes de captura: SMS y Gmail/correo
- graphify-out/ (grafo de dependencias)
- Kivo (guía del proyecto)
- NotificationAccess
- Permiso de acceso a notificaciones (habilitación manual)

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
- `Extracto bancario de ejemplo (PDF, Bancolombia)` --conceptually_related_to--> `BankParser`  [INFERRED]
  kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf → CLAUDE.md
- `Extracto bancario de ejemplo (PDF, Bancolombia)` --conceptually_related_to--> `Bancolombia`  [INFERRED]
  kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf → CLAUDE.md
- `Extracto bancario de ejemplo (PDF, Bancolombia)` --conceptually_related_to--> `pdfbox-android`  [INFERRED]
  kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf → docs/guia.md
- `Extracto bancario de ejemplo (PDF, Bancolombia)` --conceptually_related_to--> `Importación manual de extractos (CSV, texto plano, PDF)`  [INFERRED]
  kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf → docs/guia.md
- `Extracto bancario de ejemplo (PDF, Bancolombia)` --conceptually_related_to--> `RawMovement`  [INFERRED]
  kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf → CLAUDE.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Pipeline de procesamiento de movimientos** — claude_notificationlistenerservice, claude_bankparser, claude_rawmovement, claude_agenda, claude_motor_reglas, claude_room [EXTRACTED 1.00]
- **Bloqueo biométrico opcional** — claude_biometriclockgate, claude_biometricaccess, claude_biometricprompt [EXTRACTED 1.00]
- **Entidades bancarias soportadas en el MVP** — claude_bancolombia, claude_nequi, claude_daviplata, claude_nu, claude_lulo [EXTRACTED 1.00]

## Communities (85 total, 36 thin omitted)

### Community 0 - "DAOs y consultas de movimientos"
Cohesion: 0.08
Nodes (15): CategoryTotal, Flow, Int, List, Long, String, MonthlyTotal, MovementDao (+7 more)

### Community 1 - "Modelo RawMovement y fuentes"
Cohesion: 0.05
Nodes (40): AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, Failure, ParseResult, Success (+32 more)

### Community 2 - "Reglas de clasificacion (DAO)"
Cohesion: 0.06
Nodes (31): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity (+23 more)

### Community 3 - "Agenda (DAO)"
Cohesion: 0.11
Nodes (12): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+4 more)

### Community 4 - "Categorias (DAO)"
Cohesion: 0.08
Nodes (16): CategoryDao, Flow, Int, List, Long, String, CategoryEntity, DefaultCategories (+8 more)

### Community 5 - "Herramientas y CI"
Cohesion: 0.15
Nodes (15): Bancolombia, BankParser, Daviplata, Lulo Bank, Nequi, Nu, RawMovement, Importación manual de extractos (CSV, texto plano, PDF) (+7 more)

### Community 6 - "Convertidores Room"
Cohesion: 0.06
Nodes (31): Converters, Instant, Long, String, AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL (+23 more)

### Community 7 - "Presupuestos y pantalla de planes"
Cohesion: 0.18
Nodes (8): AgendaEntry, AgendaViewModel, Boolean, List, Long, StateFlow, String, ViewModel

### Community 8 - "Mapeos de dominio y utilidades UI"
Cohesion: 0.13
Nodes (17): SavingsGoal, List, Long, Modifier, NumberFormat, String, Unit, money() (+9 more)

### Community 9 - "Importador de extractos"
Cohesion: 0.10
Nodes (21): ImportSummary, Instant, Long, String, StatementImporter, BankEntity, BANCOLOMBIA, DAVIPLATA (+13 more)

### Community 10 - "Facturas y deudas (repo)"
Cohesion: 0.21
Nodes (7): InvoiceViewModel, List, Long, StateFlow, String, ViewModel, Pair

### Community 11 - "Facturas y deudas (DAO)"
Cohesion: 0.09
Nodes (17): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity (+9 more)

### Community 12 - "Ajustes y exportacion JSON"
Cohesion: 0.10
Nodes (19): JSONArray, JSONObject, AppThemePalette, FOREST_GREEN, KIVO_CORAL, MIDNIGHT_BLUE, OCEAN_TEAL, SUNSET_AMBER (+11 more)

### Community 13 - "Dashboard y balance"
Cohesion: 0.09
Nodes (37): Dp, Float, FontWeight, Movement, AnimatedAmountText(), appearFromBelow(), Color, Int (+29 more)

### Community 14 - "Lista de movimientos e importacion"
Cohesion: 0.18
Nodes (19): cleanEnum(), fromRoute(), ImportStatementDialog(), List, Long, Modifier, NumberFormat, String (+11 more)

### Community 15 - "Presupuestos (DAO)"
Cohesion: 0.10
Nodes (12): Application, BudgetDao, Flow, Int, List, Long, BudgetEntity, FinanzasDatabase (+4 more)

### Community 16 - "Metas de ahorro (DAO)"
Cohesion: 0.22
Nodes (6): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity

### Community 17 - "Bloqueo biometrico"
Cohesion: 0.13
Nodes (14): Bundle, Class, BiometricLockGate(), BiometricLockPrompt(), FragmentActivity, showPrompt(), BiometricSettingsViewModelFactory, androidx (+6 more)

### Community 18 - "Facturas y deudas (UI)"
Cohesion: 0.29
Nodes (11): DebtSummary, Invoice, InvoiceItem, DebtSummaryCard(), InvoiceItemEditorCard(), InvoiceScreen(), List, Modifier (+3 more)

### Community 19 - "Navegacion y componentes base"
Cohesion: 0.28
Nodes (12): FinanceCard(), Boolean, Color, ImageVector, Modifier, String, Triple, Unit (+4 more)

### Community 20 - "Servicio de procesamiento en segundo plano"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 21 - "Destinos de navegacion"
Cohesion: 0.27
Nodes (12): Agenda, Budgets, Dashboard, databaseViewModel(), Invoices, Login, Movements, Notifications (+4 more)

### Community 22 - "Centro de notificaciones (DAO)"
Cohesion: 0.23
Nodes (4): AppNotificationDao, Flow, Int, List

### Community 23 - "Sesion y sincronizacion web"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 24 - "Componentes UI reutilizables"
Cohesion: 0.36
Nodes (10): Composable, EmptyState(), FinanceTag(), IconBadge(), Color, ImageVector, Modifier, String (+2 more)

### Community 25 - "Centro de notificaciones (repo)"
Cohesion: 0.22
Nodes (5): AppNotificationRepository, Flow, Int, List, Long

### Community 26 - "Pantalla de agenda"
Cohesion: 0.39
Nodes (8): AddEditAgendaEntryScreen(), AgendaEntryCard(), AgendaScreen(), cleanEnum(), List, Modifier, String, Unit

### Community 28 - "Pantalla de notificaciones"
Cohesion: 0.31
Nodes (8): androidx, ImageVector, Long, String, Triple, NotificationItem(), notificationVisuals(), relativeTime()

### Community 29 - "Centro de notificaciones (VM)"
Cohesion: 0.22
Nodes (6): Int, List, Long, StateFlow, ViewModel, NotificationCenterViewModel

### Community 30 - "Acceso a notificaciones del sistema"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 31 - "Centro de notificaciones (entidad)"
Cohesion: 0.29
Nodes (3): Long, AppNotificationEntity, String

### Community 32 - "Receptor de arranque"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 33 - "Pantalla de cuenta y sincronizacion"
Cohesion: 0.20
Nodes (8): AppNavHost(), Boolean, rememberNotificationAccessEnabled(), Boolean, Modifier, String, Unit, LoginScreen()

### Community 35 - "Extraccion de texto PDF"
Cohesion: 0.40
Nodes (3): ByteArray, String, PdfStatementExtractor

### Community 36 - "Pantalla de notificaciones (utilidades)"
Cohesion: 0.40
Nodes (5): Int, List, Modifier, Unit, NotificationCenterScreen()

### Community 37 - "Deteccion biometrica"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 38 - "Periodos del dashboard"
Cohesion: 0.13
Nodes (11): EnrichmentPipeline, Boolean, Double, toDomain(), EnrichedMovement, RawMovement, Notification, String (+3 more)

### Community 39 - "Scripts de Gradle"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 48 - "BudgetsViewModel"
Cohesion: 0.18
Nodes (9): toDomain(), toEntity(), Budget, BudgetsViewModel, Boolean, List, Long, StateFlow (+1 more)

### Community 49 - "BudgetCard"
Cohesion: 0.26
Nodes (12): Category, BudgetCard(), BudgetDetailScreen(), BudgetsScreen(), List, Long, Modifier, NumberFormat (+4 more)

### Community 50 - "Contexto del proyecto — Kivo, Contabilidad Financiera Automática (Colombia)"
Cohesion: 0.17
Nodes (11): Arquitectura interna (dentro de la app Android), Contexto del proyecto — Kivo, Contabilidad Financiera Automática (Colombia), Convenciones de código, Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente), Documentos vivos, Entidades bancarias soportadas en el MVP, Estructura del repositorio (monorepo), Identidad de marca (+3 more)

### Community 51 - "Guía del proyecto — Kivo"
Cohesion: 0.20
Nodes (9): Alcance, Arquitectura, Bloqueo biométrico opcional, Estructura (monorepo), Guía del proyecto — Kivo, Identidad, Permiso de notificaciones (importante), Regla de limpieza (+1 more)

### Community 52 - "InvoiceTab"
Cohesion: 0.50
Nodes (4): InvoiceTab, DEBTS, INVOICES, UPLOAD

### Community 53 - "Workflow CI: Build Debug APK"
Cohesion: 0.67
Nodes (3): Workflow CI: Build Debug APK, gradlew de kivo-android, Kivo

## Knowledge Gaps
- **112 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+107 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **36 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AppNavHost()` connect `Pantalla de cuenta y sincronizacion` to `Presupuestos y pantalla de planes`, `Mapeos de dominio y utilidades UI`, `Importador de extractos`, `Facturas y deudas (repo)`, `Ajustes y exportacion JSON`, `Dashboard y balance`, `Lista de movimientos e importacion`, `Presupuestos (DAO)`, `Bloqueo biometrico`, `Facturas y deudas (UI)`, `Navegacion y componentes base`, `Destinos de navegacion`, `Sesion y sincronizacion web`, `Componentes UI reutilizables`, `Pantalla de agenda`, `Centro de notificaciones (VM)`, `Pantalla de notificaciones (utilidades)`, `BudgetsViewModel`, `BudgetCard`?**
  _High betweenness centrality (0.346) - this node is a cross-community bridge._
- **Why does `FinanzasDatabase` connect `Presupuestos (DAO)` to `Pantalla de cuenta y sincronizacion`, `Facturas y deudas (DAO)`, `Categorias (DAO)`, `Centro de notificaciones (DAO)`?**
  _High betweenness centrality (0.231) - this node is a cross-community bridge._
- **Why does `MovementEntity` connect `DAOs y consultas de movimientos` to `BudgetsViewModel`, `Reglas de clasificacion (DAO)`, `Periodos del dashboard`?**
  _High betweenness centrality (0.103) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `SettingsViewModel` (e.g. with `AppNavHost()` and `.create()`) actually correct?**
  _`SettingsViewModel` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _112 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `DAOs y consultas de movimientos` be split into smaller, more focused modules?**
  _Cohesion score 0.07565392354124749 - nodes in this community are weakly interconnected._
- **Should `Modelo RawMovement y fuentes` be split into smaller, more focused modules?**
  _Cohesion score 0.05191146881287726 - nodes in this community are weakly interconnected._