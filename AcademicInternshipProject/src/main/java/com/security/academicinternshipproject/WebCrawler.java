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
    private ArrayList<String> searchResults;
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

    public ArrayList<String> getHtmlResponses() {
        return searchResults;
    }

    public void setHtmlResponses(ArrayList<String> htmlResponses) {
        this.searchResults = htmlResponses;
    }
    
    public void addHtmlResponse(String response) {
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
        System.out.println("Adding command " + command);
    }

}
