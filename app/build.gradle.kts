plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "1.9.24"
}

android {
    namespace = "io.github.cczuossa.vpn"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.cczuossa.vpn"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        aidl = true
    }
    packaging {
        resources {
            excludes += "META-INF/INDEX.LIST"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.ultimatebarx)// 状态栏
    implementation(libs.lottie)// 动画
    implementation(libs.gson)// json
    implementation(libs.ktor.client.core)// http请求
    implementation(libs.ktor.client.android)// http请求
    implementation(libs.ktor.client.logging)// http请求
    implementation(libs.ktor.client.okhttp)// http请求
    implementation(libs.ktor.client.serialization)// ktor序列化
    implementation(libs.ktor.client.content.negotiation)// ktor序列化
    implementation(libs.kotlinx.serialization.json)// ktor序列化
    implementation(libs.jsoup)// html解析
    implementation(libs.hutool.http)// http库
    implementation(libs.hutool.crypto)// 加/解密
    implementation(libs.ktor.client.cio.jvm)
}