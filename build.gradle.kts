import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.compose") version "0.5.0-build270"
}

group = "me.konyaco.collinsdictionary"
version = "1.0.2"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation("org.jsoup:jsoup:1.11.3")
    implementation(compose.desktop.currentOs)
    implementation(compose.uiTooling)
    implementation("org.jetbrains.compose.material:material-icons-extended:0.5.0-build270")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}


compose.desktop {
    application {
        javaHome = System.getenv("JDK_15")
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