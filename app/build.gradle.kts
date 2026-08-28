plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "wangdaye.com.geometricweather"
    compileSdk = 35

    defaultConfig {
        applicationId = "wangdaye.com.geometricweather"
        minSdk = 24
        targetSdk = 35
        versionCode = 30102
        versionName = "3.102"
        multiDexEnabled = true
        ndk {
            abiFilters += listOf("armeabi", "x86", "armeabi-v7a", "x86_64", "arm64-v8a")
        }
    }

    ndkVersion = "22.0.6917172"

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    val buildConfigFields = mapOf(
        "CN_WEATHER_BASE_URL" to "CN_WEATHER_BASE_URL",
        "CAIYUN_WEATHER_BASE_URL" to "CAIYUN_WEATHER_BASE_URL",
        "ACCU_WEATHER_BASE_URL" to "ACCU_WEATHER_BASE_URL",
        "ACCU_WEATHER_KEY" to "ACCU_WEATHER_KEY",
        "ACCU_CURRENT_KEY" to "ACCU_CURRENT_KEY",
        "ACCU_AQI_KEY" to "ACCU_AQI_KEY",
        "OWM_KEY" to "OWM_KEY",
        "OWM_BASE_URL" to "OWM_BASE_URL",
        "BAIDU_IP_LOCATION_BASE_URL" to "BAIDU_IP_LOCATION_BASE_URL",
        "BAIDU_IP_LOCATION_AK" to "BAIDU_IP_LOCATION_AK",
        "MF_WSFT_KEY" to "MF_WSFT_KEY",
        "MF_WSFT_BASE_URL" to "MF_WSFT_BASE_URL",
        "IQA_AIR_PARIF_KEY" to "IQA_AIR_PARIF_KEY",
        "IQA_AIR_PARIF_URL" to "IQA_AIR_PARIF_URL",
        "IQA_ATMO_AURA_KEY" to "IQA_ATMO_AURA_KEY",
        "IQA_ATMO_AURA_URL" to "IQA_ATMO_AURA_URL",
        "IQA_ATMO_SUD_URL" to "IQA_ATMO_SUD_URL"
    )
    buildTypes.configureEach {
        buildConfigFields.forEach { (name, prop) ->
            val value = project.findProperty(prop)?.toString() ?: "\"\""
            buildConfigField("String", name, value)
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    flavorDimensions += "store"
    productFlavors {
        create("pub") {
            dimension = "store"
            versionNameSuffix = "_pub"
        }
        create("gplay") {
            dimension = "store"
            versionNameSuffix = "_gplay"
        }
        create("fdroid") {
            dimension = "store"
            versionNameSuffix = "_fdroid"
        }
    }

    sourceSets {
        getByName("pub") {
            java.setSrcDirs(
                listOf("src/src_bugly", "src/src_baidu", "src/src_amap", "src/src_gplay")
            )
            manifest.srcFile("manifest_pub/AndroidManifest.xml")
            jniLibs.srcDir("libs")
        }
        getByName("gplay") {
            java.setSrcDirs(
                listOf("src/src_nobugly", "src/src_nobaidu", "src/src_noamap", "src/src_nogplay")
            )
            manifest.srcFile("manifest_gplay/AndroidManifest.xml")
        }
        getByName("fdroid") {
            java.setSrcDirs(
                listOf("src/src_nobugly", "src/src_nobaidu", "src/src_noamap", "src/src_nogplay")
            )
        }
        getByName("test") {
            java.srcDir("test")
        }
        getByName("androidTest") {
            java.srcDir("androidTest")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all {
                // PowerMock on JDK 17+ needs these opens (same as the former Java tests).
                it.jvmArgs(
                    "--add-opens=java.base/java.lang=ALL-UNNAMED",
                    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
                    "--add-opens=java.base/java.util=ALL-UNNAMED",
                    "--add-opens=java.base/sun.reflect.annotation=ALL-UNNAMED"
                )
            }
        }
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }
}

android.applicationVariants.configureEach {
    val variantVersionName = versionName
    outputs.configureEach {
        (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl)
            .outputFileName = "GeometricWeather $variantVersionName.apk"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))

    add("pubImplementation", fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    add("gplayImplementation", fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(libs.kotlin.stdlib)

    testImplementation(libs.bundles.testing)

    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.cardview)
    implementation(libs.swiperefreshlayout)

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    androidTestImplementation(libs.compose.ui.test.junit4)

    implementation(libs.preference.ktx)

    implementation(libs.core.ktx)
    implementation(libs.bundles.work)

    implementation(libs.bundles.lifecycle)

    implementation(libs.multidex)

    implementation(libs.bundles.hilt)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler.ext)

    add("pubImplementation", libs.amap.location)

    add("pubImplementation", libs.gms.location)
    add("gplayImplementation", libs.gms.location)

    implementation(libs.coil)
    implementation(libs.coil.compose)

    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp) {
        version { strictly(libs.versions.okhttp.get()) }
    }
    implementation(libs.okhttp.logging)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.circularprogressview)
    implementation(libs.colorpicker)
    implementation(libs.adaptiveiconview)
    implementation(libs.materialscrollbar)
    implementation(libs.materialshetfab)
    implementation(libs.activity.ktx)

    implementation(libs.lunarcalendar)
    implementation(libs.donate)

    add("pubImplementation", libs.bugly.crashreport)
    add("pubImplementation", libs.bugly.nativecrashreport)
}
