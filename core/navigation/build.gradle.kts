plugins {
    alias(libs.plugins.gitbrowse.android.library)
    alias(libs.plugins.gitbrowse.android.library.compose)
    alias(libs.plugins.gitbrowse.android.hilt)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.anos.navigation"
}

dependencies {
    implementation(project(":core:model"))

    // Navigation3
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)

    // json parsing
    implementation(libs.kotlinx.serialization.json)
}