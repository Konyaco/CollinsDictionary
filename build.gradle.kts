plugins {
    id("com.android.application") version "7.2.0-alpha03" apply false
    id("com.android.library") version "7.2.0-alpha03" apply false
    kotlin("android") version "1.5.31" apply false
    kotlin("multiplatform") version "1.5.31" apply false
    id("org.jetbrains.compose") version "1.0.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
    extra["jsoup_version"] = "1.13.1"
}

group = "me.konyaco.collinsdictionary"
version = "1.4.1"
