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

public class ProductScraper {

    // csv file for product prices saved next to where the app runs
    private static final String CSV_FILE = "product_prices.csv";

    // timestamp format used in the csv
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // the search url templates for each supermarket
    // {QUERY} gets replaced with the product name the user types
    public static final String[][] SUPERMARKETS = {
        {
            "Aldi Ireland",
            "https://www.aldi.ie/search?q={QUERY}"
        },
        {
            "Lidl Ireland",
            "https://www.lidl.ie/search?q={QUERY}&action=list"
        },
        {
            "Tesco Ireland",
            "https://www.tesco.ie/groceries/en-IE/search?query={QUERY}"
        },
        {
            "Dunnes Stores",
            "https://www.dunnesstores.com/search?q={QUERY}"
        },
        {
            "SuperValu",
            "https://shop.supervalu.ie/sm/delivery/rsid/5550/results?q={QUERY}"
        },
    };

    /**
     * Builds the search url for a given supermarket and product query.
     * Replaces spaces with + for use in urls.
     */
    public String buildSearchUrl(String urlTemplate, String query) {
        // replace spaces with + for the url query string
        String encoded = query.trim().replace(" ", "+");
        return urlTemplate.replace("{QUERY}", encoded);
    }

    /**
     * Scrapes product results from a rendered html page.
     * Tries multiple selector patterns to match different supermarket layouts.
     * Returns a list of ProductEntry objects.
     */
    public ArrayList<ProductEntry> scrapeProducts(String html, String supermarket, String searchQuery) {
        ArrayList<ProductEntry> entries = new ArrayList<>();

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);

        Document doc = Jsoup.parse(html);

        // ── Aldi Ireland ─────────────────────────────────────────────
        // aldi uses article tags for each product card
        if (supermarket.contains("Aldi")) {
            Elements cards = doc.select("article");
            for (Element card : cards) {
                String name = "";
                Element nameEl = card.selectFirst("h2, .product-tile__name, [class*='name']");
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                // aldi price is in ins.base-price__discounted or .base-price__regular
                String price = "";
                Element priceEl = card.selectFirst("ins.base-price__discounted, .base-price__regular");
                if (priceEl != null) price = priceEl.text().trim();
                if (price.isEmpty()) continue;

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        // ── Lidl Ireland ─────────────────────────────────────────────
        // lidl uses a grid of product tiles
        else if (supermarket.contains("Lidl")) {
            Elements cards = doc.select("[class*='product'], [class*='Product'], .s-grid__item");
            for (Element card : cards) {
                String name = "";
                Element nameEl = card.selectFirst("h3, h2, [class*='title'], [class*='name']");
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                String price = "";
                Element priceEl = card.selectFirst("[class*='price'], [class*='Price']");
                if (priceEl != null) price = priceEl.text().trim();
                if (price.isEmpty()) continue;

                // clean up price - keep only up to 20 chars to avoid grabbing whole paragraphs
                if (price.length() > 20) price = price.substring(0, 20).trim();

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        // ── Tesco Ireland ─────────────────────────────────────────────
        // tesco uses product tiles with data-auto attributes
        else if (supermarket.contains("Tesco")) {
            Elements cards = doc.select("[data-auto='product-tile'], .product-list--list-item, " +
                                        "[class*='product-list'], li.product-list--list-item");
            for (Element card : cards) {
                String name = "";
                Element nameEl = card.selectFirst("[class*='title'], h2, h3, a[class*='product']");
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                String price = "";
                Element priceEl = card.selectFirst("[class*='price'], .value");
                if (priceEl != null) price = priceEl.text().trim();
                if (price.isEmpty()) continue;

                if (price.length() > 20) price = price.substring(0, 20).trim();

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        // ── Dunnes Stores ─────────────────────────────────────────────
        else if (supermarket.contains("Dunnes")) {
            Elements cards = doc.select(".product-tile, [class*='product-card'], [class*='ProductCard']");
            for (Element card : cards) {
                String name = "";
                Element nameEl = card.selectFirst("h2, h3, [class*='name'], [class*='title']");
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                String price = "";
                Element priceEl = card.selectFirst("[class*='price'], [class*='Price']");
                if (priceEl != null) price = priceEl.text().trim();
                if (price.isEmpty()) continue;

                if (price.length() > 20) price = price.substring(0, 20).trim();

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        // ── SuperValu ─────────────────────────────────────────────────
        else if (supermarket.contains("SuperValu")) {
            Elements cards = doc.select(".product-grid-item, [class*='ProductGridItem'], " +
                                        "[class*='product-item']");
            for (Element card : cards) {
                String name = "";
                Element nameEl = card.selectFirst("h2, h3, [class*='name'], [class*='title']");
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                String price = "";
                Element priceEl = card.selectFirst("[class*='price'], [class*='Price']");
                if (priceEl != null) price = priceEl.text().trim();
                if (price.isEmpty()) continue;

                if (price.length() > 20) price = price.substring(0, 20).trim();

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        return entries;
    }

    /**
     * Saves product entries to the csv file.
     * Appends so previous searches are kept.
     * Writes header only on first run.
     */
    public void saveToCsv(ArrayList<ProductEntry> entries) throws IOException {

        java.io.File file = new java.io.File(CSV_FILE);
        boolean fileExists = file.exists();

        BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE, true));

        if (!fileExists) {
            writer.write("Timestamp,Supermarket,Search Query,Product Name,Price");
            writer.newLine();
        }

        for (ProductEntry entry : entries) {
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

    // ── inner class - holds one product result ───────────────────────

    /**
     * Holds data for one product price result.
     */
    public static class ProductEntry {

        public String timestamp;
        public String supermarket;
        public String searchQuery;
        public String productName;
        public String price;

        public ProductEntry(String timestamp, String supermarket, String searchQuery,
                            String productName, String price) {
            this.timestamp = timestamp;
            this.supermarket = supermarket;
            this.searchQuery = searchQuery;
            this.productName = productName;
            this.price = price;
        }

        // formats as a csv row with quoted fields
        public String toCsvRow() {
            return "\"" + timestamp + "\","
                 + "\"" + supermarket + "\","
                 + "\"" + searchQuery.replace("\"", "'") + "\","
                 + "\"" + productName.replace("\"", "'") + "\","
                 + "\"" + price + "\"";
        }
    }
}
