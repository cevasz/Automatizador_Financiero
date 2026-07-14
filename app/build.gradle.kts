plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.finanzas.automatica"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.finanzas.automatica"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
        }
    }
}

dependencies {
    // Core Android
    val activityComposeVersion = "1.9.0"
    val lifecycleVersion = "2.8.0"
    val material3Version = "1.3.0"

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    implementation("androidx.compose.material:material-icons-extended:1.6.10")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.10")
    implementation("androidx.compose.ui:ui-graphics:1.6.10")
    implementation("androidx.compose.foundation:foundation:1.6.10")

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

    // Serialization (for JSON parsing if needed)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.24")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.10")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.6.10")

    // Debug/Tools
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.10")
    debugImplementation("androidx.compose.ui:ui-tooling-data:1.6.10")
}