/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 *
 * UC-1 RunCrawler — User executes a crawler script.
 * Covers the normal flow, alternate flow A1, and exceptions E1–E3
 * from usecases/usecases.docx.
 */
package UseCaseTests;

import com.security.academicinternshipproject.CommandParser;
import com.security.academicinternshipproject.JSoupParser;
import com.security.academicinternshipproject.MainMenuForm;
import com.security.academicinternshipproject.MySQLConnector;
import com.security.academicinternshipproject.SearchResult;
import com.security.academicinternshipproject.WebCrawler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * JUnit tests for UC-1 RunCrawler.
 *
 * <p>Pre-condition: a crawler script has been written.<br>
 * Post-condition: the main menu displays HTML responses.<br>
 * Trigger: user presses the “Crawl” button in the main menu.
 *
 * @author rokom
 */
public class RunCrawlerTest {

    private WebCrawler crawler;
    private JSoupParser htmlParser;

    @BeforeEach
    public void setUp() {
        // Pre-condition: a crawler script has been written.
        crawler = new WebCrawler("UC1-TestCrawler", "AcademicInternshipBot/1.0", 1);
        crawler.addCommand("Visit https://example.com");
        crawler.addCommand("Search h1");
        crawler.addCommand("Text");
        htmlParser = new JSoupParser();
    }

    @AfterEach
    public void tearDown() {
        crawler = null;
        htmlParser = null;
    }

    // -------------------------------------------------------------------------
    // Normal Flow
    // 1. User navigates to the main menu.
    // 2. User selects a crawler from the combo box.
    // 3. User presses the crawl button.
    // Includes: GET Request, Print Results
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-1 Normal Flow — execute crawler script")
    class NormalFlow {

        @Test
        @DisplayName("Pre-condition: crawler script is present and selectable")
        public void precondition_crawlerScriptHasBeenWritten() {
            List<WebCrawler> availableCrawlers = new ArrayList<>();
            availableCrawlers.add(crawler);

            // Step 1–2: navigate / select crawler (combo-box selection)
            WebCrawler selected = null;
            for (WebCrawler candidate : availableCrawlers) {
                if (candidate.getName().equals("UC1-TestCrawler")) {
                    selected = candidate;
                }
            }

            assertNotNull(selected, "Selected crawler must exist in the list");
            assertFalse(selected.getCommands().isEmpty(),
                    "Pre-condition requires a written crawler script");
            assertEquals(3, selected.getCommands().size());
            assertEquals("Visit", new CommandParser(selected.getCommands().get(0)).getAction());
            assertEquals("Search", new CommandParser(selected.getCommands().get(1)).getAction());
            assertEquals("Text", new CommandParser(selected.getCommands().get(2)).getAction());
        }

        @Test
        @DisplayName("Normal flow: GET request, parse results, store HTML responses")
        public void normalFlow_crawlProducesHtmlResponses() throws IOException {
            // Step 3: press Crawl — execute Visit (GET Request include)
            CommandParser visit = new CommandParser(crawler.getCommands().get(0));
            assertEquals("Visit", visit.getAction());
            String url = visit.getObject();

            Document page = Jsoup.connect(url)
                    .userAgent(crawler.getUserAgent())
                    .timeout(15_000)
                    .get();
            assertNotNull(page, "GET Request must return a document");
            assertFalse(page.html().isBlank(), "Fetched HTML must not be empty");

            ArrayList<String> visitResults = new ArrayList<>();
            visitResults.add(page.outerHtml());
            crawler.addHtmlResponse(new SearchResult(visitResults, 1));

            // Search command over previous HTML response
            CommandParser search = new CommandParser(crawler.getCommands().get(1));
            ArrayList<String> searchHits = htmlParser.searchDocument(
                    crawler.getHtmlResponses().get(0).getResults(),
                    search.getObject());
            assertFalse(searchHits.isEmpty(), "Search should find at least one h1 on example.com");
            crawler.addHtmlResponse(new SearchResult(searchHits, 2));

            // Text extraction (Print Results include)
            ArrayList<String> textResults = new ArrayList<>();
            for (String markup : crawler.getHtmlResponses().get(1).getResults()) {
                String text = htmlParser.getTextFromDocument(markup);
                textResults.add(text);
                System.out.println("Print Results: " + text);
            }
            assertFalse(textResults.isEmpty());
            assertTrue(textResults.get(0).toLowerCase().contains("example"),
                    "Extracted text should mention example.com content");
            crawler.addHtmlResponse(new SearchResult(textResults, 3));

            // Post-condition: main menu would display HTML responses
            assertEquals(3, crawler.getHtmlResponses().size(),
                    "Post-condition: HTML responses must be available for display");
            for (SearchResult response : crawler.getHtmlResponses()) {
                assertFalse(response.getResults().isEmpty(),
                        "Each stored response must contain printable results");
            }
        }

        @Test
        @DisplayName("Normal flow: Crawl is allowed when SQL credentials are not required")
        public void normalFlow_crawlAllowedWhenCredentialsNotRequired() {
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                mainMenu.setSQLCredentialsRequired(false);
                mainMenu.setSelectedCrawler(crawler);
                mainMenu.getWebCrawlers().add(crawler);

                // Mirrors MainMenuJP.crawlBTNActionPerformed: only start crawl when
                // credentials are not required.
                assertFalse(mainMenu.isSqlCredentialsRequired(),
                        "Normal flow resumes only when SQL credentials are not required");
                assertEquals("UC1-TestCrawler", mainMenu.getSelectedCrawler().getName());
                assertTrue(mainMenu.getWebCrawlers().stream()
                        .anyMatch(c -> c.getName().equals("UC1-TestCrawler")));
            } finally {
                mainMenu.dispose();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Alternate Flow A1: SQL Credentials Input
    // User must key in their database credentials before the normal flow may resume.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-1 Alternate Flow A1 — SQL credentials input")
    class AlternateFlowA1 {

        @Test
        @DisplayName("A1: Crawl is blocked until SQL credentials are supplied")
        public void alternateFlow_sqlCredentialsRequiredBeforeCrawl() {
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                // Application starts requiring credentials (see MainMenuForm default).
                mainMenu.setSQLCredentialsRequired(true);
                mainMenu.setSelectedCrawler(crawler);

                assertTrue(mainMenu.isSqlCredentialsRequired(),
                        "A1: crawl button must redirect to SQL Settings when credentials are required");

                // User keys in credentials (SQLCredentialsJP.okBTNActionPerformed)
                mainMenu.setUsername("test_user".toCharArray());
                mainMenu.setPassword("test_password".toCharArray());
                mainMenu.setSQLCredentialsRequired(false);

                assertFalse(mainMenu.isSqlCredentialsRequired(),
                        "A1: after credentials are entered, normal flow may resume");
                assertArrayEquals("test_user".toCharArray(), mainMenu.getUsername());
                assertArrayEquals("test_password".toCharArray(), mainMenu.getPassword());

                // Credentials are wiped after SQL work (WebCrawlerGUIWorker / eraseCredentials)
                mainMenu.eraseCredentials();
                assertFalse(mainMenu.isSqlCredentialsRequired());
                assertArrayEquals("000000000".toCharArray(), mainMenu.getUsername());
                assertArrayEquals("0000000000000".toCharArray(), mainMenu.getPassword());
            } finally {
                mainMenu.dispose();
            }
        }

        @Test
        @DisplayName("A1: SQL crawler command is recognised by the command parser")
        public void alternateFlow_sqlCommandIsParsedFromScript() {
            crawler.addCommand("SQL INSERT INTO testtable (id, name) VALUES (0, @1s);");

            CommandParser sqlCommand = null;
            for (String command : crawler.getCommands()) {
                CommandParser parser = new CommandParser(command);
                if ("SQL".equals(parser.getAction())) {
                    sqlCommand = parser;
                }
            }

            assertNotNull(sqlCommand, "Script containing SQL should be detected before crawl");
            assertTrue(sqlCommand.getObject().toUpperCase().contains("INSERT INTO"),
                    "SQL object should hold the full statement for execution");
        }
    }

    // -------------------------------------------------------------------------
    // Exception E1: Access Denied
    // The website being visited might not allow crawlers.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-1 Exception E1 — Access Denied")
    class ExceptionE1 {

        @Test
        @DisplayName("E1: HTTP 403 Access Denied when site blocks crawlers")
        public void exception_accessDeniedWhenSiteBlocksCrawlers() {
            // httpbin returns a controlled 403 to simulate a site that rejects bots.
            String blockedUrl = "https://httpbin.org/status/403";

            HttpStatusException thrown = assertThrows(HttpStatusException.class, () -> {
                Jsoup.connect(blockedUrl)
                        .userAgent(crawler.getUserAgent())
                        .timeout(15_000)
                        .get();
            }, "E1: visiting a site that denies crawlers must surface an HTTP error");

            assertEquals(403, thrown.getStatusCode(),
                    "E1 expects HTTP 403 Access Denied");
            System.out.println("E1 Access Denied: " + thrown.getMessage());
        }

        @Test
        @DisplayName("E1: failed Visit leaves no successful HTML response for that request")
        public void exception_accessDeniedDoesNotStoreSuccessfulResponse() {
            int responsesBefore = crawler.getHtmlResponses().size();

            try {
                Jsoup.connect("https://httpbin.org/status/403")
                        .userAgent(crawler.getUserAgent())
                        .timeout(15_000)
                        .get();
                fail("Expected access to be denied");
            } catch (HttpStatusException ex) {
                assertEquals(403, ex.getStatusCode());
                // No SearchResult is added on failure — post-condition not met for this visit.
                assertEquals(responsesBefore, crawler.getHtmlResponses().size(),
                        "Failed Visit must not append a successful HTML response");
            } catch (IOException ex) {
                // Network-level denial is also treated as an access failure.
                assertEquals(responsesBefore, crawler.getHtmlResponses().size());
            }
        }
    }

    // -------------------------------------------------------------------------
    // Exception E2: Invalid SQL Syntax
    // The database throws an error unable to parse the crawler’s SQL statement.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-1 Exception E2 — Invalid SQL Syntax")
    class ExceptionE2 {

        @Test
        @DisplayName("E2: malformed SQL command is still parsed as a SQL action")
        public void exception_invalidSqlIsRecognisedAsSqlCommand() {
            String invalidSql = "SQL INSERT INTO (id, result) VALUES (1, @2s);";
            CommandParser parser = new CommandParser(invalidSql);

            assertEquals("SQL", parser.getAction());
            assertTrue(parser.getObject().toUpperCase().contains("INSERT"),
                    "Parser should extract the SQL body even when the statement is invalid");
        }

        @Test
        @DisplayName("E2: database rejects invalid SQL syntax with SQLException")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void exception_invalidSqlSyntaxThrowsFromDatabase() {
            String url = System.getenv("TEST_MYSQL_URL");
            String user = System.getenv("TEST_MYSQL_USER");
            String password = System.getenv("TEST_MYSQL_PASSWORD");
            assumeTrue(url != null && user != null && password != null,
                    "MySQL test credentials must be configured");

            // Intentionally invalid: missing table name / broken syntax.
            String invalidStatement = "INSERT INTO (id, result) VALUES (1, 'broken');";

            SQLException thrown = assertThrows(SQLException.class, () -> {
                new MySQLConnector(url, user, password, invalidStatement);
            }, "E2: the database must throw when it cannot parse the crawler SQL statement");

            assertNotNull(thrown.getMessage());
            System.out.println("E2 Invalid SQL Syntax: " + thrown.getMessage());
        }

        @Test
        @DisplayName("E2: placeholder substitution still yields invalid SQL when structure is wrong")
        public void exception_placeholderSubstitutionCannotFixBrokenSql() {
            String sqlStatement = "INSERT INTO (id, result) VALUES (1, @2s);";
            String valueToInsert = "Example";

            boolean atSignPresent = false;
            boolean numericCharacterPresent = false;
            boolean alphabeticLetterPresent = false;
            for (int i = 0; i < sqlStatement.length(); i++) {
                if (sqlStatement.charAt(i) == '@') {
                    atSignPresent = true;
                }
                if (i + 1 < sqlStatement.length()
                        && Character.isDigit(sqlStatement.charAt(i + 1))) {
                    numericCharacterPresent = true;
                }
                if (i + 2 < sqlStatement.length()) {
                    char typeFlag = Character.toLowerCase(sqlStatement.charAt(i + 2));
                    if (typeFlag == 'i' || typeFlag == 's') {
                        alphabeticLetterPresent = true;
                    }
                }
                if (atSignPresent && numericCharacterPresent && alphabeticLetterPresent) {
                    sqlStatement = sqlStatement.replaceFirst("@\\d[iIsS]", "'" + valueToInsert + "'");
                    atSignPresent = numericCharacterPresent = alphabeticLetterPresent = false;
                } else {
                    atSignPresent = numericCharacterPresent = alphabeticLetterPresent = false;
                }
            }

            // Substitution succeeds, but the statement remains syntactically invalid.
            assertTrue(sqlStatement.contains("'Example'"));
            assertTrue(sqlStatement.contains("INSERT INTO ("),
                    "Missing table name remains — database would still reject this statement");
        }
    }

    // -------------------------------------------------------------------------
    // Exception E3: Invalid Python Syntax
    // The Python interpreter fails to execute a snippet of the script.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-1 Exception E3 — Invalid Python Syntax")
    class ExceptionE3 {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("E3: Python command in crawler script is recognised")
        public void exception_pythonCommandIsParsedFromScript() {
            crawler.addCommand("Python broken_snippet.py");
            CommandParser parser = new CommandParser("Python broken_snippet.py");

            assertEquals("Python", parser.getAction());
            assertEquals("broken_snippet.py", parser.getObject());
            assertTrue(crawler.getCommands().stream()
                    .anyMatch(cmd -> new CommandParser(cmd).getAction().equals("Python")));
        }

        @Test
        @DisplayName("E3: Python interpreter fails on invalid syntax")
        public void exception_invalidPythonSyntaxFailsExecution() throws Exception {
            Path script = tempDir.resolve("invalid_snippet.py");
            // Deliberate syntax error: unclosed string / invalid token.
            Files.writeString(script,
                    "def broken(\nprint('this is not valid python')\n",
                    StandardCharsets.UTF_8);

            // Mirrors WebCrawlerGUIWorker Python branch: invoke the interpreter on the script path.
            ProcessBuilder pb = new ProcessBuilder(
                    "python", "-u", script.toAbsolutePath().toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            boolean finished = process.waitFor(20, TimeUnit.SECONDS);
            assumeTrue(finished, "Python process should terminate promptly");

            int exitCode = process.exitValue();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("E3 Python output:\n" + output);

            assertNotEquals(0, exitCode,
                    "E3: invalid Python syntax must cause a non-zero interpreter exit code");
            assertTrue(
                    output.toLowerCase().contains("syntax")
                            || output.toLowerCase().contains("error")
                            || output.toLowerCase().contains("invalid"),
                    "Interpreter error output should indicate a syntax/parse failure");
        }

        @Test
        @DisplayName("E3: failed Python command does not add an HTML response")
        public void exception_failedPythonDoesNotAddHtmlResponse() throws Exception {
            // WebCrawlerGUIWorker skips addHtmlResponse for Python actions.
            CommandParser parser = new CommandParser("Python does_not_exist_uc1.py");
            assertEquals("Python", parser.getAction());

            int before = crawler.getHtmlResponses().size();
            ProcessBuilder pb = new ProcessBuilder(
                    "python", "-u", "does_not_exist_uc1.py");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor(20, TimeUnit.SECONDS);

            if (!parser.getAction().equals("Python") && !parser.getAction().equals("Write")) {
                crawler.addHtmlResponse(new SearchResult(new ArrayList<>(), parser.getCommandNo()));
            }

            assertEquals(before, crawler.getHtmlResponses().size(),
                    "Failed/completed Python steps must not append HTML responses");
            assertNotEquals(0, process.exitValue());
        }
    }
}
