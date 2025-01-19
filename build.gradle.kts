plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "net.jandie1505"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "chaossquad-releases"
        url = uri("https://maven.chaossquad.net/releases")
    }
    maven {
        name = "chaossquad-snapshots"
        url = uri("https://maven.chaossquad.net/snapshots")
    }
    maven {
        name = "respark-releases"
        url = uri("https://maven.respark.dev/releases")
    }
    maven {
        name = "simonsators-repo"
        url = uri("https://simonsator.de/repo/")
    }
    maven {
        name = "rosewood-repo"
        url = uri("https://repo.rosewooddev.io/repository/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("net.chaossquad:mclib:master-c31e8f69cf34a540b519cd3b45ef6edea69e594a")
    compileOnly("de.simonsator:Party-and-Friends-MySQL-Edition-Spigot-API:1.6.2-RELEASE")
    compileOnly("de.simonsator:spigot-party-api-for-party-and-friends:1.0.7-RELEASE")
    compileOnly("eu.cloudnetservice.cloudnet:bridge:4.0.0-RC10")
    compileOnly("eu.cloudnetservice.cloudnet:platform-inject-api:4.0.0-RC10")
    compileOnly("org.black_ixx:playerpoints:3.2.6")
}

tasks {
    shadowJar {
        relocate("net.chaossquad.mclib", "net.jandie1505.bedwars.dependencies.net.chaossquad.mclib")
        relocate("org.json", "net.jandie1505.bedwars.dependencies.org.json")
    }
    build {
        dependsOn(shadowJar)
    }
}

java {
    sourceSets {
        main {
            java {
                exclude ("net/jandie1505/bedwars/old/**")
            }
        }
    }
}
