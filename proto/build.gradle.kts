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
    libname = "proto"
    targets = arrayListOf("arm", "arm64", "x86", "x86_64")
    extraCargoBuildArguments = arrayListOf("-v", "-v")
    exec = { spec, toolchain ->
        println(toolchain)
        val toolchainDir = File(spec.environment["CC_${toolchain.target}"].toString()).parentFile
        //println(toolchainDir.absolutePath)
        spec.environment(
            "CC_${toolchain.target}",
            "${
                File(toolchainDir, "clang.exe").absolutePath.replace(
                    "\\",
                    "/"
                )
            } --target=${toolchain.compilerTriple}24"
        )
        spec.environment(
            "CXX_${toolchain.target}",
            "${
                File(toolchainDir, "clang++.exe").absolutePath.replace(
                    "\\",
                    "/"
                )
            } --target=${toolchain.compilerTriple}24"
        )

        spec.environment(
            "RANLIB_${toolchain.target}",
            File(toolchainDir, "llvm-ranlib.exe").absolutePath.replace(
                "\\",
                "/"
            )
        )
        /*

        val target = if (toolchain.platform == "x86" || toolchain.platform == "x86_64")
            toolchain.platform
        else toolchain.binutilsTriple

        spec.environment(
            "RANLIB_${toolchain.target}", File(
                toolchainDir.parentFile.parentFile.parentFile.parentFile,
                "$target-4.9/prebuilt/windows-x86_64/bin/${toolchain.binutilsTriple}-ranlib.exe"
            ).absolutePath.replace(
                "\\",
                "/"
            )
        )

         */
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
    if (name == "javaPreCompileDebug" || name == "javaPreCompileRelease") {
        dependsOn("cargoBuild")
    }
}