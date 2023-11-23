package src.main.java.org.example.Utils.Statistics;


import src.main.java.org.example.DO.FileListDO;
import src.main.java.org.example.Utils.CSVWriter;

import java.util.Map;

public class AverageTopFileAuthorship {
    public static void saveAverageTopFileAuthorship(String fileName, Map<String, FileListDO> fileMap) {
        StringBuilder output = new StringBuilder();
        String startRow = "project-id;average-max-authorship\n";
        output.append(startRow);

        for (Map.Entry<String, FileListDO> entry : fileMap.entrySet()) {
            String projectId = entry.getKey();
            FileListDO fileList = entry.getValue();
            Double averageMaxAuthorship = fileList.getAverageMaxAuthorship();

            String result = String.format("%s;%s\n", projectId, averageMaxAuthorship);

            output.append(result);
        }
        System.out.println(output.toString());
        CSVWriter.saveAsFile(fileName, output.toString());
    }
}
