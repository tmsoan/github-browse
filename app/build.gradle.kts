plugins {
    id("kotlin-kapt")
    alias(libs.plugins.gitbrowse.android.application)
    alias(libs.plugins.gitbrowse.android.application.compose)
    alias(libs.plugins.gitbrowse.android.hilt)
}

android {
    namespace = "com.anos.gitbrowse"

    defaultConfig {
        applicationId = "com.anos.gitbrowse"
        // version: this is usually incremented automatically by CI/CD pipeline
        // here we set a static value for debugging purposes
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Debug-specific configuration
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("prod") {
            dimension = "environment"
            // No suffix for production
        }
        // Add more flavors as needed
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":feature:home"))
    implementation(project(":feature:details"))

    // ui
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // test and debug
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // hilt
    ksp(libs.hilt.compiler)
}
