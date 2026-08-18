import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

// Credenciales del proyecto Supabase. Salen de local.properties (que no se
// versiona) o, en CI, de variables de entorno. Ver local.properties.example.
//
// Si faltan, la app compila igual y queda con la sincronizacion apagada: la
// pantalla de Cuenta lo explica en vez de fallar al pulsar "Sincronizar". Nadie
// que solo quiera compilar el MVP local necesita una cuenta de Supabase.
val localProperties = Properties().apply {
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

fun ajusteSupabase(clave: String, variableEntorno: String): String =
    localProperties.getProperty(clave) ?: System.getenv(variableEntorno) ?: ""

// Firma de la variante release. Igual que las credenciales de Supabase: de
// local.properties o de variables de entorno en CI, nunca del repositorio (un
// keystore versionado es un keystore comprometido).
//
// Si no hay firma configurada, `release` se construye SIN firmar en vez de
// fallar: cualquiera puede compilar y probar la variante ofuscada sin tener el
// keystore de publicacion.
val keystoreFile = (localProperties.getProperty("keystore.file") ?: System.getenv("KEYSTORE_FILE"))
    ?.let { rootProject.file(it) }
    ?.takeIf { it.exists() }

android {
    namespace = "com.finanzas.automatica"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.finanzas.automatica"
        minSdk = 26
        targetSdk = 34
        // Cada cambio funcional sube versionCode en 1 y versionName acorde (patch para
        // fixes, minor para features nuevas).
        versionCode = 14
        versionName = "1.9.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "SUPABASE_URL", "\"${ajusteSupabase("supabase.url", "SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${ajusteSupabase("supabase.anonKey", "SUPABASE_ANON_KEY")}\"")
    }

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    buildFeatures {
        compose = true
        viewBinding = true
        // Genera BuildConfig.VERSION_NAME/VERSION_CODE a partir de versionName/versionCode
        // de arriba, para mostrar la version real en Ajustes (Acerca de) sin duplicarla a
        // mano -- antes esa fila decia "1.0.0" fijo, sin importar la version real.
        buildConfig = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    signingConfigs {
        create("release") {
            keystoreFile?.let { archivo ->
                storeFile = archivo
                storePassword = localProperties.getProperty("keystore.password")
                    ?: System.getenv("KEYSTORE_PASSWORD")
                keyAlias = localProperties.getProperty("keystore.alias") ?: System.getenv("KEYSTORE_ALIAS")
                keyPassword = localProperties.getProperty("keystore.keyPassword")
                    ?: System.getenv("KEYSTORE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            // R8 activado: quita el codigo no usado y ofusca. Las reglas de
            // proguard-rules.pro protegen lo que se resuelve por nombre en
            // tiempo de ejecucion (Room, los servicios del manifiesto, los enums
            // que se guardan como texto en la base).
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (keystoreFile != null) signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            // A proposito NO se le pone applicationIdSuffix: cambiar el
            // applicationId de debug convertiria la app ya instalada en el
            // telefono de prueba en otra aplicacion distinta — se perderian los
            // movimientos capturados y habria que volver a conceder el permiso
            // de acceso a notificaciones.
        }
    }
}

// Room exporta el esquema de cada version aqui. Sirve para dos cosas concretas:
// comparar a mano el DDL que Room espera contra el que escribe cada Migration
// (donde un desajuste minimo hace que la app crashee al abrir), y poder
// escribir tests de migracion mas adelante.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

dependencies {
    // ── Compose BOM (gestiona todas las versiones de Compose automáticamente) ──
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core Android
    val lifecycleVersion = "2.8.4"
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.biometric:biometric:1.1.0")

    // Compose UI (versiones gestionadas por BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material:material-icons-core")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Coroutines & Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // PDF (lectura de extractos bancarios en PDF, 100% offline)
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // OCR (escaneo de facturas y capturas de pantalla de movimientos, 100% en el
    // dispositivo -- no es un LLM ni un servicio externo, ver CLAUDE.md)
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.24")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")

    // Debug/Tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-tooling-data")
}
