plugins {
    id("java")
    scala
}

repositories {
    mavenCentral()
}

scala {
    zincVersion.set("1.6.1")
}

sourceSets {
    main {
        scala {
            setSrcDirs(listOf("src/main/scala"))
        }
    }
    test {
        scala {
            setSrcDirs(listOf("src/test/scala"))
        }
    }
}

val scarlibVersion = "1.6.2"

dependencies {
    implementation("org.scala-lang:scala-library:2.13.10")
    implementation("io.github.davidedomini:scarlib-core:$scarlibVersion")
    implementation("io.github.davidedomini:alchemist-scafi:$scarlibVersion")
    implementation("it.unibo.alchemist:alchemist:25.14.6")
    implementation("it.unibo.alchemist:alchemist-incarnation-scafi:25.14.6")
    implementation("it.unibo.alchemist:alchemist-incarnation-protelis:25.14.6")
    implementation("it.unibo.alchemist:alchemist-swingui:25.7.1")
    implementation("dev.scalapy:scalapy-core_2.13:0.5.3")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}

tasks.register<JavaExec>("runCohesionAndCollisionTraining") {
    group = "ScaRLib Training"
    mainClass.set("experiments.CohesionCollisionTraining")
    classpath = sourceSets["main"].runtimeClasspath
}