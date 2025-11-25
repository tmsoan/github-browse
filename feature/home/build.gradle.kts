plugins {
    alias(libs.plugins.gitbrowse.android.feature)
    alias(libs.plugins.gitbrowse.android.library.compose)
//    alias(libs.plugins.gitbrowse.android.hilt)
    alias(libs.plugins.stability.analyzer)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.anos.home"
}

dependencies {
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

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