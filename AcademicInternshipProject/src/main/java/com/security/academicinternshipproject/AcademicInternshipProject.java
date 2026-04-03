/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.security.academicinternshipproject;

import java.awt.Dimension;

public class AcademicInternshipProject {

    public static void main(String[] args) {

        // initialise GUI
        MainMenuForm mainMenu = new MainMenuForm();
        GUIManager guiManager = new GUIManager(mainMenu);

        // initialise panels
        HomeLandingJP homeLandingJP = new HomeLandingJP();   // landing page 
        CrawlerConfigJP crawlerConfigPanel = new CrawlerConfigJP();
        MainMenuJP mainMenuPanel = new MainMenuJP();
        CrawlerScriptingJP crawlerScriptingPanel = new CrawlerScriptingJP();
        DatabaseSettingsJP databaseSettingsPanel = new DatabaseSettingsJP();

        homeLandingJP.setGuiManager(guiManager);
        crawlerConfigPanel.setGuiManager(guiManager);
        mainMenuPanel.setGuiManager(guiManager);
        crawlerScriptingPanel.setGuiManager(guiManager);
        databaseSettingsPanel.setGuiManager(guiManager);

        guiManager.addPanel(homeLandingJP);   // add landinig page first
        guiManager.addPanel(crawlerConfigPanel);
        guiManager.addPanel(mainMenuPanel);
        guiManager.addPanel(crawlerScriptingPanel);

        guiManager.setCurrentPanel(homeLandingJP);

        mainMenu.setTitle("Academic Internship Project");
        mainMenu.setMinimumSize(new Dimension(1020, 720));
        mainMenu.setSize(1020, 720);
        mainMenu.setLocationRelativeTo(null);      
        
        guiManager.addPanel(crawlerConfigPanel);
        guiManager.addPanel(mainMenuPanel);
        guiManager.addPanel(crawlerScriptingPanel);
        guiManager.addPanel(databaseSettingsPanel);
        guiManager.addPanel(homeLandingJP);
        
        guiManager.setCurrentPanel(homeLandingJP);
        
        mainMenu.setGuiManager(guiManager);
        mainMenu.setVisible(true);
    }
}
