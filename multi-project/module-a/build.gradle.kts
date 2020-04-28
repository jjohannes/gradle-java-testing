plugins {
    id("org.myproject.module")
}

dependencies {
    // now additional dependencies can be added conveniently
    testsJunit4Implementation("com.google.guava:guava:29.0-jre")
}
