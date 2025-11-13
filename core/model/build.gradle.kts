plugins {
    alias(libs.plugins.gitbrowse.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.anos.model"
}

dependencies {
    // for Json
    implementation(libs.kotlinx.serialization.json)
}
