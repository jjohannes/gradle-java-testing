package org.gradle.sample.javatesting.plugin;

import org.gradle.api.Project;
import org.gradle.api.Plugin;
import org.gradle.api.tasks.SourceSetContainer;

public class JavaTestingPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getPlugins().apply("java");
        project.getExtensions().create("javaTesting", JavaTestingExtension.class,
                project.getExtensions().getByType(SourceSetContainer.class), project);
    }
}
