plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

android {
    namespace = "co.za.rhythmandflow"
    compileSdk = 33
    defaultConfig {
        applicationId = "co.za.rhythmandflow"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
}


dependencies { testImplementation("junit:junit:4.13.2") }
