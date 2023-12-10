package org.example.Mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.DO.CSAuthorDO;
import org.example.DO.FileListDO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class OnPremMapper {

    public static Map<String, CSAuthorDO> developerImpactMapper(Map<String, String> filePaths) {
        Map<String, CSAuthorDO> returnMap = new HashMap<>();
        for (Map.Entry<String, String> entry : filePaths.entrySet()) {
            String repo = entry.getKey();
            String folderPath = entry.getValue();
            LinkedList<Integer> developerImpactList = developerImpactMap(folderPath);
            CSAuthorDO csAuthor = new CSAuthorDO(developerImpactList);

            returnMap.put(repo, csAuthor);
        }
        return returnMap;
    }

    public static LinkedList<Integer> developerImpactMap(String folderPath) {
        String developerImpactPath = folderPath + "/developer_impact.json";
        LinkedList<Integer> list = new LinkedList<>();

        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Read JSON file into JsonNode
            JsonNode jsonNode = objectMapper.readTree(new File(developerImpactPath));


            // If the JSON file contains an array
            if (jsonNode.isArray()) {
                for (JsonNode element : jsonNode) {
                    // Access elements in the array
                    int primaryAuthorOfFiles = element.get("primary-file-owner").asInt();
                    list.add(primaryAuthorOfFiles);
                }
            }
            return list;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static FileListDO mapEntities(String entryOwnershipPath) {
        FileListDO fileListDO = new FileListDO();
        //Read file
        try (BufferedReader br = new BufferedReader(new FileReader(entryOwnershipPath + "/entity-ownership.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("entity,author,author-added")) {
                    //Remove commas inside lines quotation marks, specifically for cases where more than
                    // one developer is part of a commit, for example "user1, user2"
                    String reformatedString = line.replaceAll("(\"[^\",]+),([^\"]+\")", " ");
                    String[] lineData = reformatedString.split(",");
                    String entity = lineData[0];
                    String author = lineData[1];
                    Double linesOfCode = Double.valueOf(lineData[2]);
                    Double authorship = Double.valueOf(lineData[3]);

                    fileListDO.addFileAuthorshipAndLOC(entity, author, authorship, linesOfCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileListDO;
    }

    public static Map<String, FileListDO> mapAnalysisMap(Map<String, String> fileMap) {
        HashMap<String, FileListDO> returnMap = new HashMap<>();

        for (Map.Entry<String, String> entry : fileMap.entrySet()) {
            String repo = entry.getKey();
            FileListDO fileList = mapEntities(entry.getValue());

            returnMap.put(repo, fileList);
        }
        return returnMap;

    }


}
