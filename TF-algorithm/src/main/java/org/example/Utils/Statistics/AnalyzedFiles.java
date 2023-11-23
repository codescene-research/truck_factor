package src.main.java.org.example.Utils.Statistics;

import src.main.java.org.example.DO.FileListDO;
import src.main.java.org.example.Mappers.OnPremMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static src.main.java.org.example.Main.listAnalysisFilePaths;


public class AnalyzedFiles {
    static String ANALYSES_PATH = "";

    public Map<String, Set<String>> getCSFiles() {
        Map<String, Set<String>> csFiles = new HashMap<>();
        Map<String, String> filePaths = listAnalysisFilePaths(ANALYSES_PATH);

        for (Map.Entry<String, String> entry : filePaths.entrySet()) {
            String repo = entry.getKey();
            FileListDO fileList = OnPremMapper.mapEntities(entry.getValue());
            Set<String> fileSet = fileList.getFiles();
            csFiles.put(repo, fileSet);
        }

        return csFiles;
    }
}
