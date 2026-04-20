/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.security.academicinternshipproject.CrawlerConfigJP;
import com.security.academicinternshipproject.CrawlerScriptingJP;
import com.security.academicinternshipproject.DatabaseSettingsJP;
import com.security.academicinternshipproject.HomeLandingJP;
import com.security.academicinternshipproject.MainMenuForm;
import com.security.academicinternshipproject.MainMenuJP;
import com.security.academicinternshipproject.SQLCredentialsJP;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
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
public class CardLayoutTest {
    
    public CardLayoutTest() {
        // initialise GUI
        MainMenuForm mainMenu = new MainMenuForm();

        // initialise panels
        HomeLandingJP homeLandingJP = new HomeLandingJP(mainMenu);   // landing page 
        CrawlerConfigJP crawlerConfigPanel = new CrawlerConfigJP(mainMenu);
        MainMenuJP mainMenuPanel = new MainMenuJP(mainMenu);
        CrawlerScriptingJP crawlerScriptingPanel = new CrawlerScriptingJP(mainMenu);
        DatabaseSettingsJP databaseSettingsPanel = new DatabaseSettingsJP(mainMenu);
        SQLCredentialsJP sqlCredentialsPanel = new SQLCredentialsJP(mainMenu);

        Container contentPane = mainMenu.getContentPane();
        contentPane.setLayout(new CardLayout());
        contentPane.add(homeLandingJP, "Landing");
        contentPane.add(mainMenuPanel, "Main Menu");
        contentPane.add(crawlerConfigPanel, "Crawler Configuration");
        contentPane.add(crawlerScriptingPanel, "Crawler Scripting");
        contentPane.add(databaseSettingsPanel, "Database Settings");
        contentPane.add(sqlCredentialsPanel, "SQL Credentials");

        mainMenu.setTitle("Academic Internship Project");
        mainMenu.setMinimumSize(new Dimension(1020, 720));
        mainMenu.setSize(1020, 720);
        mainMenu.setLocationRelativeTo(null);      

        mainMenu.setVisible(true);
        System.out.println("Running");
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void hello() {}
}
