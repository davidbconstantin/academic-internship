/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.academicinternshipproject;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author rokom
 */
public class GUIManager {
    ArrayList<JPanel> panelList;
    private JFrame frame;
    
    public GUIManager(JFrame mainFrame) {
        panelList = new ArrayList<>();
        frame = mainFrame;
    }
    
    public void setCurrentPanel(JPanel panel) {
        frame.setContentPane(panel);
        frame.repaint();
        frame.revalidate();
    }
    
    public void addPanel(JPanel panel) {
        panelList.add(panel);
    }
    
    public JPanel findPanel(String name) {
        for (JPanel panel: panelList) {
            if (panel.getName().equals(name))
                return panel;
        }
        return null;
    }
}
