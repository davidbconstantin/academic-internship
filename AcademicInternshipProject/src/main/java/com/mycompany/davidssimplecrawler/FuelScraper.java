/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.davidssimplecrawler;

/**
 *
 * @author david
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FuelScraper {

    // csv file saved next to where the app runs
    private static final String CSV_FILE = "fuel_prices.csv";

    // timestamp format used in the csv
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Scrapes fuel prices from pickapump html.
     *
     * Structure confirmed from the real html file:
     *   ul.no-bullets > li           - one li per station
     *     h5                         - station name
     *     div.fuel-price-item        - one per fuel type
     *       span.fuel-type           - e.g. Diesel, Petrol
     *       div.price > span.digit   - price split across individual digit spans
     *
     * The price digits are joined manually because pickapump renders
     * each digit as a separate span e.g. 1, 7, 4, ., 9, c
     */
    public ArrayList<FuelEntry> scrapePrices(String html) {
        ArrayList<FuelEntry> entries = new ArrayList<>();

        // stamp every entry from this scrape with the same time
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);

        Document doc = Jsoup.parse(html);

        // each li in ul.no-bullets is one station
        Elements stationItems = doc.select("ul.no-bullets > li");

        for (Element station : stationItems) {

            // skip ad slots - they have class listing-inline-ad, no h5
            Element nameEl = station.selectFirst("h5");
            if (nameEl == null) continue;

            String stationName = nameEl.text().trim();

            // get the address from the first p tag after the h5
            String address = "";
            Element addressEl = station.selectFirst("p");
            if (addressEl != null) {
                address = addressEl.text().trim();
            }

            // each fuel-price-item is one fuel type at this station
            Elements fuelRows = station.select("div.fuel-price-item");

            for (Element row : fuelRows) {

                // get the fuel type label
                String fuelType = "";
                Element fuelTypeEl = row.selectFirst("span.fuel-type");
                if (fuelTypeEl != null) {
                    fuelType = fuelTypeEl.text().trim();
                }

                // the price is split across multiple span.digit elements
                // e.g. <span>1</span><span>7</span><span>4</span><span>.</span><span>9</span><span>c</span>
                // so we join all the digit spans together to get "174.9c"
                String price = "";
                Element priceDiv = row.selectFirst("div.price");
                if (priceDiv != null) {
                    Elements digitSpans = priceDiv.select("span.digit");
                    StringBuilder priceBuilder = new StringBuilder();
                    for (Element digit : digitSpans) {
                        priceBuilder.append(digit.text());
                    }
                    price = priceBuilder.toString().trim();
                }

                // skip if either value is missing
                if (fuelType.isEmpty() || price.isEmpty()) continue;

                entries.add(new FuelEntry(timestamp, stationName, address, fuelType, price));
            }
        }

        return entries;
    }

    /**
     * Saves a list of fuel entries to the csv file.
     * Writes the header only on first run.
     * Appends so old scrapes are kept.
     */
    public void saveToCsv(ArrayList<FuelEntry> entries) throws IOException {

        java.io.File file = new java.io.File(CSV_FILE);
        boolean fileExists = file.exists();

        // open in append mode so we dont overwrite old data
        BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true));

        // write header on first run only
        if (!fileExists) {
            writer.write("Timestamp,Station,Address,Fuel Type,Price");
            writer.newLine();
        }

        for (FuelEntry entry : entries) {
            writer.write(entry.toCsvRow());
            writer.newLine();
        }

        writer.close();
    }

    /**
     * Returns the full path of the csv file.
     */
    public String getCsvFilePath() {
        return new java.io.File(CSV_FILE).getAbsolutePath();
    }

    // ── inner class - holds one row of scraped data ──────────────────

    /**
     * Holds data for one fuel price entry.
     * Now includes address as well as station name.
     */
    public static class FuelEntry {

        public String timestamp;
        public String stationName;
        public String address;
        public String fuelType;
        public String price;

        public FuelEntry(String timestamp, String stationName, String address,
                         String fuelType, String price) {
            this.timestamp = timestamp;
            this.stationName = stationName;
            this.address = address;
            this.fuelType = fuelType;
            this.price = price;
        }

        // formats as a csv row - wraps fields in quotes so commas dont break the csv
        public String toCsvRow() {
            return "\"" + timestamp + "\","
                 + "\"" + stationName.replace("\"", "'") + "\","
                 + "\"" + address.replace("\"", "'") + "\","
                 + "\"" + fuelType + "\","
                 + "\"" + price + "\"";
        }
    }
}