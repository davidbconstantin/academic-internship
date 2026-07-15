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
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    // ── colour palette ───────────────────────────────────────────────
    // dark background colours
    private static final Color BG_DARK      = new Color(15, 17, 26);
    private static final Color BG_CARD      = new Color(24, 28, 42);
    private static final Color BG_INPUT     = new Color(32, 37, 54);
    private static final Color BG_TABLE_ALT = new Color(20, 23, 35);

    // accent colours
    private static final Color ACCENT_BLUE    = new Color(99, 102, 241);
    private static final Color ACCENT_PURPLE  = new Color(139, 92, 246);
    private static final Color ACCENT_GREEN   = new Color(16, 185, 129);
    private static final Color ACCENT_YELLOW  = new Color(245, 158, 11);
    private static final Color ACCENT_RED     = new Color(239, 68, 68);

    // text colours
    private static final Color TEXT_PRIMARY   = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color TEXT_MUTED     = new Color(71, 85, 105);

    // border colour
    private static final Color BORDER_COLOR  = new Color(44, 50, 70);

    // fonts
    private static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD, 22);
    private static final Font FONT_LABEL  = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_INPUT  = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 12);

    // ── crawler tab vars ─────────────────────────────────────────────
    private JTextField urlField;
    private JTextField delayField;
    private JTextField selectorField;
    private JComboBox<String> parseModeBox;
    private JTextArea statusArea;
    private String lastHtml = "";
    private JComboBox<String> countyBox;

    // ── product search tab vars ──────────────────────────────────────
    private JTextField productSearchField;
    private JCheckBox[] supermarketCheckboxes;
    private JTextArea productStatusArea;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JLabel productPathLabel;

    // ── fuel tab vars ────────────────────────────────────────────────
    private JTable fuelTable;
    private DefaultTableModel fuelTableModel;
    private JLabel fuelPathLabel;
    private JLabel fuelCountLabel;

    // scrapers and readers
    private FuelScraper fuelScraper       = new FuelScraper();
    private ProductScraper productScraper = new ProductScraper();
    private CsvReader csvReader           = new CsvReader();

    // all 26 irish counties
    private static final String[][] COUNTIES = {
        {"Dublin",    "https://pickapump.com/county/dublin"},
        {"Cork",      "https://pickapump.com/county/cork"},
        {"Galway",    "https://pickapump.com/county/galway"},
        {"Limerick",  "https://pickapump.com/county/limerick"},
        {"Waterford", "https://pickapump.com/county/waterford"},
        {"Tipperary", "https://pickapump.com/county/tipperary"},
        {"Kerry",     "https://pickapump.com/county/kerry"},
        {"Donegal",   "https://pickapump.com/county/donegal"},
        {"Wexford",   "https://pickapump.com/county/wexford"},
        {"Wicklow",   "https://pickapump.com/county/wicklow"},
        {"Meath",     "https://pickapump.com/county/meath"},
        {"Kildare",   "https://pickapump.com/county/kildare"},
        {"Louth",     "https://pickapump.com/county/louth"},
        {"Kilkenny",  "https://pickapump.com/county/kilkenny"},
        {"Westmeath", "https://pickapump.com/county/westmeath"},
        {"Clare",     "https://pickapump.com/county/clare"},
        {"Offaly",    "https://pickapump.com/county/offaly"},
        {"Laois",     "https://pickapump.com/county/laois"},
        {"Cavan",     "https://pickapump.com/county/cavan"},
        {"Monaghan",  "https://pickapump.com/county/monaghan"},
        {"Longford",  "https://pickapump.com/county/longford"},
        {"Roscommon", "https://pickapump.com/county/roscommon"},
        {"Sligo",     "https://pickapump.com/county/sligo"},
        {"Leitrim",   "https://pickapump.com/county/leitrim"},
        {"Mayo",      "https://pickapump.com/county/mayo"},
        {"Carlow",    "https://pickapump.com/county/carlow"},
    };

    public MainWindow() {
        setTitle("Web Crawler — David Bulugea");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);

        // set the look and feel to system default so fonts render cleanly
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }

        buildUI();
    }

    // ── main layout ──────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);

        // sidebar navigation on the left
        root.add(buildSidebar(), BorderLayout.WEST);

        // content area on the right uses a card layout switched by the sidebar
        JPanel contentArea = new JPanel(new CardLayout());
        contentArea.setBackground(BG_DARK);
        contentArea.add(buildFuelTab(),       "fuel");
        contentArea.add(buildProductTab(),    "products");
        contentArea.add(buildCrawlerTab(),    "crawler");
        contentArea.add(buildSavedFuelTab(),  "saved_fuel");
        contentArea.add(buildSavedProdTab(),  "saved_products");
        root.add(contentArea, BorderLayout.CENTER);

        // wire up the sidebar buttons to switch cards
        wireSidebarToCards(root, contentArea);

        setContentPane(root);
    }

    // ── sidebar ──────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(BG_CARD);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // app logo area
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(BG_CARD);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));
        logoPanel.setMaximumSize(new Dimension(200, 90));

        JLabel logo = new JLabel("🕷 WebCrawler");
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));
        logo.setForeground(TEXT_PRIMARY);
        logoPanel.add(logo, BorderLayout.CENTER);

        JLabel sub = new JLabel("David Bulugea  ·  22516586");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_MUTED);
        logoPanel.add(sub, BorderLayout.SOUTH);

        sidebar.add(logoPanel);

        // divider
        sidebar.add(makeDivider());

        // nav section label
        sidebar.add(makeSectionLabel("SCRAPING"));

        // nav buttons - each has an id matching a card name
        sidebar.add(makeNavBtn("⛽  Fuel Prices",    "fuel",           ACCENT_BLUE));
        sidebar.add(makeNavBtn("🛒  Product Search", "products",       ACCENT_GREEN));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeSectionLabel("DATA"));
        sidebar.add(makeNavBtn("📋  Saved Fuel",     "saved_fuel",     ACCENT_PURPLE));
        sidebar.add(makeNavBtn("📦  Saved Products", "saved_products", ACCENT_YELLOW));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(makeDivider());
        sidebar.add(makeSectionLabel("TOOLS"));
        sidebar.add(makeNavBtn("🔧  Crawler",        "crawler",        ACCENT_RED));

        sidebar.add(Box.createVerticalGlue());

        // version label at the bottom
        JLabel version = new JLabel("  v1.0  ·  Day 7");
        version.setFont(FONT_SMALL);
        version.setForeground(TEXT_MUTED);
        version.setBorder(BorderFactory.createEmptyBorder(12, 20, 16, 0));
        sidebar.add(version);

        return sidebar;
    }

    /** Wires each sidebar nav button to show the matching card panel. */
    private void wireSidebarToCards(JPanel root, JPanel contentArea) {
        CardLayout cl = (CardLayout) contentArea.getLayout();

        // find all nav buttons in the sidebar and add listeners
        Component sidebar = root.getComponent(0);
        for (Component c : ((JPanel) sidebar).getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                String cardName = (String) btn.getClientProperty("card");
                if (cardName != null) {
                    btn.addActionListener(e -> cl.show(contentArea, cardName));
                }
            }
        }
    }

    // ── TAB: fuel prices ─────────────────────────────────────────────

    private JPanel buildFuelTab() {
        JPanel page = makePage();

        // header
        page.add(makePageHeader("⛽ Fuel Prices", "Scrape live fuel prices from Pickapump.com by county"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // left: controls card
        JPanel controlCard = makeCard();
        controlCard.setLayout(new BoxLayout(controlCard, BoxLayout.Y_AXIS));
        controlCard.setPreferredSize(new Dimension(280, 0));

        addCardLabel(controlCard, "SELECT COUNTY");
        controlCard.add(Box.createVerticalStrut(6));

        // county dropdown
        String[] countyNames = new String[COUNTIES.length];
        for (int i = 0; i < COUNTIES.length; i++) countyNames[i] = COUNTIES[i][0];
        countyBox = new JComboBox<>(countyNames);
        styleCombo(countyBox);
        countyBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        countyBox.addActionListener(e -> {
            int idx = countyBox.getSelectedIndex();
            if (idx >= 0) urlField.setText(COUNTIES[idx][1]);
        });
        controlCard.add(countyBox);
        controlCard.add(Box.createVerticalStrut(16));

        addCardLabel(controlCard, "WAIT TIME (SECONDS)");
        controlCard.add(Box.createVerticalStrut(6));
        delayField = makeInput("15");
        delayField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        controlCard.add(delayField);
        controlCard.add(Box.createVerticalStrut(20));

        // scrape button
        JButton scrapeBtn = makeButton("⛽  Scrape Fuel Prices", ACCENT_BLUE);
        scrapeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        scrapeBtn.addActionListener(e -> onScrapeFuelClicked());
        controlCard.add(scrapeBtn);
        controlCard.add(Box.createVerticalStrut(8));

        // info note
        JLabel note = new JLabel("<html><center>A Chrome window will briefly<br>appear — this is normal.</center></html>");
        note.setFont(FONT_SMALL);
        note.setForeground(TEXT_MUTED);
        note.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlCard.add(Box.createVerticalStrut(12));
        controlCard.add(note);
        controlCard.add(Box.createVerticalGlue());

        body.add(controlCard, BorderLayout.WEST);

        // right: status area
        JPanel statusCard = makeCard();
        statusCard.setLayout(new BorderLayout(0, 8));

        JLabel statusTitle = new JLabel("Status Log");
        statusTitle.setFont(FONT_LABEL);
        statusTitle.setForeground(TEXT_SECONDARY);
        statusCard.add(statusTitle, BorderLayout.NORTH);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setFont(FONT_MONO);
        statusArea.setBackground(BG_INPUT);
        statusArea.setForeground(TEXT_PRIMARY);
        statusArea.setCaretColor(TEXT_PRIMARY);
        statusArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        statusArea.setText("Select a county and click 'Scrape Fuel Prices' to begin.\n\n" +
                           "Results will appear in the Saved Fuel tab after scraping.");
        JScrollPane scroll = new JScrollPane(statusArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(BG_INPUT);
        statusCard.add(scroll, BorderLayout.CENTER);

        // hidden url field - updated by county dropdown, used by scraper
        urlField = new JTextField(COUNTIES[0][1]);
        urlField.setVisible(false);

        body.add(statusCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: product search ──────────────────────────────────────────

    private JPanel buildProductTab() {
        JPanel page = makePage();
        page.add(makePageHeader("🛒 Product Search", "Search for any product across Irish supermarkets"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // top: search controls
        JPanel topCard = makeCard();
        topCard.setLayout(new BoxLayout(topCard, BoxLayout.Y_AXIS));

        addCardLabel(topCard, "PRODUCT NAME");
        topCard.add(Box.createVerticalStrut(6));
        productSearchField = makeInput("e.g. strawberries, milk, bread");
        productSearchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        topCard.add(productSearchField);
        topCard.add(Box.createVerticalStrut(16));

        addCardLabel(topCard, "SUPERMARKETS");
        topCard.add(Box.createVerticalStrut(8));

        // supermarket checkboxes in a row
        JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        checkRow.setBackground(BG_CARD);
        checkRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        supermarketCheckboxes = new JCheckBox[ProductScraper.SUPERMARKETS.length];
        for (int i = 0; i < ProductScraper.SUPERMARKETS.length; i++) {
            JCheckBox cb = new JCheckBox(ProductScraper.SUPERMARKETS[i][0]);
            cb.setFont(FONT_INPUT);
            cb.setForeground(TEXT_PRIMARY);
            cb.setBackground(BG_CARD);
            cb.setSelected(true);
            supermarketCheckboxes[i] = cb;
            checkRow.add(cb);
        }
        topCard.add(checkRow);
        topCard.add(Box.createVerticalStrut(16));

        // search + save buttons side by side
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton searchBtn = makeButton("🔍  Search Products", ACCENT_GREEN);
        searchBtn.addActionListener(e -> onSearchProductsClicked());
        btnRow.add(searchBtn);

        JButton saveBtn = makeSmallButton("💾  Save to CSV", ACCENT_PURPLE);
        saveBtn.addActionListener(e -> onSaveProductsClicked());
        btnRow.add(saveBtn);

        topCard.add(btnRow);
        body.add(topCard, BorderLayout.NORTH);

        // bottom: split between log and results table
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setDividerLocation(110);
        split.setBackground(BG_DARK);
        split.setBorder(null);

        // status log
        JPanel logCard = makeCard();
        logCard.setLayout(new BorderLayout(0, 6));
        JLabel logTitle = new JLabel("Search Log");
        logTitle.setFont(FONT_LABEL);
        logTitle.setForeground(TEXT_SECONDARY);
        logCard.add(logTitle, BorderLayout.NORTH);
        productStatusArea = new JTextArea();
        productStatusArea.setEditable(false);
        productStatusArea.setLineWrap(true);
        productStatusArea.setFont(FONT_MONO);
        productStatusArea.setBackground(BG_INPUT);
        productStatusArea.setForeground(TEXT_PRIMARY);
        productStatusArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        productStatusArea.setText("Type a product name and click 'Search Products'.");
        JScrollPane logScroll = new JScrollPane(productStatusArea);
        logScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        logCard.add(logScroll, BorderLayout.CENTER);
        split.setTopComponent(logCard);

        // results table
        JPanel tableCard = makeCard();
        tableCard.setLayout(new BorderLayout(0, 8));
        JLabel tableTitle = new JLabel("Results");
        tableTitle.setFont(FONT_LABEL);
        tableTitle.setForeground(TEXT_SECONDARY);
        tableCard.add(tableTitle, BorderLayout.NORTH);
        String[] cols = {"Supermarket", "Product Name", "Price", "Search Query", "Timestamp"};
        productTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productTableModel);
        styleTable(productTable);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        JScrollPane tableScroll = new JScrollPane(productTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        tableScroll.getViewport().setBackground(BG_DARK);
        tableCard.add(tableScroll, BorderLayout.CENTER);
        split.setBottomComponent(tableCard);

        body.add(split, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: crawler (advanced) ──────────────────────────────────────

    private JPanel buildCrawlerTab() {
        JPanel page = makePage();
        page.add(makePageHeader("🔧 Crawler", "Fetch and parse any webpage using CSS selectors"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // controls card
        JPanel ctrlCard = makeCard();
        ctrlCard.setLayout(new BoxLayout(ctrlCard, BoxLayout.Y_AXIS));
        ctrlCard.setPreferredSize(new Dimension(300, 0));

        addCardLabel(ctrlCard, "TARGET URL");
        ctrlCard.add(Box.createVerticalStrut(6));
        JTextField crawlUrlField = makeInput("https://example.com");
        crawlUrlField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ctrlCard.add(crawlUrlField);
        ctrlCard.add(Box.createVerticalStrut(14));

        addCardLabel(ctrlCard, "PARSE MODE");
        ctrlCard.add(Box.createVerticalStrut(6));
        parseModeBox = new JComboBox<>(new String[]{"Text", "Links", "CSS Selector"});
        styleCombo(parseModeBox);
        parseModeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ctrlCard.add(parseModeBox);
        ctrlCard.add(Box.createVerticalStrut(14));

        addCardLabel(ctrlCard, "CSS SELECTOR");
        ctrlCard.add(Box.createVerticalStrut(6));
        selectorField = makeInput("div.fuel-price-item");
        selectorField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ctrlCard.add(selectorField);
        ctrlCard.add(Box.createVerticalStrut(20));

        JButton fetchBtn = makeButton("🌐  Fetch & Parse", ACCENT_BLUE);
        fetchBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fetchBtn.addActionListener(e -> {
            String url = crawlUrlField.getText().trim();
            if (url.isEmpty()) return;
            statusArea.setText("Fetching " + url + "...");
            new Thread(() -> {
                PageFetcher fetcher = new PageFetcher("Mozilla/5.0 (compatible; WebCrawlerApp/1.0)");
                String html = fetcher.fetchPage(url);
                lastHtml = html;
                SwingUtilities.invokeLater(() -> parseAndDisplay(html));
            }).start();
        });
        ctrlCard.add(fetchBtn);
        ctrlCard.add(Box.createVerticalStrut(8));

        JButton reparseBtn = makeSmallButton("🔄  Re-Parse Last HTML", ACCENT_PURPLE);
        reparseBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        reparseBtn.addActionListener(e -> parseAndDisplay(lastHtml));
        ctrlCard.add(reparseBtn);
        ctrlCard.add(Box.createVerticalGlue());

        body.add(ctrlCard, BorderLayout.WEST);

        // output area
        JPanel outCard = makeCard();
        outCard.setLayout(new BorderLayout(0, 8));
        JLabel outTitle = new JLabel("Output");
        outTitle.setFont(FONT_LABEL);
        outTitle.setForeground(TEXT_SECONDARY);
        outCard.add(outTitle, BorderLayout.NORTH);

        // reuse statusArea for crawler output - create a separate one here
        JTextArea crawlerOutput = new JTextArea();
        crawlerOutput.setEditable(false);
        crawlerOutput.setLineWrap(true);
        crawlerOutput.setWrapStyleWord(true);
        crawlerOutput.setFont(FONT_MONO);
        crawlerOutput.setBackground(BG_INPUT);
        crawlerOutput.setForeground(TEXT_PRIMARY);
        crawlerOutput.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        crawlerOutput.setText("Fetched HTML and parse results will appear here.");
        JScrollPane outScroll = new JScrollPane(crawlerOutput);
        outScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        outCard.add(outScroll, BorderLayout.CENTER);

        // wire fetch button to this output area
        fetchBtn.addActionListener(e -> {
            // second listener updates the crawlerOutput too
            new Thread(() -> {
                SwingUtilities.invokeLater(() -> {
                    HtmlParser parser = new HtmlParser();
                    if (lastHtml.isEmpty()) return;
                    String mode = (String) parseModeBox.getSelectedItem();
                    StringBuilder out = new StringBuilder();
                    out.append("=== ").append(mode).append(" ===\n\n");
                    if ("Text".equals(mode)) {
                        out.append(parser.getAllText(lastHtml));
                    } else if ("Links".equals(mode)) {
                        ArrayList<String> links = parser.getAllLinks(lastHtml);
                        out.append("Found ").append(links.size()).append(" links:\n\n");
                        for (String l : links) out.append(l).append("\n");
                    } else {
                        String sel = selectorField.getText().trim();
                        ArrayList<String> els = parser.searchByCssSelector(lastHtml, sel);
                        out.append("Found ").append(els.size()).append(" element(s):\n\n");
                        for (String el : els) out.append(el).append("\n---\n");
                    }
                    crawlerOutput.setText(out.toString());
                });
            }).start();
        });

        body.add(outCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: saved fuel ──────────────────────────────────────────────

    private JPanel buildSavedFuelTab() {
        JPanel page = makePage();
        page.add(makePageHeader("📋 Saved Fuel Prices", "All scraped fuel price records from pickapump.com"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // stats bar
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        statsBar.setBackground(BG_DARK);
        fuelCountLabel = makeStatLabel("0 records");
        statsBar.add(fuelCountLabel);
        fuelPathLabel = makeStatLabel("No data yet");
        statsBar.add(fuelPathLabel);

        JButton refreshBtn = makeSmallButton("🔄  Refresh", ACCENT_BLUE);
        refreshBtn.addActionListener(e -> loadFuelTable());
        statsBar.add(refreshBtn);
        body.add(statsBar, BorderLayout.NORTH);

        // table
        String[] cols = {"Timestamp", "Station", "Address", "Fuel Type", "Price"};
        fuelTableModel = new DefaultTableModel(cols, 0);
        fuelTable = new JTable(fuelTableModel);
        styleTable(fuelTable);
        fuelTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        fuelTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        fuelTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        fuelTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        fuelTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        JScrollPane scroll = new JScrollPane(fuelTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(BG_DARK);
        body.add(scroll, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: saved products ──────────────────────────────────────────

    private JPanel buildSavedProdTab() {
        JPanel page = makePage();
        page.add(makePageHeader("📦 Saved Products", "All saved product search results from supermarkets"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        statsBar.setBackground(BG_DARK);
        productPathLabel = makeStatLabel("No data yet");
        statsBar.add(productPathLabel);

        DefaultTableModel savedProdModel = new DefaultTableModel(
            new String[]{"Timestamp", "Supermarket", "Search Query", "Product Name", "Price"}, 0);
        JTable savedProdTable = new JTable(savedProdModel);
        styleTable(savedProdTable);

        JButton refreshBtn = makeSmallButton("🔄  Refresh", ACCENT_BLUE);
        refreshBtn.addActionListener(e -> {
            savedProdModel.setRowCount(0);
            String fp = productScraper.getCsvFilePath();
            ArrayList<String[]> rows = csvReader.readCsv(fp);
            for (String[] row : rows) savedProdModel.addRow(row);
            productPathLabel.setText(rows.size() + " records  ·  " + fp);
        });
        statsBar.add(refreshBtn);
        body.add(statsBar, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(savedProdTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(BG_DARK);
        body.add(scroll, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── ACTIONS ──────────────────────────────────────────────────────

    /** Scrapes fuel prices for the selected county. */
    private void onScrapeFuelClicked() {
        int index = countyBox.getSelectedIndex();
        String countyName = COUNTIES[index][0];
        String url        = COUNTIES[index][1];

        int waitSeconds = 15;
        try { waitSeconds = Math.max(Integer.parseInt(delayField.getText().trim()), 10); }
        catch (NumberFormatException ex) { }

        statusArea.setText("▶ Scraping " + countyName + "...\n" +
                           "  Chrome will open briefly.\n" +
                           "  Waiting up to " + waitSeconds + "s for data to load.");

        final int finalWait = waitSeconds;
        new Thread(() -> {
            try {
                JsBrowserFetcher browser = new JsBrowserFetcher();
                String html = browser.fetchWithJs(url, finalWait);
                lastHtml = html;

                if (html.startsWith("ERROR")) {
                    SwingUtilities.invokeLater(() ->
                        statusArea.setText("✗ Error: " + html));
                    return;
                }

                ArrayList<FuelScraper.FuelEntry> entries = fuelScraper.scrapePrices(html);

                if (entries.isEmpty()) {
                    SwingUtilities.invokeLater(() ->
                        statusArea.setText("✗ No prices found for " + countyName + ".\n\n" +
                            "The consent wall may still be blocking data.\n" +
                            "Try switching to the Crawler tab and using Text mode."));
                    return;
                }

                fuelScraper.saveToCsv(entries);

                StringBuilder summary = new StringBuilder();
                summary.append("✓ Scraped ").append(entries.size())
                       .append(" price(s) for ").append(countyName).append("\n\n");
                for (FuelScraper.FuelEntry e : entries) {
                    summary.append("  ").append(e.stationName)
                           .append("  ·  ").append(e.fuelType)
                           .append("  ·  ").append(e.price).append("\n");
                }

                SwingUtilities.invokeLater(() -> {
                    statusArea.setText(summary.toString());
                    loadFuelTable();
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                    statusArea.setText("✗ Error: " + ex.getMessage()));
            }
        }).start();
    }

    /** Searches selected supermarkets for the typed product. */
    private void onSearchProductsClicked() {
        String query = productSearchField.getText().trim();
        if (query.isEmpty() || query.equals("e.g. strawberries, milk, bread")) {
            productStatusArea.setText("✗ Please enter a product name first.");
            return;
        }

        ArrayList<Integer> selected = new ArrayList<>();
        for (int i = 0; i < supermarketCheckboxes.length; i++) {
            if (supermarketCheckboxes[i].isSelected()) selected.add(i);
        }

        if (selected.isEmpty()) {
            productStatusArea.setText("✗ Tick at least one supermarket.");
            return;
        }

        productTableModel.setRowCount(0);
        productStatusArea.setText("▶ Searching for '" + query + "' across " +
                                  selected.size() + " supermarket(s)...\n");

        new Thread(() -> {
            ArrayList<ProductScraper.ProductEntry> allResults = new ArrayList<>();

            for (int idx : selected) {
                String name = ProductScraper.SUPERMARKETS[idx][0];
                String tmpl = ProductScraper.SUPERMARKETS[idx][1];
                String searchUrl = productScraper.buildSearchUrl(tmpl, query);

                SwingUtilities.invokeLater(() ->
                    productStatusArea.append("  · Searching " + name + "...\n"));

                try {
                    JsBrowserFetcher browser = new JsBrowserFetcher();
                    String html = browser.fetchWithJs(searchUrl, 10);

                    if (html.startsWith("ERROR")) {
                        SwingUtilities.invokeLater(() ->
                            productStatusArea.append("    ✗ " + name + ": " + html + "\n"));
                        continue;
                    }

                    ArrayList<ProductScraper.ProductEntry> results =
                        productScraper.scrapeProducts(html, name, query);

                    allResults.addAll(results);

                    SwingUtilities.invokeLater(() -> {
                        for (ProductScraper.ProductEntry p : results) {
                            productTableModel.addRow(new String[]{
                                p.supermarket, p.productName, p.price,
                                p.searchQuery, p.timestamp
                            });
                        }
                        productStatusArea.append("    ✓ " + name + ": " + results.size() + " result(s)\n");
                    });

                } catch (Exception ex) {
                    final String err = ex.getMessage();
                    SwingUtilities.invokeLater(() ->
                        productStatusArea.append("    ✗ " + name + ": " + err + "\n"));
                }
            }

            final int total = allResults.size();
            SwingUtilities.invokeLater(() ->
                productStatusArea.append("\n✓ Done — " + total + " result(s) found.\n" +
                    "Click 'Save to CSV' to save them."));

        }).start();
    }

    /** Saves product table contents to CSV. */
    private void onSaveProductsClicked() {
        int rowCount = productTableModel.getRowCount();
        if (rowCount == 0) {
            productStatusArea.append("\n✗ Nothing to save. Search first.");
            return;
        }

        ArrayList<ProductScraper.ProductEntry> entries = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            entries.add(new ProductScraper.ProductEntry(
                (String) productTableModel.getValueAt(i, 4),
                (String) productTableModel.getValueAt(i, 0),
                (String) productTableModel.getValueAt(i, 3),
                (String) productTableModel.getValueAt(i, 1),
                (String) productTableModel.getValueAt(i, 2)
            ));
        }

        try {
            productScraper.saveToCsv(entries);
            productStatusArea.append("\n✓ Saved " + entries.size() + " rows to:\n  " +
                productScraper.getCsvFilePath());
        } catch (Exception ex) {
            productStatusArea.append("\n✗ Save failed: " + ex.getMessage());
        }
    }

    /** Loads fuel prices CSV into the saved fuel tab table. */
    private void loadFuelTable() {
        fuelTableModel.setRowCount(0);
        String fp = fuelScraper.getCsvFilePath();
        ArrayList<String[]> rows = csvReader.readCsv(fp);
        for (String[] row : rows) fuelTableModel.addRow(row);
        fuelCountLabel.setText(rows.size() + " records");
        fuelPathLabel.setText(fp);
    }

    /** Parses html and shows result in the status area. */
    private void parseAndDisplay(String html) {
        if (html == null || html.isEmpty() || html.startsWith("ERROR")) {
            statusArea.setText(html == null || html.isEmpty() ?
                "No HTML loaded yet." : html);
            return;
        }

        HtmlParser parser = new HtmlParser();
        String mode = (String) parseModeBox.getSelectedItem();
        StringBuilder out = new StringBuilder();
        out.append("=== ").append(mode).append(" ===\n\n");

        if ("Text".equals(mode)) {
            out.append(parser.getAllText(html));
        } else if ("Links".equals(mode)) {
            ArrayList<String> links = parser.getAllLinks(html);
            out.append("Found ").append(links.size()).append(" links:\n\n");
            for (String l : links) out.append(l).append("\n");
        } else {
            String sel = selectorField.getText().trim();
            ArrayList<String> els = parser.searchByCssSelector(html, sel);
            out.append("Found ").append(els.size()).append(" element(s):\n\n");
            for (String el : els) out.append(el).append("\n---\n");
        }

        statusArea.setText(out.toString());
    }

    // ── UI COMPONENT HELPERS ─────────────────────────────────────────

    /** Creates a dark page panel with BorderLayout. */
    private JPanel makePage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        return p;
    }

    /** Creates a page header with title and subtitle. */
    private JPanel makePageHeader(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 24, 16, 24)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        header.add(titleLabel, BorderLayout.CENTER);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(FONT_SMALL);
        subLabel.setForeground(TEXT_MUTED);
        header.add(subLabel, BorderLayout.SOUTH);

        return header;
    }

    /** Creates a dark card panel with padding and border. */
    private JPanel makeCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    /** Creates a styled text input field. */
    private JTextField makeInput(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(FONT_INPUT);
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        return f;
    }

    /** Creates a styled combobox. */
    private void styleCombo(JComboBox<?> box) {
        box.setFont(FONT_INPUT);
        box.setBackground(BG_INPUT);
        box.setForeground(TEXT_PRIMARY);
        box.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    /** Creates a primary action button with an accent colour. */
    private JButton makeButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(accent.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(accent.brighter());
                } else {
                    g2.setColor(accent);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Creates a smaller secondary button. */
    private JButton makeSmallButton(String text, Color accent) {
        JButton btn = makeButton(text, accent);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(160, 34));
        return btn;
    }

    /** Creates a sidebar nav button. */
    private JButton makeNavBtn(String text, String cardName, Color accent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(BG_INPUT);
                    g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
                    // left accent stripe
                    g2.setColor(accent);
                    g2.fillRoundRect(6, 2, 3, getHeight() - 4, 3, 3);
                }
                g2.setColor(getModel().isRollover() ? TEXT_PRIMARY : TEXT_SECONDARY);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), 18, y);
                g2.dispose();
            }
        };
        btn.putClientProperty("card", cardName);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setMaximumSize(new Dimension(200, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Adds a small muted section label to a panel. */
    private void addCardLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
    }

    /** Creates a horizontal divider line for the sidebar. */
    private JPanel makeDivider() {
        JPanel div = new JPanel();
        div.setBackground(BORDER_COLOR);
        div.setMaximumSize(new Dimension(200, 1));
        div.setPreferredSize(new Dimension(200, 1));
        return div;
    }

    /** Creates a sidebar section heading label. */
    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
        lbl.setMaximumSize(new Dimension(200, 28));
        return lbl;
    }

    /** Creates a small stat label for the saved tabs. */
    private JLabel makeStatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        lbl.setBackground(BG_CARD);
        lbl.setOpaque(true);
        return lbl;
    }

    /** Applies the dark theme styling to a JTable. */
    private void styleTable(JTable table) {
        table.setBackground(BG_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_INPUT);
        table.setRowHeight(30);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        // style the header
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_SECONDARY);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // alternating row colours
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                if (selected) {
                    setBackground(ACCENT_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? BG_DARK : BG_TABLE_ALT);
                    setForeground(TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }
}
