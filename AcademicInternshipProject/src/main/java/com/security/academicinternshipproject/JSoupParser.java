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
    
    private ArrayList<String> linkStrings = new ArrayList<>();
    private ArrayList<String> paragraphs = new ArrayList<>();
    
    public JSoupParser() {
        
    }
    
//    public ArrayList<String> getLinks(String htmlMarkup) {
//        Document doc = Jsoup.parse(htmlMarkup);
//        Elements links = doc.select("a.articleContainer[href^=/farming-news]").empty();
//        for (Element element: links) {
//            linkStrings.add(element.attr("href") + "\n");
//        }
//        return linkStrings;
//    }
    
    public ArrayList<String> getLinks(String htmlMarkup, String elementClass, String attribute) {
        Document doc = Jsoup.parse(htmlMarkup);
        Elements links = doc.select("a." + elementClass + "[" + attribute + "]");
        for (Element element: links) {
            linkStrings.add(element.attr("href") + "\n");
        }
        return linkStrings;
    }
    
    public ArrayList<String> getTextFromParagraphs(String htmlMarkup, String attribute) {
        Document doc = Jsoup.parse(htmlMarkup);
        Elements tags = doc.select("p[" + attribute + "]");
        for (Element element: tags) {
            paragraphs.add(element.text());
        }
        return paragraphs;
    }
    
    public ArrayList<String> searchDocument(String htmlMarkup, String searchTerm) {
        Document doc = Jsoup.parse(htmlMarkup);
        Elements tags = doc.select(searchTerm);
        for (Element element: tags) {
            paragraphs.add(element.text());
        }
        return paragraphs;
    }
    
    public ArrayList<String> getLinkStrings() {
        return linkStrings;
    }
    
    public ArrayList<String> getParagraphs() {
        return paragraphs;
    }
}
