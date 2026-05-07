/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 * https://playwright.dev/java/docs/intro
 * https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html
 * https://huggingface.co/recobo/agriculture-bert-uncased
 * https://medium.com/@evaGachirwa/running-python-script-with-arguments-in-the-command-line-93dfa5f10eff
 */
package com.security.academicinternshipproject;

import java.awt.CardLayout;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author rokom
 */
public class MainMenuForm extends javax.swing.JFrame {

    /**
     * Creates new form MainMenuForm
     */
    private CardLayout cardLayout;
    private String lastPanel = "";
//    private JPanel cardPanel;
    
    private List<WebCrawler> webCrawlers = new ArrayList<>();
    private WebCrawler selectedCrawler;
    private MySQLConnector sql;
    private List<String> sqlCredentials = new ArrayList<>();
    
    private boolean sqlCredentialsRequired = false;
    private boolean editMode = false;
    private char[] username;
    private char[] password;
    
    public MainMenuForm() {
        initComponents();
        Container contentPane = getContentPane();
        cardLayout = (CardLayout) mainPanelJP.getLayout();
        contentPane.setLayout(cardLayout);
        
        // initialise panels
        HomeLandingJP homeLandingJP = new HomeLandingJP(this);   // landing page 
        CrawlerConfigJP crawlerConfigPanel = new CrawlerConfigJP(this);
        MainMenuJP mainMenuPanel = new MainMenuJP(this);
        CrawlerScriptingJP crawlerScriptingPanel = new CrawlerScriptingJP(this);
        DatabaseSettingsJP databaseSettingsPanel = new DatabaseSettingsJP(this);
        SQLCredentialsJP sqlCredentialsPanel = new SQLCredentialsJP(this);
        SQLDatabaseJP sqlDatabasePanel = new SQLDatabaseJP(this);
        
        // add panels to card layout
        mainPanelJP.add(homeLandingJP, "Landing");
        mainPanelJP.add(crawlerConfigPanel, "Crawler Configuration");
        mainPanelJP.add(mainMenuPanel, "Main Menu");
        mainPanelJP.add(crawlerScriptingPanel, "Crawler Scripting");
        mainPanelJP.add(databaseSettingsPanel, "Database Settings");
        mainPanelJP.add(sqlCredentialsPanel, "SQL Settings");
        mainPanelJP.add(sqlDatabasePanel, "SQL Database");
        
        selectedCrawler = new WebCrawler();
        
        // load SQL database settings
        loadSQLCredentials();
    }
    
    public void displayPanel(String panelName) {
        cardLayout.show(mainPanelJP, panelName);
    }

    public List<WebCrawler> getWebCrawlers() {
        return webCrawlers;
    }

    public WebCrawler getSelectedCrawler() {
        return selectedCrawler;
    }
    
    public void setSelectedCrawler(WebCrawler crawler) {
        selectedCrawler = crawler;
    }

    public boolean isSqlCredentialsRequired() {
        return sqlCredentialsRequired;
    }
    
    public void setSQLCredentialsRequired(boolean value) {
        sqlCredentialsRequired = value;
    }

    public char[] getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }
    
    public void setUsername(char[] username) {
        this.username = username;
    }
    
    public void setPassword(char[] password) {
        this.password = password;
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    public MySQLConnector getMySQL() {
        return sql;
    }
    
    public void setEditMode(boolean value) {
        editMode = value;
    }
    
    public void eraseCredentials() {
        Arrays.fill(username, '0');
        Arrays.fill(password, '0');
        sqlCredentialsRequired = false;
    }
    
    public void writeCrawler() {
        try {
            FileOutputStream fos = new FileOutputStream("crawlers.dat");
            ObjectOutputStream ostream = new ObjectOutputStream(fos);
            if (webCrawlers.size() > 0) {
                for (WebCrawler crawler: webCrawlers) {
                    ostream.writeObject(crawler);
                }
            }
            ostream.writeObject(selectedCrawler);
            webCrawlers.clear();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
      
    public void loadCrawlers(javax.swing.JComboBox crawlersCB) {
        webCrawlers.clear();
        crawlersCB.removeAllItems();
        try {
            FileInputStream crawlers = new FileInputStream("crawlers.dat");
            try {
                ObjectInputStream ois = new ObjectInputStream(crawlers);
                while (true) {
                    try {
                        webCrawlers.add((WebCrawler)ois.readObject());
                    } catch (EOFException | ClassNotFoundException ex) {
                        //ex.printStackTrace();
                        break;
                    }
                }
                for (WebCrawler crawler: webCrawlers) {
                    if (crawler != null)
                    {
                        crawlersCB.addItem(crawler.getName());
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            //ex.printStackTrace();
        }
        crawlersCB.addItem("New Crawler...");
        if (crawlersCB.getSelectedItem().toString().equals("New Crawler...")) {
            selectedCrawler = new WebCrawler();
            editMode = false;
        } else
            editMode = true;
    }
    
    public void loadSQLCredentials() {
        File databaseSettings = new File("credentials.txt");
        sqlCredentials.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(databaseSettings));
            String line;
            while ((line = reader.readLine()) != null) {
                sqlCredentials.add(line);
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            } catch (IOException ex) {
                System.out.println(ex);
            }
    }
    
    public List<String> getSQLCredentials() {
        return sqlCredentials;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanelJP = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 255));
        setForeground(java.awt.Color.gray);
        setName("mainMenuFrame"); // NOI18N

        mainPanelJP.setBackground(new java.awt.Color(51, 51, 255));
        mainPanelJP.setName("mainPanelJP"); // NOI18N
        mainPanelJP.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanelJP, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanelJP, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainMenuForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenuForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenuForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenuForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenuForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainPanelJP;
    // End of variables declaration//GEN-END:variables
}
