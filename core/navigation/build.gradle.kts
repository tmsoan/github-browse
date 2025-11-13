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

    // navigation
    api(libs.androidx.navigation.compose)
    api(libs.androidx.navigation.fragment.ktx)
    api(libs.androidx.navigation.ui.ktx)
    api(libs.androidx.hilt.navigation.compose)

    // json parsing
    implementation(libs.kotlinx.serialization.json)
}