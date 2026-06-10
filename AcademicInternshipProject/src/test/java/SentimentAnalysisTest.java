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
        SentimentAnalyser analyser = new SentimentAnalyser("djl://ai.djl.pytorch/distilbert");
        try {
            analyser.predict("The final report by the Commission of Investigation into how abuse allegations against basketball coach Bill Kenneally were handled is due to be published today.\n" +
"\n" +
"The convicted paedophile is currently serving almost 19 years in prison for the serious sexual abuse of 15 young boys between 1979 and 1990, following two previous criminal prosecutions.");
            System.out.println("Input: " + analyser.getInput());
            System.out.println(analyser.getClassifications());
            System.out.println(analyser.getClassifications().get("Positive").getProbability());
            System.out.println(analyser.getClassifications().get("Negative").getProbability());
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
