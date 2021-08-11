plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

kotlin {
    android()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.animation)
                api(compose.material)
                api(compose.preview)
                api(compose.uiTooling)
                api(compose.materialIconsExtended)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jsoup:jsoup:1.11.3")
                api("androidx.appcompat:appcompat:1.3.1")
                api("androidx.core:core-ktx:1.6.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                api(compose.desktop.currentOs)
                implementation("org.jsoup:jsoup:1.11.3")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}
