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
 
    public MainWindow() {
        
        //bBasic window setup for main window
        
        setTitle("Web Crawler Application");
        //default size
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centre
 
        //build the ui
        buildUI();
    }
 
    private void buildUI() {
        
        // main panel witha  mouve colour 90,90,255
        JPanel panel = new JPanel();
        panel.setBackground(new Color(90, 90, 255));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 
        // title label at the top
        JLabel titleLabel = new JLabel("Web Crawler Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);
 
        // status area in the middle
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setText("Welcome! Click 'Start Crawling' to begin.");
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(statusArea);
        //centered
        panel.add(scrollPane, BorderLayout.CENTER);
 
        // button pannel at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
 
        JButton startButton = new JButton("Start Crawling");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        startButton.addActionListener(e -> {
            
            //confirming if the button actually works   
            statusArea.setText("Button clicked!.");
            System.out.println("Start button clicked.");
        });
 
        buttonPanel.add(startButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
 
        // add panel to the frame
        add(panel);
    }
}