# Graph Report - /home/sebas/Documentos/New  (2026-07-15)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 489 nodes · 837 edges · 24 communities (23 shown, 1 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 30 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `c27791c6`
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

## God Nodes (most connected - your core abstractions)
1. `MovementEntity` - 30 edges
2. `MovementDao` - 26 edges
3. `CategoryEntity` - 24 edges
4. `AgendaEntryEntity` - 22 edges
5. `MovementRepositoryImpl` - 21 edges
6. `Converters` - 15 edges
7. `CategoryDao` - 13 edges
8. `BaseBankParser` - 13 edges
9. `FinanzasDatabase` - 12 edges
10. `AgendaDao` - 12 edges

## Surprising Connections (you probably didn't know these)
- `AgendaRepositoryImpl` --implements--> `AgendaRepository`  [EXTRACTED]
  app/src/main/java/com/finanzas/automatica/data/repository/Repositories.kt → app/src/main/java/com/finanzas/automatica/domain/enrichment/AgendaRepository.kt
- `createDefault()` --calls--> `BancolombiaParser`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/domain/parser/ParserRegistry.kt → app/src/main/java/com/finanzas/automatica/domain/parser/BancolombiaParser.kt
- `BaseBankParser` --implements--> `BankParser`  [EXTRACTED]
  app/src/main/java/com/finanzas/automatica/domain/parser/BaseBankParser.kt → app/src/main/java/com/finanzas/automatica/domain/parser/BankParser.kt
- `createDefault()` --calls--> `DaviplataParser`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/domain/parser/ParserRegistry.kt → app/src/main/java/com/finanzas/automatica/domain/parser/DaviplataParser.kt
- `createDefault()` --calls--> `LuloParser`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/domain/parser/ParserRegistry.kt → app/src/main/java/com/finanzas/automatica/domain/parser/LuloParser.kt

## Import Cycles
- None detected.

## Communities (24 total, 1 thin omitted)

### Community 0 - ".buildRawMovement"
Cohesion: 0.06
Nodes (40): AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, Budget, ClassificationRule, Failure, ParseResult (+32 more)

### Community 1 - "MovementEntity"
Cohesion: 0.08
Nodes (15): CategoryTotal, Flow, Int, List, Long, String, MonthlyTotal, MovementDao (+7 more)

### Community 2 - "CategoryEntity"
Cohesion: 0.10
Nodes (13): CategoryDao, Flow, Int, List, Long, String, CategoryEntity, AgendaRepositoryImpl (+5 more)

### Community 3 - "PaymentMethod"
Cohesion: 0.06
Nodes (35): AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL, BankEntity, BANCOLOMBIA, DAVIPLATA, LULO (+27 more)

### Community 4 - "NotificationCaptureService"
Cohesion: 0.09
Nodes (17): EnrichmentPipeline, Boolean, Double, toDomain(), AgendaEntry, AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED (+9 more)

### Community 5 - "AgendaEntryEntity"
Cohesion: 0.11
Nodes (12): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+4 more)

### Community 6 - "PaymentMethod"
Cohesion: 0.06
Nodes (31): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN, ConfirmationState (+23 more)

### Community 7 - ".classify"
Cohesion: 0.12
Nodes (16): ClassificationEngine, ClassificationRuleRepository, DefaultClassificationEngine, KeywordMatch, KeywordRepository, Flow, List, Long (+8 more)

### Community 8 - "Converters"
Cohesion: 0.15
Nodes (10): AgendaOrigin, Converters, BankEntity, Instant, Long, MovementType, PaymentMethod, String (+2 more)

### Community 9 - "ClassificationRuleDao"
Cohesion: 0.20
Nodes (8): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity

### Community 10 - "FinanzasDatabase"
Cohesion: 0.12
Nodes (9): FinanzasDatabase, getInstance(), Context, DefaultCategories, List, Long, FinanzasApplication, Application (+1 more)

### Community 11 - "BudgetDao"
Cohesion: 0.24
Nodes (6): BudgetDao, Flow, Int, List, Long, BudgetEntity

### Community 12 - "SavingsGoalDao"
Cohesion: 0.22
Nodes (6): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity

### Community 13 - "DashboardScreen"
Cohesion: 0.16
Nodes (13): MainActivity, DashboardScreen(), Boolean, String, MovementItem(), PendingItem(), SummaryCard(), FinanzasAutomaticaTheme() (+5 more)

### Community 14 - "MovementProcessorService"
Cohesion: 0.22
Nodes (6): Int, Intent, Notification, MovementProcessorService, IBinder, Service

### Community 15 - "BankParser"
Cohesion: 0.32
Nodes (5): BankParser, BankEntity, Boolean, List, String

### Community 17 - "BootReceiver"
Cohesion: 0.33
Nodes (4): BootReceiver, Context, Intent, BroadcastReceiver

### Community 18 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **73 isolated node(s):** `INCOME`, `EXPENSE`, `NEQUI`, `BANCOLOMBIA`, `DAVIPLATA` (+68 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `RawMovement` connect `NotificationCaptureService` to `.buildRawMovement`, `.classify`?**
  _High betweenness centrality (0.178) - this node is a cross-community bridge._
- **Why does `FinanzasDatabase` connect `FinanzasDatabase` to `CategoryEntity`, `SavingsGoalDao`, `AgendaEntryEntity`?**
  _High betweenness centrality (0.156) - this node is a cross-community bridge._
- **Why does `MovementEntity` connect `MovementEntity` to `NotificationCaptureService`?**
  _High betweenness centrality (0.123) - this node is a cross-community bridge._
- **What connects `INCOME`, `EXPENSE`, `NEQUI` to the rest of the system?**
  _73 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `.buildRawMovement` be split into smaller, more focused modules?**
  _Cohesion score 0.055178652193577565 - nodes in this community are weakly interconnected._
- **Should `MovementEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.08461131676361713 - nodes in this community are weakly interconnected._
- **Should `CategoryEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.09966777408637874 - nodes in this community are weakly interconnected._