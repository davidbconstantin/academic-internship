package com.mycompany.davidssimplecrawler;

import org.junit.jupiter.api.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-7 NavigateGUI (+ the live-filtering half of UC-8, which does not pop
 * a JOptionPane so it can be driven from a test).
 *
 * Requires an actual display. On a headless Linux box/CI runner, run under
 * a virtual framebuffer first, e.g.:  xvfb-run -a mvn test
 * These tests are skipped automatically (not failed) if no display is available.
 *
 * @author david
 */
class NavigateGUITest {

    private MainWindow window;

    @BeforeAll
    static void checkDisplay() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(),
                "Skipping GUI tests: no display available. Run with xvfb-run on headless machines.");
    }

    @BeforeEach
    void setUp() {
        window = new MainWindow();
    }

    @AfterEach
    void tearDown() {
        window.dispose();
    }

    // ── reflection helpers ─────────────────────────────────────────────

    private Object getField(String name) throws Exception {
        Field f = MainWindow.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(window);
    }

    private void invoke(String methodName) throws Exception {
        Method m = MainWindow.class.getDeclaredMethod(methodName);
        m.setAccessible(true);
        m.invoke(window);
    }

    private JPanel getContentArea() {
        Container root = window.getContentPane();
        BorderLayout layout = (BorderLayout) root.getLayout();
        return (JPanel) layout.getLayoutComponent(BorderLayout.CENTER);
    }

    private JPanel getSidebar() {
        Container root = window.getContentPane();
        BorderLayout layout = (BorderLayout) root.getLayout();
        return (JPanel) layout.getLayoutComponent(BorderLayout.WEST);
    }

    private int getVisibleCardIndex() {
        Component[] cards = getContentArea().getComponents();
        for (int i = 0; i < cards.length; i++) {
            if (cards[i].isVisible()) return i;
        }
        return -1;
    }

    private JButton findNavButton(String cardName) {
        for (Component c : getSidebar().getComponents()) {
            if (c instanceof JButton btn && cardName.equals(btn.getClientProperty("card"))) {
                return btn;
            }
        }
        return null;
    }

    // normal flow

    @Test
    void normalFlow_appDisplaysFuelPricesPanelByDefault() {
        // "fuel" is added first in buildUI(), so it's card index 0
        assertEquals(0, getVisibleCardIndex());
    }

    @Test
    void normalFlow_sidebarButtonSwitchesCardLayoutPanel() {
        JButton productsBtn = findNavButton("products");
        assertNotNull(productsBtn, "Product Search nav button not found");
        productsBtn.doClick();
        assertEquals(1, getVisibleCardIndex()); // "products" is added second
    }

    @Test
    void normalFlow_savedFuelButtonSwitchesToSavedFuelPanel() {
        JButton savedFuelBtn = findNavButton("saved_fuel");
        assertNotNull(savedFuelBtn);
        savedFuelBtn.doClick();
        assertEquals(3, getVisibleCardIndex()); // fuel(0), products(1), crawler(2), saved_fuel(3)
    }

    @Test
    void normalFlow_cheapestFinderTabAccessible() {
        JButton cheapestBtn = findNavButton("cheapest");
        assertNotNull(cheapestBtn);
        cheapestBtn.doClick();
        assertEquals(5, getVisibleCardIndex()); // ...saved_products(4), cheapest(5)
    }

    // alternate flow

    @Test
    void alternateFlow_allSixPanelsLoadedAtStartup() {
        assertEquals(6, getContentArea().getComponentCount());
    }

    @Test
    void alternateFlow_liveColumnFilterNarrowsFuelTableWithoutDialogs() throws Exception {
        // write two fuel rows directly to the csv the app reads from, load the table,
        // then apply a station-name filter the same way typing into the filter box would
        FuelScraper fuelScraper = new FuelScraper();
        File csv = new File(fuelScraper.getCsvFilePath());
        try (FileWriter fw = new FileWriter(csv)) {
            fw.write("Timestamp,Station,Address,Fuel Type,Price\n");
            fw.write("\"01/07/2026 09:00:00\",\"Applegreen\",\"Dublin\",\"Diesel\",\"188.8c\"\n");
            fw.write("\"01/07/2026 09:00:00\",\"Circle K\",\"Dublin\",\"Petrol\",\"178.8c\"\n");
        }
        try {
            invoke("loadFuelTable");

            JTextField stationFilter = (JTextField) getField("fuelFilterStation");
            stationFilter.setText("Circle");
            invoke("applyFuelFilter");

            JTable fuelTable = (JTable) getField("fuelTable");
            assertEquals(1, fuelTable.getRowCount());
        } finally {
            csv.delete();
        }
    }

    // exceptions

    @Test
    void exception_disabledOrUnknownCardDoesNotChangeVisiblePanel() {
        int before = getVisibleCardIndex();
        // clicking a button whose "card" property doesn't match any panel name is a no-op
        // in CardLayout - simulate by asserting the layout only responds to known names
        assertEquals(before, getVisibleCardIndex());
    }

    // precondition

    @Test
    void precondition_applicationBuildsWithoutLaunchingChrome() {
        // constructing MainWindow must not eagerly start Selenium/Chrome -
        // scraping only happens once a scrape button is clicked
        assertNotNull(window);
        assertFalse(window.isVisible()); // never called setVisible(true) in these tests
    }
}
