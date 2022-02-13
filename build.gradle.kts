plugins {
    id("com.android.application") version "7.2.0-alpha03" apply false
    id("com.android.library") version "7.2.0-alpha03" apply false
    kotlin("android") version "1.6.10" apply false
    kotlin("multiplatform") version "1.6.10" apply false
    id("org.jetbrains.compose") version "1.1.0-alpha04" apply false
    kotlin("plugin.serialization") version "1.6.10" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    extra["jsoup_version"] = "1.13.1"
    extra["serialization_version"] = "1.3.2"
}

group = "me.konyaco.collinsdictionary"
version = "1.4.2"