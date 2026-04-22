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
public class SearchResult {
    private static int id = 0;
    private ArrayList<String> results;
    private int commandNo;
    
    public SearchResult(ArrayList<String> results, int commandNo) {
        id++;
        this.results = new ArrayList<>();
        this.results.addAll(results);
        this.commandNo = commandNo;
    }
    
    public SearchResult(String result) {
        id++;
        this.results = new ArrayList<>();
        this.results.add(result);
    }

    public int getId() {
        return id;
    }
    
    public int getCommandNo() {
        return commandNo;
    }

    public ArrayList<String> getResults() {
        return results;
    }

    public void setResults(ArrayList<String> results) {
        this.results = results;
    }
    
}
