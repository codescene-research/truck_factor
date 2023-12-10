package org.example.Utils;

import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

    public static void saveAsFile(String fileName, String data) {
        String filepath = "CSVOutputs/" + fileName + ".csv";

        try {
            FileWriter writer = new FileWriter(filepath, false); // The second parameter (true) is used to append to an existing file

            // Write the string to the CSV file
            writer.write(data);

            // Add a newline character to separate different lines in the CSV file
            writer.write("\n");

            // Close the writer to save the changes
            writer.close();

            System.out.println("Data has been written to " + filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
