import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform") version "2.1.0"
}

group = "dev.yidafu.terrain"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven { setUrl("https://jogamp.org/deployment/maven") }
    maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public") }
}

kotlin {
    jvmToolchain(11)

    jvm {}
    js {
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory.set(File("${rootDir}/dist/js"))
            }
            commonWebpackConfig {
                mode =  KotlinWebpackConfig.Mode.DEVELOPMENT
            }
        }
        compilerOptions {
            target.set("es2015")
        }
    }

    sourceSets {
        val koolVersion = "0.17.0-SNAPSHOT"
        val lwjglVersion = "3.3.6"
        val physxJniVersion = "2.4.0"
        val targetPlatforms = listOf("natives-windows", "natives-linux", "natives-macos", "natives-macos-arm64")

        commonTest.dependencies {
//            implementation(kotlin("test")) // Brings all the platform dependencies automatically
        }

        commonMain.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.20")
            implementation("de.fabmax.kool:kool-core:$koolVersion")
            implementation("de.fabmax.kool:kool-physics:$koolVersion")

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
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

                targetPlatforms.forEach { platform ->

                    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$platform")
                    val hasVulkanRuntime = "macos" in platform
                    listOf("glfw", "vulkan", "jemalloc", "nfd", "stb", "vma", "shaderc")
                        .filter { it != "vulkan" || hasVulkanRuntime }
                        .forEach { lib ->
                            runtimeOnly("org.lwjgl:lwjgl-$lib:$lwjglVersion:$platform")
                        }

                    // physx-jni runtime libs
                    runtimeOnly("de.fabmax:physx-jni:$physxJniVersion:$platform")
                }
            }
        }
    }
}

task("runnableJar", Jar::class) {
    dependsOn("jvmJar")

    group = "app"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveAppendix.set("runnable")
    manifest {
        attributes["Main-Class"] = "LauncherKt"
    }

    configurations
        .asSequence()
        .filter { it.name.startsWith("common") || it.name.startsWith("jvm") }
        .map { it.copyRecursive().fileCollection { true } }
        .flatten()
        .distinct()
        .filter { it.exists() }
        .map { if (it.isDirectory) it else zipTree(it) }
        .forEach { from(it) }
    from(layout.buildDirectory.files("classes/kotlin/jvm/main"))

    doLast {
        copy {
            from(layout.buildDirectory.file("libs/${archiveBaseName.get()}-runnable.jar"))
            into("$rootDir/dist/jvm")
        }
    }
}

task("runApp", JavaExec::class) {
    group = "app"
    dependsOn("jvmMainClasses")

    classpath = layout.buildDirectory.files("classes/kotlin/jvm/main")
    configurations
        .filter { it.name.startsWith("common") || it.name.startsWith("jvm") }
        .map { it.copyRecursive().filter { true } }
        .forEach { classpath += it }

    mainClass.set("LauncherKt")
    if (OperatingSystem.current().isMacOsX) {
        jvmArgs = listOf("-XstartOnFirstThread")
    }
}

val build by tasks.getting(Task::class) {
    dependsOn("runnableJar")
}

val clean by tasks.getting(Task::class) {
    doLast {
        delete("$rootDir/dist")
    }
}
