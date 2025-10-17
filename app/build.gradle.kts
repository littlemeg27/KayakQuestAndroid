plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.5" apply false
}

android {
    namespace = "com.example.kayakquest"
    compileSdk = 36

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
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.20"
    }

    // Simplified packagingOptions to avoid cast issue
    packagingOptions {
        resources.excludes.addAll(listOf("/META-INF/AL2.0", "/META-INF/LGPL2.1"))
    }
}

dependencies {
    // From Version Catalog
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
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.firebase.common.ktx)
    implementation(libs.com.google.firebase.firebase.auth.ktx)
    implementation(libs.com.google.firebase.firebase.storage.ktx)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // ViewModel & LiveData
    implementation(libs.androidx.lifecycle.viewmodel.compose.v286)
    implementation(libs.androidx.lifecycle.livedata.ktx.v286)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.analytics.ktx)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.google.firebase.storage.ktx)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)

    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Core Compose
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose.v277)

    // Lifecycle for ViewModels in Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose.v284)

    // Google Maps Compose (for MapFragment)
    implementation(libs.maps.compose.v612)
    implementation(libs.play.services.maps.v1820)

    // Retrofit and Gson (assuming from WeatherApiService)
    implementation(libs.retrofit.v2110)
    implementation(libs.converter.gson.v2110)

    // Other existing deps like Firebase, etc.
    debugImplementation(libs.ui.tooling)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.lifecycle.viewmodel.compose.v286)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.play.services.maps)
}