/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/card.html - GUI design
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/group.html
 * https://medium.com/@keeptoo/spicing-up-your-java-swingui-using-custom-look-and-feel-113501dd5920
 */

package com.security.academicinternshipproject;

import java.awt.Dimension;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class AcademicInternshipProject {

    public static void main(String[] args) {

        // initialise GUI
        MainMenuForm mainMenu = new MainMenuForm();

        mainMenu.setTitle("Nascrawler");
        mainMenu.setMinimumSize(new Dimension(1020, 720));
        mainMenu.setSize(1020, 720);
        mainMenu.setLocationRelativeTo(null);      
        
        mainMenu.displayPanel("Landing");
        
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
            System.out.println(ex);
        }
        
        mainMenu.setVisible(true);
    }
}
