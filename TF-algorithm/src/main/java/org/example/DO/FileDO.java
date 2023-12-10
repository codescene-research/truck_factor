package org.example.DO;

import org.example.Utils.Thresholds;
import org.example.Utils.Developers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class FileDO {
    private Map<String, Double> authorship = new HashMap<>();
    private Map<String, Double> linesOfCode = new HashMap<>();

    public FileDO() {
    }

    /**
     * Adds authorship to a file
     *
     * @param developer
     * @param authorship
     */
    public void addAuthorship(String developer, Double authorship) {
        this.authorship.putIfAbsent(developer, authorship);
    }

    public void addLinesOfCode(String developer, Double authorship) {
        this.linesOfCode.putIfAbsent(developer, authorship);
    }

    /**
     * Updates the authorship and lines of code.
     *
     * @param developerSet
     */
    public void updateFile(Developers developerSet) {
        Iterator<Map.Entry<String, Double>> iteratorAuthorship = authorship.entrySet().iterator();

        while (iteratorAuthorship.hasNext()) {
            Map.Entry<String, Double> entry = iteratorAuthorship.next();
            String developer = entry.getKey();
            Double authorship = entry.getValue();

            if (!developerSet.isDeveloper(developer) || authorship < Thresholds.getAuthorshipThreshold()) {
                iteratorAuthorship.remove();
            }
        }

        Iterator<Map.Entry<String, Double>> iteratorLinesOfCode = linesOfCode.entrySet().iterator();
        while (iteratorLinesOfCode.hasNext()) {
            Map.Entry<String, Double> entry = iteratorLinesOfCode.next();
            String developer = entry.getKey();
            Double authorship = entry.getValue();

            if (!developerSet.isDeveloper(developer) && authorship < Thresholds.getAuthorshipThreshold()) {
                iteratorLinesOfCode.remove();
            }
        }
    }

    /**
     * Checks if a file has an author.
     *
     * @return
     */
    public boolean hasAuthor() {
        return !this.authorship.isEmpty();
    }

    /**
     * Returns the files authorship.
     *
     * @return
     */
    public Map<String, Double> getAuthorship() {
        return this.authorship;
    }

    /**
     * Return the lines of code the developers has written.
     */
    public Map<String, Double> getLinesOfCode() {
        return linesOfCode;
    }

    public void keepOnlyDeveloperWithMostAuthorship() {
        Optional<Map.Entry<String, Double>> developer = authorship.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        Map.Entry<String, Double> entry = developer.get();

        String developerName = entry.getKey();
        Double developerValue = entry.getValue();
        authorship = new HashMap<>();
        authorship.put(developerName, developerValue);

    }

}
