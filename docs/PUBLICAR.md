---
tags: [kivo, publicacion, firma]
proyecto: Kivo
actualizado: 2026-08-18
---

# Publicar Kivo y firmar la app

Guía para pasar de "descargo el APK del CI y lo instalo a mano" a "el teléfono se
actualiza solo". Reglas y convenciones del proyecto: [[CLAUDE]].

---

## Parte 1 — Qué es exactamente firmar una app

### El problema que resuelve

Android no tiene forma de saber quién escribió un APK. Cualquiera podría fabricar
un paquete que diga `applicationId = "com.finanzas.automatica"` y hacerlo pasar por
Kivo. Si el sistema aceptara instalarlo encima del tuyo, ese impostor heredaría
**tu base de datos, tus permisos y tu sesión de Supabase**.

La firma es la respuesta: cada APK va firmado con una clave privada, y Android
guarda qué clave firmó lo que hay instalado. Al actualizar comprueba que la firma
nueva sea de **la misma clave**. Si no coincide, rechaza la instalación con
`INSTALL_FAILED_UPDATE_INCOMPATIBLE`.

O sea: la identidad de una app en Android no es su nombre ni su `applicationId`.
**Es su clave de firma.**

### Las dos claves que existen hoy en este proyecto

| | Clave de depuración | Clave de publicación |
|---|---|---|
| Quién la crea | El SDK de Android, sola | Tú, una vez |
| Dónde vive | `~/.android/debug.keystore` | Donde tú la guardes |
| Contraseña | `android` (pública, igual para todo el mundo) | Tuya |
| Sirve para | Probar en tu teléfono | Distribuir de verdad |
| Caduca | Sí, sin importancia | Debe durar décadas |

**El APK que tienes instalado ahora está firmado con la clave de depuración**, la
misma que usa cualquier proyecto Android del planeta. Por eso no sirve para
distribuir: nadie podría distinguir tu app de una falsificación.

### Por qué la clave es para siempre

No hay forma de cambiarla después. Ni renombrando la app, ni reinstalando el
sistema, ni escribiéndole a Google.

- **Distribuyendo por tu cuenta** (GitHub Releases, Obtainium): si pierdes la
  clave, la única salida es que cada usuario **desinstale** y vuelva a instalar
  — perdiendo los datos locales.
- **En Google Play**: si pierdes la clave de subida, se puede pedir un reemplazo.
  Pero si pierdes la clave de firma de app y no activaste *Play App Signing*, esa
  app queda muerta: no puedes volver a publicar una actualización nunca.

Por eso el keystore se trata como un documento de identidad, no como un archivo
del proyecto.

### Anatomía de un keystore

Un archivo `.jks` es un **contenedor de claves**, y dentro puede haber varias. De
ahí que haya tres datos y no uno:

```
kivo-release.jks              ← el archivo (el contenedor)
├── contraseña del almacén    ← storePassword: abre el contenedor
└── alias "kivo"              ← keyAlias: cuál de las claves de dentro usar
    └── contraseña de la clave ← keyPassword: abre esa clave en concreto
```

En la práctica se usa una sola clave y las dos contraseñas suelen ser iguales.
Pueden serlo; el proyecto acepta ambas configuraciones.

### Qué NO es la firma

- **No cifra el APK.** Cualquiera puede abrirlo y leer su contenido, incluida la
  `anon key` de Supabase. Por eso lo que protege los datos es Row Level Security y
  no el secreto de esa llave (ver `backend/README.md`).
- **No prueba quién eres ante nadie.** Es un certificado autofirmado: prueba que
  dos APK vienen de la misma mano, nada más.

---

## Parte 2 — Crear tu clave

Una sola vez en la vida del proyecto:

```bash
cd kivo-android
keytool -genkey -v -keystore kivo-release.jks \
        -keyalg RSA -keysize 2048 -validity 10000 -alias kivo
```

`-validity 10000` son unos 27 años. No es exagerado: Google Play exige que el
certificado sea válido al menos hasta 2033, y una app que sobreviva a su
certificado no se puede seguir actualizando.

Te va a pedir una contraseña y unos datos (nombre, organización, ciudad). Van
dentro del certificado y **son públicos**: cualquiera puede leerlos con
`keytool -list`. No pongas ahí nada que no quieras que se vea.

### Guárdalo bien, ahora

`.gitignore` ya excluye `*.jks` y `*.keystore`, así que no va a entrar al
repositorio por accidente. Pero eso significa que **si se te borra el portátil, se
fue**. Copia a dos sitios distintos:

- Un gestor de contraseñas que admita archivos adjuntos (Bitwarden, 1Password).
- Un disco externo o una nube cifrada.

Anota junto al archivo: la contraseña del almacén, el alias y la contraseña de la
clave. Un keystore sin su contraseña es exactamente igual de inútil que no tenerlo.

---

## Parte 3 — Compilar firmado en tu máquina

En `kivo-android/local.properties` (que no se versiona):

```properties
keystore.file=kivo-release.jks
keystore.password=LA-QUE-PUSISTE
keystore.alias=kivo
keystore.keyPassword=LA-QUE-PUSISTE
```

Y luego:

```bash
cd kivo-android
./gradlew assembleRelease
```

Si el archivo resultante se llama `app-release.apk`, quedó firmado. Si se llama
`app-release-**unsigned**.apk`, la configuración no se leyó — `build.gradle.kts`
está hecho para seguir compilando sin firma en vez de fallar, para que cualquiera
pueda probar la variante ofuscada sin tener tu llave.

Para comprobarlo de verdad:

```bash
$ANDROID_HOME/build-tools/34.0.0/apksigner verify --print-certs \
  app/build/outputs/apk/release/app-release.apk
```

Apunta el `SHA-256` que imprime. Si algún día cambia, es que se firmó con otra
llave y ningún teléfono aceptará la actualización.

---

## Parte 4 — Publicación automática (GitHub Releases + Obtainium)

`.github/workflows/release.yml` compila, firma, **verifica la firma** y publica el
APK como Release cada vez que empujas una etiqueta de versión.

### Cargar los secrets

En GitHub → *Settings → Secrets and variables → Actions → New repository secret*:

| Secret | Valor |
|---|---|
| `KEYSTORE_BASE64` | `base64 -w0 kivo-android/kivo-release.jks` |
| `KEYSTORE_PASSWORD` | contraseña del almacén |
| `KEYSTORE_ALIAS` | `kivo` |
| `KEYSTORE_KEY_PASSWORD` | contraseña de la clave |
| `SUPABASE_URL` | `https://etmudmitqszrawenimoi.supabase.co` |
| `SUPABASE_ANON_KEY` | la anon key del proyecto |

El keystore viaja en base64 porque los secrets de GitHub son texto y un `.jks` es
binario. El workflow lo reconstruye en un directorio temporal y lo borra al final.

Los dos últimos no son opcionales: sin ellos el APK publicado saldría con la
sincronización apagada.

### Publicar una versión

```bash
# 1. Subir versionCode y versionName en kivo-android/app/build.gradle.kts
# 2. Commit
git tag v1.9.0
git push origin v1.9.0
```

El workflow hace el resto. **La etiqueta debe coincidir con el `versionName`** y el
`versionCode` **tiene que subir siempre**: Android se niega a instalar un APK con
un `versionCode` menor o igual al instalado, y no lo dice de forma clara.

### En el teléfono

1. Instalar [Obtainium](https://github.com/ImranR98/Obtainium) (desde su propio
   GitHub Release).
2. *Add App* → pegar `https://github.com/cevasz/Automatizador_Financiero`.
3. Listo: revisa periódicamente y avisa cuando hay versión nueva.

Android va a mostrar su diálogo de instalación en cada actualización — eso no lo
puede saltar ninguna app sin permisos de sistema. Si quieres que sea de verdad
silencioso, ahí es donde entra Google Play (Parte 6).

---

## Parte 5 — El cambio de firma: cómo no perder tus datos

**Esto pasa una sola vez**, al saltar del APK de depuración que tienes hoy al
primero firmado con tu clave.

Para Android son dos apps distintas, así que la actualización directa falla. Hay
que desinstalar, y desinstalar borra la base de datos local. La secuencia correcta:

1. En la app actual: **Cuenta → Sincronizar**. Sube todo a Supabase.
2. Comprobar en el panel web que los movimientos están ahí.
3. Desinstalar Kivo.
4. Instalar el APK firmado.
5. Entrar con la misma cuenta y **Sincronizar** de nuevo. Baja todo.
6. Volver a conceder **Acceso a notificaciones** en Ajustes del sistema: ese
   permiso se otorga por app instalada y se pierde al desinstalar.

Después de esto, todas las actualizaciones futuras son normales: la firma ya no
cambia nunca más.

---

## Parte 6 — Si algún día vas a Google Play

Requisitos que hoy no están listos:

- Cuenta de Play Console (25 USD, pago único).
- **Política de tratamiento de datos publicada** con URL accesible — anotado como
  pendiente legal en [[docs/PENDIENTES]], y ahora obligatorio porque salen datos
  personales del dispositivo.
- Formulario de seguridad de datos declarando qué se recoge y a dónde va.
- **Justificación del acceso a notificaciones.** Es un permiso sensible y Google lo
  revisa con lupa. Hay riesgo real de rechazo si la justificación no es concreta.
- Un **AAB** (`./gradlew bundleRelease`), no un APK.

Y una decisión importante: **Play App Signing**. Al activarlo, Google guarda la
clave de firma final y tú conservas solo una "clave de subida". Ventaja: si pierdes
la de subida, se puede reemplazar y la app sobrevive. Sin él, perder la clave mata
la app para siempre. Actívalo.

El canal de **pruebas internas** permite hasta 100 probadores con revisión rápida, y
ahí sí las actualizaciones llegan solas y en silencio, como en cualquier otra app de
la tienda.
