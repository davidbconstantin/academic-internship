package com.mycompany.davidssimplecrawler;

import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-8 ManageCSVRecords
 * Tests the persistence layer used by MainWindow's delete/filter buttons:
 * CsvReader (reading + quoted-field parsing) and CsvWriter (deleteRows,
 * clearAllRows). The keep-index lists below mirror exactly what
 * MainWindow.onDeleteSelectedFuel / onDeleteFilteredFuel / onDeleteFuelByDateRange
 * compute before calling csvWriter.deleteRows(...).

 *
 * @author david
 */
class ManageCSVRecordsTest {

    private CsvReader reader;
    private CsvWriter writer;
    private File tempCsv;

    @BeforeEach
    void setUp() throws Exception {
        reader = new CsvReader();
        writer = new CsvWriter();
        tempCsv = File.createTempFile("saved_fuel_test", ".csv");
        Files.write(tempCsv.toPath(), List.of(
                "Timestamp,Station,Address,Fuel Type,Price",
                "\"01/07/2026 09:00:00\",\"Applegreen\",\"Naas Road, Dublin\",\"Diesel\",\"188.8c\"",
                "\"05/07/2026 09:00:00\",\"Circle K\",\"Cranley, Dublin\",\"Petrol\",\"178.8c\"",
                "\"10/07/2026 09:00:00\",\"Maxol\",\"Walkinstown, Dublin\",\"Diesel\",\"183.8c\""
        ));
    }

    @AfterEach
    void tearDown() {
        tempCsv.delete();
    }

    // normal flow

    @Test
    void normalFlow_readCsvSkipsHeaderRow() {
        ArrayList<String[]> rows = reader.readCsv(tempCsv.getPath());
        assertEquals(3, rows.size());
        assertEquals("Applegreen", rows.get(0)[1]);
    }

    @Test
    void normalFlow_quotedFieldWithEmbeddedCommaParsedAsOneColumn() {
        // the Address column contains a comma inside quotes - must not split into 2 fields
        ArrayList<String[]> rows = reader.readCsv(tempCsv.getPath());
        assertEquals(5, rows.get(0).length);
        assertEquals("Naas Road, Dublin", rows.get(0)[2]);
    }

    @Test
    void normalFlow_deleteSelectedRowKeepsOnlyRemainingIndexes() throws Exception {
        // simulates the user ctrl-clicking row 0 (Applegreen) then pressing "Delete selected" -
        // MainWindow builds keep = all model rows except the selected ones
        ArrayList<Integer> keep = new ArrayList<>(List.of(1, 2));
        writer.deleteRows(tempCsv.getPath(), keep);

        ArrayList<String[]> remaining = reader.readCsv(tempCsv.getPath());
        assertEquals(2, remaining.size());
        assertEquals("Circle K", remaining.get(0)[1]);
        assertEquals("Maxol", remaining.get(1)[1]);
    }

    @Test
    void normalFlow_tableReloadsWithCorrectRowCountAfterDeletion() throws Exception {
        writer.deleteRows(tempCsv.getPath(), new ArrayList<>(List.of(0)));
        assertEquals(1, reader.readCsv(tempCsv.getPath()).size());
    }

    // alternate flow

    @Test
    void alternateFlow_deleteFilteredKeepsRowsOutsideTheFilter() throws Exception {
        // simulates "Delete Filtered" when the Station filter is set to "Diesel" rows only -
        // rows 0 and 2 are filtered/visible, row 1 (Petrol) is kept
        ArrayList<Integer> keep = new ArrayList<>(List.of(1));
        writer.deleteRows(tempCsv.getPath(), keep);

        ArrayList<String[]> remaining = reader.readCsv(tempCsv.getPath());
        assertEquals(1, remaining.size());
        assertEquals("Petrol", remaining.get(0)[3]);
    }

    @Test
    void alternateFlow_deleteByDateRangeKeepsRowsOutsideThePeriod() throws Exception {
        // mirrors MainWindow.onDeleteFuelByDateRange: rows whose date falls
        // strictly outside [from, to] are kept, everything inside is deleted.
        // deleting 01/07/2026 - 06/07/2026 should remove rows 0 and 1, keep row 2 (10/07)
        ArrayList<Integer> keep = new ArrayList<>(List.of(2));
        writer.deleteRows(tempCsv.getPath(), keep);

        ArrayList<String[]> remaining = reader.readCsv(tempCsv.getPath());
        assertEquals(1, remaining.size());
        assertEquals("Maxol", remaining.get(0)[1]);
    }

    @Test
    void alternateFlow_deleteAllClearsRowsButKeepsHeader() throws Exception {
        writer.clearAllRows(tempCsv.getPath());

        List<String> lines = Files.readAllLines(tempCsv.toPath());
        assertEquals(1, lines.size());
        assertEquals("Timestamp,Station,Address,Fuel Type,Price", lines.get(0));
        assertTrue(reader.readCsv(tempCsv.getPath()).isEmpty());
    }

    // exceptions

    @Test
    void exception_deleteRowsOnMissingFileDoesNothing() throws Exception {
        File missing = new File(tempCsv.getParent(), "does_not_exist.csv");
        assertDoesNotThrow(() -> writer.deleteRows(missing.getPath(), new ArrayList<>(List.of(0))));
        assertFalse(missing.exists());
    }

    @Test
    void exception_clearAllRowsOnMissingFileDoesNothing() {
        File missing = new File(tempCsv.getParent(), "does_not_exist_2.csv");
        assertDoesNotThrow(() -> writer.clearAllRows(missing.getPath()));
        assertFalse(missing.exists());
    }

    @Test
    void exception_readCsvOnMissingFileReturnsEmptyList() {
        ArrayList<String[]> rows = reader.readCsv("nonexistent_saved_fuel.csv");
        assertTrue(rows.isEmpty());
    }

    @Test
    void exception_keepIndexBeyondRowCountIsIgnored() throws Exception {
        // defensive check - an out-of-range index should not throw or corrupt the file
        ArrayList<Integer> keep = new ArrayList<>(List.of(0, 99));
        assertDoesNotThrow(() -> writer.deleteRows(tempCsv.getPath(), keep));
        assertEquals(1, reader.readCsv(tempCsv.getPath()).size());
    }

    // precondition

    @Test
    void precondition_atLeastOneRecordSavedBeforeFilteringOrDeleting() {
        ArrayList<String[]> rows = reader.readCsv(tempCsv.getPath());
        assertFalse(rows.isEmpty());
    }
}
