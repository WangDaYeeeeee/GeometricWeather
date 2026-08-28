plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "wangdaye.com.geometricweather.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }

    lint {
        abortOnError = false
    }

    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(project(":domain"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp) {
        version { strictly(libs.versions.okhttp.get()) }
    }

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}