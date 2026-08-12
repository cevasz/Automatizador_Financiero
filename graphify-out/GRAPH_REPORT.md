# Graph Report - .  (2026-08-12)

## Corpus Check
- 11 files · ~35,631 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 869 nodes · 1500 edges · 62 communities (39 shown, 23 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 58 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- CategoryEntity
- MovementEntity
- .buildRawMovement()
- AgendaEntryEntity
- InvoiceViewModel
- SettingsViewModel
- FinanzasDatabase
- SavingsGoalsViewModel
- DashboardScreen()
- AppNavHost.kt
- MovementsListScreen()
- ClassificationRuleEntity
- MovementViewModel
- BudgetDao
- InvoiceDao
- SavingsGoalDao
- IconBadge()
- AgendaViewModel
- NotificationCaptureService
- Guía del proyecto (resumen operativo)
- Converters
- .parseLine()
- BudgetCard()
- MovementProcessorService
- Category
- BudgetsViewModel
- SessionViewModel
- PaymentMethod
- ParserRegistry
- Contexto del proyecto — App de Contab...
- SettingRow()
- BankParserTest
- BankEntity
- AgendaOrigin
- ConfirmationState
- Movement.kt
- BankParser
- BootReceiver
- .extractText()
- .fromInstant()
- gradlew
- toDomain()
- StatementImporterTest
- SimpleTest
- graphify.md
- graphify.md
- build.gradle.kts
- Context
- ImageVector
- Color.kt
- Type.kt
- Notification
- build.gradle.kts
- com
- Color de tema
- Paquete com
- Iconos ImageVector

## God Nodes (most connected - your core abstractions)
1. `MovementEntity` - 37 edges
2. `MovementDao` - 30 edges
3. `CategoryEntity` - 26 edges
4. `MovementRepositoryImpl` - 26 edges
5. `SettingsViewModel` - 25 edges
6. `AgendaEntryEntity` - 23 edges
7. `MovementViewModel` - 16 edges
8. `Converters` - 15 edges
9. `AgendaViewModel` - 15 edges
10. `InvoiceViewModel` - 15 edges

## Surprising Connections (you probably didn't know these)
- `Extracto Bancolombia (fixture PDF de prueba)` --conceptually_related_to--> `pdfbox-android (extracción de texto de PDF)`  [INFERRED]
  app/src/test/resources/fixtures/extracto_bancolombia.pdf → docs/guia.md
- `Extracto Bancolombia (fixture PDF de prueba)` --shares_data_with--> `RawMovement (salida normalizada)`  [INFERRED]
  app/src/test/resources/fixtures/extracto_bancolombia.pdf → docs/guia.md
- `toDomain()` --references--> `Movement`  [EXTRACTED]
  app/src/main/java/com/finanzas/automatica/domain/enrichment/ModelMappers.kt → app/src/main/java/com/finanzas/automatica/domain/model/Movement.kt
- `SavingsGoalCard()` --calls--> `FinanceCard()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/ui/screen/SavingsGoalsScreen.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/components/FinanceUi.kt
- `SavingsGoalCard()` --calls--> `FinanceTag()`  [INFERRED]
  app/src/main/java/com/finanzas/automatica/presentation/ui/screen/SavingsGoalsScreen.kt → app/src/main/java/com/finanzas/automatica/presentation/ui/components/FinanceUi.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Pipeline de captura por notificaciones (NotificationListenerService + BankParser + RawMovement + Room)** — docs_guia_notificationlistenerservice, docs_guia_bankparser, docs_guia_rawmovement, docs_guia_persistencia_en_room [EXTRACTED 1.00]
- **Flujo del permiso de notificaciones (acceso manual + NotificationAccess.isEnabled + pantalla de ajustes)** — docs_guia_acceso_a_notificaciones_manual, docs_guia_notificationaccess_isenabled, docs_guia_pantalla_de_ajustes_de_notificaciones [EXTRACTED 1.00]

## Communities (62 total, 23 thin omitted)

### Community 0 - "CategoryEntity"
Cohesion: 0.05
Nodes (53): toDomain(), toEntity(), AgendaEntry, Category, Budget, EmptyState(), FinanceCard(), FinanceTag() (+45 more)

### Community 1 - "MovementEntity"
Cohesion: 0.08
Nodes (15): CategoryTotal, Flow, Int, List, Long, String, MonthlyTotal, MovementDao (+7 more)

### Community 2 - ".buildRawMovement()"
Cohesion: 0.06
Nodes (37): AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, ClassificationRule, Failure, ParseResult, Success (+29 more)

### Community 3 - "AgendaEntryEntity"
Cohesion: 0.07
Nodes (18): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepositoryImpl (+10 more)

### Community 4 - "InvoiceViewModel"
Cohesion: 0.07
Nodes (34): InvoiceRepository, Boolean, Flow, List, Long, String, DebtStatus, PAID (+26 more)

### Community 5 - "SettingsViewModel"
Cohesion: 0.08
Nodes (23): CategoryDao, Flow, Int, List, Long, String, CategoryEntity, ClassificationEngine (+15 more)

### Community 6 - "FinanzasDatabase"
Cohesion: 0.09
Nodes (21): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity (+13 more)

### Community 7 - "SavingsGoalsViewModel"
Cohesion: 0.08
Nodes (29): Agenda, AppNavHost(), Budgets, Dashboard, databaseViewModel(), Invoices, Login, Movements (+21 more)

### Community 8 - "DashboardScreen()"
Cohesion: 0.18
Nodes (8): Boolean, Int, String, ViewModel, SettingsViewModel, JSONArray, JSONObject, kotlinx

### Community 9 - "AppNavHost.kt"
Cohesion: 0.08
Nodes (15): AgendaDao, FinanzasDatabase, getInstance(), DefaultCategories, List, Long, FinanzasApplication, Application (+7 more)

### Community 10 - "MovementsListScreen()"
Cohesion: 0.13
Nodes (17): SavingsGoal, List, Long, Modifier, NumberFormat, String, Unit, money() (+9 more)

### Community 11 - "ClassificationRuleEntity"
Cohesion: 0.16
Nodes (23): cleanEnum(), DashboardPeriod, Day, Month, Week, DashboardScreen(), Boolean, Color (+15 more)

### Community 12 - "MovementViewModel"
Cohesion: 0.18
Nodes (20): cleanEnum(), fromRoute(), ImportStatementDialog(), List, Long, Modifier, Movement, NumberFormat (+12 more)

### Community 13 - "BudgetDao"
Cohesion: 0.11
Nodes (21): Formato CSV del extracto (fecha, descripción, monto), Extracto Bancolombia (fixture PDF de prueba), Permiso de acceso a notificaciones habilitado manualmente, Agenda (cruce de comercios/cuentas), Android nativo en Kotlin, BankParser (uno por banco), BootReceiver con NotificationListenerService.requestRebind, Captura de movimientos por notificaciones (+13 more)

### Community 14 - "InvoiceDao"
Cohesion: 0.18
Nodes (11): Boolean, Int, List, Long, Movement, String, MovementViewModel, BankEntity (+3 more)

### Community 15 - "SavingsGoalDao"
Cohesion: 0.23
Nodes (6): BudgetDao, Flow, Int, List, Long, BudgetEntity

### Community 16 - "IconBadge()"
Cohesion: 0.16
Nodes (8): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, InvoiceEntity, InvoiceItemEntity

### Community 17 - "AgendaViewModel"
Cohesion: 0.22
Nodes (6): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity

### Community 18 - "NotificationCaptureService"
Cohesion: 0.17
Nodes (8): BootReceiver, Context, Boolean, Context, String, NotificationAccess, BroadcastReceiver, Intent

### Community 19 - "Guía del proyecto (resumen operativo)"
Cohesion: 0.25
Nodes (8): ImportSummary, BankEntity, Instant, Long, PaymentMethod, String, StatementImporter, RawMovement

### Community 20 - "Converters"
Cohesion: 0.20
Nodes (6): String, NotificationCaptureService, EnrichmentPipeline, Notification, NotificationListenerService, StatusBarNotification

### Community 21 - ".parseLine()"
Cohesion: 0.22
Nodes (7): Converters, String, MovementSource, MANUAL, NOTIFICATION, OCR, OPEN_FINANCE

### Community 22 - "BudgetCard()"
Cohesion: 0.22
Nodes (6): Int, Intent, Notification, MovementProcessorService, IBinder, Service

### Community 23 - "MovementProcessorService"
Cohesion: 0.29
Nodes (5): StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 24 - "Category"
Cohesion: 0.20
Nodes (10): PaymentMethod, BANCOLOMBIA, CASH, DAVIPLATA, LULO, NEQUI, NU, OTHER (+2 more)

### Community 25 - "BudgetsViewModel"
Cohesion: 0.27
Nodes (6): createDefault(), Boolean, List, ParseResult, String, ParserRegistry

### Community 27 - "PaymentMethod"
Cohesion: 0.20
Nodes (9): Arquitectura interna (dentro de la app Android), Contexto del proyecto — App de Contabilidad Financiera Automática (Colombia), Convenciones de código, Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente), Documentos vivos, Entidades bancarias soportadas en el MVP, Qué es, Qué NO hacer (+1 more)

### Community 28 - "ParserRegistry"
Cohesion: 0.25
Nodes (7): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN

### Community 29 - "Contexto del proyecto — App de Contab..."
Cohesion: 0.29
Nodes (4): AgendaOrigin, AUTO_DETECTED, COMMUNITY_SUGGESTED, MANUAL

### Community 30 - "SettingRow()"
Cohesion: 0.29
Nodes (5): ConfirmationState, AUTO_CONFIRMED, CONFIRMED, PENDING, REJECTED

### Community 31 - "BankParserTest"
Cohesion: 0.29
Nodes (4): Movement, MovementType, EXPENSE, INCOME

### Community 32 - "BankEntity"
Cohesion: 0.38
Nodes (4): BankParser, Boolean, List, String

### Community 34 - "ConfirmationState"
Cohesion: 0.40
Nodes (3): ByteArray, String, PdfStatementExtractor

### Community 36 - "BankParser"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Ambiguous Edges - Review These
- `Sesión web del panel (correo, URL del backend, token de acceso)` → `Regla de limpieza (solo documentación viva y artefactos útiles)`  [AMBIGUOUS]
  docs/guia.md · relation: conceptually_related_to

## Knowledge Gaps
- **67 isolated node(s):** `AGENDA`, `RULES`, `KEYWORDS`, `HISTORY`, `UNKNOWN` (+62 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **23 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Sesión web del panel (correo, URL del backend, token de acceso)` and `Regla de limpieza (solo documentación viva y artefactos útiles)`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **Why does `MovementEntity` connect `MovementEntity` to `CategoryEntity`, `SettingsViewModel`, `FinanzasDatabase`?**
  _High betweenness centrality (0.134) - this node is a cross-community bridge._
- **Why does `FinanzasDatabase` connect `AppNavHost.kt` to `IconBadge()`?**
  _High betweenness centrality (0.120) - this node is a cross-community bridge._
- **Why does `toEntity()` connect `CategoryEntity` to `MovementEntity`, `AgendaEntryEntity`, `AgendaViewModel`, `SavingsGoalDao`?**
  _High betweenness centrality (0.112) - this node is a cross-community bridge._
- **What connects `AGENDA`, `RULES`, `KEYWORDS` to the rest of the system?**
  _67 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `CategoryEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.05060882800608828 - nodes in this community are weakly interconnected._
- **Should `MovementEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.07565392354124749 - nodes in this community are weakly interconnected._