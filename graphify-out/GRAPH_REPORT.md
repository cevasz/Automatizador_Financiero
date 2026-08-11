# Graph Report - New  (2026-08-11)

## Corpus Check
- 65 files · ~22,555 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 724 nodes · 1336 edges · 31 communities (29 shown, 2 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 65 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b3db1625`
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
- SettingsViewModel
- BankParserTest
- Contexto del proyecto — App de Contabilidad Financiera Automática (Colombia)
- SimpleTest
- AppNavHost.kt
- SessionViewModel
- MainActivity

## God Nodes (most connected - your core abstractions)
1. `MovementEntity` - 37 edges
2. `MovementDao` - 31 edges
3. `CategoryEntity` - 26 edges
4. `MovementRepositoryImpl` - 26 edges
5. `SettingsViewModel` - 25 edges
6. `AgendaEntryEntity` - 23 edges
7. `Converters` - 15 edges
8. `IconBadge()` - 15 edges
9. `AgendaViewModel` - 15 edges
10. `CategoryDao` - 14 edges

## Surprising Connections (you probably didn't know these)
- `AgendaEntryCard()` --calls--> `FinanceCard()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/ui/screen/AgendaScreen.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/components/FinanceUi.kt
- `BudgetCard()` --calls--> `FinanceCard()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/ui/screen/BudgetsScreen.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/components/FinanceUi.kt
- `BudgetDetailScreen()` --calls--> `FinanceCard()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/ui/screen/BudgetsScreen.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/components/FinanceUi.kt
- `SavingsGoalCard()` --calls--> `FinanceCard()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/ui/screen/SavingsGoalsScreen.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/components/FinanceUi.kt
- `AgendaEntryCard()` --calls--> `FinanceTag()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/ui/screen/AgendaScreen.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/components/FinanceUi.kt

## Import Cycles
- None detected.

## Communities (31 total, 2 thin omitted)

### Community 0 - ".buildRawMovement"
Cohesion: 0.05
Nodes (39): AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, Failure, ParseResult, Success (+31 more)

### Community 1 - "MovementEntity"
Cohesion: 0.08
Nodes (15): CategoryTotal, Flow, Int, List, Long, String, MonthlyTotal, MovementDao (+7 more)

### Community 2 - "CategoryEntity"
Cohesion: 0.08
Nodes (16): CategoryDao, Flow, Int, List, Long, String, CategoryEntity, DefaultCategories (+8 more)

### Community 3 - "PaymentMethod"
Cohesion: 0.07
Nodes (35): AgendaEntry, Category, Budget, AddEditAgendaEntryScreen(), AgendaEntryCard(), AgendaScreen(), cleanEnum(), List (+27 more)

### Community 4 - "NotificationCaptureService"
Cohesion: 0.13
Nodes (11): EnrichmentPipeline, Boolean, Double, toDomain(), EnrichedMovement, RawMovement, Notification, String (+3 more)

### Community 5 - "AgendaEntryEntity"
Cohesion: 0.10
Nodes (12): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+4 more)

### Community 6 - "PaymentMethod"
Cohesion: 0.05
Nodes (38): Converters, Instant, Long, String, AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL (+30 more)

### Community 7 - ".classify"
Cohesion: 0.09
Nodes (23): CategoryLookupRepository, ClassificationEngine, ClassificationRepositoryProvider, ClassificationRuleRepository, DefaultClassificationEngine, DefaultKeywordRepository, KeywordMatch, KeywordRepository (+15 more)

### Community 8 - "Converters"
Cohesion: 0.05
Nodes (37): 10. Stack tecnológico propuesto, 11. Roadmap por fases, 12. Riesgos y mitigaciones, 13. Métricas de éxito del MVP, 14. Próximos pasos, 1. Resumen ejecutivo, 2.1 El problema, 2.2 Contexto regulatorio (Colombia, 2026) (+29 more)

### Community 9 - "ClassificationRuleDao"
Cohesion: 0.19
Nodes (8): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity

### Community 10 - "FinanzasDatabase"
Cohesion: 0.18
Nodes (6): FinanzasDatabase, getInstance(), Context, FinanzasApplication, Application, RoomDatabase

### Community 11 - "BudgetDao"
Cohesion: 0.23
Nodes (6): BudgetDao, Flow, Int, List, Long, BudgetEntity

### Community 12 - "SavingsGoalDao"
Cohesion: 0.16
Nodes (8): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity, toDomain(), toEntity()

### Community 13 - "DashboardScreen"
Cohesion: 0.13
Nodes (17): SavingsGoal, List, Long, Modifier, NumberFormat, String, Unit, money() (+9 more)

### Community 14 - "MovementProcessorService"
Cohesion: 0.22
Nodes (6): Int, Intent, Notification, MovementProcessorService, IBinder, Service

### Community 15 - "BankParser"
Cohesion: 0.06
Nodes (64): Movement, EmptyState(), FinanceCard(), FinanceTag(), IconBadge(), Color, ImageVector, Modifier (+56 more)

### Community 16 - "BankParserTest"
Cohesion: 0.27
Nodes (7): Boolean, Int, List, Long, StateFlow, ViewModel, MovementViewModel

### Community 17 - "BootReceiver"
Cohesion: 0.33
Nodes (4): BootReceiver, Context, Intent, BroadcastReceiver

### Community 18 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 24 - "SettingsViewModel"
Cohesion: 0.18
Nodes (8): Boolean, Int, String, ViewModel, SettingsViewModel, JSONArray, JSONObject, kotlinx

### Community 26 - "Contexto del proyecto — App de Contabilidad Financiera Automática (Colombia)"
Cohesion: 0.22
Nodes (8): Arquitectura interna (dentro de la app Android), Contexto del proyecto — App de Contabilidad Financiera Automática (Colombia), Convenciones de código, Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente), Entidades bancarias soportadas en el MVP, Qué es, Qué NO hacer, Sincronizacion con la web

### Community 28 - "AppNavHost.kt"
Cohesion: 0.22
Nodes (12): Agenda, Budgets, Dashboard, databaseViewModel(), String, Login, Movements, navigateTo() (+4 more)

### Community 29 - "SessionViewModel"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 30 - "MainActivity"
Cohesion: 0.25
Nodes (5): MainActivity, FinanzasAutomaticaTheme(), Boolean, Bundle, ComponentActivity

## Knowledge Gaps
- **85 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+80 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `RawMovement` connect `NotificationCaptureService` to `.buildRawMovement`, `.classify`?**
  _High betweenness centrality (0.179) - this node is a cross-community bridge._
- **Why does `MovementEntity` connect `MovementEntity` to `NotificationCaptureService`, `SavingsGoalDao`, `.classify`?**
  _High betweenness centrality (0.175) - this node is a cross-community bridge._
- **Why does `toEntity()` connect `SavingsGoalDao` to `MovementEntity`, `BudgetDao`, `AgendaEntryEntity`?**
  _High betweenness centrality (0.160) - this node is a cross-community bridge._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _85 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `.buildRawMovement` be split into smaller, more focused modules?**
  _Cohesion score 0.05370843989769821 - nodes in this community are weakly interconnected._
- **Should `MovementEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.07565392354124749 - nodes in this community are weakly interconnected._
- **Should `CategoryEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.08418367346938775 - nodes in this community are weakly interconnected._