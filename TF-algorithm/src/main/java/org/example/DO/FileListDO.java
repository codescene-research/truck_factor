package src.main.java.org.example.DO;



import src.main.java.org.example.Utils.Developers;
import src.main.java.org.example.Utils.Thresholds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FileListDO {
    private Developers developers = new Developers();
    private Map<String, FileDO> files = new HashMap<>();

    public FileListDO() {
    }


    /**
     * Removes the developer who has the most authored files.
     */
    public void removeDeveloperWithMostAuthoredFiles() {
        Map<String, Integer> developerProviderNumberOfAuthoredFiles = summarizeNumberOfAuthoredFilesForDevelopers();

        String developerWithMostAuthoredFiles = developerProviderNumberOfAuthoredFiles.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        developers.removeDeveloper(developerWithMostAuthoredFiles);

        updateFiles();
    }

    /**
     * Adds the authorship to a file.
     *
     * @param filePath
     * @param developer
     * @param authorship
     */
    public void addFileAuthorshipAndLOC(String filePath, String developer, Double authorship, Double linesOfCode) {
        files.putIfAbsent(filePath, new FileDO());
        FileDO fileDO = files.get(filePath);
        fileDO.addAuthorship(developer, authorship);
        fileDO.addLinesOfCode(developer, linesOfCode);

        developers.addDeveloper(developer);

    }

    public void updateFiles() {
        for (Map.Entry<String, FileDO> entry : files.entrySet()) {
            FileDO fileDO = entry.getValue();
            fileDO.updateFile(developers);
        }
    }

    /**
     * Returns a summarization of developers and how many files they are the author of.
     *
     * @return
     */
    public Map<String, Integer> summarizeNumberOfAuthoredFilesForDevelopers() {

        Map<String, Integer> developerProviderNumberOfAuthoredFiles = new HashMap<>();

        //For each file
        for (Map.Entry<String, FileDO> fileEntry : files.entrySet()) {
            FileDO fileDO = fileEntry.getValue();

            //For each developer
            Map<String, Double> fileAuthorship = fileDO.getAuthorship();
            for (Map.Entry<String, Double> authorshipEntry : fileAuthorship.entrySet()) {
                String developerName = authorshipEntry.getKey();
                developerProviderNumberOfAuthoredFiles.putIfAbsent(developerName, 0);

                Integer authoredFiles = developerProviderNumberOfAuthoredFiles.get(developerName);
                developerProviderNumberOfAuthoredFiles.put(developerName, authoredFiles + 1);
            }
        }

        return developerProviderNumberOfAuthoredFiles;
    }

    /**
     * Returns a summarize of developers total authorship
     *
     * @return
     */
    public Map<String, Double> summarizeDevelopersAuthorship() {

        Map<String, Double> developerProviderAuthorship = new HashMap<>();

        //For each file
        for (Map.Entry<String, FileDO> fileEntry : files.entrySet()) {
            FileDO fileDO = fileEntry.getValue();

            //For each developer
            Map<String, Double> fileAuthorship = fileDO.getAuthorship();
            for (Map.Entry<String, Double> authorshipEntry : fileAuthorship.entrySet()) {
                String developerName = authorshipEntry.getKey();
                Double developerAuthorship = authorshipEntry.getValue();

                developerProviderAuthorship.putIfAbsent(developerName, 0.0);

                Double currentAuthorship = developerProviderAuthorship.get(developerName);
                developerProviderAuthorship.put(developerName, currentAuthorship + developerAuthorship);
            }
        }

        return developerProviderAuthorship;
    }

    /**
     * Remove the developer who has the most authorship
     */

    public void removeDeveloperWithMostAuthorship() {
        Map<String, Double> developerProviderAuthorship = summarizeDevelopersAuthorship();

        String developerWithMostAuthoredFiles = developerProviderAuthorship.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        developers.removeDeveloper(developerWithMostAuthoredFiles);

        updateFiles();

    }

    /**
     * @return the number of files.
     */
    public int getNumberOfFiles() {
        return files.size();
    }

    public Integer getNumberOfFilesWithAuthor() {

        Integer numberOfFiles = 0;
        for (Map.Entry<String, FileDO> entry : files.entrySet()) {
            FileDO fileDO = entry.getValue();
            if (fileDO.hasAuthor()) {
                numberOfFiles++;
            }
        }
        return numberOfFiles;
    }

    public Set<String> getFiles() {
        return files.keySet();
    }

    /**
     * A method for returning the current authorship.
     *
     * @return
     */

    public Double getCurrentAuthorship() {
        Double currentAuthorship = 0.0;

        //For every file
        for (Map.Entry<String, FileDO> entry : files.entrySet()) {
            FileDO fileDO = entry.getValue();

            //for every developer
            currentAuthorship += fileDO.getAuthorship().values().stream().mapToDouble(Double::doubleValue).sum();
        }
        return currentAuthorship;
    }

    public Double getAverageMaxAuthorship() {
        Double authorship = 0.0;
        Integer numberOfFiles = 0;

        for (FileDO fileDO : files.values()) {
            Map<String, Double> fileAuthorship = fileDO.getAuthorship();
            Double topAuthorship = fileAuthorship.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
            authorship += topAuthorship;
            numberOfFiles++;
        }
        return authorship / Double.valueOf(numberOfFiles);
    }

    public int getNumberOfParetoDevelopers() {
        double totLinesOfCode = summarizeDevelopersLinesOfCode().entrySet().stream().mapToDouble(Map.Entry::getValue).sum();
        double currentLinesOfCode = summarizeDevelopersLinesOfCode().entrySet().stream().mapToDouble(Map.Entry::getValue).sum();

        int currentNumberOfDevelopers = 0;
        while (currentLinesOfCode / totLinesOfCode > 0.2) {
            currentNumberOfDevelopers++;
            removeDeveloperWithMostLOC();
            currentLinesOfCode = summarizeDevelopersLinesOfCode().entrySet().stream().mapToDouble(Map.Entry::getValue).sum();
        }


        return currentNumberOfDevelopers;
    }

    public void removeDeveloperWithMostLOC() {
        Map<String, Double> developerProviderLOC = summarizeDevelopersLinesOfCode();

        String developerWithMostLOC = developerProviderLOC.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        developers.removeDeveloper(developerWithMostLOC);

        updateFiles();

    }

    public Map<String, Double> summarizeDevelopersLinesOfCode() {
        Map<String, Double> result = new HashMap<>();
        for (FileDO file : files.values()) {
            Map<String, Double> developerProviderLinesOfCode = file.getLinesOfCode();

            for (Map.Entry<String, Double> entry : developerProviderLinesOfCode.entrySet()) {
                String developer = entry.getKey();
                Double LOC = entry.getValue();
                result.putIfAbsent(developer, 0.0);

                Double currentLOC = result.get(developer);
                result.put(developer, currentLOC + LOC);
            }
        }
        return result;
    }

    /**
     * Remove developers who is author of less than a percentage of the total files
     */

    public void removeOutliers() {
        Map<String, Integer> developersAuthorship = summarizeNumberOfAuthoredFilesForDevelopers();
        Integer totNumberOfFiles = getNumberOfFiles();

        List<String> outliers = developersAuthorship.entrySet()
                .stream().filter(p -> Double.valueOf(p.getValue()) / Double.valueOf(totNumberOfFiles) < Thresholds.getAuthorshipOutliersThreshold())
                .map(Map.Entry::getKey).collect(Collectors.toList());

        outliers.stream().forEach(developers::removeDeveloper);

    }


    public Integer algorithm1() {
        Thresholds.setAuthorshipThreshold(0.30);
        Thresholds.setRemainingAuthorsThreshold(0.50);
        updateFiles();
        Double totFiles = Double.valueOf(getNumberOfFiles());
        Double filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

        Integer TF = 0;

        while (filesWithAuthor / totFiles > Thresholds.getRemainingAuthorsThreshold()) {
            removeDeveloperWithMostAuthoredFiles();

            filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

            TF++;

        }
        return TF;
    }

    public Integer algorithm2() {
        //Thresholds.setAuthorshipThreshold(0.77);
        //Thresholds.setRemainingAuthorsThreshold(0.27);
        removeOutliers();
        updateFiles();
        Double totFiles = Double.valueOf(getNumberOfFiles());
        Double filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

        Integer TF = 0;

        while (filesWithAuthor / totFiles > Thresholds.getRemainingAuthorsThreshold()) {
            removeDeveloperWithMostAuthoredFiles();

            filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

            TF++;

        }
        return TF;
    }

    public Integer algorithm3() {
        Thresholds.setAuthorshipThreshold(0.71);
        Thresholds.setRemainingAuthorsThreshold(0.49);
        
        updateFiles();
        Double totFiles = Double.valueOf(getNumberOfFiles());
        Double startFiles = Double.valueOf(getNumberOfFilesWithAuthor());
        Double filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

        Integer TF = 0;

        while (filesWithAuthor / startFiles > Thresholds.getRemainingAuthorsThreshold()) {
            removeDeveloperWithMostAuthoredFiles();

            filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

            TF++;

        }
        return TF;
    }

    /*
    public Integer algorithm2() {
        //Thresholds.setAuthorshipThreshold(0.95);
        //Thresholds.setRemainingAuthorsThreshold(0.51);
        updateFiles();
        Double totFilesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());
        Double filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

        Integer TF = 0;

        while (filesWithAuthor / totFilesWithAuthor > Thresholds.getRemainingAuthorsThreshold()) {
            removeDeveloperWithMostAuthoredFiles();

            filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

            TF++;
        }

        return TF;
    }

     */

    public Integer algorithm4() {
        //Thresholds.setAuthorshipThreshold(0.77);
        //Thresholds.setRemainingAuthorsThreshold(0.27);

        for (Map.Entry<String, FileDO> entry : files.entrySet()) {
            FileDO file = entry.getValue();
            file.keepOnlyDeveloperWithMostAuthorship();
        }

        Double totFiles = Double.valueOf(getNumberOfFiles());
        Double filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

        Integer TF = 0;

        while (filesWithAuthor / totFiles > Thresholds.getRemainingAuthorsThreshold()) {
            removeDeveloperWithMostAuthoredFiles();

            filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

            TF++;

        }
        return TF;
    }

    /*
    public Integer algorithm3() {
        //Thresholds.setAuthorshipThreshold(0.50);
        //Thresholds.setRemainingAuthorsThreshold(0.57);
        updateFiles();
        Double totFilesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());
        Double filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

        Integer TF = 0;

        while (filesWithAuthor / totFilesWithAuthor > Thresholds.getRemainingAuthorsThreshold()) {
            removeDeveloperWithMostAuthoredFiles();

            filesWithAuthor = Double.valueOf(getNumberOfFilesWithAuthor());

            TF++;
        }

        return TF;

    }

     */

    /*
    public Integer algorithm4() {
        //Thresholds.setRemainingAuthorsThreshold(0.5);
        //Thresholds.setAuthorshipThreshold(0.0);
        //Thresholds.setRemainingKnowledge(0.5);
        updateFiles();
        Double totAuthorship = getCurrentAuthorship();
        Double currentAuthorship = getCurrentAuthorship();

        Integer TF = 0;
        while (currentAuthorship / totAuthorship > Thresholds.getRemainingKnowledge()) {
            removeDeveloperWithMostAuthorship();
            currentAuthorship = getCurrentAuthorship();

            TF++;
        }

        return TF;
    }

     */
}
