plugins {
    id("kotlin-kapt")
    alias(libs.plugins.gitbrowse.android.library)
    alias(libs.plugins.gitbrowse.android.hilt)
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

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}