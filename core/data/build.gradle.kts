plugins {
    id("kotlin-kapt")
    alias(libs.plugins.gitbrowse.android.library)
//    alias(libs.plugins.gitbrowse.android.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.anos.data"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:common"))

    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}

ksp {
    arg("KOIN_CONFIG_CHECK","true")
}
