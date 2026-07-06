/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * https://docs.oracle.com/javase/tutorial/uiswing/misc/systemtray.html
 * https://stackoverflow.com/questions/34490218/how-to-make-a-windows-notification-in-java
 * https://docs.oracle.com/javase/8/docs/api/javax/imageio/ImageIO.html
 */
package com.security.academicinternshipproject;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author rokom
 */
public class TaskNotifier {
    
    private String toolTip = "Web Crawler App";
    private TrayIcon icon = null;
    
    public TaskNotifier() {
        // determine whether SystemTrays are supported
        final Image image;
        if (!SystemTray.isSupported()) {
            System.out.println("System Tray is not supported.");
            return;
        } else {
            final PopupMenu popup = new PopupMenu();
            final SystemTray tray = SystemTray.getSystemTray();
            try {
                image = ImageIO.read(new File("images/bulb.gif"));
            } catch (IOException ex) {
                System.out.println(ex);
                return;
            }
            icon = new TrayIcon(image, "tray icon");
            icon.setToolTip(toolTip);
            icon.setPopupMenu(popup);
            try {
                tray.add(icon);
                //icon.displayMessage("Web Crawler App", "Initialising...", TrayIcon.MessageType.INFO);
            } catch (AWTException e) {
                System.out.println(e);
            }
        }
    }
    
    public void displayMessage(String message) {
        icon.displayMessage("Web Crawler App", message, TrayIcon.MessageType.INFO);
    }
    
    public String getToolTip() {
        return toolTip;
    }
    
    public void setToolTip(String toolTip) {
        icon.setToolTip(toolTip);
    }
}
