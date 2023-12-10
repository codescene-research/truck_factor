package org.example.Utils;

import java.util.Set;
import java.util.TreeSet;

public class Developers {
    Set developers = new TreeSet();

    public Developers() {
    }

    /**
     * Adds a developer to the develoepr set
     *
     * @param developerName
     */
    public void addDeveloper(String developerName) {
        developers.add(developerName);
    }

    /**
     * Validates that a developer name is a developer.
     *
     * @param developerName
     * @return
     */
    public boolean isDeveloper(String developerName) {
        return developers.contains(developerName);
    }

    /**
     * Removes a developer from the set.
     *
     * @param developerName
     */
    public void removeDeveloper(String developerName) {
        developers.remove(developerName);
    }

}
