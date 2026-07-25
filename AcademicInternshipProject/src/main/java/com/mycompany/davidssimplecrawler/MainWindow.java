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
import static java.awt.Component.CENTER_ALIGNMENT;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame {

    // ── colour palette ───────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(15, 17, 26);
    private static final Color BG_CARD      = new Color(24, 28, 42);
    private static final Color BG_INPUT     = new Color(32, 37, 54);
    private static final Color BG_TABLE_ALT = new Color(20, 23, 35);
    private static final Color ACCENT_BLUE   = new Color(99, 102, 241);
    private static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    private static final Color ACCENT_GREEN  = new Color(16, 185, 129);
    private static final Color ACCENT_YELLOW = new Color(245, 158, 11);
    private static final Color ACCENT_RED    = new Color(239, 68, 68);
    private static final Color ACCENT_ORANGE = new Color(249, 115, 22);
    private static final Color ACCENT_TEAL   = new Color(20, 184, 166);
    private static final Color TEXT_PRIMARY   = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color TEXT_MUTED     = new Color(71, 85, 105);
    private static final Color BORDER_COLOR   = new Color(44, 50, 70);
    private static final Color GOLD           = new Color(234, 179, 8);

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_MONO  = new Font("Monospaced", Font.PLAIN, 12);

    // ── crawler tab ──────────────────────────────────────────────────
    private JTextField urlField;
    private JTextField delayField;
    private JTextField selectorField;
    private JComboBox<String> parseModeBox;
    private JTextArea statusArea;
    private String lastHtml = "";
    private JComboBox<String> countyBox;

    // ── product search tab ───────────────────────────────────────────
    private JTextField productSearchField;
    private JCheckBox[] supermarketCheckboxes;
    private JTextArea productStatusArea;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JLabel productPathLabel;

    // ── saved fuel tab ───────────────────────────────────────────────
    private JTable fuelTable;
    private DefaultTableModel fuelTableModel;
    private TableRowSorter<DefaultTableModel> fuelSorter;
    private JLabel fuelCountLabel;
    private JLabel fuelPathLabel;
    // per-column filters
    private JTextField fuelFilterTimestamp;
    private JTextField fuelFilterStation;
    private JTextField fuelFilterAddress;
    private JTextField fuelFilterFuelType;
    private JTextField fuelFilterPrice;
    // date range delete
    private JTextField fuelDateFrom;
    private JTextField fuelDateTo;

    // ── saved products tab ───────────────────────────────────────────
    private JTable prodSavedTable;
    private DefaultTableModel prodSavedModel;
    private TableRowSorter<DefaultTableModel> prodSorter;
    private JLabel prodCountLabel;
    private JLabel prodPathLabel;
    // per-column filters
    private JTextField prodFilterTimestamp;
    private JTextField prodFilterSupermarket;
    private JTextField prodFilterQuery;
    private JTextField prodFilterName;
    private JTextField prodFilterPrice;
    // date range delete
    private JTextField prodDateFrom;
    private JTextField prodDateTo;

    // ── cheapest finder tab ──────────────────────────────────────────
    private JTextField cheapestQueryField;
    private JTextArea  cheapestResultArea;

    // scrapers, readers, writers
    private FuelScraper    fuelScraper    = new FuelScraper();
    private ProductScraper productScraper = new ProductScraper();
    private CsvReader      csvReader      = new CsvReader();
    private CsvWriter      csvWriter      = new CsvWriter();

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
        setSize(1180, 760);
        setMinimumSize(new Dimension(960, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { }
        buildUI();
    }

    // ── main layout ──────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel contentArea = new JPanel(new CardLayout());
        contentArea.setBackground(BG_DARK);
        contentArea.add(buildFuelTab(),         "fuel");
        contentArea.add(buildProductTab(),      "products");
        contentArea.add(buildCrawlerTab(),      "crawler");
        contentArea.add(buildSavedFuelTab(),    "saved_fuel");
        contentArea.add(buildSavedProdTab(),    "saved_products");
        contentArea.add(buildCheapestTab(),     "cheapest");
        root.add(contentArea, BorderLayout.CENTER);

        wireSidebarToCards(root, contentArea);
        setContentPane(root);
    }

    // ── sidebar ──────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(205, 0));
        sidebar.setBackground(BG_CARD);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(BG_CARD);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));
        logoPanel.setMaximumSize(new Dimension(205, 90));
        JLabel logo = new JLabel("🕷 WebCrawler");
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));
        logo.setForeground(TEXT_PRIMARY);
        logoPanel.add(logo, BorderLayout.CENTER);
        JLabel sub = new JLabel("David Bulugea  ·  22516586");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_MUTED);
        logoPanel.add(sub, BorderLayout.SOUTH);
        sidebar.add(logoPanel);

        sidebar.add(makeDivider());
        sidebar.add(makeSectionLabel("SCRAPING"));
        sidebar.add(makeNavBtn("⛽  Fuel Prices",     "fuel",           ACCENT_BLUE));
        sidebar.add(makeNavBtn("🛒  Product Search",  "products",       ACCENT_GREEN));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeSectionLabel("DATA"));
        sidebar.add(makeNavBtn("📋  Saved Fuel",      "saved_fuel",     ACCENT_PURPLE));
        sidebar.add(makeNavBtn("📦  Saved Products",  "saved_products", ACCENT_YELLOW));
        sidebar.add(makeNavBtn("🏆  Cheapest Finder", "cheapest",       GOLD));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(makeDivider());
        sidebar.add(makeSectionLabel("TOOLS"));
        sidebar.add(makeNavBtn("🔧  Crawler",         "crawler",        ACCENT_RED));
        sidebar.add(Box.createVerticalGlue());

        JLabel version = new JLabel("  v1.0  ·  Day 7");
        version.setFont(FONT_SMALL);
        version.setForeground(TEXT_MUTED);
        version.setBorder(BorderFactory.createEmptyBorder(12, 20, 16, 0));
        sidebar.add(version);
        return sidebar;
    }

    private void wireSidebarToCards(JPanel root, JPanel contentArea) {
        CardLayout cl = (CardLayout) contentArea.getLayout();
        for (Component c : ((JPanel) root.getComponent(0)).getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                String card = (String) btn.getClientProperty("card");
                if (card != null) btn.addActionListener(e -> cl.show(contentArea, card));
            }
        }
    }

    // ── TAB: fuel prices (scraping) ──────────────────────────────────

    private JPanel buildFuelTab() {
        JPanel page = makePage();
        page.add(makePageHeader("⛽ Fuel Prices", "Scrape live fuel prices from Pickapump.com by county"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        JPanel ctrl = makeCard();
        ctrl.setLayout(new BoxLayout(ctrl, BoxLayout.Y_AXIS));
        ctrl.setPreferredSize(new Dimension(280, 0));

        addCardLabel(ctrl, "SELECT COUNTY");
        ctrl.add(Box.createVerticalStrut(6));
        String[] cn = new String[COUNTIES.length];
        for (int i = 0; i < COUNTIES.length; i++) cn[i] = COUNTIES[i][0];
        countyBox = new JComboBox<>(cn);
        styleCombo(countyBox);
        countyBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        countyBox.addActionListener(e -> {
            int idx = countyBox.getSelectedIndex();
            if (idx >= 0) urlField.setText(COUNTIES[idx][1]);
        });
        ctrl.add(countyBox);
        ctrl.add(Box.createVerticalStrut(16));

        addCardLabel(ctrl, "WAIT TIME (SECONDS)");
        ctrl.add(Box.createVerticalStrut(6));
        delayField = makeInput("15");
        delayField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ctrl.add(delayField);
        ctrl.add(Box.createVerticalStrut(20));

        JButton scrapeBtn = makeButton("⛽  Scrape Fuel Prices", ACCENT_BLUE);
        scrapeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        scrapeBtn.addActionListener(e -> onScrapeFuelClicked());
        ctrl.add(scrapeBtn);
        ctrl.add(Box.createVerticalStrut(12));

        JLabel note = new JLabel("<html><center>Chrome will briefly appear<br>on screen — this is normal.</center></html>");
        note.setFont(FONT_SMALL);
        note.setForeground(TEXT_MUTED);
        note.setAlignmentX(CENTER_ALIGNMENT);
        ctrl.add(note);
        ctrl.add(Box.createVerticalGlue());

        body.add(ctrl, BorderLayout.WEST);

        JPanel statusCard = makeCard();
        statusCard.setLayout(new BorderLayout(0, 8));
        JLabel st = new JLabel("Status Log");
        st.setFont(FONT_LABEL); st.setForeground(TEXT_SECONDARY);
        statusCard.add(st, BorderLayout.NORTH);

        statusArea = new JTextArea();
        statusArea.setEditable(false); statusArea.setLineWrap(true); statusArea.setWrapStyleWord(true);
        statusArea.setFont(FONT_MONO); statusArea.setBackground(BG_INPUT);
        statusArea.setForeground(TEXT_PRIMARY);
        statusArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        statusArea.setText("Select a county and click 'Scrape Fuel Prices' to begin.");
        JScrollPane ss = new JScrollPane(statusArea);
        ss.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        ss.getViewport().setBackground(BG_INPUT);
        statusCard.add(ss, BorderLayout.CENTER);

        urlField = new JTextField(COUNTIES[0][1]); urlField.setVisible(false);

        body.add(statusCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: product search ──────────────────────────────────────────

    private JPanel buildProductTab() {
        JPanel page = makePage();
        page.add(makePageHeader("🛒 Product Search", "Search any product across Irish supermarkets"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        JPanel top = makeCard();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        addCardLabel(top, "PRODUCT NAME");
        top.add(Box.createVerticalStrut(6));
        productSearchField = makeInput("e.g. bread, milk, olive oil");
        productSearchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        top.add(productSearchField);
        top.add(Box.createVerticalStrut(16));

        addCardLabel(top, "SUPERMARKETS");
        top.add(Box.createVerticalStrut(8));
        JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        checkRow.setBackground(BG_CARD);
        checkRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        supermarketCheckboxes = new JCheckBox[ProductScraper.SUPERMARKETS.length];
        for (int i = 0; i < ProductScraper.SUPERMARKETS.length; i++) {
            JCheckBox cb = new JCheckBox(ProductScraper.SUPERMARKETS[i][0]);
            cb.setFont(FONT_INPUT); cb.setForeground(TEXT_PRIMARY); cb.setBackground(BG_CARD);
            cb.setSelected(true);
            supermarketCheckboxes[i] = cb;
            checkRow.add(cb);
        }
        top.add(checkRow);
        top.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JButton searchBtn = makeButton("🔍  Search Products", ACCENT_GREEN);
        searchBtn.addActionListener(e -> onSearchProductsClicked());
        btnRow.add(searchBtn);
        JButton saveBtn = makeSmallButton("💾  Save to CSV", ACCENT_PURPLE);
        saveBtn.addActionListener(e -> onSaveProductsClicked());
        btnRow.add(saveBtn);
        top.add(btnRow);
        body.add(top, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setDividerLocation(100); split.setBackground(BG_DARK); split.setBorder(null);

        JPanel logCard = makeCard();
        logCard.setLayout(new BorderLayout(0, 6));
        JLabel lt = new JLabel("Search Log"); lt.setFont(FONT_LABEL); lt.setForeground(TEXT_SECONDARY);
        logCard.add(lt, BorderLayout.NORTH);
        productStatusArea = new JTextArea();
        productStatusArea.setEditable(false); productStatusArea.setLineWrap(true);
        productStatusArea.setFont(FONT_MONO); productStatusArea.setBackground(BG_INPUT);
        productStatusArea.setForeground(TEXT_PRIMARY);
        productStatusArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        productStatusArea.setText("Type a product name and click 'Search Products'.");
        JScrollPane ls = new JScrollPane(productStatusArea);
        ls.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        logCard.add(ls, BorderLayout.CENTER);
        split.setTopComponent(logCard);

        JPanel tableCard = makeCard();
        tableCard.setLayout(new BorderLayout(0, 8));
        JLabel tt = new JLabel("Results"); tt.setFont(FONT_LABEL); tt.setForeground(TEXT_SECONDARY);
        tableCard.add(tt, BorderLayout.NORTH);
        String[] cols = {"Supermarket", "Product Name", "Price", "Search Query", "Timestamp"};
        productTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productTableModel);
        styleTable(productTable);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        JScrollPane ts = new JScrollPane(productTable);
        ts.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        ts.getViewport().setBackground(BG_DARK);
        tableCard.add(ts, BorderLayout.CENTER);
        split.setBottomComponent(tableCard);

        body.add(split, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: crawler ─────────────────────────────────────────────────

    private JPanel buildCrawlerTab() {
        JPanel page = makePage();
        page.add(makePageHeader("🔧 Crawler", "Fetch and parse any webpage using CSS selectors"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        JPanel ctrl = makeCard();
        ctrl.setLayout(new BoxLayout(ctrl, BoxLayout.Y_AXIS));
        ctrl.setPreferredSize(new Dimension(300, 0));

        addCardLabel(ctrl, "TARGET URL");
        ctrl.add(Box.createVerticalStrut(6));
        JTextField crawlUrl = makeInput("https://example.com");
        crawlUrl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ctrl.add(crawlUrl);
        ctrl.add(Box.createVerticalStrut(14));

        addCardLabel(ctrl, "PARSE MODE");
        ctrl.add(Box.createVerticalStrut(6));
        parseModeBox = new JComboBox<>(new String[]{"Text", "Links", "CSS Selector"});
        styleCombo(parseModeBox);
        parseModeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ctrl.add(parseModeBox);
        ctrl.add(Box.createVerticalStrut(14));

        addCardLabel(ctrl, "CSS SELECTOR");
        ctrl.add(Box.createVerticalStrut(6));
        selectorField = makeInput("div.fuel-price-item");
        selectorField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ctrl.add(selectorField);
        ctrl.add(Box.createVerticalStrut(20));

        JTextArea out = new JTextArea();
        out.setEditable(false); out.setLineWrap(true); out.setWrapStyleWord(true);
        out.setFont(FONT_MONO); out.setBackground(BG_INPUT); out.setForeground(TEXT_PRIMARY);
        out.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        out.setText("Results appear here.");

        JButton fetchBtn = makeButton("🌐  Fetch & Parse", ACCENT_BLUE);
        fetchBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fetchBtn.addActionListener(e -> {
            String url = crawlUrl.getText().trim();
            if (url.isEmpty()) return;
            out.setText("Fetching " + url + "...");
            new Thread(() -> {
                PageFetcher f = new PageFetcher("Mozilla/5.0 (compatible; WebCrawlerApp/1.0)");
                String html = f.fetchPage(url); lastHtml = html;
                SwingUtilities.invokeLater(() -> {
                    HtmlParser p = new HtmlParser();
                    String mode = (String) parseModeBox.getSelectedItem();
                    StringBuilder sb = new StringBuilder("=== " + mode + " ===\n\n");
                    if ("Text".equals(mode)) sb.append(p.getAllText(html));
                    else if ("Links".equals(mode)) { ArrayList<String> lk = p.getAllLinks(html); sb.append(lk.size()).append(" links:\n\n"); for (String l : lk) sb.append(l).append("\n"); }
                    else { String sel = selectorField.getText().trim(); ArrayList<String> el = p.searchByCssSelector(html, sel); sb.append(el.size()).append(" element(s):\n\n"); for (String e2 : el) sb.append(e2).append("\n---\n"); }
                    out.setText(sb.toString());
                });
            }).start();
        });
        ctrl.add(fetchBtn);
        ctrl.add(Box.createVerticalStrut(8));

        JButton rp = makeSmallButton("🔄  Re-Parse", ACCENT_PURPLE);
        rp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        rp.addActionListener(e -> {
            if (lastHtml.isEmpty()) { out.setText("No HTML loaded yet."); return; }
            HtmlParser p = new HtmlParser(); String mode = (String) parseModeBox.getSelectedItem();
            StringBuilder sb = new StringBuilder("=== " + mode + " ===\n\n");
            if ("Text".equals(mode)) sb.append(p.getAllText(lastHtml));
            else if ("Links".equals(mode)) { ArrayList<String> lk = p.getAllLinks(lastHtml); sb.append(lk.size()).append(" links:\n\n"); for (String l : lk) sb.append(l).append("\n"); }
            else { String sel = selectorField.getText().trim(); ArrayList<String> el = p.searchByCssSelector(lastHtml, sel); sb.append(el.size()).append(" element(s):\n\n"); for (String e2 : el) sb.append(e2).append("\n---\n"); }
            out.setText(sb.toString());
        });
        ctrl.add(rp);
        ctrl.add(Box.createVerticalGlue());
        body.add(ctrl, BorderLayout.WEST);

        JPanel outCard = makeCard();
        outCard.setLayout(new BorderLayout(0, 8));
        JLabel ot = new JLabel("Output"); ot.setFont(FONT_LABEL); ot.setForeground(TEXT_SECONDARY);
        outCard.add(ot, BorderLayout.NORTH);
        JScrollPane os = new JScrollPane(out);
        os.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        outCard.add(os, BorderLayout.CENTER);
        body.add(outCard, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: saved fuel ──────────────────────────────────────────────

    private JPanel buildSavedFuelTab() {
        JPanel page = makePage();
        page.add(makePageHeader("📋 Saved Fuel Prices",
            "Filter by column · Delete selected / filtered / by date / all"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(12, 24, 24, 24));

        // ── top controls ─────────────────────────────────────────────
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBackground(BG_DARK);

        // row 1: stats + action buttons
        JPanel row1 = new JPanel(new BorderLayout(8, 0));
        row1.setBackground(BG_DARK);
        row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        stats.setBackground(BG_DARK);
        fuelCountLabel = makeStatLabel("0 records");
        fuelPathLabel  = makeStatLabel("No file");
        stats.add(fuelCountLabel);
        stats.add(fuelPathLabel);
        row1.add(stats, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btns.setBackground(BG_DARK);

        JButton refreshBtn        = makeSmallButton("🔄 Refresh",           ACCENT_BLUE);
        JButton selectAllBtn      = makeSmallButton("☑ Select All",         ACCENT_PURPLE);
        JButton selectFilteredBtn = makeSmallButton("☑ Select Filtered",    ACCENT_PURPLE);
        JButton invertBtn         = makeSmallButton("⇄ Invert",             ACCENT_YELLOW);
        JButton delSelBtn         = makeSmallButton("🗑 Delete Selected",    ACCENT_RED);
        JButton delFilteredBtn    = makeSmallButton("🗑 Delete Filtered",    ACCENT_ORANGE);
        JButton delAllBtn         = makeSmallButton("⚠ Delete All",         ACCENT_RED);

        refreshBtn.addActionListener(e -> loadFuelTable());
        selectAllBtn.addActionListener(e -> { if (fuelTable.getRowCount() > 0) fuelTable.setRowSelectionInterval(0, fuelTable.getRowCount()-1); });
        selectFilteredBtn.addActionListener(e -> { fuelTable.clearSelection(); for (int i = 0; i < fuelTable.getRowCount(); i++) fuelTable.addRowSelectionInterval(i, i); });
        invertBtn.addActionListener(e -> { for (int i = 0; i < fuelTable.getRowCount(); i++) { if (fuelTable.isRowSelected(i)) fuelTable.removeRowSelectionInterval(i, i); else fuelTable.addRowSelectionInterval(i, i); } });
        delSelBtn.addActionListener(e -> onDeleteSelectedFuel());
        delFilteredBtn.addActionListener(e -> onDeleteFilteredFuel());
        delAllBtn.addActionListener(e -> onDeleteAllFuel());

        for (JButton b : new JButton[]{refreshBtn, selectAllBtn, selectFilteredBtn, invertBtn, delSelBtn, delFilteredBtn, delAllBtn})
            btns.add(b);
        row1.add(btns, BorderLayout.EAST);
        controls.add(row1);

        // row 2: per-column search filters
        JPanel row2 = new JPanel(new GridLayout(1, 5, 6, 0));
        row2.setBackground(BG_DARK);
        row2.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        fuelFilterTimestamp = makeFilterInput("🔍 Timestamp");
        fuelFilterStation   = makeFilterInput("🔍 Station");
        fuelFilterAddress   = makeFilterInput("🔍 Address");
        fuelFilterFuelType  = makeFilterInput("🔍 Fuel Type");
        fuelFilterPrice     = makeFilterInput("🔍 Price");

        for (JTextField f : new JTextField[]{fuelFilterTimestamp, fuelFilterStation, fuelFilterAddress, fuelFilterFuelType, fuelFilterPrice}) {
            row2.add(f);
            f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFuelFilter(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFuelFilter(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFuelFilter(); }
            });
        }

        JPanel row2wrap = new JPanel(new BorderLayout(6, 0));
        row2wrap.setBackground(BG_DARK);
        row2wrap.add(row2, BorderLayout.CENTER);
        JButton clearFilters = makeSmallButton("✕ Clear Filters", TEXT_MUTED);
        clearFilters.addActionListener(e -> {
            for (JTextField f : new JTextField[]{fuelFilterTimestamp, fuelFilterStation, fuelFilterAddress, fuelFilterFuelType, fuelFilterPrice})
                f.setText("");
        });
        row2wrap.add(clearFilters, BorderLayout.EAST);
        controls.add(row2wrap);

        // row 3: date range delete
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row3.setBackground(BG_DARK);
        row3.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        JLabel dlabel = new JLabel("Delete records from:");
        dlabel.setForeground(TEXT_SECONDARY); dlabel.setFont(FONT_SMALL);
        row3.add(dlabel);
        fuelDateFrom = makeInput("dd/MM/yyyy");
        fuelDateFrom.setPreferredSize(new Dimension(110, 28));
        row3.add(fuelDateFrom);
        JLabel tolabel = new JLabel("to");
        tolabel.setForeground(TEXT_MUTED); tolabel.setFont(FONT_SMALL);
        row3.add(tolabel);
        fuelDateTo = makeInput("dd/MM/yyyy");
        fuelDateTo.setPreferredSize(new Dimension(110, 28));
        row3.add(fuelDateTo);
        JButton delDateBtn = makeSmallButton("🗑 Delete Date Range", ACCENT_ORANGE);
        delDateBtn.addActionListener(e -> onDeleteFuelByDateRange());
        row3.add(delDateBtn);
        JLabel dhint = new JLabel("  e.g. 01/07/2026 to 31/07/2026");
        dhint.setForeground(TEXT_MUTED); dhint.setFont(FONT_SMALL);
        row3.add(dhint);
        controls.add(row3);

        body.add(controls, BorderLayout.NORTH);

        // ── table ─────────────────────────────────────────────────────
        String[] cols = {"Timestamp", "Station", "Address", "Fuel Type", "Price"};
        fuelTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        fuelTable = new JTable(fuelTableModel);
        styleTable(fuelTable);
        fuelTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fuelSorter = new TableRowSorter<>(fuelTableModel);
        fuelTable.setRowSorter(fuelSorter);
        fuelTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        fuelTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        fuelTable.getColumnModel().getColumn(2).setPreferredWidth(190);
        fuelTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        fuelTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        fuelTable.getSelectionModel().addListSelectionListener(e -> updateFuelCount());

        JScrollPane sc = new JScrollPane(fuelTable);
        sc.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        sc.getViewport().setBackground(BG_DARK);
        body.add(sc, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: saved products ──────────────────────────────────────────

    private JPanel buildSavedProdTab() {
        JPanel page = makePage();
        page.add(makePageHeader("📦 Saved Products",
            "Filter by column · Delete selected / filtered / by date / all"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(12, 24, 24, 24));

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBackground(BG_DARK);

        // row 1: stats + buttons
        JPanel row1 = new JPanel(new BorderLayout(8, 0));
        row1.setBackground(BG_DARK);
        row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        stats.setBackground(BG_DARK);
        prodCountLabel = makeStatLabel("0 records");
        prodPathLabel  = makeStatLabel("No file");
        stats.add(prodCountLabel); stats.add(prodPathLabel);
        row1.add(stats, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btns.setBackground(BG_DARK);

        JButton refreshBtn        = makeSmallButton("🔄 Refresh",           ACCENT_BLUE);
        JButton selectAllBtn      = makeSmallButton("☑ Select All",         ACCENT_PURPLE);
        JButton selectFilteredBtn = makeSmallButton("☑ Select Filtered",    ACCENT_PURPLE);
        JButton invertBtn         = makeSmallButton("⇄ Invert",             ACCENT_YELLOW);
        JButton delSelBtn         = makeSmallButton("🗑 Delete Selected",    ACCENT_RED);
        JButton delFilteredBtn    = makeSmallButton("🗑 Delete Filtered",    ACCENT_ORANGE);
        JButton delAllBtn         = makeSmallButton("⚠ Delete All",         ACCENT_RED);

        refreshBtn.addActionListener(e -> loadProdTable());
        selectAllBtn.addActionListener(e -> { if (prodSavedTable.getRowCount() > 0) prodSavedTable.setRowSelectionInterval(0, prodSavedTable.getRowCount()-1); });
        selectFilteredBtn.addActionListener(e -> { prodSavedTable.clearSelection(); for (int i = 0; i < prodSavedTable.getRowCount(); i++) prodSavedTable.addRowSelectionInterval(i, i); });
        invertBtn.addActionListener(e -> { for (int i = 0; i < prodSavedTable.getRowCount(); i++) { if (prodSavedTable.isRowSelected(i)) prodSavedTable.removeRowSelectionInterval(i, i); else prodSavedTable.addRowSelectionInterval(i, i); } });
        delSelBtn.addActionListener(e -> onDeleteSelectedProd());
        delFilteredBtn.addActionListener(e -> onDeleteFilteredProd());
        delAllBtn.addActionListener(e -> onDeleteAllProd());

        for (JButton b : new JButton[]{refreshBtn, selectAllBtn, selectFilteredBtn, invertBtn, delSelBtn, delFilteredBtn, delAllBtn})
            btns.add(b);
        row1.add(btns, BorderLayout.EAST);
        controls.add(row1);

        // row 2: per-column filters
        JPanel row2 = new JPanel(new GridLayout(1, 5, 6, 0));
        row2.setBackground(BG_DARK);
        row2.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        prodFilterTimestamp   = makeFilterInput("🔍 Timestamp");
        prodFilterSupermarket = makeFilterInput("🔍 Supermarket");
        prodFilterQuery       = makeFilterInput("🔍 Search Query");
        prodFilterName        = makeFilterInput("🔍 Product Name");
        prodFilterPrice       = makeFilterInput("🔍 Price");

        for (JTextField f : new JTextField[]{prodFilterTimestamp, prodFilterSupermarket, prodFilterQuery, prodFilterName, prodFilterPrice}) {
            row2.add(f);
            f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { applyProdFilter(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { applyProdFilter(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { applyProdFilter(); }
            });
        }

        JPanel row2wrap = new JPanel(new BorderLayout(6, 0));
        row2wrap.setBackground(BG_DARK);
        row2wrap.add(row2, BorderLayout.CENTER);
        JButton clearFilters = makeSmallButton("✕ Clear Filters", TEXT_MUTED);
        clearFilters.addActionListener(e -> {
            for (JTextField f : new JTextField[]{prodFilterTimestamp, prodFilterSupermarket, prodFilterQuery, prodFilterName, prodFilterPrice})
                f.setText("");
        });
        row2wrap.add(clearFilters, BorderLayout.EAST);
        controls.add(row2wrap);

        // row 3: date range delete
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row3.setBackground(BG_DARK);
        row3.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        JLabel dl = new JLabel("Delete records from:");
        dl.setForeground(TEXT_SECONDARY); dl.setFont(FONT_SMALL);
        row3.add(dl);
        prodDateFrom = makeInput("dd/MM/yyyy");
        prodDateFrom.setPreferredSize(new Dimension(110, 28));
        row3.add(prodDateFrom);
        JLabel tl = new JLabel("to"); tl.setForeground(TEXT_MUTED); tl.setFont(FONT_SMALL);
        row3.add(tl);
        prodDateTo = makeInput("dd/MM/yyyy");
        prodDateTo.setPreferredSize(new Dimension(110, 28));
        row3.add(prodDateTo);
        JButton ddb = makeSmallButton("🗑 Delete Date Range", ACCENT_ORANGE);
        ddb.addActionListener(e -> onDeleteProdByDateRange());
        row3.add(ddb);
        JLabel dh = new JLabel("  e.g. 01/07/2026 to 31/07/2026");
        dh.setForeground(TEXT_MUTED); dh.setFont(FONT_SMALL);
        row3.add(dh);
        controls.add(row3);

        body.add(controls, BorderLayout.NORTH);

        // table
        String[] cols = {"Timestamp", "Supermarket", "Search Query", "Product Name", "Price"};
        prodSavedModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        prodSavedTable = new JTable(prodSavedModel);
        styleTable(prodSavedTable);
        prodSavedTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        prodSorter = new TableRowSorter<>(prodSavedModel);
        prodSavedTable.setRowSorter(prodSorter);
        prodSavedTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        prodSavedTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        prodSavedTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        prodSavedTable.getColumnModel().getColumn(3).setPreferredWidth(260);
        prodSavedTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        prodSavedTable.getSelectionModel().addListSelectionListener(e -> updateProdCount());

        JScrollPane sc = new JScrollPane(prodSavedTable);
        sc.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        sc.getViewport().setBackground(BG_DARK);
        body.add(sc, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── TAB: cheapest finder ─────────────────────────────────────────

    private JPanel buildCheapestTab() {
        JPanel page = makePage();
        page.add(makePageHeader("🏆 Cheapest Finder",
            "Compare prices across supermarkets · Find the best deal from saved data"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BG_DARK);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // top: search input
        JPanel topCard = makeCard();
        topCard.setLayout(new BoxLayout(topCard, BoxLayout.Y_AXIS));

        addCardLabel(topCard, "PRODUCT TO COMPARE");
        topCard.add(Box.createVerticalStrut(6));
        cheapestQueryField = makeInput("e.g. bread, milk, butter");
        cheapestQueryField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        topCard.add(cheapestQueryField);
        topCard.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton findBtn = makeButton("🏆  Find Cheapest", GOLD);
        findBtn.addActionListener(e -> onFindCheapest());
        btnRow.add(findBtn);

        JButton compareBtn = makeButton("📊  Compare All Stores", ACCENT_TEAL);
        compareBtn.addActionListener(e -> onCompareAllStores());
        btnRow.add(compareBtn);

        JButton fuelCheapBtn = makeButton("⛽  Cheapest Fuel", ACCENT_BLUE);
        fuelCheapBtn.addActionListener(e -> onFindCheapestFuel());
        btnRow.add(fuelCheapBtn);

        topCard.add(btnRow);
        body.add(topCard, BorderLayout.NORTH);

        // results area
        JPanel resultCard = makeCard();
        resultCard.setLayout(new BorderLayout(0, 8));
        JLabel rt = new JLabel("Results");
        rt.setFont(FONT_LABEL); rt.setForeground(TEXT_SECONDARY);
        resultCard.add(rt, BorderLayout.NORTH);

        cheapestResultArea = new JTextArea();
        cheapestResultArea.setEditable(false);
        cheapestResultArea.setFont(FONT_MONO);
        cheapestResultArea.setBackground(BG_INPUT);
        cheapestResultArea.setForeground(TEXT_PRIMARY);
        cheapestResultArea.setLineWrap(true);
        cheapestResultArea.setWrapStyleWord(true);
        cheapestResultArea.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        cheapestResultArea.setText(
            "Type a product name and click one of the buttons above.\n\n" +
            "  🏆  Find Cheapest     — shows the single cheapest match per supermarket\n" +
            "  📊  Compare All       — lists all matching products grouped by supermarket\n" +
            "  ⛽  Cheapest Fuel     — shows cheapest Petrol and Diesel across all saved stations\n\n" +
            "Data is read from your saved CSV files.\n" +
            "Run a search or scrape first to populate the data."
        );
        JScrollPane rs = new JScrollPane(cheapestResultArea);
        rs.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        rs.getViewport().setBackground(BG_INPUT);
        resultCard.add(rs, BorderLayout.CENTER);

        body.add(resultCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ── FILTER LOGIC ─────────────────────────────────────────────────

    private void applyFuelFilter() {
        ArrayList<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
        addColFilter(filters, fuelFilterTimestamp, 0);
        addColFilter(filters, fuelFilterStation,   1);
        addColFilter(filters, fuelFilterAddress,   2);
        addColFilter(filters, fuelFilterFuelType,  3);
        addColFilter(filters, fuelFilterPrice,     4);
        fuelSorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
        updateFuelCount();
    }

    private void applyProdFilter() {
        ArrayList<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
        addColFilter(filters, prodFilterTimestamp,   0);
        addColFilter(filters, prodFilterSupermarket, 1);
        addColFilter(filters, prodFilterQuery,       2);
        addColFilter(filters, prodFilterName,        3);
        addColFilter(filters, prodFilterPrice,       4);
        prodSorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
        updateProdCount();
    }

    private void addColFilter(ArrayList<RowFilter<DefaultTableModel, Object>> list, JTextField f, int col) {
        String text = getFilterText(f);
        if (!text.isEmpty()) {
            try { list.add(RowFilter.regexFilter("(?i)" + text, col)); }
            catch (Exception e) { }
        }
    }

    /** Gets the real text from a filter field, ignoring placeholder text. */
    private String getFilterText(JTextField f) {
        String t = f.getText().trim();
        // ignore placeholder text (starts with 🔍)
        if (t.startsWith("🔍")) return "";
        return t;
    }

    private void updateFuelCount() {
        int total    = fuelTableModel.getRowCount();
        int filtered = fuelTable.getRowCount();
        int selected = fuelTable.getSelectedRowCount();
        String label = total + " total";
        if (filtered < total) label += "  ·  " + filtered + " filtered";
        if (selected > 0)     label += "  ·  " + selected + " selected";
        fuelCountLabel.setText(label);
    }

    private void updateProdCount() {
        int total    = prodSavedModel.getRowCount();
        int filtered = prodSavedTable.getRowCount();
        int selected = prodSavedTable.getSelectedRowCount();
        String label = total + " total";
        if (filtered < total) label += "  ·  " + filtered + " filtered";
        if (selected > 0)     label += "  ·  " + selected + " selected";
        prodCountLabel.setText(label);
    }

    // ── DELETE LOGIC ─────────────────────────────────────────────────

    private void onDeleteSelectedFuel() {
        int[] selected = fuelTable.getSelectedRows();
        if (selected.length == 0) { showInfo("No rows selected."); return; }
        if (!confirmDelete(selected.length + " selected row(s)")) return;
        ArrayList<Integer> modelRows = new ArrayList<>();
        for (int v : selected) modelRows.add(fuelTable.convertRowIndexToModel(v));
        ArrayList<Integer> keep = new ArrayList<>();
        for (int i = 0; i < fuelTableModel.getRowCount(); i++) if (!modelRows.contains(i)) keep.add(i);
        writeAndReloadFuel(keep);
    }

    private void onDeleteFilteredFuel() {
        int count = fuelTable.getRowCount();
        if (count == 0) { showInfo("No filtered rows to delete."); return; }
        if (!confirmDelete("all " + count + " filtered row(s)")) return;
        ArrayList<Integer> filteredModel = new ArrayList<>();
        for (int i = 0; i < count; i++) filteredModel.add(fuelTable.convertRowIndexToModel(i));
        ArrayList<Integer> keep = new ArrayList<>();
        for (int i = 0; i < fuelTableModel.getRowCount(); i++) if (!filteredModel.contains(i)) keep.add(i);
        writeAndReloadFuel(keep);
    }

    private void onDeleteAllFuel() {
        if (fuelTableModel.getRowCount() == 0) { showInfo("No records to delete."); return; }
        if (!confirmDelete("ALL " + fuelTableModel.getRowCount() + " fuel records")) return;
        if (JOptionPane.showConfirmDialog(this, "Final confirmation — delete everything?",
                "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try { csvWriter.clearAllRows(fuelScraper.getCsvFilePath()); loadFuelTable(); }
        catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void onDeleteFuelByDateRange() {
        String fromStr = fuelDateFrom.getText().trim();
        String toStr   = fuelDateTo.getText().trim();
        if (fromStr.isEmpty() || toStr.isEmpty() || fromStr.equals("dd/MM/yyyy") || toStr.equals("dd/MM/yyyy")) {
            showInfo("Enter both a From and To date in dd/MM/yyyy format."); return;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate from = LocalDate.parse(fromStr, fmt);
            LocalDate to   = LocalDate.parse(toStr, fmt);

            // find rows to keep - those whose timestamp date is outside the range
            ArrayList<Integer> keep = new ArrayList<>();
            for (int i = 0; i < fuelTableModel.getRowCount(); i++) {
                String ts = (String) fuelTableModel.getValueAt(i, 0);
                try {
                    // timestamp format is dd/MM/yyyy HH:mm:ss - take just the date part
                    LocalDate rowDate = LocalDate.parse(ts.substring(0, 10), fmt);
                    if (rowDate.isBefore(from) || rowDate.isAfter(to)) keep.add(i);
                } catch (Exception e) {
                    keep.add(i); // keep rows with unparseable dates
                }
            }

            int deleteCount = fuelTableModel.getRowCount() - keep.size();
            if (deleteCount == 0) { showInfo("No records found in that date range."); return; }
            if (!confirmDelete(deleteCount + " record(s) between " + fromStr + " and " + toStr)) return;

            writeAndReloadFuel(keep);
        } catch (Exception ex) {
            showInfo("Invalid date format. Use dd/MM/yyyy e.g. 01/07/2026");
        }
    }

    private void writeAndReloadFuel(ArrayList<Integer> keep) {
        try { csvWriter.deleteRows(fuelScraper.getCsvFilePath(), keep); loadFuelTable(); }
        catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void onDeleteSelectedProd() {
        int[] selected = prodSavedTable.getSelectedRows();
        if (selected.length == 0) { showInfo("No rows selected."); return; }
        if (!confirmDelete(selected.length + " selected row(s)")) return;
        ArrayList<Integer> modelRows = new ArrayList<>();
        for (int v : selected) modelRows.add(prodSavedTable.convertRowIndexToModel(v));
        ArrayList<Integer> keep = new ArrayList<>();
        for (int i = 0; i < prodSavedModel.getRowCount(); i++) if (!modelRows.contains(i)) keep.add(i);
        writeAndReloadProd(keep);
    }

    private void onDeleteFilteredProd() {
        int count = prodSavedTable.getRowCount();
        if (count == 0) { showInfo("No filtered rows to delete."); return; }
        if (!confirmDelete("all " + count + " filtered row(s)")) return;
        ArrayList<Integer> filteredModel = new ArrayList<>();
        for (int i = 0; i < count; i++) filteredModel.add(prodSavedTable.convertRowIndexToModel(i));
        ArrayList<Integer> keep = new ArrayList<>();
        for (int i = 0; i < prodSavedModel.getRowCount(); i++) if (!filteredModel.contains(i)) keep.add(i);
        writeAndReloadProd(keep);
    }

    private void onDeleteAllProd() {
        if (prodSavedModel.getRowCount() == 0) { showInfo("No records to delete."); return; }
        if (!confirmDelete("ALL " + prodSavedModel.getRowCount() + " product records")) return;
        if (JOptionPane.showConfirmDialog(this, "Final confirmation — delete everything?",
                "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try { csvWriter.clearAllRows(productScraper.getCsvFilePath()); loadProdTable(); }
        catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void onDeleteProdByDateRange() {
        String fromStr = prodDateFrom.getText().trim();
        String toStr   = prodDateTo.getText().trim();
        if (fromStr.isEmpty() || toStr.isEmpty() || fromStr.equals("dd/MM/yyyy") || toStr.equals("dd/MM/yyyy")) {
            showInfo("Enter both a From and To date in dd/MM/yyyy format."); return;
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate from = LocalDate.parse(fromStr, fmt);
            LocalDate to   = LocalDate.parse(toStr, fmt);
            ArrayList<Integer> keep = new ArrayList<>();
            for (int i = 0; i < prodSavedModel.getRowCount(); i++) {
                String ts = (String) prodSavedModel.getValueAt(i, 0);
                try {
                    LocalDate rowDate = LocalDate.parse(ts.substring(0, 10), fmt);
                    if (rowDate.isBefore(from) || rowDate.isAfter(to)) keep.add(i);
                } catch (Exception e) { keep.add(i); }
            }
            int deleteCount = prodSavedModel.getRowCount() - keep.size();
            if (deleteCount == 0) { showInfo("No records found in that date range."); return; }
            if (!confirmDelete(deleteCount + " record(s) between " + fromStr + " and " + toStr)) return;
            writeAndReloadProd(keep);
        } catch (Exception ex) {
            showInfo("Invalid date format. Use dd/MM/yyyy e.g. 01/07/2026");
        }
    }

    private void writeAndReloadProd(ArrayList<Integer> keep) {
        try { csvWriter.deleteRows(productScraper.getCsvFilePath(), keep); loadProdTable(); }
        catch (Exception ex) { showError(ex.getMessage()); }
    }

    // ── CHEAPEST FINDER LOGIC ────────────────────────────────────────

    /**
     * Finds the cheapest match per supermarket for the typed query.
     * Reads all product CSV rows, filters by query keyword, extracts numeric price,
     * then shows the cheapest per store and overall winner.
     */
    private void onFindCheapest() {
        String query = cheapestQueryField.getText().trim();
        if (query.isEmpty() || query.equals("e.g. bread, milk, butter")) {
            cheapestResultArea.setText("Enter a product name first."); return;
        }

        ArrayList<String[]> rows = csvReader.readCsv(productScraper.getCsvFilePath());
        if (rows.isEmpty()) {
            cheapestResultArea.setText("No product data saved yet.\nSearch for products first then come back here.");
            return;
        }

        // map: supermarket -> {name, price (numeric), priceStr}
        Map<String, String[]> cheapestPerStore = new HashMap<>();

        for (String[] row : rows) {
            if (row.length < 5) continue;
            String supermarket = row.length > 1 ? row[1] : "";
            String searchQuery = row.length > 2 ? row[2] : "";
            String name        = row.length > 3 ? row[3] : "";
            String priceStr    = row.length > 4 ? row[4] : "";

            // check if this row matches the query (by name or search query column)
            if (!name.toLowerCase().contains(query.toLowerCase()) &&
                !searchQuery.toLowerCase().contains(query.toLowerCase())) continue;

            double price = extractPrice(priceStr);
            if (price < 0) continue;

            if (!cheapestPerStore.containsKey(supermarket)) {
                cheapestPerStore.put(supermarket, new String[]{name, String.valueOf(price), priceStr});
            } else {
                double existing = Double.parseDouble(cheapestPerStore.get(supermarket)[1]);
                if (price < existing) {
                    cheapestPerStore.put(supermarket, new String[]{name, String.valueOf(price), priceStr});
                }
            }
        }

        if (cheapestPerStore.isEmpty()) {
            cheapestResultArea.setText("No results found for '" + query + "'.\n\nMake sure you have searched for this product first and saved the results.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🏆  CHEAPEST '").append(query.toUpperCase()).append("' PER SUPERMARKET\n");
        sb.append("─".repeat(60)).append("\n\n");

        // find overall winner
        String overallStore = null;
        double overallPrice = Double.MAX_VALUE;

        // sort by price
        cheapestPerStore.entrySet().stream()
            .sorted((a, b) -> Double.compare(
                Double.parseDouble(a.getValue()[1]),
                Double.parseDouble(b.getValue()[1])))
            .forEach(entry -> {
                String store    = entry.getKey();
                String prodName = entry.getValue()[0];
                String price    = entry.getValue()[2];
                sb.append(String.format("  %-18s  %s  %s\n", store, price, prodName));
            });

        // find the winner separately
        for (Map.Entry<String, String[]> e : cheapestPerStore.entrySet()) {
            double p = Double.parseDouble(e.getValue()[1]);
            if (p < overallPrice) { overallPrice = p; overallStore = e.getKey(); }
        }

        sb.append("\n").append("─".repeat(60)).append("\n");
        sb.append("🥇  BEST DEAL:  ").append(overallStore).append("  —  ")
          .append(cheapestPerStore.get(overallStore)[2])
          .append("  —  ").append(cheapestPerStore.get(overallStore)[0]).append("\n");

        cheapestResultArea.setText(sb.toString());
    }

    /**
     * Compares all matching products across supermarkets, grouped by store.
     */
    private void onCompareAllStores() {
        String query = cheapestQueryField.getText().trim();
        if (query.isEmpty() || query.equals("e.g. bread, milk, butter")) {
            cheapestResultArea.setText("Enter a product name first."); return;
        }

        ArrayList<String[]> rows = csvReader.readCsv(productScraper.getCsvFilePath());
        if (rows.isEmpty()) {
            cheapestResultArea.setText("No product data saved yet.\nSearch for products first."); return;
        }

        // group by supermarket
        Map<String, ArrayList<String[]>> byStore = new HashMap<>();
        for (String[] row : rows) {
            if (row.length < 5) continue;
            String supermarket = row.length > 1 ? row[1] : "";
            String searchQuery = row.length > 2 ? row[2] : "";
            String name        = row.length > 3 ? row[3] : "";
            String priceStr    = row.length > 4 ? row[4] : "";

            if (!name.toLowerCase().contains(query.toLowerCase()) &&
                !searchQuery.toLowerCase().contains(query.toLowerCase())) continue;

            byStore.computeIfAbsent(supermarket, k -> new ArrayList<>())
                   .add(new String[]{name, priceStr});
        }

        if (byStore.isEmpty()) {
            cheapestResultArea.setText("No results found for '" + query + "'."); return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📊  ALL '").append(query.toUpperCase()).append("' PRODUCTS BY SUPERMARKET\n");
        sb.append("─".repeat(60)).append("\n");

        for (Map.Entry<String, ArrayList<String[]>> entry : byStore.entrySet()) {
            sb.append("\n  ").append(entry.getKey().toUpperCase()).append("\n");
            // sort by price within each store
            entry.getValue().stream()
                .sorted((a, b) -> Double.compare(extractPrice(a[1]), extractPrice(b[1])))
                .forEach(p -> sb.append(String.format("    %-8s  %s\n", p[1], p[0])));
        }

        cheapestResultArea.setText(sb.toString());
    }

    /**
     * Finds cheapest Petrol and Diesel across all saved fuel price records.
     */
    private void onFindCheapestFuel() {
        ArrayList<String[]> rows = csvReader.readCsv(fuelScraper.getCsvFilePath());
        if (rows.isEmpty()) {
            cheapestResultArea.setText("No fuel data saved yet.\nScrape some fuel prices first."); return;
        }

        // track cheapest per fuel type
        Map<String, double[]> cheapest = new HashMap<>(); // fuelType -> {price, rowIndex}
        Map<String, String[]> cheapestRow = new HashMap<>(); // fuelType -> row

        for (String[] row : rows) {
            if (row.length < 5) continue;
            String station  = row.length > 1 ? row[1] : "";
            String address  = row.length > 2 ? row[2] : "";
            String fuelType = row.length > 3 ? row[3] : "";
            String priceStr = row.length > 4 ? row[4] : "";

            double price = extractPrice(priceStr);
            if (price < 0) continue;

            if (!cheapest.containsKey(fuelType) || price < cheapest.get(fuelType)[0]) {
                cheapest.put(fuelType, new double[]{price});
                cheapestRow.put(fuelType, new String[]{station, address, fuelType, priceStr});
            }
        }

        if (cheapest.isEmpty()) {
            cheapestResultArea.setText("Could not parse prices from the fuel data."); return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("⛽  CHEAPEST FUEL PRICES ACROSS ALL SAVED STATIONS\n");
        sb.append("─".repeat(60)).append("\n\n");

        cheapest.entrySet().stream()
            .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
            .forEach(entry -> {
                String[] r = cheapestRow.get(entry.getKey());
                sb.append(String.format("  %-16s  %-8s  %s\n", entry.getKey(), r[3], r[0]));
                sb.append(String.format("                    %s\n\n", r[1]));
            });

        // total count
        sb.append("─".repeat(60)).append("\n");
        sb.append("Data from ").append(rows.size()).append(" saved price records.\n");
        sb.append("Use the Fuel Prices tab to scrape more counties.");

        cheapestResultArea.setText(sb.toString());
    }

    /**
     * Extracts a numeric price from a price string like "€1.29", "174.9c", "1.79".
     * Returns -1 if the price cannot be parsed.
     */
    private double extractPrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return -1;
        // remove currency symbols and unit suffixes, keep digits and decimal point
        String cleaned = priceStr.replaceAll("[^0-9.]", "").trim();
        if (cleaned.isEmpty()) return -1;
        try {
            double val = Double.parseDouble(cleaned);
            // fuel prices in pence (e.g. 174.9) vs product prices in euros (e.g. 1.79)
            // if the value is > 10 and the original contained 'c' it is in cent/pence
            if (val > 10 && priceStr.contains("c")) val = val / 100.0;
            return val;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ── DATA LOAD METHODS ────────────────────────────────────────────

    private void loadFuelTable() {
        fuelTableModel.setRowCount(0);
        String fp = fuelScraper.getCsvFilePath();
        ArrayList<String[]> rows = csvReader.readCsv(fp);
        for (String[] row : rows) fuelTableModel.addRow(row);
        fuelPathLabel.setText(fp);
        applyFuelFilter();
    }

    private void loadProdTable() {
        prodSavedModel.setRowCount(0);
        String fp = productScraper.getCsvFilePath();
        ArrayList<String[]> rows = csvReader.readCsv(fp);
        for (String[] row : rows) prodSavedModel.addRow(row);
        prodPathLabel.setText(fp);
        applyProdFilter();
    }

    // ── SCRAPING ACTIONS ─────────────────────────────────────────────

    private void onScrapeFuelClicked() {
        int idx = countyBox.getSelectedIndex();
        String countyName = COUNTIES[idx][0];
        String url        = COUNTIES[idx][1];
        int wait = 15;
        try { wait = Math.max(Integer.parseInt(delayField.getText().trim()), 10); } catch (Exception e) { }
        statusArea.setText("▶ Scraping " + countyName + "...\n  Chrome will open briefly.\n  Waiting up to " + wait + "s...");
        final int fw = wait;
        new Thread(() -> {
            try {
                JsBrowserFetcher b = new JsBrowserFetcher();
                String html = b.fetchWithJs(url, fw); lastHtml = html;
                if (html.startsWith("ERROR")) { SwingUtilities.invokeLater(() -> statusArea.setText("✗ " + html)); return; }
                ArrayList<FuelScraper.FuelEntry> entries = fuelScraper.scrapePrices(html);
                if (entries.isEmpty()) { SwingUtilities.invokeLater(() -> statusArea.setText("✗ No prices found for " + countyName + ".")); return; }
                fuelScraper.saveToCsv(entries);
                StringBuilder sb = new StringBuilder("✓ Scraped " + entries.size() + " price(s) for " + countyName + "\n\n");
                for (FuelScraper.FuelEntry e : entries) sb.append("  ").append(e.stationName).append("  ·  ").append(e.fuelType).append("  ·  ").append(e.price).append("\n");
                SwingUtilities.invokeLater(() -> { statusArea.setText(sb.toString()); loadFuelTable(); });
            } catch (Exception ex) { SwingUtilities.invokeLater(() -> statusArea.setText("✗ " + ex.getMessage())); }
        }).start();
    }

    private void onSearchProductsClicked() {
        String query = productSearchField.getText().trim();
        if (query.isEmpty() || query.equals("e.g. bread, milk, olive oil")) { productStatusArea.setText("✗ Enter a product name first."); return; }
        ArrayList<Integer> sel = new ArrayList<>();
        for (int i = 0; i < supermarketCheckboxes.length; i++) if (supermarketCheckboxes[i].isSelected()) sel.add(i);
        if (sel.isEmpty()) { productStatusArea.setText("✗ Tick at least one supermarket."); return; }
        productTableModel.setRowCount(0);
        productStatusArea.setText("▶ Searching for '" + query + "' across " + sel.size() + " supermarket(s)...\n");
        new Thread(() -> {
            ArrayList<ProductScraper.ProductEntry> all = new ArrayList<>();
            for (int idx : sel) {
                String name = ProductScraper.SUPERMARKETS[idx][0];
                String tmpl = ProductScraper.SUPERMARKETS[idx][1];
                String surl = productScraper.buildSearchUrl(tmpl, query);
                SwingUtilities.invokeLater(() -> productStatusArea.append("  · Searching " + name + "...\n"));
                try {
                    JsBrowserFetcher b = new JsBrowserFetcher();
                    String html = b.fetchWithJs(surl, 10);
                    if (html.startsWith("ERROR")) { SwingUtilities.invokeLater(() -> productStatusArea.append("    ✗ " + name + ": " + html + "\n")); continue; }
                    ArrayList<ProductScraper.ProductEntry> res = productScraper.scrapeProducts(html, name, query);
                    all.addAll(res);
                    SwingUtilities.invokeLater(() -> {
                        for (ProductScraper.ProductEntry p : res) productTableModel.addRow(new String[]{p.supermarket, p.productName, p.price, p.searchQuery, p.timestamp});
                        productStatusArea.append("    ✓ " + name + ": " + res.size() + " result(s)\n");
                    });
                } catch (Exception ex) { final String err = ex.getMessage(); SwingUtilities.invokeLater(() -> productStatusArea.append("    ✗ " + name + ": " + err + "\n")); }
            }
            final int total = all.size();
            SwingUtilities.invokeLater(() -> productStatusArea.append("\n✓ Done — " + total + " result(s). Click 'Save to CSV' to save."));
        }).start();
    }

    private void onSaveProductsClicked() {
        int rc = productTableModel.getRowCount();
        if (rc == 0) { productStatusArea.append("\n✗ Nothing to save."); return; }
        ArrayList<ProductScraper.ProductEntry> entries = new ArrayList<>();
        for (int i = 0; i < rc; i++) entries.add(new ProductScraper.ProductEntry(
            (String) productTableModel.getValueAt(i, 4), (String) productTableModel.getValueAt(i, 0),
            (String) productTableModel.getValueAt(i, 3), (String) productTableModel.getValueAt(i, 1),
            (String) productTableModel.getValueAt(i, 2)));
        try { productScraper.saveToCsv(entries); productStatusArea.append("\n✓ Saved " + rc + " rows."); }
        catch (Exception ex) { productStatusArea.append("\n✗ " + ex.getMessage()); }
    }

    // ── HELPER DIALOGS ───────────────────────────────────────────────

    private boolean confirmDelete(String desc) {
        return JOptionPane.showConfirmDialog(this, "⚠  Delete " + desc + "?\n\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void showInfo(String msg) { JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }

    // ── UI COMPONENT HELPERS ─────────────────────────────────────────

    private JTextField makeFilterInput(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(FONT_SMALL); f.setBackground(BG_INPUT); f.setCaretColor(TEXT_PRIMARY);
        f.setForeground(TEXT_MUTED);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (f.getText().startsWith("🔍")) { f.setText(""); f.setForeground(TEXT_PRIMARY); } }
            public void focusLost(FocusEvent e)   { if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(TEXT_MUTED); } }
        });
        return f;
    }

    private JPanel makePage() { JPanel p = new JPanel(new BorderLayout()); p.setBackground(BG_DARK); return p; }

    private JPanel makePageHeader(String title, String subtitle) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_DARK);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 24, 16, 24)));
        JLabel tl = new JLabel(title); tl.setFont(FONT_TITLE); tl.setForeground(TEXT_PRIMARY);
        JLabel sl = new JLabel(subtitle); sl.setFont(FONT_SMALL); sl.setForeground(TEXT_MUTED);
        h.add(tl, BorderLayout.CENTER); h.add(sl, BorderLayout.SOUTH);
        return h;
    }

    private JPanel makeCard() {
        JPanel c = new JPanel(); c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR), BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return c;
    }

    private JTextField makeInput(String ph) {
        JTextField f = new JTextField(ph); f.setFont(FONT_INPUT); f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY); f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR), BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return f;
    }

    private void styleCombo(JComboBox<?> box) {
        box.setFont(FONT_INPUT); box.setBackground(BG_INPUT); box.setForeground(TEXT_PRIMARY);
        box.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private JButton makeButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? accent.darker() : getModel().isRollover() ? accent.brighter() : accent);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13)); btn.setPreferredSize(new Dimension(180, 40));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeSmallButton(String text, Color accent) {
        JButton btn = makeButton(text, accent);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setPreferredSize(new Dimension(130, 28));
        return btn;
    }

    private JButton makeNavBtn(String text, String card, Color accent) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(BG_INPUT); g2.fillRoundRect(6, 2, getWidth()-12, getHeight()-4, 8, 8);
                    g2.setColor(accent); g2.fillRoundRect(6, 2, 3, getHeight()-4, 3, 3);
                }
                g2.setColor(getModel().isRollover() ? TEXT_PRIMARY : TEXT_SECONDARY); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 18, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.putClientProperty("card", card);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setMaximumSize(new Dimension(205, 38)); btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addCardLabel(JPanel p, String text) {
        JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(TEXT_MUTED); l.setAlignmentX(LEFT_ALIGNMENT); p.add(l);
    }

    private JPanel makeDivider() {
        JPanel d = new JPanel(); d.setBackground(BORDER_COLOR);
        d.setMaximumSize(new Dimension(205, 1)); d.setPreferredSize(new Dimension(205, 1)); return d;
    }

    private JLabel makeSectionLabel(String text) {
        JLabel l = new JLabel("  " + text); l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(TEXT_MUTED); l.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
        l.setMaximumSize(new Dimension(205, 28)); return l;
    }

    private JLabel makeStatLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(FONT_SMALL); l.setForeground(TEXT_SECONDARY);
        l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        l.setBackground(BG_CARD); l.setOpaque(true); return l;
    }

    private void styleTable(JTable table) {
        table.setBackground(BG_DARK); table.setForeground(TEXT_PRIMARY); table.setFont(FONT_INPUT);
        table.setRowHeight(30); table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ACCENT_BLUE); table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true); table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(true); table.setFillsViewportHeight(true);
        JTableHeader h = table.getTableHeader();
        h.setBackground(BG_CARD); h.setForeground(TEXT_SECONDARY);
        h.setFont(new Font("SansSerif", Font.BOLD, 11));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? ACCENT_BLUE : (row % 2 == 0 ? BG_DARK : BG_TABLE_ALT));
                setForeground(sel ? Color.WHITE : TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }
}