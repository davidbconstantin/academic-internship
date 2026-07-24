/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.davidssimplecrawler;

/**
 *
 * @author david
 */
import java.io.*;
import java.util.ArrayList;

public class CsvWriter {

    /**
     * Rewrites a csv file keeping only the rows whose indexes are in keepIndexes.
     * Row 0 in the list = first data row (not counting the header).
     * Always keeps the header row.
     */
    public void deleteRows(String filePath, ArrayList<Integer> keepIndexes) throws IOException {

        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) return;

        // read all lines including header
        ArrayList<String> allLines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            allLines.add(line);
        }
        reader.close();

        if (allLines.isEmpty()) return;

        // rewrite the file keeping only the header + rows in keepIndexes
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));

        // always write the header (line 0)
        writer.write(allLines.get(0));
        writer.newLine();

        // write only the data rows whose index is in keepIndexes
        // data rows start at allLines index 1, so data row i = allLines.get(i + 1)
        for (int i : keepIndexes) {
            int lineIndex = i + 1; // offset by 1 for the header
            if (lineIndex < allLines.size()) {
                writer.write(allLines.get(lineIndex));
                writer.newLine();
            }
        }

        writer.close();
    }

    /**
     * Deletes all rows from the csv file except the header.
     * Used for the "Delete All" option.
     */
    public void clearAllRows(String filePath) throws IOException {

        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) return;

        // read the header line only
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String header = reader.readLine();
        reader.close();

        if (header == null) return;

        // rewrite with just the header
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));
        writer.write(header);
        writer.newLine();
        writer.close();
    }
}