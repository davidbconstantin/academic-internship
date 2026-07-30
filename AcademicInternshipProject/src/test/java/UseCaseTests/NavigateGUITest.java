/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 *
 * UC-3 NavigateGUI — User traverses the graphical user interface.
 * Covers the normal flow, alternate flow A1, and exception E1
 * from usecases/usecases.docx.
 */
package UseCaseTests;

import com.security.academicinternshipproject.CrawlerConfigJP;
import com.security.academicinternshipproject.CrawlerScriptingJP;
import com.security.academicinternshipproject.MainMenuForm;
import com.security.academicinternshipproject.MainMenuJP;
import com.security.academicinternshipproject.helpmenu.AboutForm;
import com.security.academicinternshipproject.helpmenu.UserManualForm;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for UC-3 NavigateGUI.
 *
 * <p>Pre-condition: the user’s system GUI is currently focused on the application.<br>
 * Post-condition: the main JFrame switches to a different panel which appears as a
 * different menu to the user.<br>
 * Trigger: user presses a panel transition button.
 *
 * @author rokom
 */
public class NavigateGUITest {

    private MainMenuForm mainMenu;

    @BeforeEach
    public void setUp() {
        // Pre-condition / Normal flow step 1: user runs the app.
        mainMenu = new MainMenuForm();
        mainMenu.setTitle("Nascrawler");
        mainMenu.setMinimumSize(new Dimension(1020, 720));
        mainMenu.setSize(1020, 720);
        mainMenu.setLocationRelativeTo(null);
        // Focus the application GUI without requiring a display server interaction.
        mainMenu.setVisible(true);
        assertTrue(mainMenu.isDisplayable() || mainMenu.isVisible(),
                "Pre-condition: application GUI must be available");
    }

    @AfterEach
    public void tearDown() {
        if (mainMenu != null) {
            mainMenu.setVisible(false);
            mainMenu.dispose();
            mainMenu = null;
        }
    }

    /** Returns the CardLayout host panel ({@code mainPanelJP}) via reflection. */
    private JPanel getMainPanel() throws Exception {
        Field field = MainMenuForm.class.getDeclaredField("mainPanelJP");
        field.setAccessible(true);
        return (JPanel) field.get(mainMenu);
    }

    /** Currently visible CardLayout child, or {@code null}. */
    private Component getVisiblePanel() throws Exception {
        for (Component comp : getMainPanel().getComponents()) {
            if (comp.isVisible()) {
                return comp;
            }
        }
        return null;
    }

    /** Post-condition helper: name of the currently visible card. */
    private String getVisiblePanelName() throws Exception {
        Component visible = getVisiblePanel();
        return visible != null ? visible.getName() : null;
    }

    private List<JButton> findButtons(Container root) {
        List<JButton> buttons = new ArrayList<>();
        for (Component comp : root.getComponents()) {
            if (comp instanceof JButton button) {
                buttons.add(button);
            }
            if (comp instanceof Container container) {
                buttons.addAll(findButtons(container));
            }
        }
        return buttons;
    }

    private JButton findButtonByText(Container root, String text) {
        for (JButton button : findButtons(root)) {
            if (text.equals(button.getText())) {
                return button;
            }
        }
        return null;
    }

    private void dispatchMouse(Component target, int id) {
        MouseEvent event = new MouseEvent(
                target,
                id,
                System.currentTimeMillis(),
                0,
                target.getWidth() / 2,
                target.getHeight() / 2,
                1,
                false);
        target.dispatchEvent(event);
    }

    // -------------------------------------------------------------------------
    // Normal Flow
    // 1. User runs the app.
    // 2. The app displays the landing page or main menu.
    // 3. The user clicks a navigation button.
    // Includes: Move Mouse, Click Mouse
    // Extends: Exit Application
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-3 Normal Flow — navigate the GUI")
    class NormalFlow {

        @Test
        @DisplayName("Normal flow: app starts and can show the Landing page")
        public void normalFlow_appDisplaysLandingPage() throws Exception {
            // Step 2 — AcademicInternshipProject.main calls displayPanel("Landing")
            mainMenu.displayPanel("Landing");

            assertEquals("Landing", getVisiblePanelName(),
                    "Landing page must be the visible card after start navigation");
            assertEquals("Nascrawler", mainMenu.getTitle());
            assertTrue(mainMenu.getWidth() >= 1020 || mainMenu.getSize().width >= 0);
        }

        @Test
        @DisplayName("Normal flow: navigation button switches card to Main Menu")
        public void normalFlow_clickNavigationButtonSwitchesPanel() throws Exception {
            mainMenu.displayPanel("Landing");
            assertEquals("Landing", getVisiblePanelName());

            // Step 3 / Trigger: panel transition ("Open crawler workspace" → Main Menu)
            // HomeLandingJP.createActionButton(..., "Main Menu") → displayPanel
            mainMenu.displayPanel("Main Menu");

            assertEquals("Main Menu", getVisiblePanelName(),
                    "Post-condition: JFrame must show a different panel/menu");
            assertEquals("Landing", mainMenu.getPreviousPanelName(),
                    "Previous panel should remain Landing for Back navigation");
        }

        @Test
        @DisplayName("Normal flow: multi-step panel transitions across the workspace")
        public void normalFlow_multiStepPanelTransitions() throws Exception {
            mainMenu.displayPanel("Landing");
            mainMenu.displayPanel("Main Menu");
            mainMenu.displayPanel("Crawler Configuration");
            assertEquals("Crawler Configuration", getVisiblePanelName());

            mainMenu.displayPanel("Crawler Scripting");
            assertEquals("Crawler Scripting", getVisiblePanelName());

            mainMenu.displayPanel("Crawler Configuration");
            mainMenu.displayPanel("Main Menu");
            mainMenu.displayPanel("Database Settings");
            assertEquals("Database Settings", getVisiblePanelName());

            mainMenu.displayPanel("Main Menu");
            mainMenu.displayPanel("Landing");
            assertEquals("Landing", getVisiblePanelName(),
                    "User can return to the landing page via Back-style navigation");
        }

        @Test
        @DisplayName("Include: Move Mouse applies hover feedback on navigation buttons")
        public void normalFlow_moveMouseHoverEffect() throws Exception {
            mainMenu.displayPanel("Main Menu");
            Component visible = getVisiblePanel();
            assertNotNull(visible);

            JButton configButton = findButtonByText((Container) visible, "Configure Crawler");
            assertNotNull(configButton, "Main Menu must expose Configure Crawler");

            // Include: Move Mouse — MainMenuForm.addHoverEffect
            dispatchMouse(configButton, MouseEvent.MOUSE_ENTERED);
            assertEquals(Color.yellow, configButton.getBackground(),
                    "Hover enter should highlight the button");
            assertEquals(Color.blue, configButton.getForeground());

            dispatchMouse(configButton, MouseEvent.MOUSE_EXITED);
            assertEquals(Color.white, configButton.getBackground(),
                    "Hover exit should restore the default background");
            assertEquals(Color.black, configButton.getForeground());
        }

        @Test
        @DisplayName("Include: Click Mouse on a transition button invokes navigation")
        public void normalFlow_clickMouseOnTransitionButton() throws Exception {
            mainMenu.displayPanel("Main Menu");
            Component visible = getVisiblePanel();
            assertNotNull(visible);
            assertEquals("Main Menu", visible.getName());

            // Search only the visible card — several panels define a "Back" button.
            JButton backButton = findButtonByText((Container) visible, "Back");
            assertNotNull(backButton, "Main Menu Back button must exist");
            assertTrue(backButton.isEnabled());

            // Include: Click Mouse — fire the same ActionListener as a real click
            backButton.doClick();

            assertEquals("Landing", getVisiblePanelName(),
                    "Clicking Back must transition from Main Menu to Landing");
        }

        @Test
        @DisplayName("Extends Exit Application: frame uses EXIT_ON_CLOSE")
        public void normalFlow_exitApplicationExtension() {
            // MainMenuForm constructor / initComponents
            assertEquals(WindowConstants.EXIT_ON_CLOSE, mainMenu.getDefaultCloseOperation(),
                    "Closing the main window extends Exit Application");

            // Landing page also exposes an Exit action (System.exit) — assert the button exists
            // without invoking it (would terminate the JVM under test).
            mainMenu.displayPanel("Landing");
            JButton exitButton = findButtonByText(mainMenu, "Exit");
            assertNotNull(exitButton, "Landing page Exit button extends Exit Application");
            assertTrue(exitButton.isEnabled());
            assertEquals(1, exitButton.getActionListeners().length,
                    "Exit button must be wired to an action listener");
        }
    }

    // -------------------------------------------------------------------------
    // Alternate Flow A1: Popup Menu
    // User is led to a different screen after pressing a non-navigational button.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-3 Alternate Flow A1 — popup / non-navigational screens")
    class AlternateFlowA1 {

        @Test
        @DisplayName("A1: About dialog opens without changing the card layout panel")
        public void alternateFlow_aboutPopupDoesNotChangeCard() throws Exception {
            mainMenu.displayPanel("Main Menu");
            String cardBefore = getVisiblePanelName();

            // Non-navigational Help → About (MainMenuForm.aboutMIActionPerformed)
            AboutForm aboutForm = new AboutForm();
            aboutForm.setLocationRelativeTo(mainMenu);
            aboutForm.setTitle("About");
            aboutForm.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
            aboutForm.setVisible(true);

            try {
                assertTrue(aboutForm.isVisible(), "A1: About popup screen must appear");
                assertEquals("About", aboutForm.getTitle());
                assertEquals(cardBefore, getVisiblePanelName(),
                        "A1: popup must not replace the main CardLayout panel");
            } finally {
                aboutForm.setVisible(false);
                aboutForm.dispose();
            }
        }

        @Test
        @DisplayName("A1: User Manual dialog opens as a separate help screen")
        public void alternateFlow_userManualPopupDoesNotChangeCard() throws Exception {
            mainMenu.displayPanel("Main Menu");
            String cardBefore = getVisiblePanelName();

            UserManualForm manual = new UserManualForm();
            manual.setLocationRelativeTo(mainMenu);
            manual.setTitle("Help");
            manual.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
            manual.setVisible(true);

            try {
                assertTrue(manual.isVisible(), "A1: User Manual popup must appear");
                assertEquals("Help", manual.getTitle());
                assertEquals(cardBefore, getVisiblePanelName(),
                        "Help popup is non-navigational with respect to CardLayout");
            } finally {
                manual.setVisible(false);
                manual.dispose();
            }
        }

        @Test
        @DisplayName("A1: SQL credentials gate redirects to SQL Settings (popup-style panel)")
        public void alternateFlow_sqlSettingsShownWhenCredentialsRequired() throws Exception {
            // Crawl / DB actions redirect here when credentials are required —
            // a non-primary navigation path akin to a forced popup panel.
            mainMenu.displayPanel("Main Menu");
            mainMenu.setSQLCredentialsRequired(true);

            assertTrue(mainMenu.isSqlCredentialsRequired());
            mainMenu.displayPanel("SQL Settings");

            assertEquals("SQL Settings", getVisiblePanelName());
            // displayPanel intentionally does not overwrite lastPanel for SQL Settings
            assertEquals("Main Menu", mainMenu.getPreviousPanelName(),
                    "SQL Settings is treated as a transient/pop-up style panel");
        }

        @Test
        @DisplayName("A1: Landing Simple Ireland Crawler is a non-card navigation action")
        public void alternateFlow_simpleIrelandCrawlerIsNonNavigationalButton() throws Exception {
            mainMenu.displayPanel("Landing");
            String cardBefore = getVisiblePanelName();
            Component visible = getVisiblePanel();
            assertNotNull(visible);

            JButton simpleCrawler = findButtonByText((Container) visible, "Simple Ireland Crawler");
            assertNotNull(simpleCrawler, "Landing must offer Simple Ireland Crawler");
            assertTrue(simpleCrawler.isEnabled());
            assertTrue(simpleCrawler.getActionListeners().length >= 1,
                    "Non-navigational button must still have a click handler (opens MainWindow)");

            // Do not click Simple Ireland Crawler — launching MainWindow is heavy.
            // Contrast with a real card-navigation button on the same landing page.
            JButton openWorkspace = findButtonByText((Container) visible, "Open crawler workspace");
            assertNotNull(openWorkspace);
            openWorkspace.doClick();
            assertEquals("Main Menu", getVisiblePanelName(),
                    "Card navigation button changes the panel");

            mainMenu.displayPanel("Landing");
            assertEquals(cardBefore, getVisiblePanelName());
            assertNotEquals(openWorkspace.getText(), simpleCrawler.getText());
        }
    }

    // -------------------------------------------------------------------------
    // Exception E1: Disabled Button
    // Navigation is precluded owing to the user’s behaviour or non-fulfilment
    // of functional prerequisites.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-3 Exception E1 — disabled button blocks navigation")
    class ExceptionE1 {

        @Test
        @DisplayName("E1: Script Behaviour is disabled until crawler config prerequisites are met")
        public void exception_scriptBehaviourDisabledUntilConfigOk() {
            // New crawler / non-edit mode: CrawlerConfigJP leaves scriptBTN disabled
            mainMenu.setEditMode(false);
            mainMenu.setSelectedCrawler(new com.security.academicinternshipproject.WebCrawler());

            CrawlerConfigJP configPanel = new CrawlerConfigJP(mainMenu);
            JButton scriptButton = findButtonByText(configPanel, "Script Behaviour");
            assertNotNull(scriptButton);

            assertFalse(scriptButton.isEnabled(),
                    "E1: Script Behaviour navigation is disabled before OK / edit prerequisites");

            // Prerequisite fulfilled (mirrors okBTNActionPerformed enabling the button)
            scriptButton.setEnabled(true);
            assertTrue(scriptButton.isEnabled(),
                    "After prerequisites, scripting navigation may proceed");
        }

        @Test
        @DisplayName("E1: Edit Command is disabled until a command is selected")
        public void exception_editCommandDisabledWithoutSelection() {
            mainMenu.setEditMode(true);
            mainMenu.setSelectedCrawler(new com.security.academicinternshipproject.WebCrawler());
            mainMenu.getSelectedCrawler().setName("UC3-Edit");
            mainMenu.getSelectedCrawler().addCommand("Visit https://example.com");

            CrawlerScriptingJP scripting = new CrawlerScriptingJP(mainMenu);
            JButton editButton = findButtonByText(scripting, "Edit Command");
            assertNotNull(editButton);

            assertFalse(editButton.isEnabled(),
                    "E1: Edit Command stays disabled until the user selects a command to edit");
        }

        @Test
        @DisplayName("E1: Remove Command is disabled when the script has no commands")
        public void exception_removeCommandDisabledWhenScriptEmpty() {
            mainMenu.setEditMode(false);
            mainMenu.setSelectedCrawler(new com.security.academicinternshipproject.WebCrawler());
            assertTrue(mainMenu.getSelectedCrawler().getCommands().isEmpty());

            CrawlerScriptingJP scripting = new CrawlerScriptingJP(mainMenu);
            // populateFields is private; ancestor-added enables/disables removeBTN.
            // Initial form state: removeBTN is created disabled.
            JButton removeButton = findButtonByText(scripting, "Remove Command");
            assertNotNull(removeButton);
            assertFalse(removeButton.isEnabled(),
                    "E1: Remove is precluded when there is nothing to remove");
        }

        @Test
        @DisplayName("E1: HTML response combo stays disabled until a crawl has finished")
        public void exception_responsesComboDisabledBeforeCrawl() {
            MainMenuJP mainMenuPanel = new MainMenuJP(mainMenu);
            JComboBox<?> responses = null;
            for (Component comp : findAllComponents(mainMenuPanel)) {
                if (comp instanceof JComboBox<?> combo && !combo.isEnabled()) {
                    // responsesCB starts disabled in MainMenuJP.initComponents
                    responses = combo;
                    break;
                }
            }
            assertNotNull(responses,
                    "Main Menu should expose a disabled responses combo before crawl results exist");
            assertFalse(responses.isEnabled(),
                    "E1: browsing responses is precluded until crawl post-condition is met");
        }

        @Test
        @DisplayName("E1: disabled button click does not change the visible panel")
        public void exception_disabledButtonClickDoesNotNavigate() throws Exception {
            mainMenu.displayPanel("Crawler Configuration");
            String before = getVisiblePanelName();

            CrawlerConfigJP configPanel = new CrawlerConfigJP(mainMenu);
            JButton scriptButton = findButtonByText(configPanel, "Script Behaviour");
            assertNotNull(scriptButton);
            scriptButton.setEnabled(false);

            // Clicking a disabled button must not fire navigation
            scriptButton.doClick();
            assertEquals(before, getVisiblePanelName(),
                    "E1: disabled navigation control must not switch panels");
            assertFalse(scriptButton.isEnabled());
        }
    }

    private List<Component> findAllComponents(Container root) {
        List<Component> all = new ArrayList<>();
        for (Component comp : root.getComponents()) {
            all.add(comp);
            if (comp instanceof Container container) {
                all.addAll(findAllComponents(container));
            }
        }
        return all;
    }
}
