plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("plugin.serialization")
}

kotlin {
    android()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${extra["serialization_version"]}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                api(compose.runtime)
                api(compose.foundation)
                api(compose.animation)
                api(compose.material)
                api(compose.preview)
                api(compose.uiTooling)
                api(compose.materialIconsExtended)
                api("io.ktor:ktor-client-cio:1.6.7")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jsoup:jsoup:${extra["jsoup_version"]}")
                api("androidx.appcompat:appcompat:1.4.1")
                api("androidx.core:core-ktx:1.7.0")
                api("io.ktor:ktor-client-cio:1.6.7")
            }
        }
        val jvmMain by getting {
            dependencies {
                api(compose.desktop.currentOs)
                implementation("org.jsoup:jsoup:${extra["jsoup_version"]}")
                api("io.ktor:ktor-client-cio:1.6.7")
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
