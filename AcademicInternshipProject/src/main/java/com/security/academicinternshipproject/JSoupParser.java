/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject;

import java.util.ArrayList;
import java.util.Arrays;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author rokom
 */
public class JSoupParser {
    
    private ArrayList<String> paragraphs = new ArrayList<>();
    private Elements elements;
    
    public JSoupParser() {
        
    }

    public ArrayList<String> searchDocument(String htmlMarkup, String searchTerm) {
        paragraphs.clear();
        Document doc = Jsoup.parse(htmlMarkup);
        elements = doc.select(searchTerm);
        for (Element element: elements) {
            paragraphs.add(element.toString());
        }
        System.out.println("Searching: " + htmlMarkup);
        System.out.println("Search results: " + paragraphs.toString());
        return paragraphs;
    }
    
    public ArrayList<String> getTextFromDocument(String htmlMarkup) {
        paragraphs.clear();
        Document doc = Jsoup.parse(htmlMarkup);
        elements = doc.getAllElements();
        for (Element element: elements) {
            paragraphs.add(element.text());
        }
        return paragraphs;
    }
   
    public ArrayList<String> getAttributesFromDocument(String htmlMarkup, String searchTerm) {
        paragraphs.clear();
        Document doc = Jsoup.parse(htmlMarkup);
        elements = doc.getAllElements();
        for (Element element: elements) {
            if (element.hasAttr(searchTerm))
                paragraphs.add(element.attr(searchTerm));
        }
        for (String result: paragraphs) {
            System.out.println("Result: " + result);
        }
        return paragraphs;
    }
    
    public ArrayList<String> getParagraphs() {
        return paragraphs;
    }
}
