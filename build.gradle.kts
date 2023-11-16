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

val scarlibVersion = "3.1.0"

dependencies {
    implementation("org.scala-lang:scala-library:2.13.10")
    implementation("io.github.davidedomini:scarlib-core:$scarlibVersion")
    implementation("io.github.davidedomini:dsl-core:$scarlibVersion")
    implementation("io.github.davidedomini:alchemist-scafi:$scarlibVersion")
    implementation("it.unibo.alchemist:alchemist:25.14.6")
    implementation("it.unibo.alchemist:alchemist-incarnation-scafi:25.14.6")
    implementation("it.unibo.alchemist:alchemist-incarnation-protelis:25.14.6")
    implementation("it.unibo.alchemist:alchemist-swingui:25.7.1")
    implementation("dev.scalapy:scalapy-core_2.13:0.5.3")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}

val experiments = listOf("CohesionCollision", "FollowTheLeader")

for(experiment in experiments){
    tasks.register<JavaExec>("run${experiment}Training") {
        group = "ScaRLib $experiment Training"
        mainClass.set("experiments.${experiment.toLowerCase()}.training.${experiment}Training")
        jvmArgs("-Dsun.java2d.opengl=false")
        classpath = sourceSets["main"].runtimeClasspath
    }

    tasks.register<JavaExec>("run${experiment}TrainingGui") {
        group = "ScaRLib $experiment Training"
        mainClass.set("experiments.${experiment.toLowerCase()}.training.${experiment}Training")
        jvmArgs("-Dsun.java2d.opengl=false")
        args = listOf("20")
        classpath = sourceSets["main"].runtimeClasspath
    }

    tasks.register<JavaExec>("run${experiment}Eval") {
        group = "ScaRLib $experiment Training"
        mainClass.set("experiments.${experiment.toLowerCase()}.evaluation.${experiment}Eval")
        jvmArgs("-Dsun.java2d.opengl=false")
        classpath = sourceSets["main"].runtimeClasspath
    }

    tasks.register<JavaExec>("run${experiment}EvalGui") {
        group = "ScaRLib $experiment Training"
        mainClass.set("experiments.${experiment.toLowerCase()}.evaluation.${experiment}Eval")
        jvmArgs("-Dsun.java2d.opengl=false")
        args = listOf("20")
        classpath = sourceSets["main"].runtimeClasspath
    }
}

tasks.register<JavaExec>("runSmokeTest") {
    group = "ScaRLib Training"
    mainClass.set("experiments.cohesioncollision.evaluation.SmokeTest")
    jvmArgs("-Dsun.java2d.opengl=false")
    args = listOf("20")
    classpath = sourceSets["main"].runtimeClasspath
}
