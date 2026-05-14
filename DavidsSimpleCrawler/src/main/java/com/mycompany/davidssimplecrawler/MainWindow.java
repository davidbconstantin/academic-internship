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
import java.awt.*;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    //basic window setup for main window

    //vars
    private JTextField urlField;
    private JTextField delayField;
    private JTextField selectorField;
    private JComboBox<String> parseModeBox;
    private JTextArea statusArea;

    // store the last fetched html so we can re-parse without re-fetching
    private String lastHtml = "";

    public MainWindow() {
        setTitle("Web Crawler Application");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //build the ui
        buildUI();
    }

    private void buildUI() {

        // main panel with a mauve colour 90,90,255
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(90, 90, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // title label at the top
        JLabel titleLabel = new JLabel("Web Crawler Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // status area in the middle
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setText("Enter a URL, choose a parse mode, then click 'Fetch & Parse'.");
        JScrollPane scrollPane = new JScrollPane(statusArea);
        //centered
        panel.add(scrollPane, BorderLayout.CENTER);

        // button panel at the bottom
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // URL label + field
        JLabel urlLabel = new JLabel("URL:");
        urlLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        inputPanel.add(urlLabel, gbc);

        urlField = new JTextField("https://example.com", 28);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        inputPanel.add(urlField, gbc);

        // Delay label + field
        JLabel delayLabel = new JLabel("Delay in (s):");
        delayLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        inputPanel.add(delayLabel, gbc);

        delayField = new JTextField("1", 5);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0;
        inputPanel.add(delayField, gbc);

        // parse mode dropdown - text, links or css selector
        JLabel parseModeLabel = new JLabel("Parse Mode:");
        parseModeLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        inputPanel.add(parseModeLabel, gbc);

        parseModeBox = new JComboBox<>(new String[]{"Text", "Links", "CSS Selector"});
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0;
        inputPanel.add(parseModeBox, gbc);

        // css selector field - only used when CSS Selector mode is picked
        JLabel selectorLabel = new JLabel("CSS Selector:");
        selectorLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        inputPanel.add(selectorLabel, gbc);

        selectorField = new JTextField("p", 10);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0;
        inputPanel.add(selectorField, gbc);

        // buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);

        // fetch and parse button
        JButton fetchParseBtn = new JButton("Fetch & Parse");
        fetchParseBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        fetchParseBtn.addActionListener(e -> onFetchAndParseClicked());
        btnRow.add(fetchParseBtn);

        // re-parse button - parses the already fetched html again with new settings
        JButton reparseBtn = new JButton("Re-Parse Last HTML");
        reparseBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        reparseBtn.addActionListener(e -> parseAndDisplay(lastHtml));
        btnRow.add(reparseBtn);

        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0;
        inputPanel.add(btnRow, gbc);

        panel.add(inputPanel, BorderLayout.SOUTH);
        add(panel);
    }

    /**
     * Called when the user clicks "Fetch & Parse".
     * Reads the URL field, applies the crawl delay, then fetches and parses.
     */
    private void onFetchAndParseClicked() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            statusArea.setText("Please enter a URL first.");
            return;
        }

        // apply crawl delay before fetching
        int delay = 0;
        try {
            delay = Integer.parseInt(delayField.getText().trim());
        } catch (NumberFormatException ex) {
            statusArea.setText("Delay must be a number. Defaulting to 0.");
        }

        statusArea.setText("Fetching: " + url + " (delay: " + delay + "s)...\n");

        // do the delay + fetch on a background thread so the interface doesn't freeze
        final int finalDelay = delay;
        new Thread(() -> {
            try {
                if (finalDelay > 0) {
                    Thread.sleep(finalDelay * 1000L);
                }

                PageFetcher fetcher = new PageFetcher("Mozilla/5.0 (compatible; WebCrawlerApp/1.0)");
                String html = fetcher.fetchPage(url);

                //save html for re-parsing later
                lastHtml = html;

                // update the UI back on the Swing thread
                SwingUtilities.invokeLater(() -> parseAndDisplay(html));

            } catch (InterruptedException ex) {
                SwingUtilities.invokeLater(() -> statusArea.setText("Interrupted."));
            }
        }).start();
    }

    /**
     * Parses html based on the selected mode and shows results in status area.
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
            String text = parser.getAllText(html);
            output.append(text);

        // links mode - pulls every url from the page
        } else if ("Links".equals(mode)) {
            ArrayList<String> links = parser.getAllLinks(html);
            output.append("Found ").append(links.size()).append(" links:\n\n");
            for (String link : links) {
                output.append(link).append("\n");
            }

        // css selector mode - finds elements matching the selector typed in
        } else if ("CSS Selector".equals(mode)) {
            String selector = selectorField.getText().trim();
            if (selector.isEmpty()) {
                statusArea.setText("Please enter a CSS selector (e.g. 'p', 'h1', 'a').");
                return;
            }
            ArrayList<String> elements = parser.searchByCssSelector(html, selector);
            output.append("Found ").append(elements.size()).append(" element(s) matching '")
                  .append(selector).append("':\n\n");
            for (String el : elements) {
                output.append(el).append("\n---\n");
            }
        }

        //display results
        statusArea.setText(output.toString());
    }
}