plugins {
    alias(libs.plugins.gitbrowse.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.anos.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        // configure here are able to be overridden from CI/CD pipeline or a `secret.properties` file
        buildConfigField("String", "BASE_URL", "\"https://api.github.com\"")
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)
}