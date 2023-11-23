package src.main.java.org.example;


import src.main.java.org.example.DO.CSAuthorDO;
import src.main.java.org.example.DO.FileListDO;
import src.main.java.org.example.Mappers.CloudMapper;
import src.main.java.org.example.Mappers.OnPremMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static String ANALYSES_PATH = "InputData/OnPremData"; //The path to the folder which contains all the on-prem analyses
    static String CS_DATA_CS = "InputData/CloudData/truck_factor__entity_ownership.csv";
    static String CS_DATA_OSS = "InputData/CloudData/truck_factor__entity_ownership__oss.csv";

    public static void main(String[] args) {

        //For retrieveing TF values
        String resultFerreira = getTFResultFromOnPremData("TF_Ferreira", ANALYSES_PATH);
        System.out.println("TF results for Ferreira:");
        System.out.print(resultFerreira);

        String resultCS = getTFResultFromCloudData("TF_CS_CS", CS_DATA_CS);
        System.out.println("TF result for Closed Source projects:");
        System.out.println(resultCS);

        String resultOS = getTFResultFromCloudData("TF_CS_OSS", CS_DATA_OSS);
        System.out.println("Result for proprietary projects:");
        System.out.println(resultOS);

        //listAnalysisFilePaths

        //For saving.
        //CSVWriter.saveAsFile("TF_CS_CS_ALGO3", resultCS)
        //CSVWriter.saveAsFile("TF_CS_CS_ALGO3", resultCS);
        //CSVWriter.saveAsFile("TF_CS_OS_ALGO3", resultOS);

        //For optimizing
        //OptimizeThresholds.runRemainingAuthorshipAndAuthorThreshold(listAnalysisFilePaths(ANALYSES_PATH));

    }


    public static String getTFResultFromOnPremData(String filename, String data_folder_filepath) {
        Map<String, String> filePaths = listAnalysisFilePaths(data_folder_filepath);
        Map<String, FileListDO> repoProviderFileListDO = OnPremMapper.mapAnalysisMap(filePaths);

        StringBuilder output = new StringBuilder();
        output.append("repo,tf\n");

        for (Map.Entry<String, FileListDO> entry : repoProviderFileListDO.entrySet()) {
            String repo = entry.getKey();
            FileListDO fileList = entry.getValue();

            Integer tf = fileList.algorithm1();
            output.append(String.format("%s,%s\n", repo, tf));
        }

        return output.toString();


    }

    ;

    public static String getAlgoCSTFResult(String analyses_path) {
        Map<String, String> feirraAnalysReults = listAnalysisFilePaths(analyses_path);
        Map<String, CSAuthorDO> csPrimaryOwners = OnPremMapper.developerImpactMapper(feirraAnalysReults);
        StringBuilder output = new StringBuilder();
        output.append("repo,TF\n");

        for (Map.Entry<String, CSAuthorDO> entry : csPrimaryOwners.entrySet()) {
            String repo = entry.getKey();
            Integer tf = entry.getValue().calculateTF();

            output.append(String.format("%s,%s\n", repo, tf));
        }

        return output.toString();


    }

    public static Map<String, String> listAnalysisFilePaths(String folderPath) {
        Map<String, String> repositoryMapPath = new HashMap<>();
        File folder = new File(folderPath);
        //Get filepaths
        if (folder.isDirectory()) {
            File[] subdirectories = folder.listFiles(File::isDirectory);

            if (subdirectories != null) {
                for (File subdirectory : subdirectories) {
                    File[] analysises = subdirectory.listFiles(File::isDirectory);

                    //Get the latest analysis
                    File lastCreatedAnalysis = null;
                    long lastModifiedTime = Long.MIN_VALUE;

                    for (File analysis : analysises) {
                        long modifiedTime = subdirectory.lastModified();
                        if (modifiedTime > lastModifiedTime) {
                            lastModifiedTime = modifiedTime;
                            lastCreatedAnalysis = analysis;
                        }
                    }
                    String filePath = lastCreatedAnalysis.getAbsolutePath();

                    repositoryMapPath.put(subdirectory.getName(), filePath);
                }
            }
        }

        return repositoryMapPath;
    }

    public static String getTFResultFromCloudData(String fileName, String data_filepath) {
        Map<String, FileListDO> reposMapper = CloudMapper.mapEntities(data_filepath);
        StringBuilder output = new StringBuilder();
        output.append("project_id,tf\n");
        for (Map.Entry<String, FileListDO> entity : reposMapper.entrySet()) {
            String repo = entity.getKey();
            FileListDO fileList = entity.getValue();

            int tf = fileList.algorithm3();

            String row = String.format("%s,%s\n", repo, tf);
            output.append(row);
        }

        return output.toString();
    }
}