plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.botzone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.botzone"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {



    // ✅ Room Database (ORM برای ذخیره‌سازی محلی)
    implementation ("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

//    implementation ("androidx.room:room-runtime:2.8.3")
//    ksp("androidx.room:room-compiler:2.8.3")
//    implementation ("androidx.room:room-ktx:2.8.3")



    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")


    implementation("com.google.code.gson:gson:2.13.2")

    implementation ("dev.shreyaspatil:capturable:2.1.0")

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")


    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    // ✅ ViewModel + LiveData برای مدیریت داده‌ها در Compose
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")


    // ✅ Kotlin Coroutines (برای async کار کردن با دیتابیس)
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // ✅ QRCode generator (برای ساخت QR در صفحه‌ی فاکتور)
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
    implementation(libs.androidx.ui.graphics)
    implementation("androidx.biometric:biometric:1.4.0-alpha03")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.runtime.android)
    implementation("androidx.navigation:navigation-compose:2.9.5")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}