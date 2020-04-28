package org.gradle.sample.javatesting.plugin;

import java.util.ArrayList;
import java.util.List;

public class TestSetSpec {

    private final List<String> tagsOrCategories = new ArrayList<>();
    private boolean testsAsJar;

    public void extraGroup(String tag) {
        tagsOrCategories.add(tag);
    }

    public void testsAsJar() {
        testsAsJar = true;
    }

    List<String> getTagsOrCategories() {
        return tagsOrCategories;
    }

    boolean isTestsAsJar() {
        return testsAsJar;
    }
}
