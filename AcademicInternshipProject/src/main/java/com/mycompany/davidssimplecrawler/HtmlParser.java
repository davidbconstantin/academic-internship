/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.davidssimplecrawler;

/**
 *
 * @author david
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
import java.util.ArrayList;
 
public class HtmlParser {
 
    /**
     * Searches the HTML for elements matching a CSS selector
     * example  selector "p" returns all paragraph elements
     * example  selector "a" returns all link elements
     * Returns each matching element as a String in the lis
     */
    public ArrayList<String> searchByCssSelector(String html, String cssSelector) {
        ArrayList<String> results = new ArrayList<>();
 
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(cssSelector);
 
        for (Element element : elements) {
            results.add(element.toString()); // full HTML of each element
        }
 
        return results;
    }
 
    /**
     * Extracts all visible text from the page — strips all HTML tags.

     */
    public String getAllText(String html) {
        Document doc = Jsoup.parse(html);
        return doc.text();
    }
 
    /**
     * Pulls every href attribute from the page — i.e. all hyperlinks.
     * Returns a list of URL strings found on the page -
     */
    public ArrayList<String> getAllLinks(String html) {
        ArrayList<String> links = new ArrayList<>();
 
        Document doc = Jsoup.parse(html);
        Elements anchorTags = doc.select("a[href]"); // all <a> tags that have an href
 
        for (Element anchor : anchorTags) {
            String href = anchor.attr("abs:href"); // abs: gives full absolute URL
            if (!href.isEmpty()) {
                links.add(href);
            }
        }
 
        return links;
    }
}
 