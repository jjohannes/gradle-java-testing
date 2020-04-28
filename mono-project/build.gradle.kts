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
    registerJUnit5TestSet("test") {
        extraGroup("slow")
    }
    registerJUnit4TestSet("testsJunit4") {
        extraGroup("SlowTests")
    }
    registerJUnit5TestSet("testingModuleJunit5") {
        testsAsJar()
    }

    registerJUnit5TestSet("testsJunit5Vintage")
}

dependencies {
    "testsJunit5VintageImplementation"("junit:junit:latest.release")
    "testsJunit5VintageRuntimeOnly"("org.junit.vintage:junit-vintage-engine:latest.release")
}

tasks.withType<Test>().configureEach { testLogging.showStandardStreams = true }