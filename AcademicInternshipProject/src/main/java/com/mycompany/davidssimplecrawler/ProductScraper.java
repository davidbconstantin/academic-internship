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

    // csv file saved next to where the app runs
    private static final String CSV_FILE = "product_prices.csv";

    // timestamp format used in the csv
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // supermarket url templates
    // aldi uses ?q= as a signal - JsBrowserFetcher extracts the query and types it into the search box
    // because aldi.ie does not support url-based search (it redirects to homepage)
    public static final String[][] SUPERMARKETS = {
        {"Aldi Ireland",  "https://www.aldi.ie/search?q={QUERY}"},
        {"Lidl Ireland",  "https://www.lidl.ie/search?q={QUERY}&action=list"},
        {"Tesco Ireland", "https://www.tesco.ie/groceries/en-IE/search?query={QUERY}"},
        {"Dunnes Stores", "https://www.dunnesstores.com/search?q={QUERY}"},
        {"SuperValu",     "https://shop.supervalu.ie/sm/delivery/rsid/5550/results?q={QUERY}"},
    };

    /**
     * Builds the search url for a supermarket by replacing {QUERY} with the encoded query.
     * For Aldi the url is passed to JsBrowserFetcher which extracts the query
     * and types it into the search box directly.
     */
    public String buildSearchUrl(String urlTemplate, String query) {
        String encoded = query.trim().replace(" ", "+");
        return urlTemplate.replace("{QUERY}", encoded);
    }

    /**
     * Scrapes product results from a rendered html page.
     *
     * Confirmed selectors:
     *
     * Aldi (from saved Search___ALDI_IE.html - 30 tiles):
     *   div.product-tile                               - card
     *   div.product-tile__brandname p                 - brand
     *   div.product-tile__name p                      - name
     *   div.product-tile__unit-of-measurement p       - size
     *   div.base-price--product-tile span.digit       - price digits joined manually
     *
     * Tesco (from saved Results_for_bread_-_Tesco_Groceries.html - 27 products):
     *   h2.online-components-product-tile-product-heading__heading a  - name (link text)
     *   p.online-components-product-tile-price__text                  - price
     *
     * Lidl (from saved results_for_lidl_ie_for__bread.html - 32 tiles):
     *   .product-grid-box                             - card
     *   .product-grid-box__title                      - name
     *   .ods-price__value                             - price value e.g. 1.79
     *   .ods-price__prefix                            - price prefix e.g. "each" or currency
     */
    public ArrayList<ProductEntry> scrapeProducts(String html, String supermarket, String searchQuery) {
        ArrayList<ProductEntry> entries = new ArrayList<>();

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);

        Document doc = Jsoup.parse(html);

        // ── Aldi Ireland ─────────────────────────────────────────────
        if (supermarket.contains("Aldi")) {
            Elements tiles = doc.select("div.product-tile");
            System.out.println("Aldi: found " + tiles.size() + " tiles");

            for (Element tile : tiles) {

                // brand + name combined
                String brand = "";
                Element brandEl = tile.selectFirst("div.product-tile__brandname p");
                if (brandEl != null) brand = brandEl.text().trim();

                String name = "";
                Element nameEl = tile.selectFirst("div.product-tile__name p");
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                String fullName = brand.isEmpty() ? name : brand + " - " + name;

                // size appended to name
                Element sizeEl = tile.selectFirst("div.product-tile__unit-of-measurement p");
                if (sizeEl != null && !sizeEl.text().trim().isEmpty()) {
                    fullName = fullName + " (" + sizeEl.text().trim() + ")";
                }

                // price - join individual span.digit elements to reconstruct e.g. "€1.29"
                String price = "";
                Element priceDiv = tile.selectFirst("div.base-price--product-tile");
                if (priceDiv != null) {
                    Elements digitSpans = priceDiv.select("span.digit");
                    if (!digitSpans.isEmpty()) {
                        StringBuilder pb = new StringBuilder();
                        for (Element d : digitSpans) pb.append(d.text());
                        price = pb.toString().trim();
                    } else {
                        Element priceEl = priceDiv.selectFirst("span.base-price__regular span");
                        if (priceEl != null) price = priceEl.text().trim();
                    }
                }

                if (price.isEmpty()) continue;

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, fullName, price));
            }
        }

        // ── Tesco Ireland ─────────────────────────────────────────────
        // confirmed from real saved html - 27 products paired by index
        else if (supermarket.contains("Tesco")) {
            Elements nameEls  = doc.select("h2.online-components-product-tile-product-heading__heading a");
            Elements priceEls = doc.select("p.online-components-product-tile-price__text");
            System.out.println("Tesco: " + nameEls.size() + " names, " + priceEls.size() + " prices");

            int count = Math.min(nameEls.size(), priceEls.size());
            for (int i = 0; i < count; i++) {
                String name  = nameEls.get(i).text().trim();
                String price = priceEls.get(i).text().trim();
                if (name.isEmpty() || price.isEmpty()) continue;
                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        // ── Lidl Ireland ─────────────────────────────────────────────
        // confirmed from saved results_for_lidl_ie_for__bread.html - 32 tiles
        // card:  .product-grid-box
        // name:  .product-grid-box__title
        // price: .ods-price__value (the number) - prefix is "each" not a currency symbol
        //        price text is just the number e.g. "1.79" - we add € in front
        else if (supermarket.contains("Lidl")) {
            Elements tiles = doc.select(".product-grid-box");
            System.out.println("Lidl: found " + tiles.size() + " tiles");

            for (Element tile : tiles) {
                String name = "";
                Element nameEl = tile.selectFirst(".product-grid-box__title");
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                String price = "";
                Element priceEl = tile.selectFirst(".ods-price__value");
                if (priceEl != null) {
                    price = priceEl.text().trim();
                    // add euro sign if not already present
                    if (!price.startsWith("€") && !price.startsWith("£")) {
                        price = "€" + price;
                    }
                }
                if (price.isEmpty() || price.equals("€")) continue;

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        // ── Dunnes Stores ─────────────────────────────────────────────
        else if (supermarket.contains("Dunnes")) {
            Elements tiles = doc.select(
                ".product-tile, [class*='ProductCard'], [class*='product-card']"
            );
            System.out.println("Dunnes: found " + tiles.size() + " tiles");

            for (Element tile : tiles) {
                String name = "";
                Element nameEl = tile.selectFirst(
                    "h2, h3, [class*='product-name'], [class*='ProductName'], [class*='title']"
                );
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                String price = "";
                Element priceEl = tile.selectFirst(
                    "[class*='product-price'], [class*='ProductPrice'], [class*='price']"
                );
                if (priceEl != null) price = priceEl.text().trim();
                if (price.isEmpty()) continue;
                if (price.length() > 20) price = price.substring(0, 20).trim();

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        // ── SuperValu ─────────────────────────────────────────────────
        else if (supermarket.contains("SuperValu")) {
            Elements tiles = doc.select(
                "[class*='ProductCardWrapper'], [class*='ColProductCard'], [class*='product-grid-item']"
            );
            System.out.println("SuperValu: found " + tiles.size() + " tiles");

            for (Element tile : tiles) {
                String name = "";
                Element nameEl = tile.selectFirst(
                    "h2, h3, [class*='ProductName'], [class*='product-name'], [class*='Title']"
                );
                if (nameEl != null) name = nameEl.text().trim();
                if (name.isEmpty()) continue;

                String price = "";
                Element priceEl = tile.selectFirst("[class*='Price'], [class*='price']");
                if (priceEl != null) price = priceEl.text().trim();
                if (price.isEmpty()) continue;
                if (price.length() > 20) price = price.substring(0, 20).trim();

                entries.add(new ProductEntry(timestamp, supermarket, searchQuery, name, price));
            }
        }

        System.out.println(supermarket + ": returning " + entries.size() + " entries");
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

    // ── inner class ──────────────────────────────────────────────────

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

        public String toCsvRow() {
            return "\"" + timestamp + "\","
                 + "\"" + supermarket + "\","
                 + "\"" + searchQuery.replace("\"", "'") + "\","
                 + "\"" + productName.replace("\"", "'") + "\","
                 + "\"" + price + "\"";
        }
    }
}