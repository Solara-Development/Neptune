plugins {
    id("buildlogic.java-conventions")
    id("com.gradleup.shadow") version "9.4.2"
}

group = "me.dev.lrxh"
version = "2.5"
description = "Plugin"

val mcVersion = "1.21.11"
val pluginVersion = version.toString()

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(project(":API"))
    implementation(libs.org.mongodb.mongodb.driver.sync)
    implementation(libs.com.github.devlrxxh.blockchanger)

    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
    compileOnly(libs.me.clip.placeholderapi)
    compileOnly(libs.com.github.retrooper.packetevents.spigot)

    implementation(platform("com.intellectualsites.bom:bom-newest:1.56"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") {
        exclude(group = "*", module = "FastAsyncWorldEdit-Core")
    }
}

val gitPropertiesFile = layout.buildDirectory.file("generated/git/git.properties")

val generateGitProperties by tasks.registering {
    val outputFile = gitPropertiesFile
    val workingDir = layout.projectDirectory
    outputs.file(outputFile)

    doLast {
        val abbrev = runCatching {
            ProcessBuilder("git", "rev-parse", "--short", "HEAD")
                .directory(workingDir.asFile)
                .redirectErrorStream(true)
                .start()
                .inputStream.bufferedReader()
                .use { it.readText().trim() }
        }.getOrDefault("").ifEmpty { "unknown" }

        outputFile.get().asFile.also { file ->
            file.parentFile.mkdirs()
            file.writeText("git.commit.id.abbrev=$abbrev\n")
        }
    }
}

tasks.processResources {
    dependsOn(generateGitProperties)
    from(layout.buildDirectory.dir("generated/git"))
    inputs.property("projectVersion", pluginVersion)
    inputs.property("mcVersion", mcVersion)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        filter { line ->
            line
                .replace("\${project.version}", pluginVersion)
                .replace("\${mc.version}", mcVersion)
        }
    }
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    dependsOn(generateGitProperties)
    archiveBaseName.set("Neptune")
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveVersion.set(
        gitPropertiesFile.map { file ->
            val abbrev = file.asFile.takeIf { it.exists() }
                ?.readLines()
                ?.firstOrNull { line -> line.startsWith("git.commit.id.abbrev=") }
                ?.substringAfter("=")
                ?.trim()
                ?: "unknown"
            "$pluginVersion-$mcVersion-$abbrev"
        },
    )
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
