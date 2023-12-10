package org.example.Utils;

public class Thresholds {
    static private Double authorshipThreshold = 0.0;
    static private Double authorshipOutliersThreshold = 0.1;
    static private Double remainingAuthorsThreshold = 0.0;
    static private Double remainingKnowledge = 0.0;
    static private Double linesOfCodeAuthorship = 0.8;

    public static Double getAuthorshipThreshold() {
        return Double.valueOf(authorshipThreshold);
    }

    public static void setAuthorshipThreshold(Double authorshipThreshold) {
        Thresholds.authorshipThreshold = authorshipThreshold;
    }

    public static Double getAuthorshipOutliersThreshold() {
        return authorshipOutliersThreshold;
    }

    public static void setAuthorshipOutliersThreshold(Double authorshipOutliersThreshold) {
        Thresholds.authorshipOutliersThreshold = authorshipOutliersThreshold;
    }

    public static Double getRemainingAuthorsThreshold() {
        return remainingAuthorsThreshold;
    }

    public static void setRemainingAuthorsThreshold(Double remainingAuthorsThreshold) {
        Thresholds.remainingAuthorsThreshold = remainingAuthorsThreshold;
    }

    public static Double getRemainingKnowledge() {
        return remainingKnowledge;
    }

    public static void setRemainingKnowledge(Double remainingKnowledge) {
        Thresholds.remainingKnowledge = remainingKnowledge;
    }

    public static Double getLinesOfCodeAuthorship() {
        return linesOfCodeAuthorship;
    }

    public static void setLinesOfCodeAuthorship(Double linesOfCodeAuthorship) {
        Thresholds.linesOfCodeAuthorship = linesOfCodeAuthorship;
    }
}
