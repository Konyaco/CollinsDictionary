plugins {
    id("com.android.application") version "7.1.0-alpha06" apply false
    id("com.android.library") version "7.1.0-alpha06" apply false
    kotlin("android") version "1.5.21" apply false
    kotlin("multiplatform") version "1.5.21" apply false
    id("org.jetbrains.compose") version "1.0.0-alpha3" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

group = "me.konyaco.collinsdictionary"
version = "1.3.0"
