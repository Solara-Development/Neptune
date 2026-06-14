plugins {
    id("buildlogic.java-conventions")
}

group = "me.dev.lrxh"
version = "2.5"
description = "API"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

dependencies {
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
    compileOnly(libs.io.papermc.paper.paper.api)
}

tasks.jar {
    archiveBaseName.set("NeptuneAPI")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
}
