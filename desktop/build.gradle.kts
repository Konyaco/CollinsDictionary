import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "me.konyaco.collinsdictionary"
version = "1.1.0"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "CollinsDictionary"
            vendor = "Konyaco"
            windows {
                perUserInstall = true
                shortcut = true
                upgradeUuid = "1869b274-ab91-48de-9ff4-b6e9baacf00b"
                menu = true
                menuGroup = "Konyaco"
            }
            linux {
                shortcut = true
                menuGroup = "Konyaco"
            }
        }
    }
}