plugins {
    `java-gradle-plugin`
}

repositories {
    jcenter()
}

gradlePlugin {
    val greeting by plugins.creating {
        id = "java-testing"
        implementationClass = "org.gradle.sample.javatesting.plugin.JavaTestingPlugin"
    }
}
