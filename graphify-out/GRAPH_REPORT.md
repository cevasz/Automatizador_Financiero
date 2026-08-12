# Graph Report - New  (2026-08-11)

## Corpus Check
- 76 files · ~24,656 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 783 nodes · 1491 edges · 50 communities (44 shown, 6 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 98 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `50768c36`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- .buildRawMovement
- MovementEntity
- CategoryEntity
- PaymentMethod
- NotificationCaptureService
- AgendaEntryEntity
- PaymentMethod
- .classify
- Converters
- ClassificationRuleDao
- FinanzasDatabase
- BudgetDao
- SavingsGoalDao
- DashboardScreen
- MovementProcessorService
- BankParser
- BankParserTest
- BootReceiver
- gradlew
- build.gradle.kts
- Color.kt
- FinanceCard
- build.gradle.kts
- settings.gradle.kts
- Category
- BankParserTest
- BudgetCard
- SimpleTest
- AppNavHost.kt
- .parseLine
- PaymentMethod
- SettingRow
- BankEntity
- AgendaOrigin
- BootReceiver
- Guía del proyecto
- ConfirmationState
- .fromInstant
- InvoiceTab
- StatementImporterTest
- gradlew
- SimpleTest
- AppNavHost
- BudgetsViewModel
- DashboardPeriod

## God Nodes (most connected - your core abstractions)
1. `MovementEntity` - 37 edges
2. `MovementDao` - 31 edges
3. `CategoryEntity` - 26 edges
4. `MovementRepositoryImpl` - 26 edges
5. `SettingsViewModel` - 26 edges
6. `AgendaEntryEntity` - 23 edges
7. `AppNavHost()` - 22 edges
8. `FinanceCard()` - 20 edges
9. `IconBadge()` - 18 edges
10. `InvoiceScreen()` - 17 edges

## Surprising Connections (you probably didn't know these)
- `AppNavHost()` --calls--> `FinanceCard()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/navigation/AppNavHost.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/components/FinanceUi.kt
- `AppNavHost()` --calls--> `AgendaScreen()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/navigation/AppNavHost.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/screen/AgendaScreen.kt
- `AppNavHost()` --calls--> `BudgetsScreen()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/navigation/AppNavHost.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/screen/BudgetsScreen.kt
- `AppNavHost()` --calls--> `DashboardScreen()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/navigation/AppNavHost.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/screen/DashboardScreen.kt
- `AppNavHost()` --calls--> `InvoiceScreen()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/navigation/AppNavHost.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/screen/InvoiceScreen.kt

## Import Cycles
- None detected.

## Communities (50 total, 6 thin omitted)

### Community 0 - ".buildRawMovement"
Cohesion: 0.22
Nodes (18): Movement, cleanEnum(), DashboardScreen(), Color, ImageVector, Int, List, Long (+10 more)

### Community 1 - "MovementEntity"
Cohesion: 0.16
Nodes (10): Converters, String, MovementSource, MANUAL, NOTIFICATION, OCR, OPEN_FINANCE, MovementType (+2 more)

### Community 2 - "CategoryEntity"
Cohesion: 0.08
Nodes (16): CategoryDao, Flow, Int, List, Long, String, CategoryEntity, DefaultCategories (+8 more)

### Community 3 - "PaymentMethod"
Cohesion: 0.09
Nodes (23): CategoryLookupRepository, ClassificationEngine, ClassificationRepositoryProvider, ClassificationRuleRepository, DefaultClassificationEngine, DefaultKeywordRepository, KeywordMatch, KeywordRepository (+15 more)

### Community 4 - "NotificationCaptureService"
Cohesion: 0.08
Nodes (15): CategoryTotal, Flow, Int, List, Long, String, MonthlyTotal, MovementDao (+7 more)

### Community 5 - "AgendaEntryEntity"
Cohesion: 0.10
Nodes (17): InvoiceRepository, Boolean, Flow, List, Long, String, DebtStatus, PAID (+9 more)

### Community 6 - "PaymentMethod"
Cohesion: 0.23
Nodes (6): BudgetDao, Flow, Int, List, Long, BudgetEntity

### Community 7 - ".classify"
Cohesion: 0.13
Nodes (11): EnrichmentPipeline, Boolean, Double, toDomain(), EnrichedMovement, RawMovement, Notification, String (+3 more)

### Community 8 - "Converters"
Cohesion: 0.18
Nodes (8): Boolean, Int, String, ViewModel, SettingsViewModel, JSONArray, JSONObject, kotlinx

### Community 9 - "ClassificationRuleDao"
Cohesion: 0.16
Nodes (8): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity

### Community 10 - "FinanzasDatabase"
Cohesion: 0.10
Nodes (12): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+4 more)

### Community 11 - "BudgetDao"
Cohesion: 0.20
Nodes (16): AppNavHost(), EmptyState(), FinanceTag(), IconBadge(), Color, ImageVector, Modifier, String (+8 more)

### Community 12 - "SavingsGoalDao"
Cohesion: 0.18
Nodes (19): cleanEnum(), fromRoute(), ImportStatementDialog(), List, Long, Modifier, NumberFormat, String (+11 more)

### Community 13 - "DashboardScreen"
Cohesion: 0.20
Nodes (6): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity

### Community 14 - "MovementProcessorService"
Cohesion: 0.18
Nodes (7): AgendaViewModel, Boolean, List, Long, StateFlow, String, ViewModel

### Community 15 - "BankParser"
Cohesion: 0.05
Nodes (40): AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, Failure, ParseResult, Success (+32 more)

### Community 18 - "gradlew"
Cohesion: 0.22
Nodes (9): Boolean, Int, List, Long, StateFlow, String, ViewModel, MovementViewModel (+1 more)

### Community 19 - "build.gradle.kts"
Cohesion: 0.13
Nodes (17): SavingsGoal, List, Long, Modifier, NumberFormat, String, Unit, money() (+9 more)

### Community 20 - "Color.kt"
Cohesion: 0.22
Nodes (6): Int, Intent, Notification, MovementProcessorService, IBinder, Service

### Community 21 - "FinanceCard"
Cohesion: 0.31
Nodes (11): DebtSummary, Invoice, FinanceCard(), DebtSummaryCard(), InvoiceItemEditorCard(), InvoiceScreen(), List, Modifier (+3 more)

### Community 22 - "build.gradle.kts"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 23 - "settings.gradle.kts"
Cohesion: 0.29
Nodes (11): Agenda, Budgets, Dashboard, databaseViewModel(), Invoices, Login, Movements, Savings (+3 more)

### Community 24 - "Category"
Cohesion: 0.38
Nodes (9): AgendaEntry, AddEditAgendaEntryScreen(), AgendaEntryCard(), AgendaScreen(), cleanEnum(), List, Modifier, String (+1 more)

### Community 25 - "BankParserTest"
Cohesion: 0.20
Nodes (9): Arquitectura interna (dentro de la app Android), Contexto del proyecto — App de Contabilidad Financiera Automática (Colombia), Convenciones de código, Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente), Documentos vivos, Entidades bancarias soportadas en el MVP, Qué es, Qué NO hacer (+1 more)

### Community 26 - "BudgetCard"
Cohesion: 0.26
Nodes (12): Category, BudgetCard(), BudgetDetailScreen(), BudgetsScreen(), List, Long, Modifier, NumberFormat (+4 more)

### Community 27 - "SimpleTest"
Cohesion: 0.25
Nodes (5): MainActivity, FinanzasAutomaticaTheme(), Boolean, Bundle, ComponentActivity

### Community 29 - ".parseLine"
Cohesion: 0.33
Nodes (5): ImportSummary, Instant, Long, String, StatementImporter

### Community 30 - "PaymentMethod"
Cohesion: 0.20
Nodes (10): PaymentMethod, BANCOLOMBIA, CASH, DAVIPLATA, LULO, NEQUI, NU, OTHER (+2 more)

### Community 31 - "SettingRow"
Cohesion: 0.33
Nodes (8): Boolean, Color, ImageVector, Modifier, String, Unit, SettingRow(), SettingsScreen()

### Community 32 - "BankEntity"
Cohesion: 0.25
Nodes (7): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN

### Community 33 - "AgendaOrigin"
Cohesion: 0.29
Nodes (4): AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL

### Community 34 - "BootReceiver"
Cohesion: 0.33
Nodes (4): BootReceiver, Context, Intent, BroadcastReceiver

### Community 35 - "Guía del proyecto"
Cohesion: 0.33
Nodes (5): Alcance, Arquitectura, Guía del proyecto, Regla de limpieza, Sincronizacion web

### Community 36 - "ConfirmationState"
Cohesion: 0.29
Nodes (5): ConfirmationState, AUTO_CONFIRMED, CONFIRMED, PENDING, REJECTED

### Community 38 - "InvoiceTab"
Cohesion: 0.50
Nodes (4): InvoiceTab, DEBTS, INVOICES, UPLOAD

### Community 40 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 42 - "AppNavHost"
Cohesion: 0.10
Nodes (14): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity (+6 more)

### Community 43 - "BudgetsViewModel"
Cohesion: 0.17
Nodes (9): toDomain(), toEntity(), Budget, BudgetsViewModel, Boolean, List, Long, StateFlow (+1 more)

### Community 44 - "DashboardPeriod"
Cohesion: 0.50
Nodes (4): DashboardPeriod, Day, Month, Week

## Knowledge Gaps
- **67 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+62 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **6 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AppNavHost()` connect `BudgetDao` to `.buildRawMovement`, `AgendaEntryEntity`, `Converters`, `AppNavHost`, `BudgetsViewModel`, `SavingsGoalDao`, `MovementProcessorService`, `gradlew`, `build.gradle.kts`, `FinanceCard`, `build.gradle.kts`, `settings.gradle.kts`, `Category`, `BudgetCard`, `SimpleTest`, `SettingRow`?**
  _High betweenness centrality (0.339) - this node is a cross-community bridge._
- **Why does `FinanzasDatabase` connect `AppNavHost` to `CategoryEntity`, `ClassificationRuleDao`, `FinanzasDatabase`, `BudgetDao`, `DashboardScreen`?**
  _High betweenness centrality (0.254) - this node is a cross-community bridge._
- **Why does `MovementEntity` connect `NotificationCaptureService` to `BudgetsViewModel`, `PaymentMethod`, `.classify`?**
  _High betweenness centrality (0.144) - this node is a cross-community bridge._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _67 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `CategoryEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.08163265306122448 - nodes in this community are weakly interconnected._
- **Should `PaymentMethod` be split into smaller, more focused modules?**
  _Cohesion score 0.09291521486643438 - nodes in this community are weakly interconnected._
- **Should `NotificationCaptureService` be split into smaller, more focused modules?**
  _Cohesion score 0.07565392354124749 - nodes in this community are weakly interconnected._