/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.davidssimplecrawler;

/**
 *
 * @author david
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CsvReader {

    /**
     * Reads a csv file and returns each row as a string array.
     * Skips the header row.
     * Returns an empty list if the file doesnt exist yet.
     */
    public ArrayList<String[]> readCsv(String filePath) {
        ArrayList<String[]> rows = new ArrayList<>();

        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) return rows;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                rows.add(parseCsvLine(line));
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Error reading csv: " + e.getMessage());
        }

        return rows;
    }

    /**
     * Splits a csv line into columns.
     * Handles quoted fields so commas inside values dont break the split.
     */
    private String[] parseCsvLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString().trim());
        return fields.toArray(new String[0]);
    }
}