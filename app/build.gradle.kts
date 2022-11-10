import java.io.FileInputStream
import java.util.*

// /*
// * Designed and developed by 2021 Batuhan Demir
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = AppConfig.compileSdk
    defaultConfig {
        applicationId = AppConfig.applicationId
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
        testInstrumentationRunner = AppConfig.androidTestInstrumentation
        multiDexEnabled = true
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    signingConfigs {
        create("release") {
            val properties = Properties()
            properties.load(FileInputStream(project.rootProject.file("gradle.properties")))
            storeFile = file(properties.getProperty("STORE_FILE"))
            storePassword = properties.getProperty("STORE_PASSWORD")
            keyPassword = properties.getProperty("KEY_PASSWORD")
            keyAlias = properties.getProperty("KEY_ALIAS")
        }
    }

    buildTypes {
        all {
            this.buildConfigField(
                "String",
                "BASE_URL",
                "\"${AppConfig.BASE_URL}\""
            )
            this.buildConfigField(
                "String",
                "BASE_URL_GITLAB",
                "\"${AppConfig.BASE_URL_GITLAB}\""
            )
            this.buildConfigField(
                "String",
                "API_KEY",
                "\"${AppConfig.API_KEY}\""
            )
            this.buildConfigField(
                "String",
                "API_SECRET",
                "\"${AppConfig.API_SECRET}\""
            )
        }
        debug {
            this.resValue(
                "string",
                "app_name",
                "Flowpod - Debug"
            )
            signingConfig = signingConfigs.getByName("debug")
            //applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            this.resValue(
                "string",
                "app_name",
                "Flowpod"
            )
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
}

dependencies {
    implementation(
        fileTree(
            mapOf(
                "dir" to "libs",
                "include" to listOf("*.jar")
            )
        )
    )
    implementation(project(":core"))
    implementation(AppDependencies.appLibraries)
    implementation(project.dependencies.platform(Libraries.firebaseBom))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(project.dependencies.platform(Libraries.firebaseAnalytics))
    implementation(project.dependencies.platform(Libraries.firebaseCrashlytics))
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("com.google.android.gms:play-services-base:18.1.0")
    implementation("com.google.android.gms:play-services-auth:20.3.0")
    implementation("com.squareup.picasso:picasso:2.8")
    testImplementation(AppDependencies.testLibraries)
    androidTestImplementation(AppDependencies.androidTestLibraries)
    kapt(AppDependencies.compilerLibraries)
    kaptTest(AppDependencies.compilerTestLibraries)
    kaptAndroidTest(AppDependencies.compilerAndroidTestLibraries)
    debugImplementation(AppDependencies.debugLibraries)
    releaseImplementation(AppDependencies.releaseLibraries)
    implementation("androidx.media:media:1.6.0")
    implementation("com.google.android.exoplayer:exoplayer:2.18.1")
    implementation("com.google.android.exoplayer:extension-mediasession:2.18.1")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.18.1")
}
enum class BuildType(val value: String) {
    DEBUG("debug"),
    RELEASE("release")
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}
