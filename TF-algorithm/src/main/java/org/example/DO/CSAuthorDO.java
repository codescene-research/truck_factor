package org.example.DO;

import org.example.Utils.Thresholds;

import java.util.Collections;
import java.util.LinkedList;

public class CSAuthorDO {
    LinkedList<Integer> authoredFiles;

    public CSAuthorDO(LinkedList<Integer> list) {
        authoredFiles = list;
    }


    public void addDeveloperFiles(Integer numberOfFiles) {
        authoredFiles.add(numberOfFiles);
    }

    private int getSum() {
        return authoredFiles.stream().mapToInt(Integer::intValue).sum();
    }

    public int calculateTF() {
        int tf = 0;
        Collections.sort(authoredFiles, Collections.reverseOrder());
        Double startingFiles = Double.valueOf(getSum());
        Double currentFiles = Double.valueOf(getSum());
        while (currentFiles / startingFiles > Thresholds.getRemainingAuthorsThreshold()) {
            authoredFiles.pop();
            tf++;
            currentFiles = Double.valueOf(getSum());
            if (authoredFiles.isEmpty()) {
                break;
            }
        }
        return tf;
    }
}
