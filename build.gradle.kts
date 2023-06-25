import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `maven-publish`
    signing
    val kotlinVersion = "1.8.22"
    kotlin("jvm").version(kotlinVersion)
    kotlin("plugin.serialization").version(kotlinVersion)
    id("com.github.ben-manes.versions").version("0.47.0")  //For finding outdated dependencies
}

allprojects {
    version = "1.0.5"
    group = "net.justmachinery.futility"


    repositories {
        mavenCentral()
    }
}
subprojects {
    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.gradle.signing")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    val sourcesJar by tasks.registering(Jar::class){
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    val javadocJar by tasks.registering(Jar::class){
        dependsOn.add(JavaPlugin.JAVADOC_TASK_NAME)
        archiveClassifier.set("javadoc")
        from(tasks.getByName("javadoc"))
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    val projectName = name
    publishing {
        publications {
            create<MavenPublication>("mavenKotlin") {
                artifactId = "futility-$projectName"
                from(components["kotlin"])
                pom {
                    name.set("Futility $projectName")
                    description.set("$description")
                    url.set("https://github.com/ScottPeterJohnson/futility")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
                        url.set("http://github.com/ScottPeterJohnson/futility")
                    }
                }
                artifact(sourcesJar)
                artifact(javadocJar)
            }
        }
        repositories {
            maven {
                name = "central"
                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                credentials {
                    username = findProperty("ossrhUsername") as? String
                    password = findProperty("ossrhPassword") as? String
                }
            }
        }
    }

    signing {
        sign(publishing.publications["mavenKotlin"])
    }

    kotlin {
        explicitApi()
        jvmToolchain(12)
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("io.github.microutils:kotlin-logging:3.0.5")
        api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1")
        implementation("com.squareup.okio:okio:3.0.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    }
}