/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.security.academicinternshipproject.SentimentAnalyser;
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
public class SentimentAnalysisTest {
    
    public SentimentAnalysisTest() {
    }
    
    @Test
    public void predict() {
        SentimentAnalyser analyser = new SentimentAnalyser();
        try {
            analyser.predict("This is bad stuff!");
            System.out.println("Input: " + analyser.getInput());
            System.out.println(analyser.getClassifications().best());
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
