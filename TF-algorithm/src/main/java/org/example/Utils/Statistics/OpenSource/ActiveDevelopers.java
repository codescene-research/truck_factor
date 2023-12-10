package org.example.Utils.Statistics.OpenSource;

import org.example.Utils.CSVWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActiveDevelopers {

    public static void saveActiveDevelopers(Map<String, String> analysisPath) {
        StringBuilder output = new StringBuilder();
        output.append("repository;active-developers;developers\n");
        for (Map.Entry<String, String> entry : analysisPath.entrySet()) {
            String repo = entry.getKey();
            String dashboardPath = entry.getValue() + "/dashboard.csv";
            String activeDevelopers = getActiveDeveloper(dashboardPath);
            String allDevelopers = getAllDeveloper(dashboardPath);

            String result = String.format("%s;%s;%s\n", repo, activeDevelopers, allDevelopers);
            output.append(result);
        }
        CSVWriter.saveAsFile("developersOpenSource", output.toString());
    }

    public static Map<String, Integer> getActiveDevelopers(Map<String, String> analysisPath) {
        Map<String, Integer> repositoryProviderActiveDevelopers = new HashMap<>();
        for (Map.Entry<String, String> entry : analysisPath.entrySet()) {
            String repo = entry.getKey();
            String dashboardPath = entry.getValue() + "/dashboard.csv";
            Integer activeDevelopers = Integer.valueOf(getActiveDeveloper(dashboardPath));

            repositoryProviderActiveDevelopers.put(repo, activeDevelopers);
        }
        return repositoryProviderActiveDevelopers;

    }

    public static String getAllDeveloper(String dashboardPath) {

        try {
            String fileName = dashboardPath; // Replace with the path to your file
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read and process the file content
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Do something with the line, e.g., print it
                if (line.contains("authors")) {
                    bufferedReader.close();
                    return line.split(",")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "something";

    }

    public static String getActiveDeveloper(String dashboardPath) {

        try {
            String fileName = dashboardPath; // Replace with the path to your file
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read and process the file content
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Do something with the line, e.g., print it
                if (line.contains("activeauthors")) {
                    bufferedReader.close();
                    return line.split(",")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "something";

    }
}
