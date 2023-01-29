plugins {
    kotlin("jvm") version "1.7.10"
}

group = "live.myoun.aestus"
version = property("version")!!

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}


dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:${property("mcVersion")}-R0.1-SNAPSHOT")
}

tasks {

    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}