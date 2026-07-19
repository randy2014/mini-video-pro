plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

android {
    namespace = "com.video.entitlement"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.video.entitlement"
        minSdk = 24
        targetSdk = 35
        // 测试版: 0.9.yyyyMMddHHmm  正式版: 1.0.yyyyMMddHHmm
        val now = LocalDateTime.now()
        val ts = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
        versionName = "0.9.$ts"
        versionCode = ts.substring(4).toInt()  // MMddHHmm 整数, 如 07191345
    }
    signingConfigs {
        create("release") {
            storeFile = file("../mini-video-release.jks")
            storePassword = "MiniVideo@2026!"
            keyAlias = "mini-video"
            keyPassword = "MiniVideo@2026!"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
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

    // 下拉刷新
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Media3 ExoPlayer 视频播放引擎
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")

    // ExoPlayer 支持更多格式
    implementation("androidx.media3:media3-datasource-okhttp:1.4.1")
}
