# Arquitectura

Documento técnico. Para qué es Kivo y cómo compilarlo, ver [README.md](README.md).

## Principio de diseño

El motor de captura y clasificación no sabe nada de interfaz. Se puede probar
entero con JUnit, sin emulador y sin Android: entra texto, sale un movimiento.
Esa separación es lo que hace que agregar un banco no toque nada más.

```
service/         NotificationListenerService: recibe y entrega texto crudo
  ↓
domain/parser/   Reglas + regex por entidad bancaria → Movimiento propuesto
  ↓
domain/          Enriquecimiento, importadores, modelos puros
  ↓
data/            Room (local) · repositorios · sincronización
  ↓
presentation/    Compose, ViewModels, navegación
```

## Captura

`NotificationListenerService` recibe las notificaciones de las aplicaciones que
el usuario habilitó. El permiso se concede en Ajustes del sistema, aplicación
por aplicación, y se pierde al desinstalar.

No se usa la API de Accesibilidad. Además de estar prohibida por la política de
Google Play para este uso, no hace falta: leer la notificación basta.

### Analizadores bancarios

Cada entidad tiene su propia clase y no comparte reglas con ninguna otra:

```
domain/parser/
  BankParser.kt            contrato
  BaseBankParser.kt        utilidades comunes
  ParserRegistry.kt        despacho por paquete de origen
  ColombianAmountParser.kt "$ 1.234.567,89" → centavos
  NequiParser.kt  BancolombiaParser.kt  DaviplataParser.kt
  NuParser.kt     LuloParser.kt
```

Agregar un banco es un `BankParser` nuevo más sus *fixtures*. Los textos de
prueba viven en `app/src/test/resources/fixtures/` y son transcripciones, no
invenciones: un formato inventado produce un analizador que pasa las pruebas y
falla en el teléfono.

El resultado nunca entra solo al historial. Queda como movimiento **propuesto** y
el usuario lo confirma o lo descarta.

### Otras entradas

- **OCR de facturas** con ML Kit, en el dispositivo.
- **Extractos PDF** con PDFBox.
- **Captura manual**, incluido el ajuste de saldo (`domain/model/BalanceAdjustment.kt`),
  que compara lo que el usuario dice tener contra lo que Kivo calculó y registra
  la diferencia como un movimiento con signo explícito.

## Datos locales

Room, versión de esquema **5**, con nueve entidades sincronizables más el
registro de borrados:

```
MovementEntity      CategoryEntity        ClassificationRuleEntity
BudgetEntity        SavingsGoalEntity     AgendaEntryEntity
InvoiceEntity       InvoiceItemEntity     AppNotificationEntity
SyncDeletionEntity  (lápidas de borrado local)
```

Dos convenciones que atraviesan todo el proyecto:

- **El dinero se guarda en centavos**, como entero. En pesos solo al mostrarlo.
  El formateo vive en un único sitio, `presentation/ui/format/Money.kt`.
- **Las fechas** son epoch millis en Room y `timestamptz` en Postgres.

Cada cambio de esquema necesita su `Migration` en `FinanzasMigrations.kt`. El
esquema esperado queda versionado en `app/schemas/` para poder compararlo contra
el DDL de la migración: un desajuste mínimo deja la aplicación sin abrir.

## Sincronización (opcional)

Sin credenciales de Supabase la aplicación funciona completa; la pantalla de
Cuenta lo dice en vez de ofrecer botones que fallan.

La implementación está en `data/sync/` y habla **HTTP plano contra PostgREST**;
no entra un SDK pesado ni Google Play Services al APK.

```
  Android (Room)                Supabase (Postgres)              Panel web
       │                                │                             │
       │ 1. kivo_pull_changes(cursor)   │                             │
       │<───────────────────────────────┤  todo lo que cambió después │
       │                                │  de cursor (reloj servidor) │
       │ 2. kivo_push_changes(payload)  │                             │
       ├───────────────────────────────>│  upsert last-write-wins     │
       │                                │<────────────────────────────┤
       │                                │   lee y edita vía PostgREST │
```

Cuatro decisiones que explican el resto:

1. **Identidad de fila:** un `syncId` (UUID) generado en el cliente. El
   autoincremental de Room no sirve como identidad entre dispositivos.
2. **Orden:** primero bajar, luego subir. Al revés, un conflicto se resuelve
   contra datos que ya quedaron viejos.
3. **Conflictos:** gana la escritura más reciente según `updated_at`.
4. **Borrados:** son lógicos (`deleted = true`), nunca `DELETE`. Viajan como una
   lista aparte de identificadores, no como filas: una fila con `deleted = true`
   y un identificador desconocido intentaría insertarse y abortaría la
   transacción entera.

El cursor `synced_at` lo pone el **reloj del servidor**. Usar el del teléfono
haría que un dispositivo desfasado se saltara cambios en silencio.

Detalle del esquema, las políticas y las funciones: [`backend/README.md`](backend/README.md).

## Aislamiento entre usuarios

Está **en la base de datos**, con Row Level Security activado y forzado, no en
código de aplicación que haya que auditar en cada endpoint nuevo. Sin un JWT de
sesión válido `auth.uid()` es `null` y toda política falla.

La `anon key` es pública por diseño: viaja dentro del APK y dentro del bundle JS
del panel, y no hay forma de esconderla en ninguno de los dos.

## Panel web

Next.js 16 (App Router) sobre `@supabase/ssr`. **Lee y corrige; nunca captura
movimientos** — eso es exclusivo del móvil, porque la captura depende de un
permiso que solo existe en Android.

Toda escritura pasa por `web/src/app/panel/actions.ts` y actualiza `updated_at`.
Un solo punto de escritura evita que una ruta nueva olvide la marca de tiempo y
rompa la resolución de conflictos.

## Interfaz

Jetpack Compose con Material 3. El sistema tipográfico está en
`presentation/ui/theme/Type.kt`: Manrope para interfaz y cifras, Fraunces para
títulos. Los montos usan `fontFeatureSettings = "tnum"` (cifras tabulares) para
que las columnas de dinero no bailen al cambiar de dígito.
