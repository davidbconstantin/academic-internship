/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.davidssimplecrawler;

/**
 *
 * @author david
 */
public class WebCrawlerApp {
 
    public static void main(String[] args) {
        
        //launch main window
        javax.swing.SwingUtilities.invokeLater(() -> {
            
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
 