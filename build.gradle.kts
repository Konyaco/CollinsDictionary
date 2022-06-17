plugins {
    id("com.android.application") version "7.4.0-alpha03" apply false
    id("com.android.library") version "7.4.0-alpha03" apply false
    kotlin("android") version "1.6.21" apply false
    kotlin("multiplatform") version "1.6.21" apply false
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev686" apply false
    kotlin("plugin.serialization") version "1.6.21" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    extra["jsoup_version"] = "1.15.1"
    extra["serialization_version"] = "1.3.3"
    extra["coroutines_version"] = "1.6.1"
    extra["ktor_version"] = "2.0.1"
}

group = "me.konyaco.collinsdictionary"
version = "1.4.5"