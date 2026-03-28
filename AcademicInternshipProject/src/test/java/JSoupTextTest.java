/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author rokom
 */
public class JSoupTextTest {
    private ArrayList<String> paragraphs;
    private ArrayList<String> markup;
    
    public JSoupTextTest() {
        paragraphs = new ArrayList<>();
        markup = new ArrayList<>();
        markup.add("<p>Hello world</p>");
    }
    

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void getTextFromDocument() {
    paragraphs.clear();
    System.out.println("Extracting text...");
    for (String textBlock: markup) {
        Document doc = Jsoup.parse(textBlock);
        System.out.println(doc.text());
        paragraphs.add(doc.text());
    }
    assertEquals("Hello world", paragraphs.get(0));
    }
}
