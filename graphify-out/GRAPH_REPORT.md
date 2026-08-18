# Graph Report - New  (2026-08-18)

## Corpus Check
- 156 files · ~157,814 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1824 nodes · 2978 edges · 298 communities (85 shown, 213 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 164 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `940649f3`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

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
- Ilustracion: bloqueo biometrico
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
- SavingsGoalsScreen.SavingsGoal
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
- SettingsScreen.Triple
- Tema: tipografia
- MovementViewModel.BankEntity
- MovementViewModel (Int)
- MovementViewModel.StateFlow
- MovementViewModel.ViewModel
- Ilustracion: bloqueo biometrico
- Ilustracion: meta de ahorro
- Fixtures Daviplata
- Fixtures Lulo Bank
- Fixtures Nequi
- Fixtures Nu
- Gradle: proyecto raiz
- Gradle: settings
- Movement (tipo generico)
- Unit (tipo generico)
- Fixtures Lulo Bank
- Fixtures Nequi
- Fixtures Nu
- FinanzasMigrations.kt
- next.config.mjs
- Graphify Integration Rule
- Graphify Workflow
- backend/ (módulo, pendiente)
- BankParser (convención)
- Convención: tests unitarios de BankParser con fixtures reales
- Daviplata
- Política: no construir backend/web/sync hasta validar MVP local
- docs/ (documentación viva)
- Modelo de negocio: núcleo gratuito e ilimitado
- graphify-out/ (mapa de dependencias)
- Flujo de contextualización vía graphify query
- Kivo
- kivo-android/ (módulo)
- Decisión: motor de reglas/regex, sin LLM, local-first
- Lulo Bank
- CLAUDE.md como archivo de reglas y punteros
- Nequi
- Política: no usar Accessibility API para acciones automáticas
- Política: nunca pedir credenciales bancarias
- Nu
- web/ (módulo, pendiente)
- Agenda
- app_notifications (centro de notificaciones in-app)
- BankParser
- BiometricAccess
- BiometricLockGate
- BiometricPrompt
- Regla de limpieza del repo
- Decreto 0368 de 2026 (en guía)
- docs/guia.md
- Kivo (identidad)
- Ley 1581 de 2012 (en guía)
- NotificationListenerService
- Importación manual de extractos (CSV/texto/PDF)
- pdfbox-android
- Permiso de notificaciones
- RawMovement
- Persistencia en Room
- Sincronización con la web
- Celebración peak-end al completar meta de ahorro
- Fix: excepciones sin atrapar en CoroutineScopes tumbaban toda la app al abrir
- Crear/editar presupuesto desde la UI (AddEditBudgetScreen)
- PENDIENTES.md
- Rediseño FinanceCard (esquinas/sombra) e IconBadge (44dp)
- Fondo del splash: curvas topográficas en Canvas (reemplaza JPG generado)
- 5 ilustraciones de marca conectadas (antes sin usar)
- Integración oficial con Sistema de Finanzas Abiertas (Decreto 0368)
- iOS vía Share Extension
- Fix: movimientos duplicados por SMS+correo (mismo banco/tipo/monto en 5 min)
- Navegación redistribuida (barra inferior vs menú lateral)
- Paleta "Barro & Ocre" reemplaza "Kivo Coral"
- Panel web completo (requiere backend)
- Bug: paquetes de banco declarados no coincidían con apps reales
- Crash al ingresar contraseña correcta de PDF (catch ampliado a Throwable)
- Extractos PDF protegidos con contraseña (requiresPassword/extractText)
- Cámara y biometría revisados: consentimiento ya correcto, sin cambios
- Permisos solicitados gradualmente (POST_NOTIFICATIONS en el momento justo)
- POST_NOTIFICATIONS nunca se pedía en tiempo de ejecución (fix)
- Reactividad Agenda/Presupuestos/Metas convertida a Flow
- Skill mobile-app-ui-design instalado y adaptado a Compose
- Sugerencia proactiva de agenda para número repetido
- Temas exclusivos (tema "fundador" diferenciado de Barro & Ocre)
- Versión mostrada en Ajustes ligada a BuildConfig (antes fija 1.0.0)
- Versionado: subir versionCode/versionName por cambio funcional
- SDD_App_Finanzas.docx (fuente original)
- Aplicación móvil (Android)
- Aprendizaje comunitario opt-in de la agenda
- Backend / API
- Capas premium no esenciales (9.2)
- Captura automática de movimientos (6.1)
- Clasificación automática (6.3)
- Confirmación ligera de movimientos (6.10)
- Cumplimiento normativo (8.2)
- Dashboard y cronología (6.4)
- Decreto 0368 de 2026 (Finanzas Abiertas)
- docs/SDD.md
- Exportación (6.9)
- Fase 0 — Validación
- Fase 1 (MVP)
- Fase 2 — Robustecimiento
- Fase 3 — Multiplataforma
- Fase 4 — Open Finance
- Huella financiera generativa
- Ley 1581 de 2012 (Habeas Data)
- Metas de ahorro (6.5)
- Métricas de éxito del MVP (13)
- Modelo de datos (entidades principales)
- Modelo de sostenibilidad y financiación (9)
- Movimiento crudo (objeto estandarizado)
- Fuente 1 — NotificationListenerService
- Número de fundador
- Fuente 3 — OCR
- Fuente 5 — Open Finance
- Panel web
- Fuente 4 — Parseo de correos
- Presupuestos por categoría (6.6)
- Próximos pasos (14)
- Restricciones de plataforma iOS
- Resumen del año enriquecido
- Riesgos y mitigaciones (12)
- Seguridad y privacidad (8.1)
- Fuente 2 — Share Extension
- Stack tecnológico propuesto (10)
- Temas exclusivos
- Voto de roadmap
- Prohibición de usar Accessibility API para acciones autónomas
- Agenda financiera
- Alertas y detección de patrones (§6.7)
- Aportes voluntarios con recompensa (§9.1)
- Aprendizaje comunitario opt-in de la agenda financiera (§6.2 refuerzo)
- Principio de captura multi-fuente (§5.2)
- Confirmación ligera de movimientos (patrón swipe)
- Cumplimiento normativo (§8.2)
- Decreto 0368 de 2026 (Sistema de Finanzas Abiertas, Colombia)
- SDD — Aplicación de Contabilidad Financiera Personal Automática (v1.0)
- Fase 0: Validación
- Fase 1 (MVP): Android, reglas + regex, gratis
- Fase 2: Robustecimiento
- Fase 3: Multiplataforma (iOS)
- Fase 4: Open Finance
- Huella financiera generativa (recompensa)
- Ley 1581 de 2012 (habeas data / protección de datos personales)
- Motor de reglas / clasificación (reglas + regex)
- "Movimiento crudo" (objeto interno estandarizado)
- NotificationListenerService (Android)
- Número de fundador (recompensa)
- Escaneo OCR de comprobantes (ML Kit Text Recognition)
- Panel web (análisis, exportación, presupuestos, metas)
- Principios de privacidad por diseño (§8.1)
- Resumen del año enriquecido (recompensa)
- Riesgo: rechazo/restricción en Google Play por permiso de notificaciones
- Riesgo: iOS no permite leer notificaciones de terceros
- Roadmap por fases (§11)
- Share Extension ("compartir hacia la app")
- Sistema de Finanzas Abiertas (Open Finance, Colombia)
- Stack tecnológico propuesto (§10)
- Superintendencia Financiera de Colombia
- Temas exclusivos, incl. tema "fundador" terracota-ocre (recompensa)
- Voto de roadmap (recompensa)

## God Nodes (most connected - your core abstractions)
1. `SyncDao` - 53 edges
2. `MovementEntity` - 48 edges
3. `CategoryEntity` - 40 edges
4. `AppNavHost()` - 36 edges
5. `MovementDao` - 35 edges
6. `AgendaEntryEntity` - 31 edges
7. `MovementRepositoryImpl` - 28 edges
8. `FinanceCard()` - 28 edges
9. `SyncMappers` - 24 edges
10. `SettingsViewModel` - 24 edges

## Surprising Connections (you probably didn't know these)
- `Bancolombia Notification Fixtures` --semantically_similar_to--> `Bancolombia Statement PDF Fixture`  [INFERRED] [semantically similar]
  kivo-android/app/src/test/resources/fixtures/bancolombia_notifications.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `PDFBox Glyph List (vendored resource tables)` --shares_data_with--> `Bancolombia Statement PDF Fixture`  [INFERRED]
  kivo-android/app/src/test/resources/com/tom_roush/pdfbox/resources/glyphlist/glyphlist.txt → kivo-android/app/src/test/resources/fixtures/extracto_bancolombia.pdf
- `createDefault()` --calls--> `BancolombiaParser`  [INFERRED]
  kivo-android/app/src/main/java/com/finanzas/automatica/domain/parser/ParserRegistry.kt → kivo-android/app/src/main/java/com/finanzas/automatica/domain/parser/BancolombiaParser.kt
- `createDefault()` --calls--> `DaviplataParser`  [INFERRED]
  kivo-android/app/src/main/java/com/finanzas/automatica/domain/parser/ParserRegistry.kt → kivo-android/app/src/main/java/com/finanzas/automatica/domain/parser/DaviplataParser.kt
- `createDefault()` --calls--> `LuloParser`  [INFERRED]
  kivo-android/app/src/main/java/com/finanzas/automatica/domain/parser/ParserRegistry.kt → kivo-android/app/src/main/java/com/finanzas/automatica/domain/parser/LuloParser.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Sesión 2026-08-15: duplicados multicanal + crashes de CoroutineScope** — docs_pendientes_movimientos_duplicados_multicanal, docs_pendientes_crashes_coroutinescope_sin_manejador, kivo_android_app_src_main_java_com_finanzas_automatica_domain_enrichment_enrichmentpipeline_enrichmentpipeline_process [INFERRED 0.85]
- **Auditoría de AppNavHost: botones huérfanos y reactividad** — docs_pendientes_boton_agenda_agregar_contacto, docs_pendientes_boton_movimientos_detalle, docs_pendientes_reactividad_agenda_presupuestos_metas [EXTRACTED 1.00]
- **Sesión 2026-08-15: PDF con contraseña + bug de biometría + versión en app** — docs_pendientes_pdf_password, docs_pendientes_biometria_reset_navegacion, docs_pendientes_version_en_app [EXTRACTED 1.00]
- **Kivo CI Build Pipeline (GitHub Actions)** — github_workflows_build_workflow, github_workflows_build_run_unit_tests_step, github_workflows_build_assembledebug_step, github_workflows_build_upload_apk_step [EXTRACTED 1.00]
- **SDD Data Model Entities (§7)** — graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_usuario, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_movimiento, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_agenda, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_categoria, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_presupuesto, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_meta_ahorro, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_regla_clasificacion, graphify_out_converted_sdd_app_finanzas_94a76240_modelo_datos_aporte_voluntario [EXTRACTED 1.00]

## Communities (298 total, 213 thin omitted)

### Community 0 - "Dashboard & Database Core"
Cohesion: 0.22
Nodes (19): cleanEnum(), DashboardScreen(), Boolean, Color, ImageVector, Int, List, Long (+11 more)

### Community 1 - "Bank Parsers & Raw Movement Model"
Cohesion: 0.06
Nodes (15): AgendaDao, Flow, Int, List, Long, String, AgendaEntryEntity, AgendaRepository (+7 more)

### Community 2 - "Android/Kotlin Common Types"
Cohesion: 0.11
Nodes (15): AppThemePalette, FOREST_GREEN, KIVO_CORAL, MIDNIGHT_BLUE, OCEAN_TEAL, SUNSET_AMBER, FinanzasAutomaticaTheme(), ThemeMode (+7 more)

### Community 3 - "Agenda Data Access"
Cohesion: 0.31
Nodes (5): createDefault(), Boolean, List, String, ParserRegistry

### Community 4 - "Invoice/Debt Data Access"
Cohesion: 0.15
Nodes (10): AgendaViewModel, AgendaEntry, Boolean, Category, List, Long, MovementType, StateFlow (+2 more)

### Community 5 - "Movement Data Access (DAO)"
Cohesion: 0.09
Nodes (23): CategoryLookupRepository, ClassificationEngine, ClassificationRepositoryProvider, ClassificationRuleRepository, DefaultClassificationEngine, DefaultKeywordRepository, KeywordMatch, KeywordRepository (+15 more)

### Community 6 - "In-App Notification DAO"
Cohesion: 0.07
Nodes (15): CategoryTotal, Flow, Int, List, Long, String, MonthlyTotal, MovementDao (+7 more)

### Community 7 - "Budget Data Access"
Cohesion: 0.22
Nodes (7): Bundle, Class, BiometricSettingsViewModelFactory, androidx, FragmentActivity, MainActivity, T

### Community 8 - "Movement Repository"
Cohesion: 0.23
Nodes (4): AppNotificationDao, Flow, Int, List

### Community 9 - "Bank Statement Importer"
Cohesion: 0.20
Nodes (6): Flow, Int, List, Long, SavingsGoalDao, SavingsGoalEntity

### Community 10 - "UI Motion & Animation"
Cohesion: 0.28
Nodes (6): InputImage, ImageTextRecognizer, Bitmap, Context, String, Uri

### Community 12 - "Movement Entity & Classification Rule Repo"
Cohesion: 0.05
Nodes (69): csv(), GET(), Modo, metadata, abonarMeta(), actualizarMovimiento(), ahora(), borrarContacto() (+61 more)

### Community 13 - "Category Data Access"
Cohesion: 0.15
Nodes (13): budgetKey(), BudgetsViewModel, Boolean, Budget, Category, Int, List, Long (+5 more)

### Community 14 - "Classification Rule DAO"
Cohesion: 0.06
Nodes (13): InvoiceDao, InvoiceWithItemsRelation, Flow, List, Long, String, Int, List (+5 more)

### Community 15 - "Enrichment Pipeline & Agenda/Category Models"
Cohesion: 0.14
Nodes (27): cleanEnum(), fromRoute(), ImportStatementDialog(), Boolean, Category, List, Long, Modifier (+19 more)

### Community 16 - "Budgets ViewModel"
Cohesion: 0.15
Nodes (21): Agenda, agendaEditRoute(), AppNavHost(), budgetEditRoute(), Budgets, Categories, Dashboard, databaseViewModel() (+13 more)

### Community 17 - "Movements List Screen"
Cohesion: 0.07
Nodes (25): InvoiceRepository, Boolean, Flow, List, Long, String, List, Pair (+17 more)

### Community 18 - "Docs: Brand & Core Concepts Overview"
Cohesion: 0.11
Nodes (3): BankParserTest, List, String

### Community 19 - "Savings Goal Data Access"
Cohesion: 0.12
Nodes (15): D, BudgetDao, Flow, Int, List, Long, BudgetEntity, AgendaEntry (+7 more)

### Community 20 - "Bank Parser Tests"
Cohesion: 0.05
Nodes (37): 10. Stack tecnológico propuesto, 11. Roadmap por fases, 12. Riesgos y mitigaciones, 13. Métricas de éxito del MVP, 14. Próximos pasos, 1. Resumen ejecutivo, 2.1 El problema, 2.2 Contexto regulatorio (Colombia, 2026) (+29 more)

### Community 21 - "Repo Structure & CI/Graphify Integration"
Cohesion: 0.06
Nodes (23): CategoryDao, CategoryUsage, Flow, Int, List, Long, String, CategoryEntity (+15 more)

### Community 22 - "SDD: Módulos Pendientes (Alertas, Confirmación)"
Cohesion: 0.20
Nodes (8): Boolean, Category, Int, Long, Movement, StateFlow, ViewModel, MovementViewModel

### Community 25 - "Movement Processor Service"
Cohesion: 0.05
Nodes (36): ConfirmationState, Converters, Instant, Long, MovementSource, MovementType, String, AgendaEntry (+28 more)

### Community 26 - "Room Type Converters"
Cohesion: 0.29
Nodes (6): Despliegue, Estructura, Kivo Web — panel, Puesta en marcha, Reglas al tocar este codigo, Stack

### Community 27 - "App Navigation Routes"
Cohesion: 0.20
Nodes (13): androidx, ImageVector, Int, List, Long, Modifier, String, Triple (+5 more)

### Community 28 - "Notification Capture Service"
Cohesion: 0.22
Nodes (6): IBinder, Int, Intent, Notification, MovementProcessorService, Service

### Community 29 - "Web Session ViewModel"
Cohesion: 0.38
Nodes (5): Instant, Long, MovementSource, String, StatementImporter

### Community 30 - "SDD: Módulos Core (Agenda, Captura, Clasificación)"
Cohesion: 0.22
Nodes (6): Boolean, Int, String, ViewModel, SettingsViewModel, kotlinx

### Community 31 - "Classification Engine Support"
Cohesion: 0.06
Nodes (30): next, react, react-dom, @supabase/ssr, @supabase/supabase-js, @types/node, @types/react, @types/react-dom (+22 more)

### Community 33 - "Enrichment Pipeline Logic"
Cohesion: 0.21
Nodes (7): Boolean, List, Long, SavingsGoal, StateFlow, ViewModel, SavingsGoalsViewModel

### Community 34 - "Payment Method Enum"
Cohesion: 0.21
Nodes (6): Boolean, StateFlow, String, ViewModel, SessionState, SessionViewModel

### Community 35 - "Contexto Regulatorio y Roadmap"
Cohesion: 0.26
Nodes (11): on_auth_user_created, public.agenda_entries, public.budgets, public.categories, public.classification_rules, public.invoice_items, public.invoices, public.kivo_handle_new_user() (+3 more)

### Community 36 - "Notification Access Permission"
Cohesion: 0.22
Nodes (10): AgendaSource, AUTO_LEARNED, COMMUNITY_SUGGESTED, MANUAL, Budget, ClassificationRule, Failure, ParseResult (+2 more)

### Community 38 - "Modelo de Sostenibilidad"
Cohesion: 0.06
Nodes (31): Botones que además se arreglaron en esta pasada (auditoría completa de `AppNavHost`), Correcciones que conviene hacer pronto, Cómo ponerlo en marcha, 🟡 Deuda técnica / funcionalidad incompleta, Estado de la lista (2026-08-18), 🟢 Fase 1 (MVP) — entregables del propio alcance aún sin terminar, 🔵 Fase 2 (Robustecimiento) — sin iniciar, ⏳ Fase 3-4 (futuro explícito — no iniciar sin discutirlo, per [[CLAUDE]]) (+23 more)

### Community 39 - "Colombian Amount Parser Tests"
Cohesion: 0.09
Nodes (42): Dp, FontWeight, AnimatedAmountText(), appearFromBelow(), Color, Float, Int, Long (+34 more)

### Community 40 - "Confirmation State Enum"
Cohesion: 0.33
Nodes (4): Boolean, Context, String, NotificationAccess

### Community 43 - "Boot Receiver"
Cohesion: 0.27
Nodes (7): EnrichmentPipeline, Boolean, Category, Double, toDomain(), EnrichedMovement, RawMovement

### Community 44 - "Agenda Origin Enum"
Cohesion: 0.23
Nodes (9): BankEntity, BANCOLOMBIA, DAVIPLATA, LULO, NEQUI, NU, UNKNOWN, ByteArray (+1 more)

### Community 45 - "Notification Access Tests"
Cohesion: 0.29
Nodes (6): Boolean, JsonObject, Long, String, putNullable(), SyncMappers

### Community 49 - "Biometric Availability Check"
Cohesion: 0.21
Nodes (5): Notification, String, NotificationCaptureService, NotificationListenerService, StatusBarNotification

### Community 50 - "Kivo Brand Identity & Colors"
Cohesion: 0.33
Nodes (4): Boolean, ByteArray, String, PdfStatementExtractor

### Community 51 - "Instant/Long Conversions"
Cohesion: 0.07
Nodes (29): dom, dom.iterable, esnext, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules, ./src/* (+21 more)

### Community 52 - "Statement Importer Tests"
Cohesion: 0.38
Nodes (7): Dollar Coin Icons, Onboarding Security Illustration, Onboarding Security/Privacy Screen Purpose, Rising Bar Chart with Trend Arrow, Security Shield with Padlock, Smartphone Finance Dashboard Mockup, Teal/Coral/Cream Color Palette

### Community 53 - "Gradle Wrapper Script"
Cohesion: 0.33
Nodes (4): BroadcastReceiver, BootReceiver, Context, Intent

### Community 59 - "PDFBox Vendored Resources"
Cohesion: 0.40
Nodes (3): ColombianAmountParser, Long, String

### Community 60 - "Bancolombia Fixtures & PDFBox Data"
Cohesion: 0.40
Nodes (3): BiometricAccess, Boolean, Context

### Community 62 - "Double Type"
Cohesion: 0.67
Nodes (3): Coral and Pizarra Color Palette, Kivo Brand Icon (K Monogram), Kivo Brand Identity

### Community 65 - "ByteArray Type"
Cohesion: 0.33
Nodes (9): drawContourCluster(), KivoLogo(), Color, Float, Int, Modifier, SplashScreen(), TopographicBackground() (+1 more)

### Community 67 - "Savings Goal Illustration"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 69 - "Daviplata Notification Fixtures"
Cohesion: 0.38
Nodes (4): BankParser, Boolean, List, String

### Community 70 - "Lulo Notification Fixtures"
Cohesion: 0.67
Nodes (3): Empty State UX Pattern, Empty State Wallet Illustration, Kivo Brand Color Palette

### Community 72 - "Nu Notification Fixtures"
Cohesion: 1.00
Nodes (3): PDFBox Additional Glyph List, PDFBox ZapfDingbats Glyph List, PDFBox Bidi Mirroring Table

### Community 73 - "Root Build Config"
Cohesion: 0.67
Nodes (3): PDFBox Glyph List (vendored resource tables), Bancolombia Notification Fixtures, Bancolombia Statement PDF Fixture

### Community 78 - "Config release pendiente"
Cohesion: 0.06
Nodes (18): Application, Int, List, String, SyncDeletionDao, SyncDeletionEntity, buildDatabase(), FinanzasDatabase (+10 more)

### Community 111 - "LoginScreen (Boolean)"
Cohesion: 0.16
Nodes (14): Exception, AuthTokens, contentOrNullSafe(), Boolean, Int, JsonElement, JsonObject, String (+6 more)

### Community 112 - "MovementsListScreen.Category"
Cohesion: 0.19
Nodes (9): JsonArray, Int, JsonElement, JsonObject, List, Pair, String, SyncEngine (+1 more)

### Community 113 - "MovementsListScreen.Movement"
Cohesion: 0.08
Nodes (27): ClassificationRuleDao, Boolean, Flow, Int, List, Long, String, ClassificationRuleEntity (+19 more)

### Community 114 - "NotificationCenterScreen (androidx)"
Cohesion: 0.40
Nodes (3): ImportSummary, List, Uri

### Community 115 - "SavingsGoalsScreen.SavingsGoal"
Cohesion: 0.18
Nodes (10): Alcance, Arquitectura, Bloqueo biométrico opcional, Contexto regulatorio (Colombia, 2026), Estructura (monorepo), Guía del proyecto — Kivo, Identidad, Panel web y sincronización con la nube (+2 more)

### Community 116 - "Tema: tipografia"
Cohesion: 0.25
Nodes (8): BaseBankParser, Boolean, Double, Instant, Int, Long, MovementType, String

### Community 117 - "MovementViewModel (ByteArray)"
Cohesion: 0.22
Nodes (5): AppNotificationRepository, Flow, Int, List, Long

### Community 118 - "MovementViewModel (Int)"
Cohesion: 0.36
Nodes (10): AddEditAgendaEntryScreen(), AgendaEntryCard(), AgendaScreen(), cleanEnum(), AgendaEntry, Category, List, Modifier (+2 more)

### Community 119 - "Ilustracion: bloqueo biometrico"
Cohesion: 0.24
Nodes (9): CategoriesScreen(), DialogoCategoria(), Boolean, List, Long, Map, Modifier, String (+1 more)

### Community 120 - "Ilustracion: meta de ahorro"
Cohesion: 0.60
Nodes (4): BiometricLockGate(), BiometricLockPrompt(), FragmentActivity, showPrompt()

### Community 121 - "Ilustracion: splash"
Cohesion: 0.19
Nodes (19): Composable, EmptyState(), FinanceCard(), FinanceTag(), IconBadge(), Color, ImageVector, Int (+11 more)

### Community 122 - "Fixtures Daviplata"
Cohesion: 0.22
Nodes (8): Contexto del proyecto — Kivo, Contabilidad Financiera Automática (Colombia), Contexto regulatorio (Colombia, 2026) — condiciona el diseño, no solo el MVP, Convenciones de código, Cómo contextualizarse en este repo (para agentes / sesiones nuevas), Decisiones de alcance ya tomadas (no reabrir sin discutirlo explícitamente), Estructura del repositorio (monorepo), Qué es, Qué NO hacer

### Community 123 - "Fixtures Lulo Bank"
Cohesion: 0.42
Nodes (8): android, AutoRequestPostNotificationsWhenRelevant(), hasAskedPostNotificationsBefore(), isPostNotificationsGranted(), Boolean, Context, markPostNotificationsAsked(), rememberPostNotificationsGranted()

### Community 124 - "Fixtures Nequi"
Cohesion: 0.21
Nodes (15): DebtSummary, Invoice, DebtSummaryCard(), InvoiceItemEditorCard(), InvoiceScreen(), InvoiceTab, DEBTS, INVOICES (+7 more)

### Community 125 - "Fixtures Nu"
Cohesion: 0.22
Nodes (6): Int, List, Long, StateFlow, ViewModel, NotificationCenterViewModel

### Community 126 - "Gradle: proyecto raiz"
Cohesion: 0.36
Nodes (3): Long, String, ModelMappersTest

### Community 128 - "NotificationCenterScreen (androidx)"
Cohesion: 0.29
Nodes (3): Long, AppNotificationEntity, String

### Community 129 - "SavingsGoalsScreen.SavingsGoal"
Cohesion: 0.29
Nodes (6): DatePattern, DatePatternType, DAY_MONTH_YEAR, DMY, TODAY, YESTERDAY

### Community 130 - "SettingsScreen.Color"
Cohesion: 0.38
Nodes (5): CategoryIcons, ImageVector, List, Map, String

### Community 132 - "SettingsScreen.Triple"
Cohesion: 0.29
Nodes (11): Boolean, Color, ImageVector, Modifier, String, Triple, Unit, NotificationAccessRow() (+3 more)

### Community 139 - "Ilustracion: bloqueo biometrico"
Cohesion: 0.50
Nodes (4): DashboardPeriod, Day, Month, Week

### Community 147 - "Movement (tipo generico)"
Cohesion: 0.20
Nodes (9): Archivos, Configuración pendiente en el panel de Supabase, Cómo funciona la sincronización, Dónde viven las credenciales, Estado: desplegado y verificado, Kivo — Backend (Supabase), Por qué Supabase y no un backend propio, Qué NO se sube (+1 more)

## Ambiguous Edges - Review These
- `Onboarding Security Illustration` → `Smartphone Finance Dashboard Mockup`  [AMBIGUOUS]
  kivo-android/app/src/main/res/drawable-nodpi/onboarding_security.jpg · relation: no_login_or_credential_fields_shown

## Knowledge Gaps
- **404 isolated node(s):** `public.profiles`, `public.savings_goals`, `FinanzasMigrations`, `AGENDA`, `RULES` (+399 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **213 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Onboarding Security Illustration` and `Smartphone Finance Dashboard Mockup`?**
  _Edge tagged AMBIGUOUS (relation: no_login_or_credential_fields_shown) - confidence is low._
- **Why does `AppNavHost()` connect `Budgets ViewModel` to `Dashboard & Database Core`, `SettingsScreen.Triple`, `Invoice/Debt Data Access`, `Budget Data Access`, `Category Data Access`, `Enrichment Pipeline & Agenda/Category Models`, `Movements List Screen`, `Repo Structure & CI/Graphify Integration`, `SDD: Módulos Pendientes (Alertas, Confirmación)`, `App Navigation Routes`, `SDD: Módulos Core (Agenda, Captura, Clasificación)`, `Enrichment Pipeline Logic`, `Payment Method Enum`, `Colombian Amount Parser Tests`, `Config release pendiente`, `MovementsListScreen.Movement`, `MovementViewModel (Int)`, `Ilustracion: bloqueo biometrico`, `Ilustracion: splash`, `Fixtures Lulo Bank`, `Fixtures Nequi`, `Fixtures Nu`?**
  _High betweenness centrality (0.160) - this node is a cross-community bridge._
- **Why does `FinanzasDatabase` connect `Config release pendiente` to `Bank Parsers & Raw Movement Model`, `Movement Repository`, `Bank Statement Importer`, `Classification Rule DAO`, `Budgets ViewModel`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **Why does `CategoryEntity` connect `Repo Structure & CI/Graphify Integration` to `Movement Data Access (DAO)`, `Boot Receiver`, `Notification Access Tests`, `Config release pendiente`, `Classification Rule DAO`, `MovementsListScreen.Movement`, `Ilustracion: bloqueo biometrico`, `Ilustracion: splash`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `MovementEntity` (e.g. with `.saveEnriched()` and `.movementEntity()`) actually correct?**
  _`MovementEntity` has 3 INFERRED edges - model-reasoned connections that need verification._
- **Are the 30 inferred relationships involving `AppNavHost()` (e.g. with `FinanceCard()` and `FinanceTag()`) actually correct?**
  _`AppNavHost()` has 30 INFERRED edges - model-reasoned connections that need verification._
- **What connects `public.profiles`, `public.savings_goals`, `FinanzasMigrations` to the rest of the system?**
  _404 weakly-connected nodes found - possible documentation gaps or missing edges._