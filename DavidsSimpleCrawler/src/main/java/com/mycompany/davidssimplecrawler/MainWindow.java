/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.davidssimplecrawler;

/**
 *
 * @author david
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    //basic window setup for main window

    //vars for the crawler tab
    private JTextField urlField;
    private JTextField delayField;
    private JTextField selectorField;
    private JComboBox<String> parseModeBox;
    private JTextArea statusArea;

    // store the last fetched html so we can re-parse without re-fetching
    private String lastHtml = "";

    //vars for the fuel saved prices tab
    private JTable fuelTable;
    private DefaultTableModel fuelTableModel;
    private JLabel fuelPathLabel;

    //vars for the county dropdown
    private JComboBox<String> countyBox;

    //vars for the product search tab
    private JTextField productSearchField;
    private JCheckBox[] supermarketCheckboxes;
    private JTextArea productStatusArea;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JLabel productPathLabel;

    // scrapers and readers
    private FuelScraper fuelScraper = new FuelScraper();
    private ProductScraper productScraper = new ProductScraper();
    private CsvReader csvReader = new CsvReader();

    // all 26 irish counties mapped to their pickapump urls
    private static final String[][] COUNTIES = {
        {"Dublin",      "https://pickapump.com/county/dublin"},
        {"Cork",        "https://pickapump.com/county/cork"},
        {"Galway",      "https://pickapump.com/county/galway"},
        {"Limerick",    "https://pickapump.com/county/limerick"},
        {"Waterford",   "https://pickapump.com/county/waterford"},
        {"Tipperary",   "https://pickapump.com/county/tipperary"},
        {"Kerry",       "https://pickapump.com/county/kerry"},
        {"Donegal",     "https://pickapump.com/county/donegal"},
        {"Wexford",     "https://pickapump.com/county/wexford"},
        {"Wicklow",     "https://pickapump.com/county/wicklow"},
        {"Meath",       "https://pickapump.com/county/meath"},
        {"Kildare",     "https://pickapump.com/county/kildare"},
        {"Louth",       "https://pickapump.com/county/louth"},
        {"Kilkenny",    "https://pickapump.com/county/kilkenny"},
        {"Westmeath",   "https://pickapump.com/county/westmeath"},
        {"Clare",       "https://pickapump.com/county/clare"},
        {"Offaly",      "https://pickapump.com/county/offaly"},
        {"Laois",       "https://pickapump.com/county/laois"},
        {"Cavan",       "https://pickapump.com/county/cavan"},
        {"Monaghan",    "https://pickapump.com/county/monaghan"},
        {"Longford",    "https://pickapump.com/county/longford"},
        {"Roscommon",   "https://pickapump.com/county/roscommon"},
        {"Sligo",       "https://pickapump.com/county/sligo"},
        {"Leitrim",     "https://pickapump.com/county/leitrim"},
        {"Mayo",        "https://pickapump.com/county/mayo"},
        {"Carlow",      "https://pickapump.com/county/carlow"},
    };

    public MainWindow() {
        setTitle("Web Crawler Application");
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //build the ui
        buildUI();
    }

    private void buildUI() {

        // main panel with a mauve colour 90,90,255
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(90, 90, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // title label at the top
        JLabel titleLabel = new JLabel("Web Crawler Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // three tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Crawler", buildCrawlerTab());
        tabs.addTab("Product Search", buildProductSearchTab());
        tabs.addTab("Saved Prices", buildFuelSavedTab());
        tabs.addTab("Saved Products", buildProductSavedTab());
        mainPanel.add(tabs, BorderLayout.CENTER);

        add(mainPanel);
    }

    // ── TAB 1: general crawler ───────────────────────────────────────

    private JPanel buildCrawlerTab() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setText("Select a county and click 'Scrape Fuel Prices', or enter any URL and use 'Fetch & Parse'.\n\n" +
                           "A Chrome window will briefly appear when scraping fuel prices - this is normal.");
        panel.add(new JScrollPane(statusArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // county dropdown - row 0
        addWhiteLabel(inputPanel, "County:", gbc, 0, 0);
        String[] countyNames = new String[COUNTIES.length];
        for (int i = 0; i < COUNTIES.length; i++) countyNames[i] = COUNTIES[i][0];
        countyBox = new JComboBox<>(countyNames);
        countyBox.addActionListener(e -> updateUrlFromCounty());
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        inputPanel.add(countyBox, gbc);

        // URL field - row 1
        addWhiteLabel(inputPanel, "URL:", gbc, 0, 1);
        urlField = new JTextField(COUNTIES[0][1], 28);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
        inputPanel.add(urlField, gbc);

        // delay field - row 2
        addWhiteLabel(inputPanel, "Delay in (s):", gbc, 0, 2);
        delayField = new JTextField("15", 5);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0;
        inputPanel.add(delayField, gbc);

        // parse mode - row 3
        addWhiteLabel(inputPanel, "Parse Mode:", gbc, 0, 3);
        parseModeBox = new JComboBox<>(new String[]{"Text", "Links", "CSS Selector"});
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0;
        inputPanel.add(parseModeBox, gbc);

        // css selector - row 4
        addWhiteLabel(inputPanel, "CSS Selector:", gbc, 0, 4);
        selectorField = new JTextField("div.fuel-price-item", 10);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0;
        inputPanel.add(selectorField, gbc);

        // buttons - row 5
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);

        JButton fetchParseBtn = new JButton("Fetch & Parse");
        fetchParseBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        fetchParseBtn.addActionListener(e -> onFetchAndParseClicked());
        btnRow.add(fetchParseBtn);

        JButton reparseBtn = new JButton("Re-Parse Last HTML");
        reparseBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        reparseBtn.addActionListener(e -> parseAndDisplay(lastHtml));
        btnRow.add(reparseBtn);

        JButton fuelBtn = new JButton("Scrape Fuel Prices");
        fuelBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        fuelBtn.setBackground(new Color(255, 200, 0));
        fuelBtn.addActionListener(e -> onScrapeFuelClicked());
        btnRow.add(fuelBtn);

        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0;
        inputPanel.add(btnRow, gbc);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ── TAB 2: product search ────────────────────────────────────────

    /**
     * Builds the product search tab.
     * User types a product name, picks which supermarkets to search,
     * clicks search and results appear in the table below.
     */
    private JPanel buildProductSearchTab() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // top controls section
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // product search input - row 0
        addWhiteLabel(topPanel, "Search Product:", gbc, 0, 0);
        productSearchField = new JTextField("strawberries", 22);
        productSearchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        topPanel.add(productSearchField, gbc);

        // supermarket checkboxes - row 1
        addWhiteLabel(topPanel, "Supermarkets:", gbc, 0, 1);
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        checkPanel.setOpaque(false);
        supermarketCheckboxes = new JCheckBox[ProductScraper.SUPERMARKETS.length];
        for (int i = 0; i < ProductScraper.SUPERMARKETS.length; i++) {
            JCheckBox cb = new JCheckBox(ProductScraper.SUPERMARKETS[i][0]);
            cb.setForeground(Color.WHITE);
            cb.setOpaque(false);
            cb.setSelected(true); // all ticked by default
            supermarketCheckboxes[i] = cb;
            checkPanel.add(cb);
        }
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
        topPanel.add(checkPanel, gbc);

        // search button - row 2
        JPanel searchBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBtnRow.setOpaque(false);

        JButton searchBtn = new JButton("Search Products");
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchBtn.setBackground(new Color(0, 200, 100));
        searchBtn.addActionListener(e -> onSearchProductsClicked());
        searchBtnRow.add(searchBtn);

        JButton saveProductsBtn = new JButton("Save Results to CSV");
        saveProductsBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        saveProductsBtn.addActionListener(e -> onSaveProductsClicked());
        searchBtnRow.add(saveProductsBtn);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0;
        topPanel.add(searchBtnRow, gbc);

        panel.add(topPanel, BorderLayout.NORTH);

        // split pane - status log top, results table bottom
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setDividerLocation(100);
        split.setOpaque(false);

        productStatusArea = new JTextArea();
        productStatusArea.setEditable(false);
        productStatusArea.setLineWrap(true);
        productStatusArea.setWrapStyleWord(true);
        productStatusArea.setText("Type a product name, pick supermarkets, then click 'Search Products'.\n" +
                                  "Chrome will open for each supermarket to load JavaScript content.");
        split.setTopComponent(new JScrollPane(productStatusArea));

        // results table
        String[] columns = {"Supermarket", "Product Name", "Price", "Search Query", "Timestamp"};
        productTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setFillsViewportHeight(true);
        productTable.setAutoCreateRowSorter(true);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(280);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        split.setBottomComponent(new JScrollPane(productTable));

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // ── TAB 3: saved fuel prices ─────────────────────────────────────

    private JPanel buildFuelSavedTab() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        String[] columns = {"Timestamp", "Station", "Address", "Fuel Type", "Price"};
        fuelTableModel = new DefaultTableModel(columns, 0);
        fuelTable = new JTable(fuelTableModel);
        fuelTable.setFillsViewportHeight(true);
        fuelTable.setAutoCreateRowSorter(true);
        fuelTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        fuelTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        fuelTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        fuelTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        fuelTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        panel.add(new JScrollPane(fuelTable), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new BorderLayout(8, 8));
        bottomBar.setOpaque(false);
        fuelPathLabel = new JLabel("No fuel data saved yet.");
        fuelPathLabel.setForeground(Color.WHITE);
        fuelPathLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        bottomBar.add(fuelPathLabel, BorderLayout.CENTER);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadFuelTable());
        bottomBar.add(refreshBtn, BorderLayout.EAST);
        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    // ── TAB 4: saved product prices ──────────────────────────────────

    private JPanel buildProductSavedTab() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        String[] columns = {"Timestamp", "Supermarket", "Search Query", "Product Name", "Price"};
        DefaultTableModel savedProductModel = new DefaultTableModel(columns, 0);
        JTable savedProductTable = new JTable(savedProductModel);
        savedProductTable.setFillsViewportHeight(true);
        savedProductTable.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(savedProductTable), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new BorderLayout(8, 8));
        bottomBar.setOpaque(false);
        productPathLabel = new JLabel("No product data saved yet.");
        productPathLabel.setForeground(Color.WHITE);
        productPathLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        bottomBar.add(productPathLabel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            savedProductModel.setRowCount(0);
            String filePath = productScraper.getCsvFilePath();
            ArrayList<String[]> rows = csvReader.readCsv(filePath);
            for (String[] row : rows) savedProductModel.addRow(row);
            productPathLabel.setText("File: " + filePath + "  (" + rows.size() + " rows)");
        });
        bottomBar.add(refreshBtn, BorderLayout.EAST);
        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    // ── ACTIONS ──────────────────────────────────────────────────────

    /** Updates the url field when the county dropdown changes. */
    private void updateUrlFromCounty() {
        int index = countyBox.getSelectedIndex();
        if (index >= 0 && index < COUNTIES.length) {
            urlField.setText(COUNTIES[index][1]);
        }
    }

    /**
     * Called when the user clicks "Fetch and Parse".
     * Plain http fetch for normal websites.
     */
    private void onFetchAndParseClicked() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            statusArea.setText("Please enter a URL first.");
            return;
        }

        int delay = 0;
        try {
            delay = Integer.parseInt(delayField.getText().trim());
        } catch (NumberFormatException ex) {
            statusArea.setText("Delay must be a number. Defaulting to 0.");
        }

        statusArea.setText("Fetching: " + url + " (delay: " + delay + "s)...\n");

        final int finalDelay = delay;
        new Thread(() -> {
            try {
                if (finalDelay > 0) Thread.sleep(finalDelay * 1000L);
                PageFetcher fetcher = new PageFetcher("Mozilla/5.0 (compatible; WebCrawlerApp/1.0)");
                String html = fetcher.fetchPage(url);
                lastHtml = html;
                SwingUtilities.invokeLater(() -> parseAndDisplay(html));
            } catch (InterruptedException ex) {
                SwingUtilities.invokeLater(() -> statusArea.setText("Interrupted."));
            }
        }).start();
    }

    /**
     * Called when the user clicks "Scrape Fuel Prices".
     * Uses the selected county, opens Chrome, scrapes and saves.
     */
    private void onScrapeFuelClicked() {
        int index = countyBox.getSelectedIndex();
        String countyName = COUNTIES[index][0];
        String url = COUNTIES[index][1];
        urlField.setText(url);

        int waitSeconds = 15;
        try {
            waitSeconds = Math.max(Integer.parseInt(delayField.getText().trim()), 10);
        } catch (NumberFormatException ex) { }

        statusArea.setText("Scraping fuel prices for: " + countyName + "\n" +
                           "Chrome will open briefly - this is normal.\n" +
                           "Waiting up to " + waitSeconds + "s for prices...");

        final int finalWait = waitSeconds;
        new Thread(() -> {
            try {
                JsBrowserFetcher browserFetcher = new JsBrowserFetcher();
                String html = browserFetcher.fetchWithJs(url, finalWait);
                lastHtml = html;

                if (html.startsWith("ERROR")) {
                    SwingUtilities.invokeLater(() ->
                        statusArea.setText(html + "\n\nMake sure Google Chrome is installed."));
                    return;
                }

                ArrayList<FuelScraper.FuelEntry> entries = fuelScraper.scrapePrices(html);

                if (entries.isEmpty()) {
                    SwingUtilities.invokeLater(() ->
                        statusArea.setText("No prices found for " + countyName + ".\n" +
                            "Click 'Re-Parse Last HTML' in Text mode to inspect what loaded."));
                    return;
                }

                fuelScraper.saveToCsv(entries);

                StringBuilder summary = new StringBuilder();
                summary.append("Scraped ").append(entries.size())
                       .append(" price(s) for ").append(countyName).append(".\n");
                summary.append("Saved to: ").append(fuelScraper.getCsvFilePath()).append("\n\n");
                for (FuelScraper.FuelEntry e : entries) {
                    summary.append(e.stationName).append(" | ").append(e.address)
                           .append(" | ").append(e.fuelType).append(" | ").append(e.price).append("\n");
                }

                SwingUtilities.invokeLater(() -> {
                    statusArea.setText(summary.toString());
                    loadFuelTable();
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> statusArea.setText("Error: " + ex.getMessage()));
            }
        }).start();
    }

    /**
     * Called when the user clicks "Search Products".
     * Loops through the ticked supermarkets, fetches each search page with Selenium,
     * scrapes product results and shows them in the table.
     */
    private void onSearchProductsClicked() {
        String query = productSearchField.getText().trim();
        if (query.isEmpty()) {
            productStatusArea.setText("Please enter a product to search for.");
            return;
        }

        // collect which supermarkets are ticked
        ArrayList<Integer> selectedIndexes = new ArrayList<>();
        for (int i = 0; i < supermarketCheckboxes.length; i++) {
            if (supermarketCheckboxes[i].isSelected()) {
                selectedIndexes.add(i);
            }
        }

        if (selectedIndexes.isEmpty()) {
            productStatusArea.setText("Please tick at least one supermarket.");
            return;
        }

        // clear the live results table
        productTableModel.setRowCount(0);

        productStatusArea.setText("Searching for '" + query + "' across " +
                                  selectedIndexes.size() + " supermarket(s)...\n" +
                                  "Chrome will open for each one. Please wait.");

        new Thread(() -> {
            ArrayList<ProductScraper.ProductEntry> allResults = new ArrayList<>();

            for (int idx : selectedIndexes) {
                String supermarketName = ProductScraper.SUPERMARKETS[idx][0];
                String urlTemplate    = ProductScraper.SUPERMARKETS[idx][1];
                String searchUrl = productScraper.buildSearchUrl(urlTemplate, query);

                SwingUtilities.invokeLater(() ->
                    productStatusArea.setText(productStatusArea.getText() +
                        "\nSearching " + supermarketName + "...")
                );

                try {
                    // use selenium to load the search results page
                    JsBrowserFetcher browserFetcher = new JsBrowserFetcher();
                    String html = browserFetcher.fetchWithJs(searchUrl, 10);

                    if (html.startsWith("ERROR")) {
                        SwingUtilities.invokeLater(() ->
                            productStatusArea.setText(productStatusArea.getText() +
                                "\n" + supermarketName + ": ERROR - " + html)
                        );
                        continue;
                    }

                    // scrape the results
                    ArrayList<ProductScraper.ProductEntry> results =
                        productScraper.scrapeProducts(html, supermarketName, query);

                    allResults.addAll(results);

                    // add results to table on swing thread
                    SwingUtilities.invokeLater(() -> {
                        for (ProductScraper.ProductEntry p : results) {
                            productTableModel.addRow(new String[]{
                                p.supermarket, p.productName, p.price,
                                p.searchQuery, p.timestamp
                            });
                        }
                        productStatusArea.setText(productStatusArea.getText() +
                            "\n" + supermarketName + ": found " + results.size() + " result(s).");
                    });

                } catch (Exception ex) {
                    final String err = ex.getMessage();
                    SwingUtilities.invokeLater(() ->
                        productStatusArea.setText(productStatusArea.getText() +
                            "\n" + supermarketName + ": error - " + err)
                    );
                }
            }

            // done - show summary
            final int total = allResults.size();
            SwingUtilities.invokeLater(() ->
                productStatusArea.setText(productStatusArea.getText() +
                    "\n\nDone. Found " + total + " result(s) in total.\n" +
                    "Click 'Save Results to CSV' to save them.")
            );

        }).start();
    }

    /**
     * Called when the user clicks "Save Results to CSV".
     * Saves whatever is currently in the product results table.
     */
    private void onSaveProductsClicked() {
        int rowCount = productTableModel.getRowCount();
        if (rowCount == 0) {
            productStatusArea.setText("No results to save. Search for something first.");
            return;
        }

        // rebuild entries from the table model
        ArrayList<ProductScraper.ProductEntry> entries = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            String supermarket  = (String) productTableModel.getValueAt(i, 0);
            String name         = (String) productTableModel.getValueAt(i, 1);
            String price        = (String) productTableModel.getValueAt(i, 2);
            String query        = (String) productTableModel.getValueAt(i, 3);
            String timestamp    = (String) productTableModel.getValueAt(i, 4);
            entries.add(new ProductScraper.ProductEntry(timestamp, supermarket, query, name, price));
        }

        try {
            productScraper.saveToCsv(entries);
            productStatusArea.setText(productStatusArea.getText() +
                "\n\nSaved " + entries.size() + " results to:\n" +
                productScraper.getCsvFilePath());
        } catch (Exception ex) {
            productStatusArea.setText("Error saving: " + ex.getMessage());
        }
    }

    /** Loads the fuel prices csv into the fuel saved tab table. */
    private void loadFuelTable() {
        fuelTableModel.setRowCount(0);
        String filePath = fuelScraper.getCsvFilePath();
        ArrayList<String[]> rows = csvReader.readCsv(filePath);
        for (String[] row : rows) fuelTableModel.addRow(row);
        fuelPathLabel.setText("File: " + filePath + "  (" + rows.size() + " rows)");
    }

    /** Helper to add a white label at a grid position. */
    private void addWhiteLabel(JPanel panel, String text, GridBagConstraints gbc, int x, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        gbc.gridx = x; gbc.gridy = y; gbc.weightx = 0;
        panel.add(label, gbc);
    }

    /**
     * Parses html based on the selected mode and shows results in the status area.
     */
    private void parseAndDisplay(String html) {
        if (html.isEmpty() || html.startsWith("ERROR")) {
            statusArea.setText(html.isEmpty() ? "No HTML to parse. Fetch a page first." : html);
            return;
        }

        HtmlParser parser = new HtmlParser();
        String mode = (String) parseModeBox.getSelectedItem();
        StringBuilder output = new StringBuilder();
        output.append("=== Parse Mode: ").append(mode).append(" ===\n\n");

        // text mode - strips all tags, shows only readable text
        if ("Text".equals(mode)) {
            output.append(parser.getAllText(html));

        // links mode - pulls every url from the page
        } else if ("Links".equals(mode)) {
            ArrayList<String> links = parser.getAllLinks(html);
            output.append("Found ").append(links.size()).append(" links:\n\n");
            for (String link : links) output.append(link).append("\n");

        // css selector mode - finds elements matching the selector typed in
        } else if ("CSS Selector".equals(mode)) {
            String selector = selectorField.getText().trim();
            if (selector.isEmpty()) {
                statusArea.setText("Please enter a CSS selector.");
                return;
            }
            ArrayList<String> elements = parser.searchByCssSelector(html, selector);
            output.append("Found ").append(elements.size()).append(" element(s) matching '")
                  .append(selector).append("':\n\n");
            for (String el : elements) output.append(el).append("\n---\n");
        }

        //display results
        statusArea.setText(output.toString());
    }
}
