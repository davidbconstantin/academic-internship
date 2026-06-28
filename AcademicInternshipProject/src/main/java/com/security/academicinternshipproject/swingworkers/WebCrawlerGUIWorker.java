/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject.swingworkers;

import com.security.academicinternshipproject.MainMenuForm;
import com.security.academicinternshipproject.MySQLConnector;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author rokom
 * This worker is designed to handle status updates while the web crawler is in operation
 */
public class WebCrawlerGUIWorker extends SwingWorker<Void, Void>{
 
    private MainMenuForm mainMenuForm;
    private MySQLConnector mysql;
    private javax.swing.JTextArea textArea;
    private javax.swing.JComboBox crawlersCB;
    private javax.swing.JComboBox responsesCB;

    public WebCrawlerGUIWorker(MainMenuForm mainMenuForm, MySQLConnector mysql, javax.swing.JTextArea textArea,
            javax.swing.JComboBox crawlersCB, javax.swing.JComboBox responsesCB) {
        this.mainMenuForm = mainMenuForm;
        this.mysql = mysql;
        this.textArea = textArea;
        this.crawlersCB = crawlersCB;
        this.responsesCB = responsesCB;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    protected void process(List<Void> value) {
        
    }
    
    @Override
    protected void done() {
        
    }
    
}
