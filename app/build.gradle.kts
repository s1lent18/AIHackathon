plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.aihackaton"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.aihackaton"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.material3)
    implementation(libs.core.ktx)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Hilt Dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.datastore.preferences)

    androidTestImplementation(libs.hilt.android.testing)
    //androidTestAnnotationProcessor(libs.hilt.compiler)

    // For local unit tests
    testImplementation(libs.hilt.android.testing)
    //testAnnotationProcessor(libs.hilt.compiler)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.kotlinx.serialization.json)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //fingerprint
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.appcompat)

    //QR-CODE
    implementation(libs.core)

    implementation(libs.play.services.location)

    //Security
    implementation(libs.androidx.security.crypto)
    implementation(libs.bouncycastle)

    implementation(platform(libs.firebase.bom))

    // Swipe to Refresh
    // Removed Accompanist SwipeRefresh since we migrated to Material3 PullToRefreshBox

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)

    // ML-Barcode
    implementation(libs.barcode.scanning)

    implementation(libs.play.services.nearby)
}