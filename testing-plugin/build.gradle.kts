plugins {
    `java-gradle-plugin`
}

repositories {
    jcenter()
}

gradlePlugin {
    val javaTesting by plugins.creating {
        id = "java-testing"
        implementationClass = "org.gradle.sample.javatesting.plugin.JavaTestingPlugin"
    }
}

group = "org.gradle"
version = "0.1"
