import ru.endlesscode.bukkitgradle.dependencies.dmulloy2
import ru.endlesscode.bukkitgradle.dependencies.jitpack
import ru.endlesscode.bukkitgradle.dependencies.md5
import ru.endlesscode.bukkitgradle.dependencies.paperApi
import ru.endlesscode.bukkitgradle.dependencies.papermc
import ru.endlesscode.bukkitgradle.dependencies.spigot

plugins {
    idea
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
    id("ru.endlesscode.bukkitgradle") version "0.9.2"
    id("org.ajoberstar.grgit") version "4.1.1"
}

group = "daylightnebula"
description = "MCMoba Game Plugin"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
    md5()
    spigot()
    papermc()
    dmulloy2()
    maven { url = uri("https://nexus.devsrsouza.com.br/repository/maven-public/") } // KotlinBukkitAPI
    maven { url = uri("https://maven.elmakers.com/repository/") } // EffectLib
    maven { url = uri("https://repo.janmm14.de/repository/public/") }

    jitpack()
}

dependencies {
    compileOnly(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.3.1")
    compileOnly(group = "net.kyori", name = "adventure-platform-bukkit", version = "4.0.0")
    compileOnly(group = "com.comphenix.packetwrapper", name = "PacketWrapper", version = "1.15.2-R0.1-SNAPSHOT")
    compileOnly(group = "de.janmm14", name = "jsonmessagemaker", version = "3.1.0")
    compileOnly(group = "org.joml", name = "joml", version = "1.9.2")

    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))

    compileOnly(group = "org.jetbrains", name = "annotations", version = "22.0.0")

    compileOnly(paperApi)

    compileOnly(group = "com.comphenix.protocol", name = "ProtocolLib", version = "4.7.0") {
        exclude(group = "cglib", module = "cglib-nodep")
        exclude(group = "net.bytebuddy", module = "byte-buddy")
    }

    compileOnly(group = "com.elmakers.mine.bukkit", name = "EffectLib", version = "9.0")

    compileOnly(group = "br.com.devsrsouza.kotlinbukkitapi", name = "core", version = "0.2.0-SNAPSHOT") {
        exclude(group = "org.bstats", module = "bstats-bukkit")
    }

    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("reflect"))
    testImplementation(group = "junit", name = "junit", version = "4.13.2")
}

bukkit {
    apiVersion = "1.18.1"

    meta {
        name.set("MCMobaPlugin")
        description.set("Main game plugin")
        main.set("daylightnebula.mcmobaplugin.Main")
        version.set("0.1-SNAPSHOT")
        authors.set(listOf("DaylightNebula"))
    }

    server {
        this.setCore("paper")
        eula = true
        debug = true
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xlambdas=indy")
    }
}

tasks {
    jar {
        archiveBaseName.set("MCMobaGame")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}

idea {
    module {
        isDownloadJavadoc = true
    }
}