plugins {
    id("kotlin-kapt")
    alias(libs.plugins.gitbrowse.android.library)
    alias(libs.plugins.gitbrowse.android.hilt)
}

android {
    namespace = "com.anos.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
}