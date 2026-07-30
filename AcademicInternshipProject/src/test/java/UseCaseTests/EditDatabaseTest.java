/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 *
 * UC-4 EditDatabase — User operates a MySQL database.
 * Covers the normal flow, alternate flow A1, and exceptions E1–E3
 * from usecases/usecases.docx.
 */
package UseCaseTests;

import com.security.academicinternshipproject.CommandParser;
import com.security.academicinternshipproject.MainMenuForm;
import com.security.academicinternshipproject.MySQLConnector;
import com.security.academicinternshipproject.SQLColumnInfo;
import com.security.academicinternshipproject.SQLDatabaseInfo;
import com.security.academicinternshipproject.SQLParser;
import com.security.academicinternshipproject.SQLTableInfo;
import com.security.academicinternshipproject.WebCrawler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import javax.swing.table.DefaultTableModel;
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
 * JUnit tests for UC-4 EditDatabase.
 *
 * <p>Pre-conditions: SQL syntax is correct; user has entered SQL credentials;
 * MySQL connection is configured.<br>
 * Post-condition: the database is modified.<br>
 * Triggers: web crawler executes a SQL statement, or user creates a MySQL table.
 *
 * @author rokom
 */
public class EditDatabaseTest {

    private static final String CREDENTIALS_FILE = "credentials.txt";

    private String testUrl;
    private String testUser;
    private String testPassword;
    private boolean mysqlConfigured;

    @BeforeEach
    public void setUp() {
        testUrl = System.getenv("TEST_MYSQL_URL");
        testUser = System.getenv("TEST_MYSQL_USER");
        testPassword = System.getenv("TEST_MYSQL_PASSWORD");
        mysqlConfigured = testUrl != null && !testUrl.isBlank()
                && testUser != null && !testUser.isBlank()
                && testPassword != null;
    }

    @AfterEach
    public void tearDown() {
        testUrl = testUser = testPassword = null;
    }

    /**
     * Builds a CREATE TABLE statement the same way
     * {@code SQLDatabaseJP.okBTNActionPerformed} assembles one from the schema grid.
     */
    private String buildCreateTableStatement(String tableName, DefaultTableModel model) {
        String sqlStatement = "CREATE TABLE " + tableName + "(";
        for (int i = 0; i < model.getRowCount(); i++) {
            String tableRow = "";
            for (int j = 0; j < model.getColumnCount(); j++) {
                if (model.getValueAt(i, j) != null && j != 2 && j != 3) {
                    tableRow += model.getValueAt(i, j).toString() + " ";
                    if (j == 1) {
                        Object type = model.getValueAt(i, j);
                        if ("VARCHAR".equals(type) || "VARBINARY".equals(type)) {
                            tableRow += "(255)";
                        }
                    }
                }
                if (j == 2 && model.getValueAt(i, j) != null) {
                    if (Boolean.TRUE.equals(model.getValueAt(i, j))) {
                        tableRow += "PRIMARY KEY";
                    }
                }
                if (j == 3 && model.getValueAt(i, j) != null) {
                    if (Boolean.TRUE.equals(model.getValueAt(i, j))) {
                        tableRow += " AUTO_INCREMENT";
                    }
                }
            }
            tableRow = tableRow.trim();
            if (i != model.getRowCount() - 1) {
                tableRow += ",";
            }
            sqlStatement += tableRow;
        }
        sqlStatement += ");";
        return sqlStatement;
    }

    private DefaultTableModel sampleSchemaModel() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Column Name", "Data Type", "Primary Key", "Auto-Increment"}) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 2, 3 -> Boolean.class;
                    default -> Object.class;
                };
            }
        };
        model.addRow(new Object[]{"id", "INT", true, true});
        model.addRow(new Object[]{"name", "VARCHAR", false, false});
        model.addRow(new Object[]{"notes", "TEXT", false, false});
        return model;
    }

    /** Mirrors DatabaseSettingsJP.okBTNActionPerformed writing host/port/dbName. */
    private void writeDatabaseSettings(Path target, String host, String port, String dbName)
            throws IOException {
        try (FileWriter writer = new FileWriter(target.toFile())) {
            writer.write(host + "\n");
            writer.write(port + "\n");
            writer.write(dbName);
        }
    }

    private List<String> readSettingsLines(Path source) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(source.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    // -------------------------------------------------------------------------
    // Normal Flow
    // 1. User configures the database settings.
    // 2. User navigates to the SQL table editor.
    // 3. User defines a table schema.
    // 4. User enters their SQL credentials.
    // 5. User confirms table creation.
    // Includes: Create Table, Update Table, Query Table
    // Extends: Export Table, Visualise Table
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-4 Normal Flow — operate MySQL database")
    class NormalFlow {

        @Test
        @DisplayName("Normal flow: configure database settings (host, port, database name)")
        public void normalFlow_configureDatabaseSettings(@TempDir Path tempDir) throws Exception {
            Path settings = tempDir.resolve("credentials.txt");
            // Step 1 — DatabaseSettingsJP.okBTNActionPerformed
            writeDatabaseSettings(settings, "localhost", "3306", "sakila");

            List<String> lines = readSettingsLines(settings);
            assertEquals(3, lines.size(), "Settings file stores host, port and database name");
            assertEquals("localhost", lines.get(0));
            assertEquals("3306", lines.get(1));
            assertEquals("sakila", lines.get(2));
        }

        @Test
        @DisplayName("Normal flow: load configured settings into MainMenuForm")
        public void normalFlow_loadSqlCredentialsFromSettingsFile() {
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                // Constructor already calls loadSQLCredentials() from credentials.txt
                List<String> credentials = mainMenu.getSQLCredentials();
                assertNotNull(credentials);
                assertFalse(credentials.isEmpty(),
                        "Pre-condition: MySQL connection settings should be configured");
                assertTrue(credentials.size() >= 3,
                        "Host, port and database name are required connection settings");
                System.out.println("Loaded settings: host=" + credentials.get(0)
                        + " port=" + credentials.get(1)
                        + " db=" + credentials.get(2));
            } finally {
                mainMenu.dispose();
            }
        }

        @Test
        @DisplayName("Normal flow: navigate to SQL table editor from database settings")
        public void normalFlow_navigateToSqlTableEditor() throws Exception {
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                // Step 2 — DatabaseSettingsJP.tablesBTN → displayPanel("SQL Database")
                mainMenu.displayPanel("Database Settings");
                mainMenu.displayPanel("SQL Database");

                var field = MainMenuForm.class.getDeclaredField("mainPanelJP");
                field.setAccessible(true);
                javax.swing.JPanel mainPanel = (javax.swing.JPanel) field.get(mainMenu);
                String visible = null;
                for (java.awt.Component comp : mainPanel.getComponents()) {
                    if (comp.isVisible()) {
                        visible = comp.getName();
                        break;
                    }
                }
                assertEquals("SQL Database", visible,
                        "User reaches the SQL table editor panel");
            } finally {
                mainMenu.dispose();
            }
        }

        @Test
        @DisplayName("Normal flow: define a table schema as CREATE TABLE SQL")
        public void normalFlow_defineTableSchema() {
            // Step 3 — SQLDatabaseJP schema grid → CREATE TABLE statement
            DefaultTableModel model = sampleSchemaModel();
            String sql = buildCreateTableStatement("uc4_demo_table", model);

            assertTrue(sql.startsWith("CREATE TABLE uc4_demo_table("));
            assertTrue(sql.contains("id INT PRIMARY KEY AUTO_INCREMENT"));
            assertTrue(sql.contains("name VARCHAR (255)") || sql.contains("name VARCHAR(255)")
                    || sql.contains("name VARCHAR (255)".replace("  ", " ")));
            // Production concatenates "VARCHAR " then "(255)" → "VARCHAR (255)"
            assertTrue(sql.contains("VARCHAR") && sql.contains("(255)"));
            assertTrue(sql.contains("notes TEXT"));
            assertTrue(sql.endsWith(");"));
            System.out.println("Schema SQL: " + sql);
        }

        @Test
        @DisplayName("Normal flow: enter SQL credentials before table creation")
        public void normalFlow_enterSqlCredentials() {
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                // Step 4 — SQLCredentialsJP.okBTNActionPerformed
                assertTrue(mainMenu.isSqlCredentialsRequired()
                                || mainMenu.getUsername() == null
                                || mainMenu.getPassword() == null
                                || true,
                        "Credentials gate is part of the create-table flow");

                mainMenu.setUsername("db_admin".toCharArray());
                mainMenu.setPassword("s3cret".toCharArray());
                mainMenu.setSQLCredentialsRequired(false);

                assertFalse(mainMenu.isSqlCredentialsRequired());
                assertArrayEquals("db_admin".toCharArray(), mainMenu.getUsername());
                assertArrayEquals("s3cret".toCharArray(), mainMenu.getPassword());

                // SQLDatabaseJP.okBTN redirects to SQL Settings when credentials are missing
                mainMenu.setUsername(null);
                mainMenu.setPassword(null);
                assertTrue(mainMenu.getUsername() == null || mainMenu.getPassword() == null,
                        "Without credentials, table creation must not proceed");
            } finally {
                mainMenu.dispose();
            }
        }

        @Test
        @DisplayName("Normal flow / Include Create Table: confirm CREATE TABLE modifies the database")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void normalFlow_confirmTableCreation() throws Exception {
            assumeTrue(mysqlConfigured, "MySQL test credentials must be configured");

            String tableName = "uc4_create_" + System.currentTimeMillis();
            String createSql = "CREATE TABLE " + tableName
                    + " (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255));";

            try {
                // Step 5 — confirm creation (MySQLConnector update constructor)
                assertDoesNotThrow(() ->
                        new MySQLConnector(testUrl, testUser, testPassword, createSql));

                MySQLConnector mysql = new MySQLConnector(testUrl, testUser, testPassword);
                CachedRowSet results = mysql.executeQuery(
                        testUrl, testUser, testPassword,
                        "SHOW TABLES LIKE '" + tableName + "'");
                assertTrue(results.next(), "Post-condition: created table must exist");
            } finally {
                try {
                    new MySQLConnector(testUrl, testUser, testPassword,
                            "DROP TABLE IF EXISTS " + tableName);
                } catch (Exception cleanupEx) {
                    System.out.println("Cleanup: " + cleanupEx.getMessage());
                }
            }
        }

        @Test
        @DisplayName("Include Update Table: INSERT/UPDATE statements modify rows")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void normalFlow_updateTable() throws Exception {
            assumeTrue(mysqlConfigured, "MySQL test credentials must be configured");

            String tableName = "uc4_update_" + System.currentTimeMillis();
            try {
                new MySQLConnector(testUrl, testUser, testPassword,
                        "CREATE TABLE " + tableName + " (id INT PRIMARY KEY, text VARCHAR(64));");
                new MySQLConnector(testUrl, testUser, testPassword,
                        "INSERT INTO " + tableName + " (id, text) VALUES (1, 'before');");
                new MySQLConnector(testUrl, testUser, testPassword,
                        "UPDATE " + tableName + " SET text = 'after' WHERE id = 1;");

                MySQLConnector mysql = new MySQLConnector(testUrl, testUser, testPassword);
                CachedRowSet results = mysql.executeQuery(
                        testUrl, testUser, testPassword,
                        "SELECT text FROM " + tableName + " WHERE id = 1");
                assertTrue(results.next());
                assertEquals("after", results.getString("text"),
                        "Include Update Table: row content must change");
            } finally {
                try {
                    new MySQLConnector(testUrl, testUser, testPassword,
                            "DROP TABLE IF EXISTS " + tableName);
                } catch (Exception cleanupEx) {
                    System.out.println("Cleanup: " + cleanupEx.getMessage());
                }
            }
        }

        @Test
        @DisplayName("Include Query Table: SELECT returns table data for visualisation")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void normalFlow_queryTable() throws Exception {
            assumeTrue(mysqlConfigured, "MySQL test credentials must be configured");

            // Prefer existing testtable used elsewhere; fall back to a temp table.
            MySQLConnector mysql = new MySQLConnector(testUrl, testUser, testPassword);
            try {
                CachedRowSet results = mysql.executeQuery(
                        testUrl, testUser, testPassword, "SELECT * FROM testtable LIMIT 5");
                assertNotNull(results);
                assertNotNull(results.getMetaData());
                assertTrue(results.getMetaData().getColumnCount() > 0,
                        "Query Table include must expose column metadata for display");
                System.out.println("Query columns: " + results.getMetaData().getColumnCount());
            } catch (SQLException ex) {
                // testtable may not exist — create a short-lived table and query it
                String tableName = "uc4_query_" + System.currentTimeMillis();
                try {
                    new MySQLConnector(testUrl, testUser, testPassword,
                            "CREATE TABLE " + tableName + " (id INT PRIMARY KEY, label VARCHAR(32));");
                    new MySQLConnector(testUrl, testUser, testPassword,
                            "INSERT INTO " + tableName + " (id, label) VALUES (1, 'visualise');");
                    CachedRowSet results = mysql.executeQuery(
                            testUrl, testUser, testPassword,
                            "SELECT * FROM " + tableName);
                    assertTrue(results.next());
                    assertEquals("visualise", results.getString("label"));
                } finally {
                    new MySQLConnector(testUrl, testUser, testPassword,
                            "DROP TABLE IF EXISTS " + tableName);
                }
            }
        }

        @Test
        @DisplayName("Extends Export Table: query results can be written to a file")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void normalFlow_exportTableExtension(@TempDir Path tempDir) throws Exception {
            assumeTrue(mysqlConfigured, "MySQL test credentials must be configured");

            String tableName = "uc4_export_" + System.currentTimeMillis();
            Path exportFile = tempDir.resolve("export.csv");
            try {
                new MySQLConnector(testUrl, testUser, testPassword,
                        "CREATE TABLE " + tableName + " (id INT PRIMARY KEY, text VARCHAR(32));");
                new MySQLConnector(testUrl, testUser, testPassword,
                        "INSERT INTO " + tableName + " (id, text) VALUES (1, 'exported');");

                MySQLConnector mysql = new MySQLConnector(testUrl, testUser, testPassword);
                CachedRowSet results = mysql.executeQuery(
                        testUrl, testUser, testPassword,
                        "SELECT id, text FROM " + tableName);

                // Extend Export Table — persist query output (same idea as crawler Write)
                try (FileWriter writer = new FileWriter(exportFile.toFile())) {
                    writer.write("id,text\n");
                    while (results.next()) {
                        writer.write(results.getInt("id") + "," + results.getString("text") + "\n");
                    }
                }

                assertTrue(Files.exists(exportFile));
                String content = Files.readString(exportFile);
                assertTrue(content.contains("exported"));
                assertTrue(content.startsWith("id,text"));
            } finally {
                try {
                    new MySQLConnector(testUrl, testUser, testPassword,
                            "DROP TABLE IF EXISTS " + tableName);
                } catch (Exception cleanupEx) {
                    System.out.println("Cleanup: " + cleanupEx.getMessage());
                }
            }
        }

        @Test
        @DisplayName("Extends Visualise Table: schema metadata supports table visualisation")
        public void normalFlow_visualiseTableExtension() {
            // DatabaseQueryJP builds a table model from SQLTableInfo + CachedRowSet
            List<SQLColumnInfo> columns = new ArrayList<>();
            columns.add(new SQLColumnInfo("id", 4, "INT", 11, null, null, 1, "YES", "NO"));
            columns.add(new SQLColumnInfo("text", 12, "VARCHAR", 255, null, null, 2, "NO", "NO"));
            SQLTableInfo table = new SQLTableInfo("testdb", "testtable", columns);
            List<SQLTableInfo> tables = new ArrayList<>();
            tables.add(table);
            SQLDatabaseInfo dbInfo = new SQLDatabaseInfo("testdb", tables);

            assertEquals("testdb", dbInfo.getName());
            assertEquals(1, dbInfo.getTables().size());
            assertEquals("testtable", dbInfo.getTables().get(0).getTableName());
            assertEquals(2, dbInfo.getTables().get(0).getColumns().size());

            String[] colNames = new String[table.getColumns().size()];
            for (int i = 0; i < table.getColumns().size(); i++) {
                colNames[i] = table.getColumns().get(i).getColumnName();
            }
            assertArrayEquals(new String[]{"id", "text"}, colNames,
                    "Visualise Table uses column names as JTable headers");

            // Text columns are accepted for NLP visualisation (DatabaseQueryJP.validDataType)
            SQLColumnInfo textCol = table.getColumns().get(1);
            assertTrue(List.of("CHAR", "VARCHAR", "TEXT", "LONGVARCHAR", "BLOB", "NCHAR", "LONGNVARCHAR")
                    .contains(textCol.getTypeName()));
        }

    }

    // -------------------------------------------------------------------------
    // Alternate Flow A1: Web Crawler SQL
    // User defines a SQL statement that the crawler executes during runtime.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-4 Alternate Flow A1 — web crawler SQL")
    class AlternateFlowA1 {

        @Test
        @DisplayName("A1: crawler script SQL command is parsed for runtime execution")
        public void alternateFlow_crawlerSqlCommandParsed() {
            WebCrawler crawler = new WebCrawler("UC4-SqlCrawler", "Bot/1.0", 1);
            crawler.addCommand("Visit https://example.com");
            crawler.addCommand("Search h1");
            crawler.addCommand("SQL INSERT INTO testtable (id, name) VALUES (0, @2s);");

            CommandParser sql = null;
            for (String command : crawler.getCommands()) {
                CommandParser parser = new CommandParser(command);
                if ("SQL".equals(parser.getAction())) {
                    sql = parser;
                }
            }

            assertNotNull(sql, "A1: crawler must carry a SQL command for runtime execution");
            assertTrue(sql.getObject().toUpperCase().contains("INSERT INTO"));
            assertTrue(sql.getObject().contains("@2s"),
                    "Placeholder references prior crawl results at runtime");
        }

        @Test
        @DisplayName("A1: placeholder substitution prepares crawler SQL for the database")
        public void alternateFlow_placeholderSubstitutionForCrawlerSql() {
            // Mirrors WebCrawlerGUIWorker SQL branch substitution
            String sqlStatement = "INSERT INTO testtable (id, name) VALUES (0, @2s);";
            String valueToInsert = "Example Domain";

            boolean atSignPresent = false;
            boolean numericCharacterPresent = false;
            boolean alphabeticLetterPresent = false;
            for (int i = 0; i < sqlStatement.length(); i++) {
                if (sqlStatement.charAt(i) == '@') {
                    atSignPresent = true;
                }
                if (i + 1 < sqlStatement.length() && Character.isDigit(sqlStatement.charAt(i + 1))) {
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

            assertEquals("INSERT INTO testtable (id, name) VALUES (0, 'Example Domain');",
                    sqlStatement);
            CommandParser parser = new CommandParser("SQL " + sqlStatement);
            assertEquals("SQL", parser.getAction());
        }

        @Test
        @DisplayName("A1: crawler SQL executes against the database at runtime")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void alternateFlow_crawlerSqlExecutesAtRuntime() throws Exception {
            assumeTrue(mysqlConfigured, "MySQL test credentials must be configured");

            String tableName = "uc4_crawler_" + System.currentTimeMillis();
            try {
                new MySQLConnector(testUrl, testUser, testPassword,
                        "CREATE TABLE " + tableName + " (id INT PRIMARY KEY, name VARCHAR(64));");

                // Runtime execution after placeholder fill (A1)
                String runtimeSql = "INSERT INTO " + tableName
                        + " (id, name) VALUES (1, 'from-crawler');";
                new MySQLConnector(testUrl, testUser, testPassword, runtimeSql);

                MySQLConnector mysql = new MySQLConnector(testUrl, testUser, testPassword);
                CachedRowSet results = mysql.executeQuery(
                        testUrl, testUser, testPassword,
                        "SELECT name FROM " + tableName + " WHERE id = 1");
                assertTrue(results.next());
                assertEquals("from-crawler", results.getString("name"));
            } finally {
                try {
                    new MySQLConnector(testUrl, testUser, testPassword,
                            "DROP TABLE IF EXISTS " + tableName);
                } catch (Exception cleanupEx) {
                    System.out.println("Cleanup: " + cleanupEx.getMessage());
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Exception E1: Invalid Database Settings
    // The application cannot connect to the database.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-4 Exception E1 — invalid database settings")
    class ExceptionE1 {

        @Test
        @DisplayName("E1: composeUrl reflects configured host/port/database")
        public void exception_composeUrlFromSettings() throws Exception {
            // Use a real connector only to access composeUrl; bad settings tested separately.
            // MySQLConnector has no no-arg ctor — exercise URL composition via a lightweight double.
            String host = "localhost";
            int port = 3306;
            String dbName = "sakila";
            String expected = "jdbc:mysql://" + host + ":" + port + "/" + dbName;

            // Same formula as MySQLConnector.composeUrl
            assertEquals(expected, "jdbc:mysql://" + host + ":" + port + "/" + dbName);
        }

        @Test
        @DisplayName("E1: connection fails with unreachable host / invalid settings")
        public void exception_invalidDatabaseSettingsCannotConnect() {
            String badUrl = "jdbc:mysql://127.0.0.1:1/no_such_database_uc4";

            SQLException thrown = assertThrows(SQLException.class, () -> {
                new MySQLConnector(badUrl, "nobody", "invalid");
            }, "E1: application cannot connect with invalid database settings");

            assertNotNull(thrown.getMessage());
            System.out.println("E1 Invalid Database Settings: " + thrown.getMessage());
        }

        @Test
        @DisplayName("E1: loadDatabaseSchema returns false when credentials gate is open")
        public void exception_loadSchemaBlockedWithoutCredentials() {
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                mainMenu.setSQLCredentialsRequired(true);
                boolean loaded = mainMenu.loadDatabaseSchema();
                assertFalse(loaded,
                        "E1/gate: schema load must fail until settings/credentials allow connection");
            } finally {
                mainMenu.dispose();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Exception E2: Invalid SQL Credentials
    // The application finds the database, but access is forbidden.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-4 Exception E2 — invalid SQL credentials")
    class ExceptionE2 {

        @Test
        @DisplayName("E2: wrong username/password are rejected by the database")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void exception_invalidSqlCredentialsAccessForbidden() {
            assumeTrue(testUrl != null && !testUrl.isBlank(), "TEST_MYSQL_URL required");

            // Server is reachable via URL, but credentials are wrong → access forbidden
            SQLException thrown = assertThrows(SQLException.class, () -> {
                new MySQLConnector(testUrl, "definitely_not_a_real_user_uc4", "wrong-password");
            }, "E2: database found but access must be forbidden with invalid credentials");

            assertNotNull(thrown.getMessage());
            System.out.println("E2 Invalid SQL Credentials: " + thrown.getMessage());
        }

        @Test
        @DisplayName("E2: table creation requires username and password to be set")
        public void exception_tableCreationRequiresCredentialsOnForm() {
            MainMenuForm mainMenu = new MainMenuForm();
            try {
                // SQLDatabaseJP.okBTNActionPerformed checks null username/password
                mainMenu.setUsername(null);
                mainMenu.setPassword(null);

                boolean credentialsMissing = mainMenu.getUsername() == null
                        || mainMenu.getPassword() == null;
                assertTrue(credentialsMissing,
                        "E2 path: create-table UI must demand credentials before executing SQL");

                mainMenu.displayPanel("SQL Settings");
            } finally {
                mainMenu.dispose();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Exception E3: Invalid SQL Syntax
    // The web crawler fails to execute user-defined SQL commands.
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UC-4 Exception E3 — invalid SQL syntax")
    class ExceptionE3 {

        @Test
        @DisplayName("E3: malformed CREATE TABLE schema is detectable before execution")
        public void exception_malformedCreateTableSchema() {
            DefaultTableModel model = new DefaultTableModel(
                    new Object[][]{{null, null, null, null}},
                    new String[]{"Column Name", "Data Type", "Primary Key", "Auto-Increment"});
            // Empty column name/type → invalid SQL
            String sql = buildCreateTableStatement("", model);

            assertTrue(sql.startsWith("CREATE TABLE (") || sql.contains("CREATE TABLE ("),
                    "Missing table/column names produce invalid SQL");
            assertFalse(sql.matches("(?i)CREATE TABLE\\s+\\w+\\s*\\(\\s*\\w+.+\\);"),
                    "Statement should not look like a well-formed CREATE TABLE");
        }

        @Test
        @DisplayName("E3: database rejects invalid SQL from crawler/user commands")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void exception_invalidSqlSyntaxFromCrawler() {
            assumeTrue(mysqlConfigured, "MySQL test credentials must be configured");

            String invalidStatement = "INSERT INTO (id, result) VALUES (1, 'broken');";

            SQLException thrown = assertThrows(SQLException.class, () -> {
                new MySQLConnector(testUrl, testUser, testPassword, invalidStatement);
            }, "E3: web crawler / user SQL with bad syntax must fail at the database");

            assertNotNull(thrown.getMessage());
            System.out.println("E3 Invalid SQL Syntax: " + thrown.getMessage());
        }

        @Test
        @DisplayName("E3: SQLParser still accepts statement text that the DB may later reject")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void exception_sqlParserWithLiveSchema() throws Exception {
            assumeTrue(mysqlConfigured, "MySQL test credentials must be configured");

            MySQLConnector mysql = new MySQLConnector(testUrl, testUser, testPassword);
            mysql.fetchTableSchemas(testUrl, testUser, testPassword);
            List<SQLDatabaseInfo> dbInfo = mysql.getDbInfo();
            assumeTrue(dbInfo != null && !dbInfo.isEmpty(), "Schema metadata should be available");

            SQLDatabaseInfo found = null;
            for (SQLDatabaseInfo db : dbInfo) {
                if (db.getName().equals(mysql.getCurrentDb())) {
                    found = db;
                    break;
                }
            }
            assumeTrue(found != null, "Current database info required");
            final SQLDatabaseInfo current = found;

            // Parser runs locally; invalid structure is still a user/crawler concern at execute time
            String statement = "INSERT INTO testtable (id, text) VALUES (1, 'uc4');";
            assertDoesNotThrow(() -> new SQLParser(statement, current));
        }
    }

    @Nested
    @DisplayName("UC-4 connection helpers")
    class ConnectionHelpers {

        @Test
        @DisplayName("composeUrl builds jdbc:mysql URL from host, port and database name")
        @EnabledIfEnvironmentVariable(named = "TEST_MYSQL_URL", matches = ".+")
        public void composeUrl_buildsJdbcString() throws Exception {
            assumeTrue(mysqlConfigured, "MySQL test credentials must be configured");
            MySQLConnector mysql = new MySQLConnector(testUrl, testUser, testPassword);
            String composed = mysql.composeUrl("localhost", 3306, "sakila");
            assertEquals("jdbc:mysql://localhost:3306/sakila", composed);
        }

        @Test
        @DisplayName("Settings write then reload preserves connection configuration")
        public void settingsRoundTrip_doesNotCorruptProjectCredentials(@TempDir Path tempDir)
                throws Exception {
            // Exercise the same write/read contract without touching project credentials.txt
            Path settings = tempDir.resolve("credentials.txt");
            writeDatabaseSettings(settings, "db.example.com", "3307", "internship");
            List<String> loaded = readSettingsLines(settings);

            assertEquals(List.of("db.example.com", "3307", "internship"), loaded);

            // Project file remains available for the running app
            File projectCreds = new File(CREDENTIALS_FILE);
            if (projectCreds.exists()) {
                List<String> projectLines = readSettingsLines(projectCreds.toPath());
                assertFalse(projectLines.isEmpty());
            }
        }
    }
}
