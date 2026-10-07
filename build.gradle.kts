import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    val kotlinVersion = "2.4.0"
    kotlin("jvm").version(kotlinVersion)
    kotlin("plugin.serialization").version(kotlinVersion)
    id("com.github.ben-manes.versions").version("0.54.0")  //For finding outdated dependencies
    id("com.vanniktech.maven.publish").version("0.34.0").apply(false)
}

allprojects {
    version = "1.2.0"
    group = "net.justmachinery.futility"


    repositories {
        mavenCentral()
    }
}
subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.vanniktech.maven.publish")

    val projectName = name
    // Applied via apply(plugin = ...) inside subprojects, so the type-safe `mavenPublishing { }`
    // accessor isn't available here; configure the extension by type instead.
    configure<MavenPublishBaseExtension> {
        publishToMavenCentral()
        signAllPublications()

        coordinates(groupId = group.toString(), artifactId = "futility-$projectName", version = version.toString())
        pom {
            name.set("Futility $projectName")
            description.set(project.description)
            url.set("https://github.com/ScottPeterJohnson/futility")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("scottj")
                    name.set("Scott Johnson")
                    email.set("mavenfutility@justmachinery.net")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/ScottPeterJohnson/futility.git")
                developerConnection.set("scm:git:ssh://github.com/ScottPeterJohnson/futility.git")
                url.set("https://github.com/ScottPeterJohnson/futility")
            }
        }
    }


    java {
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        explicitApi()
        jvmToolchain(25)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
        implementation("org.slf4j:slf4j-api:2.0.17")
        api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.9.1")

        testImplementation(kotlin("test"))
        testImplementation(platform("org.junit:junit-bom:5.13.4"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}