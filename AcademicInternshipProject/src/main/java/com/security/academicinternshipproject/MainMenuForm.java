/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 * https://playwright.dev/java/docs/intro
 * https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html
 * https://huggingface.co/recobo/agriculture-bert-uncased
 * https://medium.com/@evaGachirwa/running-python-script-with-arguments-in-the-command-line-93dfa5f10eff
 * https://stackoverflow.com/questions/6505953/cardlayout-get-the-selected-cards-name
 * https://docs.oracle.com/javase/tutorial/uiswing/components/frame.html#windowevents
 */
package com.security.academicinternshipproject;

import com.security.academicinternshipproject.helpmenu.AboutForm;
import com.security.academicinternshipproject.helpmenu.UserManualForm;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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
import javax.sql.rowset.CachedRowSet;
import javax.swing.JDialog;
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
    private JPanel lastPanel;
//    private JPanel cardPanel;
    
    private List<WebCrawler> webCrawlers = new ArrayList<>();
    private WebCrawler selectedCrawler;
    private MySQLConnector sql;
    private List<String> sqlCredentials = new ArrayList<>();
    private SQLDatabaseInfo dbInfo;
    private CachedRowSet crs;
    
    // database query variables
    private SentimentAnalyser sentimentAnalyser;
    private String selectedColumnName;
    private String selectedTableName;
    private boolean analysingSQLTable = false;
    
    private boolean sqlCredentialsRequired = true;
    private boolean editMode = false;
    private char[] username;
    private char[] password;
    
    private String lastSelectedCrawlerName = null;
    
    // notification functionality is initialised below
    TaskNotifier notifier = new TaskNotifier();
    
    public MainMenuForm() {
        initComponents();
        Container contentPane = getContentPane();
        cardLayout = (CardLayout) mainPanelJP.getLayout();
        contentPane.setLayout(cardLayout);
        
        // initialise sentiment analyser
        sentimentAnalyser = new SentimentAnalyser("djl://ai.djl.pytorch/distilbert");
        
        // initialise panels
        HomeLandingJP homeLandingJP = new HomeLandingJP(this);   // landing page 
        CrawlerConfigJP crawlerConfigPanel = new CrawlerConfigJP(this);
        MainMenuJP mainMenuPanel = new MainMenuJP(this);
        CrawlerScriptingJP crawlerScriptingPanel = new CrawlerScriptingJP(this);
        DatabaseSettingsJP databaseSettingsPanel = new DatabaseSettingsJP(this);
        SQLCredentialsJP sqlCredentialsPanel = new SQLCredentialsJP(this);
        SQLDatabaseJP sqlDatabasePanel = new SQLDatabaseJP(this);
        SentimentAnalysisJP sentimentAnalysisPanel = new SentimentAnalysisJP(this);
        DatabaseQueryJP databaseQueryPanel = new DatabaseQueryJP(this);
        
        // add panels to card layout
        mainPanelJP.add(homeLandingJP, "Landing");
        homeLandingJP.setName("Landing");
        mainPanelJP.add(crawlerConfigPanel, "Crawler Configuration");
        crawlerConfigPanel.setName("Crawler Configuration");
        mainPanelJP.add(mainMenuPanel, "Main Menu");
        mainMenuPanel.setName("Main Menu");
        mainPanelJP.add(crawlerScriptingPanel, "Crawler Scripting");
        crawlerScriptingPanel.setName("Crawler Scripting");
        mainPanelJP.add(databaseSettingsPanel, "Database Settings");
        databaseSettingsPanel.setName("Database Settings");
        mainPanelJP.add(sqlCredentialsPanel, "SQL Settings");
        sqlCredentialsPanel.setName("SQL Settings");
        mainPanelJP.add(sqlDatabasePanel, "SQL Database");
        sqlDatabasePanel.setName("SQL Database");
        mainPanelJP.add(sentimentAnalysisPanel, "Sentiment Analysis");
        sentimentAnalysisPanel.setName("Sentiment Analysis");
        mainPanelJP.add(databaseQueryPanel, "Database Query");
        databaseQueryPanel.setName("Database Query");
        
        lastPanel = mainMenuPanel;  
        selectedCrawler = new WebCrawler();
        
        // load SQL database settings
        loadSQLCredentials();
    }
    
    public void displayPanel(String panelName) {
        // get the previous panel's name while ignoring pop-up menus
        for (Component comp: mainPanelJP.getComponents()) {
            if (comp.isVisible()) {
                if (!comp.getName().equals("SQL Settings")) 
                    lastPanel = (javax.swing.JPanel)comp;
            }
        }
        cardLayout.show(mainPanelJP, panelName);
        for (Component comp: mainPanelJP.getComponents()) {
            // hide menu bar if outside main menu
            if (comp.isVisible() && comp.getName().equals("Main Menu")) {
                mainMB.setVisible(true);
                System.out.println("Rendering menu bar");
                break;
            }
            else
                mainMB.setVisible(false);
        }
        //System.out.println("Previous panel: " + lastPanel.getName());
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
    
    public SentimentAnalyser getSentimentAnalyser() {
        return sentimentAnalyser;
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
    
    public String getLastSelectedCrawlerName() {
        return lastSelectedCrawlerName;
    }
    
    public void setLastSelectedCrawlerName(String name) {
        lastSelectedCrawlerName = name;
    }
    
    public boolean isAnalysingSQLTable() {
        return analysingSQLTable;
    }
    
    public void setIsAnalysingTable(boolean value) {
        analysingSQLTable = value;
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
    
    public String getPreviousPanelName() {
        return lastPanel.getName();
    }
    
    public SQLDatabaseInfo getDatabaseInfo() {
        return dbInfo;
    }
    
    public CachedRowSet getCrs() {
        return crs;
    }
    
    public void setSelectedColumnName(String selectedColumn) {
        selectedColumnName = selectedColumn;
    }
    
    public String getSelectedColumnName() {
        return selectedColumnName;
    }
    
    public void setSelectedTableName(String selectedTable) {
        selectedTableName = selectedTable;
    }
    
    public String getSelectedTableName() {
        return selectedTableName;
    }
    
    public TaskNotifier getTaskNotifier() {
        return notifier;
    }
    
    public void populateComboBoxWithHTMLResponses(javax.swing.JComboBox cbox, WebCrawler crawler) {
        // add HTML responses to combo box
        int counter = -1;
        cbox.setEnabled(true);
        cbox.removeAllItems();
        for (SearchResult result: crawler.getHtmlResponses()) {
            counter++;
            cbox.addItem("Response " + counter);
        }
    }
    
    // requires the database admin's credentials
    public boolean loadDatabaseSchema() {
        if (!sqlCredentialsRequired) {
        try {
            sql = new MySQLConnector(sqlCredentials.get(0), Integer.parseInt(sqlCredentials.get(1)), sqlCredentials.get(2),
            String.valueOf(username),
            String.valueOf(password));
            sql.fetchTableSchemas(sqlCredentials.get(0), Integer.parseInt(sqlCredentials.get(1)), sqlCredentials.get(2), String.valueOf(username), String.valueOf(password));
            dbInfo = sql.getCurrentDbAsInfo();
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
            }
        } else {
            displayPanel("SQL Settings");
            return false;
        }
    }
    
    public boolean returnTableData(String tableName) {
        if (!sqlCredentialsRequired) {
            try {
                 sql = new MySQLConnector(sqlCredentials.get(0), Integer.parseInt(sqlCredentials.get(1)), sqlCredentials.get(2),
                 String.valueOf(username),
                 String.valueOf(password));
                 sql.fetchTableSchemas(sqlCredentials.get(0), Integer.parseInt(sqlCredentials.get(1)), sqlCredentials.get(2), String.valueOf(username), String.valueOf(password));
                 crs = sql.executeQuery(sql.composeUrl(sqlCredentials.get(0), Integer.parseInt(sqlCredentials.get(1)), sqlCredentials.get(2)), String.valueOf(username), String.valueOf(password), "SELECT * FROM " + tableName);
                 return true;
             } catch (Exception ex) {
                 System.out.println(ex);
                 return false;
                 }
             } else {
                 displayPanel("SQL Settings");
                 return false;
             }
    }
    
     public boolean returnColumnData(String tableName, String columnName) {
        if (!sqlCredentialsRequired) {
            try {
                 sql = new MySQLConnector(sqlCredentials.get(0), Integer.parseInt(sqlCredentials.get(1)), sqlCredentials.get(2),
                 String.valueOf(username),
                 String.valueOf(password));
                 sql.fetchTableSchemas(sqlCredentials.get(0), Integer.parseInt(sqlCredentials.get(1)), sqlCredentials.get(2), String.valueOf(username), String.valueOf(password));
                 crs = sql.executeQuery(sql.composeUrl(sqlCredentials.get(0), Integer.parseInt(sqlCredentials.get(1)), sqlCredentials.get(2)), String.valueOf(username), String.valueOf(password), "SELECT " + columnName + " FROM " + tableName);
                 return true;
             } catch (Exception ex) {
                 System.out.println(ex);
                 return false;
                 }
             } else {
                 displayPanel("SQL Settings");
                 return false;
             }
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
        mainMB = new javax.swing.JMenuBar();
        actionsMU = new javax.swing.JMenu();
        sAnalysisMI = new javax.swing.JMenuItem();
        helpMU = new javax.swing.JMenu();
        aboutMI = new javax.swing.JMenuItem();
        userManualMI = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(java.awt.Color.gray);
        setName("mainMenuFrame"); // NOI18N

        mainPanelJP.setBackground(new java.awt.Color(51, 51, 255));
        mainPanelJP.setName("mainPanelJP"); // NOI18N
        mainPanelJP.setLayout(new java.awt.CardLayout());

        actionsMU.setText("Actions");

        sAnalysisMI.setText("Sentiment Analysis");
        sAnalysisMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sAnalysisMIActionPerformed(evt);
            }
        });
        actionsMU.add(sAnalysisMI);

        mainMB.add(actionsMU);

        helpMU.setText("Help");

        aboutMI.setText("About");
        aboutMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMIActionPerformed(evt);
            }
        });
        helpMU.add(aboutMI);

        userManualMI.setText("User Manual");
        userManualMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userManualMIActionPerformed(evt);
            }
        });
        helpMU.add(userManualMI);

        mainMB.add(helpMU);

        setJMenuBar(mainMB);

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

    private void sAnalysisMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sAnalysisMIActionPerformed
        // TODO add your handling code here:
        displayPanel("Sentiment Analysis");
    }//GEN-LAST:event_sAnalysisMIActionPerformed

    private void aboutMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMIActionPerformed
        // TODO add your handling code here:
        AboutForm aboutForm = new AboutForm();
        aboutForm.setLocationRelativeTo(this);
        aboutForm.setTitle("About");
        aboutForm.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        aboutForm.setVisible(true);
    }//GEN-LAST:event_aboutMIActionPerformed

    private void userManualMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userManualMIActionPerformed
        // TODO add your handling code here:
        UserManualForm userManualForm = new UserManualForm();
        userManualForm.setLocationRelativeTo(this);
        userManualForm.setTitle("Help");
        userManualForm.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        userManualForm.setVisible(true);
    }//GEN-LAST:event_userManualMIActionPerformed

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
    private javax.swing.JMenuItem aboutMI;
    private javax.swing.JMenu actionsMU;
    private javax.swing.JMenu helpMU;
    private javax.swing.JMenuBar mainMB;
    private javax.swing.JPanel mainPanelJP;
    private javax.swing.JMenuItem sAnalysisMI;
    private javax.swing.JMenuItem userManualMI;
    // End of variables declaration//GEN-END:variables
}
