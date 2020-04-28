package org.gradle.sample.javatesting.plugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class JavaTestingExtension {

    private final SourceSetContainer sourceSets;
    private final Project project;

    public JavaTestingExtension(
            SourceSetContainer sourceSets,
            Project project) {

        this.sourceSets = sourceSets;
        this.project = project;
    }

    public void registerJUnit5TestSet(String name) {
        registerJUnit5TestSet(name, "latest.release");
    }

    public void registerJUnit5TestSet(String name, String version) {
        registerJUnit5TestSet(name, version, testSetSpec -> { });
    }

    public void registerJUnit5TestSet(String name, Action<? super TestSetSpec> conf) {
        registerJUnit5TestSet(name, "latest.release", conf);
    }

    public void registerJUnit5TestSet(String name, String version, Action<? super TestSetSpec> conf) {
        TestSet testSet = new TestSet(maybeCreateSourceSet(name), project);
        testSet.init();
        testSet.useJUnit5(version, conf);
    }

    public void registerJUnit4TestSet(String name) {
        registerJUnit4TestSet(name, "latest.release");
    }

    public void registerJUnit4TestSet(String name, String version) {
        registerJUnit4TestSet(name, version, testSetSpec -> { });
    }

    public void registerJUnit4TestSet(String name, Action<? super TestSetSpec> conf) {
        registerJUnit4TestSet(name, "latest.release", conf);
    }

    public void registerJUnit4TestSet(String name, String version, Action<? super TestSetSpec> conf) {
        TestSet testSet = new TestSet(maybeCreateSourceSet(name), project);
        testSet.init();
        testSet.useJUnit4(version, conf);
    }

    private SourceSet maybeCreateSourceSet(String name) {
        return sourceSets.maybeCreate(name);
    }
}
