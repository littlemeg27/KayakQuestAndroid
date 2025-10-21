plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") version "4.4.2"  // Latest version; applies to this module for Firebase integration
}

android {
    namespace = "com.example.kayakquest"
    compileSdk = 36

    // Load local.properties (mirroring Java project)
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    defaultConfig {
        applicationId = "com.example.kayakquest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("maps.api.key", "")
        buildConfigField("String", "OPENWEATHER_API_KEY", "\"${localProperties.getProperty("openweather.api.key", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "OPENWEATHER_API_KEY", "\"${localProperties.getProperty("openweather.api.key", "")}\"")
        }
        debug {
            buildConfigField("String", "OPENWEATHER_API_KEY", "\"${localProperties.getProperty("openweather.api.key", "")}\"")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "123456"
            keyAlias = "my-alias"
            keyPassword = "123456"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true  // Enable BuildConfig generation (mirroring Java project)
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"  // Compatible with Kotlin 1.9+; adjust based on your Kotlin version
    }

    packaging {
        resources.excludes.addAll(listOf("/META-INF/AL2.0", "/META-INF/LGPL2.1"))
    }
}

dependencies {
    // Core Android/Kotlin
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Compose
    implementation(platform(libs.androidx.compose.bom.v20240903))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // ViewModel & LiveData for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose.v286)
    implementation(libs.androidx.lifecycle.livedata.ktx.v286)
    implementation(libs.androidx.compose.runtime.livedata)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Firebase (using BOM for version management)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.analytics.ktx)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.google.firebase.storage.ktx)
    implementation(libs.firebase.common.ktx)
    implementation(libs.com.google.firebase.firebase.auth.ktx)
    implementation(libs.com.google.firebase.firebase.storage.ktx)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)

    // Google Maps & Location
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.play.services.auth)

    // iText for PDF (from Java project)
    implementation(libs.itext)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}