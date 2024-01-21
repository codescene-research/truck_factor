package src.main.java.org.example.Optimize;

import src.main.java.org.example.DO.CSAuthorDO;
import src.main.java.org.example.DO.FileListDO;
import src.main.java.org.example.Mappers.OnPremMapper;
import src.main.java.org.example.Utils.CSVWriter;
import src.main.java.org.example.Utils.Thresholds;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static src.main.java.org.example.Main.listAnalysisFilePaths;
import static src.main.java.org.example.Utils.Statistics.OpenSource.ActiveDevelopers.getActiveDevelopers;

public class OptimizeThresholds {
    static String RESULT_ORACLE_PATH = "InputData/result-oracle.csv";
    static String ANALYSES_PATH = "InputData/OnPremData";

    public static void runRemainingAuthorshipAndAuthorThreshold(Map<String, String> filePaths) {
        Map<String, Integer> resultOracle = generateResultOracle(RESULT_ORACLE_PATH);
        Map<String, Integer> tfResults = new HashMap<>();
        StringBuilder output = new StringBuilder();
        Map<String, Integer> repositoryProviderActiveDevelopers = getRepositoryProviderActiveDevelopers();

        String startRow = "remaining-authorship-threshold;authorship-percentage;correct-tf-estimations;percentage-diff;mean-absolute-diff;mean-square-diff\n";
        output.append(startRow);
        System.out.print(startRow);

        //Construct grid-search.
        for (Double remainingAuthorshipThreshold = 0.00; remainingAuthorshipThreshold <= 1.0; remainingAuthorshipThreshold += 0.01) {
            for (Double authorshipPercentage = 0.00; authorshipPercentage <= 1.0; authorshipPercentage += 0.01) {
                Thresholds.setRemainingAuthorsThreshold(remainingAuthorshipThreshold);
                Thresholds.setAuthorshipThreshold(authorshipPercentage);

                //For every repository
                for (Map.Entry<String, String> entity : filePaths.entrySet()) {
                    String repository = entity.getKey();
                    String filePath = entity.getValue();
                    //for dynamicly setting the threshold.
                    //Double activeDevelopers = Double.valueOf(repositoryProviderActiveDevelopers.get(repository));
                    //Double threshold = 0.5 / activeDevelopers;
                    //Thresholds.setAuthorshipOutliersThreshold(threshold);


                    FileListDO fileListDO = OnPremMapper.mapEntities(filePath);
                    Integer TF = fileListDO.algorithm2();
                    tfResults.put(repository, TF);
                }

                //Save results.
                DecimalFormat df = new DecimalFormat("0.00");
                Integer correctRepositories = calculateNumberOfCorrectResults(resultOracle, tfResults);
                Double percentageDiff = calculatePercentageDiff(resultOracle, tfResults);
                Double meanAbsoluteDiff = calculateMeanAbsoluteDiff(resultOracle, tfResults);
                Double meanSquareDiff = calculateMeanSquareDiff(resultOracle, tfResults);

                String result = String.format("%s;%s;%s;%s;%s;%s\n", df.format(remainingAuthorshipThreshold), df.format(authorshipPercentage), correctRepositories, df.format(percentageDiff), df.format(meanAbsoluteDiff), df.format(meanSquareDiff));
                output.append(result);
                System.out.print(result);
            }
        }
        CSVWriter.saveAsFile("TuneAlgo4", output.toString());

    }


    public static void runRemainingFilesWithAuthor(Map<String, String> filePaths) {
        Map<String, Integer> resultOracle = generateResultOracle(RESULT_ORACLE_PATH);
        Map<String, Integer> tfResults = new HashMap<>();

        Double authorshipPercentage = 0.0;
        Thresholds.setAuthorshipThreshold(authorshipPercentage);

        StringBuilder output = new StringBuilder();
        String firstRow = "remaining-authorship-threshold;authorship-percentage;correct-tf-estimations;percentage-diff;mean-absolute-diff;mean-square-diff\n";
        output.append(firstRow);
        System.out.println(firstRow);

        for (Double remainingAuthorshipThreshold = 0.00; remainingAuthorshipThreshold <= 1.0; remainingAuthorshipThreshold += 0.01) {
            Thresholds.setRemainingAuthorsThreshold(remainingAuthorshipThreshold);
            //For every repository
            for (Map.Entry<String, String> entity : filePaths.entrySet()) {
                String repository = entity.getKey();
                String filePath = entity.getValue();

                //FileListDOv2 fileListDO = EntityOwnershipMapperV2.map(filePath);
                LinkedList<Integer> list = OnPremMapper.developerImpactMap(filePath);
                CSAuthorDO csDeveloperDO = new CSAuthorDO(list);
                Integer TF = csDeveloperDO.calculateTF();
                tfResults.put(repository, TF);
            }

            DecimalFormat df = new DecimalFormat("0.00");
            Integer correctRepositories = calculateNumberOfCorrectResults(resultOracle, tfResults);
            Double percentageDiff = calculatePercentageDiff(resultOracle, tfResults);
            Double meanAbsoluteDiff = calculateMeanAbsoluteDiff(resultOracle, tfResults);
            Double meanErrorDiff = calculateMeanSquareDiff(resultOracle, tfResults);

            String result = String.format("%s;%s;%s;%s;%s;%s\n", df.format(remainingAuthorshipThreshold), df.format(authorshipPercentage), correctRepositories, df.format(percentageDiff), df.format(meanAbsoluteDiff), df.format(meanErrorDiff));
            output.append(result);
            System.out.print(result);
        }
        CSVWriter.saveAsFile("TuneAlgoCS", output.toString());

    }


    private static Integer calculateNumberOfCorrectResults(Map<String, Integer> resultOracle, Map<String, Integer> tfResults) {
        int correctResults = 0;
        for (Map.Entry<String, Integer> repoResult : tfResults.entrySet()) {
            String repo = repoResult.getKey();
            Integer repoTF = repoResult.getValue();

            Integer oracleResult = resultOracle.get(repo);
            if (oracleResult == repoTF) {
                correctResults++;
            }
        }
        return correctResults;
    }

    private static Double calculatePercentageDiff(Map<String, Integer> resultOracle, Map<String, Integer> tfResults) {
        int totalRepos = tfResults.size();
        int diff = 0;
        for (Map.Entry<String, Integer> repoResult : tfResults.entrySet()) {
            String repo = repoResult.getKey();
            Integer repoTF = repoResult.getValue();

            Integer oracleResult = resultOracle.get(repo);
            if (oracleResult != repoTF) {
                diff++;
            }
        }

        Double percentage = Double.valueOf(totalRepos - diff) / Double.valueOf(totalRepos);
        return percentage;
    }

    private static Double calculateMeanAbsoluteDiff(Map<String, Integer> resultOracle, Map<String, Integer> tfResults) {
        double absoulteDiff = 0.0;
        double numberOfIterations = 0.0;
        for (Map.Entry<String, Integer> repoResult : tfResults.entrySet()) {
            String repo = repoResult.getKey();
            Integer repoTF = repoResult.getValue();

            Integer oracleResults = resultOracle.get(repo);
            absoulteDiff += Math.abs(repoTF - oracleResults);
            numberOfIterations++;
        }

        return absoulteDiff / numberOfIterations;
    }

    private static Double calculateMeanSquareDiff(Map<String, Integer> resultOracle, Map<String, Integer> tfResults) {
        Double squareDiff = 0.0;
        Double numberOfResults = 0.0;

        for (Map.Entry<String, Integer> repoResult : tfResults.entrySet()) {
            String repo = repoResult.getKey();
            Integer repoTF = repoResult.getValue();
            Integer oracleResults = resultOracle.get(repo);

            squareDiff += Math.pow(Math.abs(repoTF - oracleResults), 2);
            numberOfResults++;
        }

        return squareDiff / numberOfResults;
    }

    private static Map<String, Integer> generateResultOracle(String resultOraclePath) {
        HashMap<String, Integer> resultOracle = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(resultOraclePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("System;Link;Language;Stars;Contributors;Age;TF;Date")) {
                    String[] lineData = line.split(";");
                    String repo = lineData[0].split("/")[1];
                    Integer TF = Integer.valueOf(lineData[6]);
                    resultOracle.put(repo, TF);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultOracle;
    }

    private static Map<String, Integer> getRepositoryProviderActiveDevelopers() {
        Map<String, String> filePaths = listAnalysisFilePaths(ANALYSES_PATH);
        return getActiveDevelopers(filePaths);

    }

}
