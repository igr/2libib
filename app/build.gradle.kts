plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.okhttp)
    implementation(libs.dotenv)
    implementation(libs.jackson)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "tolibib.AppKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
