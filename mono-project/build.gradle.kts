plugins {
    `java-library`
    id ("java-testing")
}

repositories {
    jcenter()
}
java {
    modularity.inferModulePath.set(true)
}

javaTesting {
    registerTestSet("testsJunit5") {
        useJUnit5 {
            extraGroup("slow")
        }
    }
    registerTestSet("testsJunit4") {
        useJUnit4 {
            extraGroup("SlowTests")
        }
    }

    registerTestSet("testingModuleJunit5") {
        useJUnit5()
        testsAsJar()
    }

    registerTestSet("testsJunit5Vintage")
}

dependencies {
    "testsJunit5VintageImplementation"("junit:junit:latest.release")
    "testsJunit5VintageRuntimeOnly"("org.junit.vintage:junit-vintage-engine:latest.release")
}

tasks.withType(Test::class).configureEach { testLogging.showStandardStreams = true }