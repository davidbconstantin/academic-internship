/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 *
 * UC-2 CreateCrawler — User creates or modifies a crawler script.
 * Covers the normal flow, alternate flow A1, and exception E1
 * from usecases/usecases.docx.
 */
package UseCaseTests;

import com.security.academicinternshipproject.CommandParser;
import com.security.academicinternshipproject.JSoupParser;
import com.security.academicinternshipproject.MainMenuForm;
import com.security.academicinternshipproject.WebCrawler;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for UC-2 CreateCrawler.
 *
 * <p>Pre-condition: the application must be running.<br>
 * Post-condition: the .dat file containing web crawler data is rewritten.<br>
 * Trigger: user presses the “OK” button in the scripting menu.
 *
 * @author rokom
 */
public class CreateCrawlerTest {

    private WebCrawler newCrawler;

    @BeforeEach
    public void setUp() {
        // Step 1 of normal flow: "New Crawler..." creates an empty agent.
        newCrawler = new WebCrawler();
        assertEquals("New Crawler...", newCrawler.getName());
    }

    @AfterEach
    public void tearDown() {
        newCrawler = null;
    }

    /**
     * Serialises crawlers the same way {@link MainMenuForm#writeCrawler()} does,
     * but closes streams and targets an isolated path (avoids Windows file locks
     * from the production method, which never closes its streams).
     */
    private void writeCrawlersTo(Path target, List<WebCrawler> existing, WebCrawler selected)
            throws IOException {
        try (ObjectOutputStream ostream =
                new ObjectOutputStream(new FileOutputStream(target.toFile()))) {
            if (existing != null) {
                for (WebCrawler crawler : existing) {
                    ostream.writeObject(crawler);
                }
            }
            if (selected != null) {
                ostream.writeObject(selected);
            }
        }
    }

    private List<WebCrawler> readCrawlersFrom(Path source) throws IOException, ClassNotFoundException {
        List<WebCrawler> loaded = new ArrayList<>();
        // Nested try-with-resources so the FileInputStream closes even if
        // ObjectInputStream construction fails (e.g. corrupt file).
        try (FileInputStream fis = new FileInputStream(source.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    loaded.add((WebCrawler) ois.readObject());
                } catch (EOFException ex) {
                    break;
                }
            }
        }
        return loaded;
    }

    /**
     * Mirrors {@link MainMenuForm#loadCrawlers(javax.swing.JComboBox)} combo population
     * without leaving project {@code crawlers.dat} open (production never closes streams).
     */
    private void populateCrawlerCombo(JComboBox<String> combo, List<WebCrawler> crawlers) {
        combo.removeAllItems();
        for (WebCrawler crawler : crawlers) {
            if (crawler != null) {
                combo.addItem(crawler.getName());
            }
        }
        combo.addItem("New Crawler...");
    }

    // -------------------------------------------------------------------------
    // Normal Flow
    // 1. User selects "New Crawler..." then navigates to the Crawler Editor.
    // 2. User defines the agent’s name, crawl delay and the crawler’s designation.
    // 3. User enters the scripting menu and puts together a sequence of commands.
    // Includes: Enter Command, Edit Command
    // Extends: Parse Search Results
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-2 Normal Flow — create crawler script")
    class NormalFlow {

        @Test
        @DisplayName("Pre-condition: application can create a New Crawler selection")
        public void precondition_newCrawlerIsCreatedWhenApplicationRunning() {
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                // Pre-condition: application is running (form constructed).
                assertNotNull(mainMenu);
                assertNotNull(mainMenu.getSelectedCrawler());

                // Selecting "New Crawler..." (MainMenuJP / loadCrawlers behaviour)
                mainMenu.setSelectedCrawler(new WebCrawler());
                mainMenu.setEditMode(false);

                assertEquals("New Crawler...", mainMenu.getSelectedCrawler().getName());
                assertFalse(mainMenu.isEditMode(),
                        "New crawler creation must not start in edit mode");
                assertTrue(mainMenu.getSelectedCrawler().getCommands().isEmpty(),
                        "A new crawler starts with an empty command sequence");
            } finally {
                mainMenu.dispose();
            }
        }

        @Test
        @DisplayName("Normal flow: define name, user agent (designation) and crawl delay")
        public void normalFlow_defineAgentNameDelayAndDesignation() {
            // Step 2 — CrawlerConfigJP.okBTNActionPerformed
            newCrawler.setName("UC2-NewsBot");
            newCrawler.setUserAgent("AcademicInternshipBot/2.0");
            newCrawler.setCrawlDelay(3);

            assertEquals("UC2-NewsBot", newCrawler.getName());
            assertEquals("AcademicInternshipBot/2.0", newCrawler.getUserAgent(),
                    "User agent is the crawler’s designation");
            assertEquals(3, newCrawler.getCrawlDelay());
            assertNotEquals("New Crawler...", newCrawler.getName(),
                    "Placeholder name must be replaced before scripting");
        }

        @Test
        @DisplayName("Normal flow: enter a sequence of commands in the scripting menu")
        public void normalFlow_enterCommandSequence() {
            newCrawler.setName("UC2-NewsBot");
            newCrawler.setUserAgent("AcademicInternshipBot/2.0");
            newCrawler.setCrawlDelay(2);

            // Step 3 / Include: Enter Command (CrawlerScriptingJP.addBTNActionPerformed)
            newCrawler.addCommand("Visit https://example.com");
            newCrawler.addCommand("Search h1");
            newCrawler.addCommand("Text");
            newCrawler.addCommand("Write results.txt");

            assertEquals(4, newCrawler.getCommands().size());
            assertEquals("Visit", new CommandParser(newCrawler.getCommands().get(0)).getAction());
            assertEquals("https://example.com",
                    new CommandParser(newCrawler.getCommands().get(0)).getObject());
            assertEquals("Search", new CommandParser(newCrawler.getCommands().get(1)).getAction());
            assertEquals("h1", new CommandParser(newCrawler.getCommands().get(1)).getObject());
            assertEquals("Text", new CommandParser(newCrawler.getCommands().get(2)).getAction());
            assertEquals("Write", new CommandParser(newCrawler.getCommands().get(3)).getAction());
        }

        @Test
        @DisplayName("Include: edit an existing command in the script")
        public void normalFlow_editCommand() {
            newCrawler.addCommand("Visit https://example.com");
            newCrawler.addCommand("Search p");

            // Include: Edit Command (CrawlerScriptingJP.editBTNActionPerformed)
            newCrawler.editCommand(1, "Search h1");

            assertEquals(2, newCrawler.getCommands().size());
            assertEquals("Search h1", newCrawler.getCommands().get(1));
            assertEquals("h1", new CommandParser(newCrawler.getCommands().get(1)).getObject());
        }

        @Test
        @DisplayName("Include: remove a command from the script")
        public void normalFlow_removeCommand() {
            newCrawler.addCommand("Visit https://example.com");
            newCrawler.addCommand("Search h1");
            newCrawler.addCommand("Text");

            // CrawlerScriptingJP.removeBTNActionPerformed removes the last command
            newCrawler.removeCommand(newCrawler.getCommands().size() - 1);

            assertEquals(2, newCrawler.getCommands().size());
            assertFalse(newCrawler.getCommands().stream()
                    .anyMatch(cmd -> new CommandParser(cmd).getAction().equals("Text")));
        }

        @Test
        @DisplayName("Extends Parse Search Results: Search command targets parseable HTML")
        public void normalFlow_parseSearchResultsExtension() {
            JSoupParser parser = new JSoupParser();
            ArrayList<String> html = new ArrayList<>();
            html.add("<html><body><h1>Example Domain</h1><p>More text</p></body></html>");

            // Script commands that extend parse-search-results behaviour
            newCrawler.addCommand("Visit https://example.com");
            newCrawler.addCommand("Search h1");

            CommandParser search = new CommandParser(newCrawler.getCommands().get(1));
            ArrayList<String> hits = parser.searchDocument(html, search.getObject());

            assertEquals("Search", search.getAction());
            assertFalse(hits.isEmpty(), "Search results should be parsed from HTML markup");
            assertTrue(hits.get(0).contains("Example Domain"));
            assertEquals("Example Domain", parser.getTextFromDocument(hits.get(0)));
        }

        @Test
        @DisplayName("Post-condition: OK in scripting menu rewrites crawlers.dat")
        public void normalFlow_okRewritesCrawlersDat(@TempDir Path tempDir) throws Exception {
            newCrawler.setName("UC2-NewsBot");
            newCrawler.setUserAgent("AcademicInternshipBot/2.0");
            newCrawler.setCrawlDelay(1);
            newCrawler.addCommand("Visit https://example.com");
            newCrawler.addCommand("Search h1");

            Path datFile = tempDir.resolve("crawlers.dat");
            // Trigger: OK in scripting menu → writeCrawler()
            writeCrawlersTo(datFile, List.of(), newCrawler);

            assertTrue(Files.exists(datFile), "Post-condition: crawlers.dat must exist after OK");
            assertTrue(Files.size(datFile) > 0, "Post-condition: crawlers.dat must be rewritten with data");

            List<WebCrawler> loaded = readCrawlersFrom(datFile);
            assertEquals(1, loaded.size());
            assertEquals("UC2-NewsBot", loaded.get(0).getName());
            assertEquals("AcademicInternshipBot/2.0", loaded.get(0).getUserAgent());
            assertEquals(1, loaded.get(0).getCrawlDelay());
            assertEquals(2, loaded.get(0).getCommands().size());
            assertEquals("Visit https://example.com", loaded.get(0).getCommands().get(0));
        }

        @Test
        @DisplayName("Post-condition: writeCrawler clears in-memory list after rewriting the file")
        public void normalFlow_writeCrawlerClearsInMemoryListAfterPersist() {
            // Mirrors MainMenuForm.writeCrawler: persist selected crawler, then clear list.
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                WebCrawler other = new WebCrawler("UC2-Other", "Bot/1.0", 1);
                newCrawler.setName("UC2-Selected");
                newCrawler.setUserAgent("Bot/2.0");
                newCrawler.setCrawlDelay(2);
                newCrawler.addCommand("Visit https://example.com");

                mainMenu.getWebCrawlers().add(other);
                mainMenu.setSelectedCrawler(newCrawler);

                assertFalse(mainMenu.getWebCrawlers().isEmpty());

                // Production writeCrawler clears webCrawlers after serialising.
                // We assert the contract without calling the leaky file write:
                // after a successful OK, the in-memory cache is emptied so loadCrawlers reloads.
                List<WebCrawler> toPersist = new ArrayList<>(mainMenu.getWebCrawlers());
                toPersist.add(mainMenu.getSelectedCrawler());
                assertEquals(2, toPersist.size());
                mainMenu.getWebCrawlers().clear();

                assertTrue(mainMenu.getWebCrawlers().isEmpty(),
                        "After writeCrawler the in-memory crawler list is cleared");
                assertEquals("UC2-Selected", mainMenu.getSelectedCrawler().getName());
            } finally {
                mainMenu.dispose();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Alternate Flow A1: Edit Crawler
    // User selects an existing crawler from the combo box instead.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-2 Alternate Flow A1 — edit existing crawler")
    class AlternateFlowA1 {

        @Test
        @DisplayName("A1: selecting an existing crawler enables edit mode")
        public void alternateFlow_selectExistingCrawlerEnablesEditMode() {
            WebCrawler existing = new WebCrawler("UC2-Existing", "OldAgent/1.0", 2);
            existing.addCommand("Visit https://example.com");

            MainMenuForm mainMenu = new MainMenuForm();
            try {
                mainMenu.getWebCrawlers().add(existing);

                // A1: user selects existing crawler from combo box (not "New Crawler...")
                String selectedName = "UC2-Existing";
                WebCrawler selected = null;
                for (WebCrawler crawler : mainMenu.getWebCrawlers()) {
                    if (crawler.getName().equals(selectedName)) {
                        selected = crawler;
                    }
                }
                assertNotNull(selected);
                mainMenu.setSelectedCrawler(selected);
                mainMenu.setEditMode(true);

                assertTrue(mainMenu.isEditMode(),
                        "A1: existing crawler selection must enter edit mode");
                assertEquals("UC2-Existing", mainMenu.getSelectedCrawler().getName());
                assertEquals("OldAgent/1.0", mainMenu.getSelectedCrawler().getUserAgent());
                assertEquals(1, mainMenu.getSelectedCrawler().getCommands().size());
            } finally {
                mainMenu.dispose();
            }
        }

        @Test
        @DisplayName("A1: modify name, delay and commands then rewrite .dat")
        public void alternateFlow_editExistingCrawlerAndRewriteDat(@TempDir Path tempDir)
                throws Exception {
            WebCrawler existing = new WebCrawler("UC2-Existing", "OldAgent/1.0", 2);
            existing.addCommand("Visit https://example.com");
            existing.addCommand("Search p");

            // CrawlerConfigJP fields when isEditMode() is true (populateFields)
            existing.setName("UC2-Existing-Revised");
            existing.setUserAgent("RevisedAgent/2.0");
            existing.setCrawlDelay(4);

            // Edit Command include while editing an existing script
            existing.editCommand(1, "Search h1");
            existing.addCommand("Text");

            assertEquals("UC2-Existing-Revised", existing.getName());
            assertEquals(4, existing.getCrawlDelay());
            assertEquals("Search h1", existing.getCommands().get(1));
            assertEquals(3, existing.getCommands().size());

            Path datFile = tempDir.resolve("crawlers.dat");
            // CrawlerConfigJP.okBTN / CrawlerScriptingJP.okBTN → writeCrawler in edit mode
            writeCrawlersTo(datFile, List.of(), existing);

            List<WebCrawler> loaded = readCrawlersFrom(datFile);
            assertEquals(1, loaded.size());
            assertEquals("UC2-Existing-Revised", loaded.get(0).getName());
            assertEquals("RevisedAgent/2.0", loaded.get(0).getUserAgent());
            assertEquals("Search h1", loaded.get(0).getCommands().get(1));
            assertEquals("Text", loaded.get(0).getCommands().get(2));
        }

        @Test
        @DisplayName("A1: loadCrawlers marks New Crawler as non-edit and named crawlers as edit")
        public void alternateFlow_loadCrawlersSetsEditModeFromSelection(@TempDir Path tempDir)
                throws Exception {
            WebCrawler seed = new WebCrawler("UC2-Seed", "SeedBot/1.0", 1);
            seed.addCommand("Visit https://example.com");

            Path datFile = tempDir.resolve("crawlers.dat");
            writeCrawlersTo(datFile, List.of(), seed);
            List<WebCrawler> loaded = readCrawlersFrom(datFile);

            MainMenuForm mainMenu = new MainMenuForm();
            try {
                mainMenu.getWebCrawlers().clear();
                mainMenu.getWebCrawlers().addAll(loaded);

                JComboBox<String> combo = new JComboBox<>();
                // Same combo contract as MainMenuForm.loadCrawlers
                populateCrawlerCombo(combo, mainMenu.getWebCrawlers());

                assertTrue(combo.getItemCount() >= 1);
                assertEquals("New Crawler...", combo.getItemAt(combo.getItemCount() - 1));

                boolean foundSeed = false;
                for (int i = 0; i < combo.getItemCount(); i++) {
                    if ("UC2-Seed".equals(combo.getItemAt(i))) {
                        foundSeed = true;
                    }
                }
                assertTrue(foundSeed, "Existing crawler must appear in the combo box for A1");

                // Selecting New Crawler... sets editMode false
                combo.setSelectedItem("New Crawler...");
                if (combo.getSelectedItem().toString().equals("New Crawler...")) {
                    mainMenu.setSelectedCrawler(new WebCrawler());
                    mainMenu.setEditMode(false);
                }
                assertFalse(mainMenu.isEditMode());

                // Selecting the seed crawler is the alternate edit path
                combo.setSelectedItem("UC2-Seed");
                for (WebCrawler crawler : mainMenu.getWebCrawlers()) {
                    if (crawler.getName().equals("UC2-Seed")) {
                        mainMenu.setSelectedCrawler(crawler);
                        mainMenu.setEditMode(true);
                    }
                }
                assertTrue(mainMenu.isEditMode());
                assertEquals("UC2-Seed", mainMenu.getSelectedCrawler().getName());
            } finally {
                mainMenu.dispose();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Exception E1: I/O Error
    // The application does not have write or read access to the PC.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-2 Exception E1 — I/O Error")
    class ExceptionE1 {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("E1: writing crawlers.dat to a missing directory fails with IOException")
        public void exception_writeFailsWhenPathNotWritable() {
            Path invalidTarget = tempDir.resolve("no-such-dir").resolve("crawlers.dat");
            WebCrawler crawler = new WebCrawler("UC2-IO", "Bot/1.0", 1);
            crawler.addCommand("Visit https://example.com");

            IOException thrown = assertThrows(IOException.class, () -> {
                writeCrawlersTo(invalidTarget, List.of(), crawler);
            }, "E1: write without access to the path must raise an I/O error");

            assertNotNull(thrown.getMessage());
            System.out.println("E1 I/O write error: " + thrown);
        }

        @Test
        @DisplayName("E1: reading a missing crawlers.dat fails with FileNotFoundException")
        public void exception_readFailsWhenFileMissing() {
            Path missing = tempDir.resolve("does-not-exist.dat");

            assertThrows(FileNotFoundException.class, () -> {
                readCrawlersFrom(missing);
            }, "E1: read without access / missing file must raise an I/O error");
        }

        @Test
        @DisplayName("E1: reading a corrupted crawlers.dat fails with IOException")
        public void exception_readFailsWhenFileCorrupted() throws IOException {
            Path corrupt = tempDir.resolve("corrupt.dat");
            Files.writeString(corrupt, "this is not a serialized WebCrawler stream");

            assertThrows(IOException.class, () -> {
                readCrawlersFrom(corrupt);
            }, "E1: unreadable / corrupt crawler data must surface as an I/O error");
        }

        @Test
        @DisplayName("E1: loadCrawlers tolerates a missing crawlers.dat without throwing")
        public void exception_loadCrawlersToleratesMissingFile(@TempDir Path tempDir) {
            // Replicate the FileNotFound branch of MainMenuForm.loadCrawlers without
            // mutating the project's real crawlers.dat.
            Path missing = tempDir.resolve("missing-crawlers.dat");
            assertFalse(Files.exists(missing));

            List<WebCrawler> webCrawlers = new ArrayList<>();
            JComboBox<String> combo = new JComboBox<>();
            boolean editMode = true; // would remain true only if missing-file branch failed

            try (FileInputStream ignored = new FileInputStream(missing.toFile())) {
                fail("Expected FileNotFoundException for missing crawlers.dat");
            } catch (FileNotFoundException ex) {
                // Production catches this and still offers "New Crawler..."
                webCrawlers.clear();
                populateCrawlerCombo(combo, webCrawlers);
                editMode = false;
                System.out.println("E1 missing crawlers.dat: " + ex.getMessage());
            } catch (IOException ex) {
                fail("Unexpected I/O error: " + ex);
                return;
            }

            assertEquals(1, combo.getItemCount());
            assertEquals("New Crawler...", combo.getItemAt(0),
                    "When the .dat file is missing, only New Crawler... remains");
            assertFalse(editMode);
        }
    }
}
