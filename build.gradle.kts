plugins {
    id("com.android.application") version "7.1.0-alpha13" apply false
    id("com.android.library") version "7.1.0-alpha13" apply false
    kotlin("android") version "1.5.31" apply false
    kotlin("multiplatform") version "1.5.31" apply false
    id("org.jetbrains.compose") version "1.0.0-alpha4-build398" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    extra["jsoup_version"] = "1.13.1"
}

group = "me.konyaco.collinsdictionary"
version = "1.4.0"
