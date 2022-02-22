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
        val ktorVersion = extra["ktor_version"]
        val serializationVersion = extra["serialization_version"]
        val jsoupVersion = extra["jsoup_version"]
        val coroutinesVersion = extra["coroutines_version"]

        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.animation)
                api(compose.material)
                api(compose.preview)
                api(compose.uiTooling)
                api(compose.materialIconsExtended)
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                api("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.4.1")
                api("androidx.core:core-ktx:1.7.0")
                api("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("org.jsoup:jsoup:$jsoupVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                api(compose.desktop.currentOs)
                api("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("org.jsoup:jsoup:$jsoupVersion")
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
