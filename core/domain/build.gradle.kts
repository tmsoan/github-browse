plugins {
    id("kotlin-kapt")
    alias(libs.plugins.gitbrowse.android.library)
//    alias(libs.plugins.gitbrowse.android.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.anos.domain"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.javax.inject)
    ksp(libs.koin.ksp.compiler)

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
}

ksp {
    arg("KOIN_CONFIG_CHECK","true")
}
