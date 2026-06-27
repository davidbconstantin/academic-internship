/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 * https://docs.oracle.com/javase/8/docs/api/javax/swing/SwingWorker.html
 * https://docs.oracle.com/javase/tutorial/uiswing/concurrency/simple.html
 */

import com.security.academicinternshipproject.SentimentAnalyser;
import com.security.academicinternshipproject.TestForm;
import com.security.academicinternshipproject.TestPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.SwingWorker;
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
public class AsynchronousGUITest {
    
    TestForm mainMenu;
    
    public AsynchronousGUITest() {
        // initialise GUI
        mainMenu = new TestForm();

        mainMenu.setTitle("Academic Internship Project");
        mainMenu.setMinimumSize(new Dimension(1020, 720));
        mainMenu.setSize(1020, 720);
        mainMenu.setLocationRelativeTo(null);      
        
        mainMenu.displayPanel("Test");
        mainMenu.setVisible(true);
        
    }
    
    @BeforeAll
    public static void setUpClass() {
        AsynchronousGUITest test = new AsynchronousGUITest();
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void hello() throws InterruptedException, ExecutionException {
        System.setProperty("java.awt.headless", "false");
        SwingWorker<List<Integer>, Integer> worker = new SwingWorker<>() {
            
        List<Integer> scores;
        int iterations = 30;
        int loopCounter = 0;
            
        @Override
        public List<Integer> doInBackground() {
            scores = new ArrayList<>();
            SentimentAnalyser analyser = new SentimentAnalyser("djl://ai.djl.pytorch/distilbert");
            try {
                while (loopCounter < iterations) {
                    analyser.predict("This is great!");
                    loopCounter++;
                    publish(loopCounter);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
            scores.add((int)(analyser.getClassifications().get("Positive").getProbability() * 100));
            return scores;
        }
        
        @Override
        public void process(List<Integer> values) {
            for (Component comp: mainMenu.getTestPanel().getComponents()) {
                System.out.println(comp.getName());
                if (comp.getName().equals("testPanel") && comp instanceof TestPanel) {
                    TestPanel myPanel = assertInstanceOf(TestPanel.class, comp);
                    myPanel.setLabelText("Operations Run: " + values.get(values.size() - 1) + "/" + iterations);
                    float interimValue = values.get(values.size() - 1);
                    System.out.println(interimValue);
                    System.out.println(interimValue / iterations);
                    float finalValue = (int) (interimValue / iterations * 100);
                    System.out.println(finalValue);
                    myPanel.setProgressBarValue((int)finalValue);
                }
            }
        }
        
        @Override
        public void done() {
            System.out.println("Done!" + scores.get(0));
        }
    };
        worker.execute();
        List<Integer> results = worker.get();
        assertNotNull(results);
    }
}
