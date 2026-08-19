# Kivo

Aplicación Android que construye un historial financiero a partir de las
notificaciones bancarias que el usuario autoriza explícitamente. No pide
credenciales de banco, no accede a cuentas y no automatiza acciones dentro de
otras aplicaciones.

**Estado:** en desarrollo (`1.9.0`). No publicado en ninguna tienda.

---

## Cómo funciona, en corto

Un `NotificationListenerService` recibe las notificaciones de las aplicaciones
que el usuario habilita, una por una. Un motor de reglas y expresiones regulares
—sin servicios de IA ni red— extrae monto, fecha y comercio, y propone un
movimiento que el usuario confirma o descarta.

Todo vive en la base de datos local del teléfono. La sincronización con la nube
existe y es **opcional**: sin cuenta y sin red la aplicación funciona completa.

Entidades con analizador propio: Nequi, Bancolombia, Daviplata, Nu y Lulo Bank.

Detalle técnico en [ARQUITECTURA.md](ARQUITECTURA.md).

## Estructura del repositorio

```
kivo-android/   App Android nativa (Kotlin + Jetpack Compose). Proyecto Gradle autocontenido.
web/            Panel de lectura y corrección (Next.js + TypeScript). Nunca captura movimientos.
backend/        Sin servidor propio: esquema SQL versionado de Supabase y sus funciones.
```

## Stack

| | |
|---|---|
| Android | Kotlin, Jetpack Compose, Material 3, Room, Coroutines/Flow, KSP |
| Extras | ML Kit (OCR de facturas), PDFBox (extractos), BiometricPrompt |
| Nube | Supabase — Postgres gestionado, PostgREST, Auth, Row Level Security |
| Web | Next.js 16 (App Router), React 19, TypeScript, `@supabase/ssr` |
| Pruebas | JUnit 5 |

`minSdk 26` (Android 8.0), `targetSdk 34`.

## Compilar

Requiere **JDK 17** y el SDK de Android 34.

```bash
cd kivo-android
cp local.properties.example local.properties   # ver notas abajo
./gradlew assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/`.

### Configuración

`local.properties` no se versiona. La plantilla incluye:

- `supabase.url` y `supabase.anonKey` — **opcionales**. Sin ellos la aplicación
  compila y funciona igual: la pantalla de Cuenta indica que la sincronización no
  está configurada en vez de fallar al pulsar el botón.
- `keystore.*` — opcionales, solo para firmar la variante `release`. Sin ellos
  `assembleRelease` produce un APK sin firmar.

### Pruebas

```bash
cd kivo-android
./gradlew test
```

Cada analizador bancario tiene pruebas contra textos de notificación guardados en
`app/src/test/resources/fixtures/`. El formato de una notificación nunca se
inventa: se transcribe.

### Panel web

```bash
cd web
cp .env.local.example .env.local
npm install && npm run dev
```

## Privacidad

- El texto de las notificaciones se procesa **en el dispositivo**. No se envía a
  ningún servicio de terceros para clasificarlo.
- Nunca se solicitan usuario ni contraseña bancarios.
- No se usa la API de Accesibilidad para ejecutar acciones.
- Con la sincronización activada, cada usuario solo alcanza sus propias filas:
  el aislamiento está en la base de datos vía Row Level Security, no en código de
  aplicación. Ver [`backend/README.md`](backend/README.md).
- El usuario puede exportar y borrar toda su información en cualquier momento.
- Las imágenes de facturas y el texto crudo del banco no se suben.

## Licencia

Software propietario. Todos los derechos reservados — ver [LICENSE](LICENSE).
El código está disponible para lectura y evaluación; no se concede permiso de
uso, copia, modificación ni distribución.
