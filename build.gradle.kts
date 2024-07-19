import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`
    `maven-publish`
    id("net.linguica.maven-settings") version "0.5"
    id("io.github.goooler.shadow") version "8.1.8"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.freefair.lombok") version "8.6"
}

repositories {
    mavenCentral()

    maven("https://repo.slne.dev/repository/maven-proxy") { name = "maven-proxy" }
    maven("https://repo.slne.dev/repository/maven-public") { name = "maven-public" }

//    maven {
//        url = uri("https://repo.papermc.io/repository/maven-public/")
//    }
//    maven("https://repo.papermc.io/repository/maven-public/")
//
//    maven {
//        url = uri(" ")
//    }
//    maven("https://maven.playpro.com")
//
//    maven {
//        url = uri("https://repo.codemc.io/repository/maven-snapshots/")
//    }
//    maven("https://repo.codemc.io/repository/maven-snapshots/")
//
//    maven {
//        url = uri("https://repo.codemc.io/repository/maven-releases/")
//    }

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.mojang:brigadier:1.0.18")
    compileOnly("net.coreprotect:coreprotect:22.4")

    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.15")
    implementation("dev.jorel:commandapi-bukkit-shade:9.5.1")
    implementation("com.github.retrooper:packetevents-spigot:2.4.0")

    paperLibrary("org.apache.commons:commons-lang3:3.14.0")
    paperLibrary("net.kyori:adventure-nbt:4.17.0")
    paperLibrary("net.kyori:adventure-text-minimessage:4.17.0")
    paperLibrary("com.saicone.rtag:rtag:1.5.5")
    paperLibrary("com.saicone.rtag:rtag-entity:1.5.5")
}

group = "dev.slne.surf.essentials"
version = "1.21-3.0.0-SNAPSHOT"
description = "SurfEssentials"

paper {
    main = "dev.slne.surf.essentials.SurfEssentials"
    bootstrapper = "dev.slne.surf.essentials.SurfEssentialsBootstrap"
    loader = "dev.slne.surf.essentials.SurfEssentialsLoader"
    generateLibrariesJson = true

    apiVersion = "1.21"
    authors = listOf("twisti")

    serverDependencies {
        registerSoft("CoreProtect")
        registerSoft("ProtocolLib")
        registerSoft("ProtocolSupport")
        registerSoft("ViaVersion")
        registerSoft("ViaBackwards")
        registerSoft("ViaRewind")
        registerSoft("Geyser-Spigot")
    }
}

tasks {
    shadowJar {
        minimize()

        essentialsRelocate("com.github.stefvanschie.inventoryframework", "if")
        essentialsRelocate("dev.jorel.commandapi", "commandapi")
        essentialsRelocate("com.github.retrooper.packetevents", "packetevents.api")
        essentialsRelocate("io.github.retrooper.packetevents", "packetevents.impl")
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-parameters")
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}


publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

fun NamedDomainObjectContainer<PaperPluginDescription.DependencyDefinition>.registerDepend(
    name: String,
    joinClasspath: Boolean = true
) {
    register(name) {
        required = true
        this.joinClasspath = joinClasspath
        load = PaperPluginDescription.RelativeLoadOrder.BEFORE
    }
}

fun NamedDomainObjectContainer<PaperPluginDescription.DependencyDefinition>.registerSoft(
    name: String,
    joinClasspath: Boolean = true
) {
    register(name) {
        required = false
        this.joinClasspath = joinClasspath
        load = PaperPluginDescription.RelativeLoadOrder.BEFORE
    }
}

fun ShadowJar.essentialsRelocate(pattern: String, relocatedSuffix: String): ShadowJar =
    relocate(pattern, "dev.slne.surf.essentials.libs.$relocatedSuffix")
