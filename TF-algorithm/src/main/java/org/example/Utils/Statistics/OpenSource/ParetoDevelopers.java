package org.example.Utils.Statistics.OpenSource;

import org.example.DO.FileListDO;
import org.example.Utils.CSVWriter;
import org.example.Mappers.OnPremMapper;

import java.util.HashMap;
import java.util.Map;

import static org.example.Main.listAnalysisFilePaths;

public class ParetoDevelopers {

    public static void run(String analysisPath) {
        Map<String, String> filePaths = listAnalysisFilePaths(analysisPath);

        Map<String, FileListDO> files = new HashMap<>();
        for (Map.Entry<String, String> entry : filePaths.entrySet()) {
            String repository = entry.getKey();
            String filePath = entry.getValue();
            FileListDO fileList = OnPremMapper.mapEntities(filePath);
            files.put(repository, fileList);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("repository;pareto-developers\n");
        for (Map.Entry<String, FileListDO> entry : files.entrySet()) {
            String repository = entry.getKey();
            FileListDO fileList = entry.getValue();

            int numberOfParetoDevelopers = fileList.getNumberOfParetoDevelopers();
            String result = String.format("%s;%s\n", repository, numberOfParetoDevelopers);
            sb.append(result);
            //System.out.println(String.format("%s;%s", repository, numberOfParetoDevelopers));
        }
        CSVWriter.saveAsFile("paretoDevelopers", sb.toString());
    }
}
