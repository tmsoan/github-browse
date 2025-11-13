plugins {
    alias(libs.plugins.gitbrowse.android.library)
    alias(libs.plugins.gitbrowse.android.library.compose)
}

android {
    namespace = "com.anos.ui"
}

dependencies {
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(platform(libs.androidx.compose.bom))
    api(libs.landscapist.glide)
    api(libs.androidx.core.splashscreen)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}