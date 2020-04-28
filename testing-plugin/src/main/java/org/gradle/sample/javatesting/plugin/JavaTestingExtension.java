package org.gradle.sample.javatesting.plugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
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

    public void registerTestSet(String name) {
        registerTestSet(name, null);
    }

    public void registerTestSet(String name, Action<? super TestSet> conf) {
        TestSet testSet = new TestSet(sourceSets.create(name), project);
        testSet.init();
        if (conf != null) {
            configureTestSet(name, conf);
        } else {
            testSet.useJUnit5();
        }
    }

    public void configureTestSet(String name, Action<? super TestSet> conf) {
        TestSet testSet = new TestSet(sourceSets.getByName(name), project);
        conf.execute(testSet);
    }
}
