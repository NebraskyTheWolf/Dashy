plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")


}

android {
    namespace = "eu.fluffici.dashy"
    compileSdk = 34

    defaultConfig {
        applicationId = "eu.fluffici.dashy"
        minSdk = 24
        targetSdk = 34
        versionCode = 105
        versionName = "1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    dependenciesInfo.includeInApk = false
}

configurations {
    kapt
}

kapt {
    correctErrorTypes = true
}


dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.saadahmedev.popup-dialog:popup-dialog:1.0.5")

    implementation("org.greenrobot:eventbus:3.3.1")

    implementation("com.github.gmfe:gm_pda_scanner:1.0.14")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")

    implementation("com.github.volsahin:bottomify-navigation-view:1.0.2")
    implementation("com.github.bumptech.glide:glide:3.7.0")
    implementation("com.anggrayudi:materialpreference:3.8.0")
    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("io.insert-koin:koin-android:3.3.0")
    implementation("androidx.multidex:multidex:2.0.1")

    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    kapt("com.anggrayudi:materialpreference-compiler:1.8")
    implementation("com.scottyab:rootbeer-lib:0.1.0")

    implementation("com.github.topjohnwu.libsu:core:5.0.3")

    // Optional: APIs for creating root services. Depends on ":core"
    implementation("com.github.topjohnwu.libsu:service:5.0.3")

    // Optional: Provides remote file system support
    implementation("com.github.topjohnwu.libsu:nio:5.0.3")

    implementation("com.google.android.play:integrity:1.3.0")
    implementation("com.github.AAChartModel:AAChartCore-Kotlin:7.2.1")
    implementation("com.github.razir.progressbutton:progressbutton:2.1.0")
    implementation("com.evrencoskun.library:tableview:0.8.8")

    // Lifecycle
    val roomVersion = "2.3.0"
    val retrofitVersion = "2.3.0"
    val rxjavaVersion = "2.1.6"
    val rxandroidVersion = "2.0.1"

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    kapt("androidx.lifecycle:lifecycle-compiler:2.7.0")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Retrofit2

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")

    // RxJava

    implementation("io.reactivex.rxjava2:rxjava:$rxjavaVersion")
    implementation("io.reactivex.rxjava2:rxandroid:$rxandroidVersion")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    val composeVersion = "1.6.1"
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")


    implementation("com.github.alorma.compose-settings:ui-tiles:2.1.0")
    implementation("com.github.alorma.compose-settings:ui-tiles-extended:2.1.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.kizitonwose.calendar:compose:2.5.0")

    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-view:1.2.2")
    implementation("com.google.mlkit:barcode-scanning:17.1.0")
}