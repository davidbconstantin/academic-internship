/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject;

import java.util.ArrayList;

/**
 *
 * @author rokom
 */
public class WebCrawler implements java.io.Serializable {
    private String name, userAgent;
    private int crawlDelay;
    private ArrayList<String> urls;
    private ArrayList<SearchResult> searchResults;
    private ArrayList<String> commands;
    
    public WebCrawler(String name, String userAgent, int crawlDelay) {
        this.name = name;
        this.userAgent = userAgent;
        this.crawlDelay = crawlDelay;
        this.urls = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.searchResults = new ArrayList<>();
    }
    
    public WebCrawler(String name, String userAgent, int crawlDelay, ArrayList<String> urls, ArrayList<String> commands) {
        this.name = name;
        this.userAgent = userAgent;
        this.crawlDelay = crawlDelay;
        this.urls = new ArrayList<>();
        this.urls.addAll(urls);
        this.searchResults = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.commands.addAll(commands);
    }
    
    public WebCrawler() {
        name = "New Crawler...";
        userAgent = "";
        crawlDelay = 0;
        urls = new ArrayList<>();
        searchResults = new ArrayList<>();
        commands = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getCrawlDelay() {
        return crawlDelay;
    }

    public void setCrawlDelay(int crawlDelay) {
        this.crawlDelay = crawlDelay;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public ArrayList<SearchResult> getHtmlResponses() {
        return searchResults;
    }
    
    public void addHtmlResponse(SearchResult response) {
        searchResults.add(response);
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    public void setCommands(ArrayList<String> commands) {
        this.commands = commands;
    }
    
    public void addCommand(String command) {
        commands.add(command);
    }
    
    public void editCommand(int index, String command) {
        commands.set(index, command);
    }

    public void printInfo() {
        System.out.println("Name: " + name);
        System.out.println("User Agent: " + userAgent);
        System.out.println("Crawl Delay: " + crawlDelay);
        int counter = 0;
        for (String command: commands) {
            counter++;
            System.out.println("Command " + counter + ": " + command);
        }
    }
}
