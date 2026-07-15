package com.security.academicinternshipproject;

import com.mycompany.davidssimplecrawler.MainWindow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class HomeLandingJP extends JPanel {

    private MainMenuForm mainMenuForm;
    private final JLabel savedCrawlersValue = new JLabel("0");
    private final JLabel statusValue = new JLabel("Ready");

    public HomeLandingJP(MainMenuForm mainMenuForm) {
        this.mainMenuForm = mainMenuForm;
        setName("homeLandingJP");
        buildUI();
        refreshStats();
    }
    
    private void buildUI() {

        setBackground(new Color(51,51,255));
        setLayout(new BorderLayout(16,16));
        setBorder(BorderFactory.createEmptyBorder(22,22,22,22));

        JLabel title = new JLabel("Nascrawler");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Landing page for navigation and quick project access");
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0,6)));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0,0,14,0);

        JPanel stats = createCardPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
        stats.setMaximumSize(new Dimension(860,180));

        JLabel statsTitle = createCardTitle("Project Status");
        statsTitle.setAlignmentX(CENTER_ALIGNMENT);

        savedCrawlersValue.setForeground(Color.WHITE);
        savedCrawlersValue.setFont(new Font("SansSerif", Font.BOLD, 18));

        statusValue.setForeground(Color.WHITE);
        statusValue.setFont(new Font("SansSerif", Font.BOLD, 18));

        stats.add(statsTitle);
        stats.add(Box.createRigidArea(new Dimension(0,12)));
        stats.add(createRow("Saved crawlers:", savedCrawlersValue));
        stats.add(Box.createRigidArea(new Dimension(0,10)));
        stats.add(createRow("Workspace:", statusValue));

        JPanel actions = createCardPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setMaximumSize(new Dimension(320,280));

        JLabel navTitle = createCardTitle("Navigation");
        navTitle.setAlignmentX(CENTER_ALIGNMENT);

        actions.add(navTitle);
        actions.add(Box.createRigidArea(new Dimension(0,12)));

        actions.add(createActionButton("Open crawler workspace","Main Menu"));
        actions.add(Box.createRigidArea(new Dimension(0,10)));
        
        actions.add(createActionButton("Simple Ireland Crawler", null));
        actions.add(Box.createRigidArea(new Dimension(0, 10)));

        actions.add(createActionButton("Exit", null));
        actions.add(Box.createRigidArea(new Dimension(0,10)));

        center.add(stats, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0,0,0,0);
        gbc.anchor = GridBagConstraints.CENTER;

        center.add(actions, gbc);

        centerWrap.add(center);
        add(centerWrap, BorderLayout.CENTER);
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(35,35,180));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180,200,255),1),
                BorderFactory.createEmptyBorder(18,18,18,18)));
        return panel;
    }

    private JLabel createCardTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        return label;
    }

    private JPanel createRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));

        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }

    private JButton createActionButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220,34));
        if (panelName != null)
            button.addActionListener(e -> openPanel(panelName));
        else if (text.equals("Exit"))
            button.addActionListener(e -> System.exit(0));
        // launch applet if the simple crawler is selected
        if (text.equals("Simple Ireland Crawler"))
            button.addActionListener(e -> {
                MainWindow mainWindow = new MainWindow();
                mainWindow.setLocationRelativeTo(this);
                mainWindow.setTitle("About");
                mainWindow.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                mainWindow.setVisible(true);
            });
        return button;
    }

    private void openPanel(String panelName) {

    mainMenuForm.displayPanel(panelName);
    
    }

    public final void refreshStats() {
        savedCrawlersValue.setText(String.valueOf(countSavedCrawlers()));
        statusValue.setText("Ready");
    }

    private int countSavedCrawlers() {

        int count = 0;

        try(FileInputStream crawlers = new FileInputStream("crawlers.dat");
            ObjectInputStream ois = new ObjectInputStream(crawlers)) {

            while(true){
                try{
                    ois.readObject();
                    count++;
                }catch(EOFException eof){
                    break;
                }catch(ClassNotFoundException ex){
                    break;
                }
            }

        } catch(FileNotFoundException ex){
            return 0;
        } catch(IOException ex){
            return count;
        }

        return count;
    }
}
