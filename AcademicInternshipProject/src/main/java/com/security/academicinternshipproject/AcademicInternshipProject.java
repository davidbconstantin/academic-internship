/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.security.academicinternshipproject;

import javax.swing.JPanel;

/**
 *
 * @author rokom
 */
public class AcademicInternshipProject {

    public static void main(String[] args) {
        // initialise GUI
        MainMenuForm mainMenu = new MainMenuForm();
        GUIManager guiManager = new GUIManager(mainMenu);
        // initialise panels
        CrawlerConfigJP crawlerConfigPanel = new CrawlerConfigJP();
        MainMenuJP mainMenuPanel = new MainMenuJP();
        CrawlerScriptingJP crawlerScriptingPanel = new CrawlerScriptingJP();

        crawlerConfigPanel.setGuiManager(guiManager);
        mainMenuPanel.setGuiManager(guiManager);
        crawlerScriptingPanel.setGuiManager(guiManager);
        
        guiManager.addPanel(crawlerConfigPanel);
        guiManager.addPanel(mainMenuPanel);
        guiManager.addPanel(crawlerScriptingPanel);
        
        guiManager.setCurrentPanel(mainMenuPanel);
        
        mainMenu.setGuiManager(guiManager);
        mainMenu.setVisible(true);
    }
}
