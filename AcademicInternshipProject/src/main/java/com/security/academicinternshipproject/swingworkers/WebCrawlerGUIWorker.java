/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject.swingworkers;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.security.academicinternshipproject.CommandParser;
import com.security.academicinternshipproject.JSoupParser;
import com.security.academicinternshipproject.MainMenuForm;
import com.security.academicinternshipproject.MySQLConnector;
import com.security.academicinternshipproject.SQLDatabaseInfo;
import com.security.academicinternshipproject.SQLParser;
import com.security.academicinternshipproject.SearchResult;
import com.security.academicinternshipproject.WebCrawler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author rokom
 * This worker is designed to handle status updates while the web crawler is in operation
 */
public class WebCrawlerGUIWorker extends SwingWorker<String, String>{
 
    private MainMenuForm mainMenuForm;
    private MySQLConnector mysql;
    private javax.swing.JTextArea textArea;
    private javax.swing.JComboBox crawlersCB;
    private javax.swing.JComboBox responsesCB;
    private Playwright playwright;

    public WebCrawlerGUIWorker(MainMenuForm mainMenuForm, MySQLConnector mysql, javax.swing.JTextArea textArea,
            javax.swing.JComboBox crawlersCB, javax.swing.JComboBox responsesCB) {
        this.mainMenuForm = mainMenuForm;
        this.mysql = mysql;
        this.textArea = textArea;
        this.crawlersCB = crawlersCB;
        this.responsesCB = responsesCB;
    }
    
    @Override
    protected String doInBackground() throws Exception {
        textArea.setText("");
        publish("Initialising Playwright...");
        playwright = Playwright.create();
        String baseUrl = "";
        for (WebCrawler crawler: mainMenuForm.getWebCrawlers()) {
            if (crawlersCB.getSelectedItem().toString().equals(crawler.getName())) {
                textArea.setText("");
                JSoupParser parser = new JSoupParser();
                Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions()
                .setSlowMo(crawler.getCrawlDelay() * 1000)
                //.setHeadless(false)
            );
                // initialise browser
                Page page = null;
                BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent(crawler.getUserAgent())
                .setLocale("en-ie"));
                
                for (String command: crawler.getCommands()) {
                    ArrayList<String> response = new ArrayList<>();
                    ArrayList<String> results = new ArrayList<>();
                    CommandParser commandParser = new CommandParser(command);
                    publish("Executing command " + command);
                    command = commandParser.getAction();
                    // if operating on a previous command's results
                    if (commandParser.getSubjectNo() > -1) {
                        int resultsIndex = commandParser.getSubjectNo() - 1;
                        // find the final results of a particular operation by looping through the crawler in reverse direction
                        for (int i = crawler.getHtmlResponses().size() - 1; i >= 0; i--) {
                            // never evaluates true if subject number is -1
                            if (crawler.getHtmlResponses().get(i).getCommandNo() == commandParser.getSubjectNo()) {
                                resultsIndex = i;
                                break;
                            }
                        }
                        for (String result: crawler.getHtmlResponses().get(resultsIndex).getResults()) {
                            results.add(result);
                        }
                    }
                    else
                        results.add(commandParser.getObject());
                    int loopCounter = 0;
                    for (String result: results) {
                        loopCounter++;
                        if (result.length() > 32) {
                            System.out.println("Result # " + loopCounter + ": " + result.substring(0, 31));
                            publish("Result # " + loopCounter + ": " + result.substring(0, 31));
                        }
                        else {
                            System.out.println("Result # " + loopCounter + ": " + result);
                            publish("Result # " + loopCounter + ": " + result);
                        }
                        if (command.equals("Visit")) {
                            page = browser.newPage();
                            if (result.startsWith("/"))
                                result = baseUrl + result;
                            page.navigate(result);
                            if (baseUrl.equals(""))
                                baseUrl = page.url().substring(0, page.url().length() - 1);
                            response.add(page.content());
                            //crawler.addHtmlResponse(new SearchResult(response, commandParser.getCommandNo()));
                            page.close();
                        }
                        else if (command.equals("Search")) {
                            response.addAll(parser.searchDocument(crawler.getHtmlResponses().getLast().getResults(), result));
                            //crawler.addHtmlResponse(new SearchResult(response, commandParser.getCommandNo()));
                        }
                        else if (command.equals("Text")) {
                            response.add(parser.getTextFromDocument(result));
                            //crawler.addHtmlResponse(new SearchResult(response, commandParser.getCommandNo()));
                        }
                        else if (command.equals("Attribute")) {
                            response.addAll(parser.getAttributesFromDocument(result, commandParser.getObject()));
                            //crawler.addHtmlResponse(new SearchResult(response, commandParser.getCommandNo()));
                        }
                        else if (command.equals("SQL")) {
                            try {
                                List<String> credentials = mainMenuForm.getSQLCredentials();
                                String sqlStatement = commandParser.getObject();
                                // parse contents of the SQL command
                                // example command: @2i or @2is
                                // s stands for String, i means Integer (numeric value)7
                                boolean atSignPresent = false;
                                boolean numericCharacterPresent = false;
                                boolean alphabeticLetterPresent = false;
                                int numericCharacter = -1;
                                boolean isNumeric = false;
                                for (int i = 0; i < sqlStatement.length(); i++) {
                                    // sub in HTML responses 
                                    if (sqlStatement.charAt(i) == '@') {
                                        atSignPresent = true;
                                        System.out.println("At sign present.");
                                    }
                                    if (i + 1 < sqlStatement.length()) {
                                        if (sqlStatement.charAt(i + 1) == '1' || sqlStatement.charAt(i + 1) == '2' ||
                                            sqlStatement.charAt(i + 1) == '3' || sqlStatement.charAt(i + 1) == '4' ||
                                            sqlStatement.charAt(i + 1) == '5' || sqlStatement.charAt(i + 1) == '6' ||
                                            sqlStatement.charAt(i + 1) == '7' || sqlStatement.charAt(i + 1) == '8' ||
                                            sqlStatement.charAt(i + 1) == '9') {
                                        numericCharacterPresent = true;
                                        numericCharacter = Integer.parseInt(String.valueOf(sqlStatement.charAt(i + 1)));
                                        System.out.println("Numeric character: " + numericCharacter);
                                        System.out.println("Numeric character present.");
                                        }
                                    }
                                    if (i + 2 < sqlStatement.length()) {
                                        if (sqlStatement.charAt(i + 2) == 'i' || sqlStatement.charAt(i + 2) == 's' ||
                                                sqlStatement.charAt(i + 2) == 'I' || sqlStatement.charAt(i + 2) == 'S') {
                                            alphabeticLetterPresent = true;
                                            System.out.println("Alphabetic letter present.");
                                        }
                                    }
                                    if (atSignPresent && numericCharacterPresent && alphabeticLetterPresent) {
                                        String modifiedSqlStatement = "";
                                        SQLDatabaseInfo dbInfo = null;
                                        for (String valueToInsert: crawler.getHtmlResponses().get(numericCharacter - 1).getResults()) {
                                            modifiedSqlStatement = sqlStatement;
                                            valueToInsert = "'" + valueToInsert + "'";
                                            modifiedSqlStatement = modifiedSqlStatement.replaceFirst("@\\d[iIsS]", valueToInsert);
                                            // check SQL statement for errors
                                            try {
                                                mysql = new MySQLConnector(credentials.get(0), Integer.parseInt(credentials.get(1)), credentials.get(2),
                                                String.valueOf(mainMenuForm.getUsername()),
                                                String.valueOf(mainMenuForm.getPassword()));
                                                mysql.fetchTableSchemas(credentials.get(0), Integer.parseInt(credentials.get(1)), credentials.get(2), String.valueOf(mainMenuForm.getUsername()), String.valueOf(mainMenuForm.getPassword()));
                                                dbInfo = mysql.getCurrentDbAsInfo();
                                            } catch (Exception ex) {
                                                System.out.println(ex);
                                                publish("Exception: " + ex);
                                            }
                                            SQLParser sqlParser = new SQLParser(modifiedSqlStatement, dbInfo);
                                            System.out.println("SQL Statement: " + sqlStatement);
                                            if (!sqlParser.getMustSplit()) {
                                                // insert a single statement
                                                mysql = new MySQLConnector(credentials.get(0), Integer.parseInt(credentials.get(1)), credentials.get(2),
                                                String.valueOf(mainMenuForm.getUsername()),
                                                String.valueOf(mainMenuForm.getPassword()), modifiedSqlStatement);
                                            } else {
                                                // insert multiple statements
                                                for (String toInsert: sqlParser.getStringsToInsert()) {
                                                    mysql = new MySQLConnector(credentials.get(0), Integer.parseInt(credentials.get(1)), credentials.get(2),
                                                    String.valueOf(mainMenuForm.getUsername()),
                                                    String.valueOf(mainMenuForm.getPassword()), toInsert);                                                  
                                                }
                                                
                                            }
                                        }
                                        atSignPresent = numericCharacterPresent = isNumeric = false;
                                    } else
                                        atSignPresent = numericCharacterPresent = isNumeric = false;
                                }
                                // prevent credentials lingering in memory
                                mainMenuForm.eraseCredentials();
                                response.add(mysql.getResult());
                            } catch (ClassNotFoundException ex) {
                                System.out.println(ex);
                                publish("Exception: " + ex);
                            } 
                        }
                        else if (command.startsWith("Write")) {
                            File paragraphs = new File(result);
                            try {
                                FileWriter writer = new FileWriter(paragraphs);
                                for (String toWrite: crawler.getHtmlResponses().getLast().getResults()) {
                                    writer.write(toWrite + "\n");
                                }
                                writer.close();
                            } catch (IOException ex) {
                                System.out.println(ex);
                                publish("Exception: " + ex);
                            }
                        }   
                        else if (command.startsWith("Python")) {
                        // invoke Python script
                            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "python", "-u", commandParser.getObject())
                                    .inheritIO();
                            try {
                                Process process = pb.start();
                            } catch (IOException ex) {
                                Logger.getLogger(MainMenuForm.class.getName()).log(Level.SEVERE, null, ex);
                                publish("Exception: " + ex);
                            }                   
                        }
                    }
                    if (!commandParser.getAction().equals("Python") && !commandParser.getAction().equals("Write"))
                        crawler.addHtmlResponse(new SearchResult(response, commandParser.getCommandNo()));
                }
                // add HTML responses to combo box
                int counter = -1;
                responsesCB.setEnabled(true);
                responsesCB.removeAllItems();
                for (SearchResult result: crawler.getHtmlResponses()) {
                    counter++;
                    responsesCB.addItem("Response " + counter);
                }
                playwright.close();
            }
        }
        return "";
    }
    
    @Override
    protected void process(List<String> value) {
        textArea.append(value.get(value.size() - 1) + "\n");
    }
    
    @Override
    protected void done() {
        textArea.append("Finished crawling!");
    }
    
}
