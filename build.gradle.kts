plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.bimabk.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    api(libs.androidx.core.ktx)
    api(libs.androidx.activity.compose)
    api(libs.androidx.constraintlayout.compose)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    api(libs.androidx.material3)
    api(libs.androidx.material3.window)

    api(libs.coil.compose)
    api(libs.lifecycle.compose)
    api(libs.navigation.compose)
    api(libs.serialization.json)

    api(platform(libs.koin.bom))
    api(libs.koin.compose)
    api(libs.koin.compose.navigation)

    api(libs.gson)
    api(libs.retrofit)
    api(libs.retrofit.gson)

    api(libs.webkit)

    testImplementation(libs.robolectric)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.logging.interceptor)
    testImplementation(libs.koin.android.test)

    androidTestImplementation(libs.ui.test.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.uiautomator)
    androidTestImplementation(libs.test.rule)
    androidTestUtil(libs.test.services)

    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.noop)
}