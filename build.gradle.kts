import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("multiplatform") version "2.0.20"
}

group = "dev.yidafu.terrain"
version = "1.0-SNAPSHOT"

repositories {
    maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public") }
    mavenCentral()
    maven { setUrl("https://jogamp.org/deployment/maven") }
}

dependencies {
}

kotlin {
    js {
        browser {
        }
        binaries.executable()
    }

    sourceSets {
        commonTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.20")

            implementation(kotlin("test")) // Brings all the platform dependencies automatically
        }

        val jsMain by getting {

            dependencies {
            }
        }

        jvmMain {
            dependencies {
                implementation("org.jogamp.gluegen:gluegen-rt-main:2.5.0")
                implementation("org.jogamp.jogl:jogl-all-main:2.5.0")
                implementation("org.joml:joml:1.10.5")
            }
        }
    }

    jvm {
    }
}

tasks.withType<KotlinJsCompile>().configureEach {
    kotlinOptions {
        target = "es2015"
    }
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().download = false
    // "true" for default behavior
}
