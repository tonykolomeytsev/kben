import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.6.10" apply false
}

allprojects {
    group = "kekmech"
    version = "0.1.5"

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}