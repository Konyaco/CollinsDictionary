plugins {
    id("com.android.application")
    id("org.jetbrains.compose")
    kotlin("android")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "me.konyaco.collinsdictionary"
        minSdk = 21
        targetSdk = 31
        versionCode = 3
        versionName = rootProject.version as String
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }

    lint {
        checkDependencies = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":common"))
    implementation("org.jsoup:jsoup:${rootProject.extra["jsoup_version"]}")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
}