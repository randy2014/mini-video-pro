plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.video.entitlement"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.video.entitlement"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.2"
    }
    buildTypes {
        release { isMinifyEnabled = false }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // WebView 增强引擎
    implementation("androidx.webkit:webkit:1.9.0")

    // Media3 ExoPlayer 视频播放引擎
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")

    // ExoPlayer 支持更多格式
    implementation("androidx.media3:media3-datasource-okhttp:1.4.1")
}
