plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    kotlin("android") version "1.7.20" apply false
    kotlin("multiplatform") version "1.7.20" apply false
    id("org.jetbrains.compose") version "1.3.0" apply false
    kotlin("plugin.serialization") version "1.7.20" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    extra["jsoup_version"] = "1.15.3"
    extra["serialization_version"] = "1.4.1"
    extra["coroutines_version"] = "1.6.4"
    extra["ktor_version"] = "2.1.2"
    extra["koin_version"] = "3.2.2"
}

group = "me.konyaco.collinsdictionary"
version = "1.4.6"