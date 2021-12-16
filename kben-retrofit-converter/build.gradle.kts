plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation(project(":kben-core"))
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