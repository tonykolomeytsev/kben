plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation(project(":kben-core"))
}