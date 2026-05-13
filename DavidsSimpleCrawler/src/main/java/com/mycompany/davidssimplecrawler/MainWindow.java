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
 

public class MainWindow extends JFrame {
 
        //bBasic window setup for main window
    
    //vars
    private JTextField urlField;
    private JTextField delayField;
    private JTextArea statusArea;
 
    public MainWindow() {
        setTitle("Web Crawler Application");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
                //build the ui
        buildUI();
    }
 
    private void buildUI() {
        
        // main panel witha  mouve colour 90,90,255
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
        statusArea.setText("Enter a URL below and click 'Fetch Page'.");
        JScrollPane scrollPane = new JScrollPane(statusArea);
                //centered
        panel.add(scrollPane, BorderLayout.CENTER);
 
        // button pannel at the bottom
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
 
        urlField = new JTextField("https://example.com", 30);
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
 
        // fetch 
        JButton fetchButton = new JButton("Fetch Page");
        fetchButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        fetchButton.addActionListener(e -> onFetchClicked());
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0;
        inputPanel.add(fetchButton, gbc);
 
        panel.add(inputPanel, BorderLayout.SOUTH);
        add(panel);
    }
 
    /**
     * Called when the user clicks "Fetch Page".
     * Reads the URL field, applies the crawl delay, then fetches and shows the HTML.
     */
    private void onFetchClicked() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            statusArea.setText("Please enter a URL first.");
            return;
        }
 
        // Apply crawl delay before fetching
        int delay = 0;
        try {
            delay = Integer.parseInt(delayField.getText().trim());
        } catch (NumberFormatException ex) {
            statusArea.setText("Delay must be a number. Defaulting to 0.");
        }
 
        statusArea.setText("Fetching: " + url + " (delay: " + delay + "s)...\n");
 
        // Do the delay + fetch on a background thread so the interface doesn't freeze
        final int finalDelay = delay;
        new Thread(() -> {
            try {
                if (finalDelay > 0) {
                    Thread.sleep(finalDelay * 1000L);
                }
 
                PageFetcher fetcher = new PageFetcher("Mozilla/5.0 (compatible; WebCrawlerApp/1.0)");
                String html = fetcher.fetchPage(url);
 
                // Update the UI back on the Swing thread
                SwingUtilities.invokeLater(() -> {
                    statusArea.setText("=== HTML from " + url + " ===\n\n" + html);
                });
 
            } catch (InterruptedException ex) {
                SwingUtilities.invokeLater(() -> statusArea.setText("Interrupted."));
            }
            
        }).start();
    }
}