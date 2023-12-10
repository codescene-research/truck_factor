package org.example.Mappers;

import org.example.DO.FileListDO;
import org.example.Utils.CSVWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CloudMapper {
    public static void init(String filePath) {

        calculateTFs(filePath);

    }

    public static void saveAverageMaxAuthorship(String filePath, String fileName) {
        StringBuilder output = new StringBuilder();
        output.append("repository;average-max-authorship");

        Map<String, FileListDO> fileMap = mapEntities(filePath);
        for (Map.Entry<String, FileListDO> entry : fileMap.entrySet()) {
            String repository = entry.getKey();
            FileListDO fileList = entry.getValue();
            double averageMaxAuthorship = fileList.getAverageMaxAuthorship();
            String result = String.format("%s;%s\n", repository, averageMaxAuthorship);
            output.append(result);
        }
        System.out.println(output);
        CSVWriter.saveAsFile(fileName, output.toString());
    }

    public static void saveParetoDevelopers(String filePath, String fileName) {
        StringBuilder output = new StringBuilder();
        output.append("repository;pareto-developers\n");

        Map<String, FileListDO> fileMap = mapEntities(filePath);
        for (Map.Entry<String, FileListDO> entry : fileMap.entrySet()) {
            String repository = entry.getKey();
            FileListDO fileList = entry.getValue();
            int numberOfParetoDevelopers = fileList.getNumberOfParetoDevelopers();
            String result = String.format("%s;%s\n", repository, numberOfParetoDevelopers);
            output.append(result);
        }

        System.out.println(output);
        CSVWriter.saveAsFile(fileName, output.toString());
    }


    public static Map<String, FileListDO> mapEntities(String filePath) {
        Map<String, FileListDO> fileMap = new HashMap<>();
        //Read file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentProjectId = "NoProjectAtStart";

            StringBuilder sb = new StringBuilder();
            sb.append("projectId;jobId;TFAllFiles;TFFilteredFiles;TFPrimaryFileOwner;TFKnowlege\n");
            while ((line = br.readLine()) != null) {
                if (!line.contains("\"project_id\",\"job_id\",\"entity\",\"author\",\"author_added\",\"author_deleted\",\"added_ownership\",\"deleted_ownership\"")) {
                    //Read row info, remove " characters.
                    String[] lineData = line.split(",");
                    String projectId = lineData[0].replaceAll("\"", "");
                    String jobId = lineData[1].replaceAll("\"", "");
                    String entity = lineData[2].replaceAll("\"", "");
                    String author = lineData[3].replaceAll("\"", "");
                    Double linesOfCode = Double.valueOf(lineData[4].replaceAll("\"", ""));
                    Double authorship = Double.valueOf(lineData[6].replaceAll("\"", ""));

                    fileMap.putIfAbsent(projectId, new FileListDO());
                    FileListDO fileList = fileMap.get(projectId);
                    fileList.addFileAuthorshipAndLOC(entity, author, authorship, linesOfCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileMap;
    }

    private static void calculateTFs(String filePath) {
        StringBuilder output = new StringBuilder();
        output.append("repository;algorithm1-tf\n");

        Map<String, FileListDO> fileMapAlgorithm1 = mapEntities(filePath);

        for (Map.Entry<String, FileListDO> entry : fileMapAlgorithm1.entrySet()) {
            String repository = entry.getKey();
            FileListDO fileListAlgorithm1 = fileMapAlgorithm1.get(repository);

            Integer algorithm1TF = fileListAlgorithm1.algorithm1();

            String result = String.format("%s;%s\n", repository, algorithm1TF);
            output.append(result);
        }
        System.out.println(output.toString());
        CSVWriter.saveAsFile("openSourceTF", output.toString());
    }
}
