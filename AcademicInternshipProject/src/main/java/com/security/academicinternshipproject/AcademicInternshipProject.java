/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/card.html - GUI design
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/group.html
 */

package com.security.academicinternshipproject;

import java.awt.Dimension;

public class AcademicInternshipProject {

    public static void main(String[] args) {

        // initialise GUI
        MainMenuForm mainMenu = new MainMenuForm();

        mainMenu.setTitle("Nascrawler");
        mainMenu.setMinimumSize(new Dimension(1020, 720));
        mainMenu.setSize(1020, 720);
        mainMenu.setLocationRelativeTo(null);      
        
        mainMenu.displayPanel("Landing");
        mainMenu.setVisible(true);
    }
}
