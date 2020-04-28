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

public class TestSet {

    private final SourceSet sourceSet;

    private final ConfigurationContainer configurations;
    private final DependencyHandler dependencies;
    private final TaskContainer tasks;
    private final Project project;

    public TestSet(SourceSet sourceSet, Project project) {
        this.sourceSet = sourceSet;
        this.configurations = project.getConfigurations();
        this.dependencies = project.getDependencies();
        this.tasks = project.getTasks();
        this.project = project;
    }

    protected void init() {
        TaskProvider<Test> testTask = tasks.register(sourceSet.getName(), Test.class);
        testTask.configure(t -> t.setTestClassesDirs(sourceSet.getOutput().getClassesDirs()));
        testsAsClasses(testTask);
        findTestImplementation().getDependencies().add(dependencies.create(project));
        tasks.named("check").configure(t -> t.dependsOn(testTask));
    }

    public void useJUnit4() {
        useJUnit4("latest.release");
    }

    public void useJUnit4(String version) {
        useJUnit5(version, new TestGroups());
    }

    public void useJUnit4(Action<? super TestGroups> categories) {
        TestGroups testTags = new TestGroups();
        categories.execute(testTags);
        useJUnit4("latest.release", testTags);
    }

    public void useJUnit4(String version, Action<? super TestGroups> categories) {
        TestGroups testTags = new TestGroups();
        categories.execute(testTags);
        useJUnit4(version, testTags);
    }

    public void useJUnit4(String version, TestGroups testTags) {
        TaskProvider<Test> testTask = findTestTask();
        testTask.configure(Test::useJUnit);
        testTask.configure(t -> t.useJUnit(p -> p.excludeCategories(testTags.getTagsOrCategories().toArray(new String[0]))));
        for (String tag : testTags.getTagsOrCategories()) {
            TaskProvider<Test> tagTestTask = tasks.register(sourceSet.getName() + sanitizeName(tag), Test.class);
            tagTestTask.configure(t -> t.setTestClassesDirs(sourceSet.getOutput().getClassesDirs()));
            tagTestTask.configure(t -> t.useJUnit(p -> p.includeCategories(tag)));
            testsAsClasses(tagTestTask);
            tasks.named("check").configure(t -> t.dependsOn(tagTestTask));
        }
        findTestImplementation().getDependencies().add(dependencies.create("junit:junit:" + version));
    }

    public void useJUnit5() {
        useJUnit5("latest.release", new TestGroups());
    }

    public void useJUnit5(String version) {
        useJUnit5(version, new TestGroups());
    }

    public void useJUnit5(Action<? super TestGroups> tags) {
        TestGroups testTags = new TestGroups();
        tags.execute(testTags);
        useJUnit5("latest.release", testTags);
    }

    public void useJUnit5(String version, Action<? super TestGroups> tags) {
        TestGroups testTags = new TestGroups();
        tags.execute(testTags);
        useJUnit5(version, testTags);
    }

    private void useJUnit5(String version, TestGroups tags) {
        TaskProvider<Test> testTask = findTestTask();
        testTask.configure(t -> t.useJUnitPlatform(p -> p.excludeTags(tags.getTagsOrCategories().toArray(new String[0]))));
        for (String tag : tags.getTagsOrCategories()) {
            TaskProvider<Test> tagTestTask = tasks.register(sourceSet.getName() + sanitizeName(tag), Test.class);
            tagTestTask.configure(t -> t.setTestClassesDirs(sourceSet.getOutput().getClassesDirs()));
            tagTestTask.configure(t -> t.useJUnitPlatform(p -> p.includeTags(tag)));
            testsAsClasses(tagTestTask);
            tasks.named("check").configure(t -> t.dependsOn(tagTestTask));
        }
        findTestImplementation().getDependencies().add(dependencies.create("org.junit.jupiter:junit-jupiter-api:" + version));
        findTestRuntimeOnly().getDependencies().add(dependencies.create("org.junit.jupiter:junit-jupiter-engine:" + version));
    }

    private String sanitizeName(String tag) {
        //TODO remove spaces and replace && by And etc
        return tag.substring(0, 1).toUpperCase() + tag.substring(1);
    }

    public void useJUnit5Vintage() {
        useJUnit5Vintage("latest.release");
    }

    public void useJUnit5Vintage(String version) {
        TaskProvider<Test> testTask = findTestTask();
        testTask.configure(Test::useJUnitPlatform);
        findTestImplementation().getDependencies().add(dependencies.create("junit:junit:" + version));
        findTestRuntimeOnly().getDependencies().add(dependencies.create("org.junit.jupiter:junit-jupiter-engine:" + version));
    }

    // TODO TestNG
    // TODO ??? public void mainAsClasses() { } vs mainAsJar()
    // TODO test fixtures?

    public void testsAsClasses(TaskProvider<Test> testTask) {
        testTask.configure(t -> t.setClasspath(sourceSet.getRuntimeClasspath()));
    }

    public void testsAsJar() {
        TaskProvider<Test> testTask = findTestTask();
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
}
