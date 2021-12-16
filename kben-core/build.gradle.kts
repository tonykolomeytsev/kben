plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group as String
                artifactId = project.name
                version = project.version as String

                from(components["java"])
            }
        }
    }
}