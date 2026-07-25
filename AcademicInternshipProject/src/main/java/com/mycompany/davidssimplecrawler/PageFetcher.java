/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.davidssimplecrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author david
 */
public class PageFetcher {

    // The User-Agent tells the website what "browser" is visiting.
    // Using a real browser User-Agent helps avoid being blocked.
    private String userAgent;

    public PageFetcher(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Connects to the given URL and returns the full HTML as a String.
     * Returns an error message string if something goes wrong.
     */
    public String fetchPage(String urlString) {
        StringBuilder htmlContent = new StringBuilder();

        try {
            // Create the URL object and open a connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request type and headers
            // GET means we are just reading the page, not sending data
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setConnectTimeout(5000); // 5 second timeout to connect
            connection.setReadTimeout(5000);    // 5 second timeout to read

            // Check if the server responded successfully
            // HTTP 200 means OK, anything else is an error
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "ERROR: Server returned HTTP " + responseCode;
            }

            // Read the response line by line and build it into one string
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line).append("\n");
            }
            //close the reader when done
            reader.close();

        } catch (IOException e) {
            //if anything goes wrong, return the error message instead of crashing
            return "ERROR fetching page: " + e.getMessage();
        }

        //return the full html as a string
        return htmlContent.toString();
    }
}