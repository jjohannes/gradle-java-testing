package org.gradle.sample.javatesting.plugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.testing.Test;

class TestSet {

    private final SourceSet sourceSet;

    private final ConfigurationContainer configurations;
    private final DependencyHandler dependencies;
    private final TaskContainer tasks;
    private final Project project;

    TestSet(SourceSet sourceSet, Project project) {
        this.sourceSet = sourceSet;
        this.configurations = project.getConfigurations();
        this.dependencies = project.getDependencies();
        this.tasks = project.getTasks();
        this.project = project;
    }

    void init() {
        TaskProvider<Test> testTask = maybeRegisterTestTask(sourceSet.getName());
        testTask.configure(t -> t.setTestClassesDirs(sourceSet.getOutput().getClassesDirs()));
        testsAsClasses(testTask);
        findTestImplementation().getDependencies().add(dependencies.create(project));
    }

    void useJUnit4(String version, Action<? super TestSetSpec> categories) {
        TestSetSpec testTags = new TestSetSpec();
        categories.execute(testTags);
        useJUnit4(version, testTags);
    }

    private void useJUnit4(String version, TestSetSpec testConfig) {
        TaskProvider<Test> testTask = findTestTask();
        testTask.configure(t -> t.useJUnit(p -> p.excludeCategories(testConfig.getTagsOrCategories().toArray(new String[0]))));
        findTestImplementation().getDependencies().add(dependencies.create("junit:junit:" + version));
        if (testConfig.isTestsAsJar()) {
            testsAsJar(testTask);
        }
        for (String tag : testConfig.getTagsOrCategories()) {
            TaskProvider<Test> tagTestTask = maybeRegisterTestTask(sourceSet.getName() + sanitizeName(tag));
            tagTestTask.configure(t -> t.setTestClassesDirs(sourceSet.getOutput().getClassesDirs()));
            tagTestTask.configure(t -> t.useJUnit(p -> p.includeCategories(tag)));
            testsAsClasses(tagTestTask);
            tasks.named("check").configure(t -> t.dependsOn(tagTestTask));
        }

    }

    void useJUnit5(String version, Action<? super TestSetSpec> testConfig) {
        TestSetSpec testTags = new TestSetSpec();
        testConfig.execute(testTags);
        useJUnit5(version, testTags);
    }

    private void useJUnit5(String version, TestSetSpec testConfig) {
        TaskProvider<Test> testTask = findTestTask();
        testTask.configure(t -> t.useJUnitPlatform(p -> p.excludeTags(testConfig.getTagsOrCategories().toArray(new String[0]))));
        findTestImplementation().getDependencies().add(dependencies.create("org.junit.jupiter:junit-jupiter-api:" + version));
        findTestRuntimeOnly().getDependencies().add(dependencies.create("org.junit.jupiter:junit-jupiter-engine:" + version));
        if (testConfig.isTestsAsJar()) {
            testsAsJar(testTask);
        }
        for (String tag : testConfig.getTagsOrCategories()) {
            TaskProvider<Test> tagTestTask = maybeRegisterTestTask(sourceSet.getName() + sanitizeName(tag));
            tagTestTask.configure(t -> t.setTestClassesDirs(sourceSet.getOutput().getClassesDirs()));
            tagTestTask.configure(t -> t.useJUnitPlatform(p -> p.includeTags(tag)));
            testsAsClasses(tagTestTask);
            tasks.named("check").configure(t -> t.dependsOn(tagTestTask));
        }
    }

    void useTestNG(String version, Action<? super TestSetSpec> testConfig) {
        TestSetSpec testTags = new TestSetSpec();
        testConfig.execute(testTags);
        useTestNG(version, testTags);
    }

    private void useTestNG(String version, TestSetSpec testConfig) {
        TaskProvider<Test> testTask = findTestTask();
        testTask.configure(t -> t.useTestNG(p -> p.excludeGroups(testConfig.getTagsOrCategories().toArray(new String[0]))));
        findTestImplementation().getDependencies().add(dependencies.create("org.testng:testng:" + version));
        if (testConfig.isTestsAsJar()) {
            testsAsJar(testTask);
        }
        for (String group : testConfig.getTagsOrCategories()) {
            TaskProvider<Test> tagTestTask = maybeRegisterTestTask(sourceSet.getTaskName(null, sanitizeName(group)));
            tagTestTask.configure(t -> t.setTestClassesDirs(sourceSet.getOutput().getClassesDirs()));
            tagTestTask.configure(t -> t.useTestNG(p -> p.includeGroups(group)));

            if (testConfig.isTestsAsJar()) {
                testsAsJar(tagTestTask);
            } else {
                testsAsClasses(tagTestTask);
            }
            tasks.named("check").configure(t -> t.dependsOn(tagTestTask));
        }
    }

    private String sanitizeName(String tag) {
        //TODO remove spaces and replace && by And etc
        return tag;
    }

    void testsAsClasses(TaskProvider<Test> testTask) {
        testTask.configure(t -> t.setClasspath(sourceSet.getRuntimeClasspath()));
    }

    void testsAsJar(TaskProvider<Test> testTask) {
        TaskProvider<Jar> testJarTask;
        if (!tasks.getNames().contains(sourceSet.getJarTaskName())) {
            testJarTask = tasks.register(sourceSet.getJarTaskName(), Jar.class, t -> {
                t.getArchiveClassifier().set(sourceSet.getName());
                t.from(sourceSet.getOutput());
            });
        } else {
            testJarTask = tasks.named(sourceSet.getJarTaskName(), Jar.class);
        }
        testTask.configure(t ->
                t.setClasspath(configurations.getByName(sourceSet.getRuntimeClasspathConfigurationName()).plus(project.files(testJarTask))));
    }

    private TaskProvider<Test> findTestTask() {
        return tasks.named(sourceSet.getName(), Test.class);
    }

    private Configuration findTestImplementation() {
        return configurations.getByName(sourceSet.getImplementationConfigurationName());
    }

    private Configuration findTestRuntimeOnly() {
        return configurations.getByName(sourceSet.getRuntimeOnlyConfigurationName());
    }

    private TaskProvider<Test> maybeRegisterTestTask(String name) {
        if (tasks.getNames().contains(name)) {
            return tasks.named(name, Test.class);
        } else {
            TaskProvider<Test> testTask = tasks.register(name, Test.class);
            tasks.named("check").configure(t -> t.dependsOn(testTask));
            return testTask;
        }
    }

    // TODO test fixtures?
}
