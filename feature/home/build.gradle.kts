plugins {
    alias(libs.plugins.gitbrowse.android.feature)
    alias(libs.plugins.gitbrowse.android.library.compose)
    alias(libs.plugins.stability.analyzer)
}

android {
    namespace = "com.anos.feature.home"
}

dependencies {
    // koin (compose-viewmodel, annotations, ksp compiler) comes from the feature convention plugin

    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

ksp {
    arg("KOIN_CONFIG_CHECK","true")
}