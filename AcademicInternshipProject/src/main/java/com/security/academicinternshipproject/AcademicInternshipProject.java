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
        SQLCredentialsJP sqlCredentialsPanel = new SQLCredentialsJP();

        homeLandingJP.setGuiManager(guiManager);
        crawlerConfigPanel.setGuiManager(guiManager);
        mainMenuPanel.setGuiManager(guiManager);
        crawlerScriptingPanel.setGuiManager(guiManager);
        databaseSettingsPanel.setGuiManager(guiManager);
        sqlCredentialsPanel.setGuiManager(guiManager);

        guiManager.addPanel(homeLandingJP);   // add landinig page first
        guiManager.addPanel(crawlerConfigPanel);
        guiManager.addPanel(crawlerScriptingPanel);
        guiManager.addPanel(databaseSettingsPanel);
        guiManager.addPanel(sqlCredentialsPanel);
        guiManager.addPanel(mainMenuPanel);

        mainMenu.setTitle("Academic Internship Project");
        mainMenu.setMinimumSize(new Dimension(1020, 720));
        mainMenu.setSize(1020, 720);
        mainMenu.setLocationRelativeTo(null);      
        
        mainMenu.setGuiManager(guiManager);
        guiManager.setCurrentPanel(homeLandingJP);
        mainMenu.setVisible(true);
    }
}
