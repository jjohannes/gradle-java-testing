package org.gradle.sample.javatesting.plugin;

import java.util.ArrayList;
import java.util.List;

public class TestGroups {

    private final List<String> tagsOrCategories = new ArrayList<>();

    public void extraGroup(String tag) {
        tagsOrCategories.add(tag);
    }

    public List<String> getTagsOrCategories() {
        return tagsOrCategories;
    }
}
