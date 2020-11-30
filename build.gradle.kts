import java.util.Date

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.4.20")
    }
}

plugins {
    java
    kotlin("jvm") version "1.4.10"

    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}

val artifactName = "jotai"
val artifactGroup = "com.noahbres.jotai"
val artifactVersion = "1.0.0"

val pomUrl = "https://github.com/NoahBres/jotai"
val pomScmUrl = "https://github.com/NoahBres/jotai"
val pomIssueUrl = "https://github.com/NoahBres/jotai/issues"
val pomDesc = "https://github.com/NoahBres/jotai"

val githubRepo = "NoahBres/jotai"
val githubReadme = "README.md"

val pomLicenseName = "MIT"
val pomLicenseUrl = "https://opensource.org/licenses/mit-license.php"
val pomLicenseDist = "repo"

val pomDeveloperId = "noahbres"
val pomDeveloperName = "Noah Bresler"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        create<MavenPublication>("jotai") {
            groupId = artifactGroup
            artifactId = artifactName
            version = artifactVersion
            from(components["java"])

            artifact(sourcesJar)

            pom.withXml {
                asNode().apply {
                    appendNode("description", pomDesc)
                    appendNode("name", rootProject.name)
                    appendNode("url", pomUrl)
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", pomLicenseName)
                        appendNode("url", pomLicenseUrl)
                        appendNode("distribution", pomLicenseDist)
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", pomDeveloperId)
                        appendNode("name", pomDeveloperName)
                    }
                    appendNode("scm").apply {
                        appendNode("url", pomScmUrl)
                    }
                }
            }
        }
    }
}

bintray {
    user = project.findProperty("bintrayUser").toString()
    key = project.findProperty("bintrayKey").toString()
    publish = true

    setPublications("jotai")

    pkg.apply {
        repo = "jotai"
        name = artifactName
        userOrg = "noahbres"
        githubRepo = githubRepo
        vcsUrl = pomScmUrl
        description = "Simple finite state machine for the FIRST Tech Challenge robotics competition"
        setLabels("kotlin", "finite-state-machine", "fsm", "FIRST-Tech-Challenge", "FTC", "robotics")
        setLicenses("MIT")
        desc = description
        websiteUrl = pomUrl
        issueTrackerUrl = pomIssueUrl
        githubReleaseNotesFile = githubReadme

        version.apply {
            name = artifactVersion
            desc = pomDesc
            released = Date().toString()
            vcsTag = artifactVersion
        }
    }
}