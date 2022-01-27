plugins {
    kotlin("jvm") version "1.6.10"
}

group = "live.myoun"
version = "1.2.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}


dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
}

val shade = configurations.create("shade")
shade.extendsFrom(configurations.implementation.get())

tasks {
    jar {
        from (shade.map { if (it.isDirectory) it else zipTree(it) })
    }
}