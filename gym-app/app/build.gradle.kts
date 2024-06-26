plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.gms.google-services")
}

android {
  namespace = "com.example.gym_app"
  compileSdk = 34

  defaultConfig {
    manifestPlaceholders["auth0Domain"] = "@string/com_auth0_domain"
    manifestPlaceholders["auth0Scheme"] = "demo"
    applicationId = "com.example.gym_app"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = "1.8" }
  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation(platform("androidx.compose:compose-bom:2023.08.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3-android:1.2.1")
  implementation("androidx.compose.animation:animation")
    implementation("androidx.browser:browser:1.8.0")
    testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
  implementation("androidx.navigation:navigation-compose:2.7.7")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("org.gymapp:Library:1.0-SNAPSHOT")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
  implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
  implementation("com.auth0.android:auth0:2.+")
  implementation("com.auth0.android:jwtdecode:2.0.0")
  implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
  implementation("androidx.compose.ui:ui:1.+")
  implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
  implementation("io.coil-kt:coil-compose:2.1.0")
  implementation("com.squareup.moshi:moshi:1.12.0")
  implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
  implementation("androidx.compose.runtime:runtime-livedata:1.6.1")
  implementation("com.google.android.material:material:+")
  implementation("com.google.zxing:core:3.4.1")

  implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.14")

  // For `compose`. Creates a `ChartStyle` based on an M2 Material Theme.
  implementation("com.patrykandpatrick.vico:compose-m2:2.0.0-alpha.14")

  // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
  implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.14")

  // Houses the core logic for charts and other elements. Included in all other modules.
  implementation("com.patrykandpatrick.vico:core:2.0.0-alpha.14")

  // For the view system.
  implementation("com.patrykandpatrick.vico:views:2.0.0-alpha.14")

  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

  implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
  implementation("com.google.firebase:firebase-messaging")

  implementation("com.stripe:stripe-android:20.42.0")
}