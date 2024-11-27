import org.jetbrains.kotlin.incremental.deleteDirectoryContents

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.rust)
}

val targets = arrayListOf("arm64", "arm")

android {
    namespace = "io.github.cczuossa.vpn.proto"
    compileSdk = 34
    ndkVersion = "23.2.8568313"
    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }

        ndk {
            //abiFilters.addAll(arrayListOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
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
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


cargo {
    module = "./proto"
    libname = "cczuvpnproto"
    targets = arrayListOf("arm", "arm64", "x86", "x86_64")
    extraCargoBuildArguments = arrayListOf("-v", "-v")
    apiLevel = android.defaultConfig.minSdk
    exec = { spec, toolchain ->
        val toolchainDir = File(spec.environment["CC_${toolchain.target}"].toString())
            .parentFile
            .absolutePath
            .replace(
                "\\",
                "/"
            )
        spec.environment.forEach { s, any ->
            println("$s:$any")
        }
        spec.environment(
            "CC_${toolchain.target}",
            "$toolchainDir/clang.exe --target=${toolchain.compilerTriple}$apiLevel"
        )
        spec.environment(
            "CXX_${toolchain.target}",
            "$toolchainDir/clang++.exe --target=${toolchain.compilerTriple}$apiLevel"
        )
        spec.environment(
            "RANLIB_${toolchain.target}",
            "$toolchainDir/llvm-ranlib.exe"
        )
        spec.environment("CFLAGS_${toolchain.target}", "-Wl,--hash-style=both")
        spec.environment("CXXFLAGS_${toolchain.target}", "-Wl,--hash-style=both")
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


tasks.whenTaskAdded {
    if (name.startsWith("buildCMake")) {
        val target = if (name.contains("x86_64")) {
            "X86_64"
        } else if (name.contains("x86")) {
            "X86"
        } else if (name.contains("armeabi-v7a")) {
            "Arm"
        } else {
            "Arm64"
        }
        dependsOn("cargoBuild$target")
    }
    if (name == "clean") {
        dependsOn("cargoClean")
    }
}

tasks.create("cargoClean") {
    group = "rust"
    val target = File(buildFile.parentFile, "${cargo.module}/target")
    if (target.exists()) {
        target.deleteDirectoryContents()
    }
    println("Clean rust target")
}


