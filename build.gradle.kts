plugins {
    kotlin("jvm") version "1.6.10"
}

group = "live.myoun"
version = property("version")!!

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}


dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}

// No Longer Needed
//val shade = configurations.create("shade")
//shade.extendsFrom(configurations.implementation.get())

tasks {
//    jar {
//        from (shade.map { if (it.isDirectory) it else zipTree(it) })
//    }
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }
}