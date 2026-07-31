import com.mycompany.davidssimplecrawler.FuelScraper;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-5 ScrapeFuelPrices
 * Tests FuelScraper against real pickapump.com markup structure:
 *   ul.no-bullets > li > h5 (station name), div.fuel-price-item
 *   > span.fuel-type + div.price > span.digit (joined manually)
 *
 * @author david
 */
class ScrapeFuelPricesTest {

    private FuelScraper scraper;

    // one station, two fuel types (Diesel + Petrol), digits split into spans
    // exactly the way pickapump.com renders them
    private static final String SAMPLE_HTML =
        "<html><body><ul class='no-bullets'>" +
        "  <li>" +
        "    <h5>Applegreen Rathcoole</h5>" +
        "    <p>Naas Road, Rathcoole, Co. Dublin</p>" +
        "    <div class='fuel-price-item'>" +
        "      <span class='fuel-type'>Diesel</span>" +
        "      <div class='price'>" +
        "        <span class='digit'>1</span><span class='digit'>8</span>" +
        "        <span class='digit'>8</span><span class='digit'>.</span>" +
        "        <span class='digit'>8</span><span class='digit'>c</span>" +
        "      </div>" +
        "    </div>" +
        "    <div class='fuel-price-item'>" +
        "      <span class='fuel-type'>Petrol</span>" +
        "      <div class='price'>" +
        "        <span class='digit'>1</span><span class='digit'>8</span>" +
        "        <span class='digit'>1</span><span class='digit'>.</span>" +
        "        <span class='digit'>8</span><span class='digit'>c</span>" +
        "      </div>" +
        "    </div>" +
        "  </li>" +
        "  <li class='listing-inline-ad'></li>" + // ad slot, no h5 - must be skipped
        "</ul></body></html>";

    private static final String EMPTY_HTML = "<html><body></body></html>";

    private File csvFile;

    @BeforeEach
    void setUp() {
        scraper = new FuelScraper();
        csvFile = new File(scraper.getCsvFilePath());
    }

    @AfterEach
    void tearDown() {
        // clean up any csv written into the project working directory during the test
        if (csvFile.exists()) csvFile.delete();
    }

    // normal flow

    @Test
    void normalFlow_stationNameAndAddressParsed() {
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(SAMPLE_HTML);
        assertFalse(entries.isEmpty());
        assertEquals("Applegreen Rathcoole", entries.get(0).stationName);
        assertTrue(entries.get(0).address.contains("Rathcoole"));
    }

    @Test
    void normalFlow_bothFuelTypesParsedPerStation() {
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(SAMPLE_HTML);
        assertEquals(2, entries.size());
        assertEquals("Diesel", entries.get(0).fuelType);
        assertEquals("Petrol", entries.get(1).fuelType);
    }

    @Test
    void normalFlow_priceDigitSpansJoinedIntoSingleString() {
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(SAMPLE_HTML);
        assertEquals("188.8c", entries.get(0).price);
        assertEquals("181.8c", entries.get(1).price);
    }

    @Test
    void normalFlow_everyEntryStampedWithSameTimestamp() {
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(SAMPLE_HTML);
        assertEquals(entries.get(0).timestamp, entries.get(1).timestamp);
        assertFalse(entries.get(0).timestamp.isEmpty());
    }

    @Test
    void normalFlow_pricesWrittenToCsvWithHeader() throws Exception {
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(SAMPLE_HTML);
        scraper.saveToCsv(entries);

        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertEquals("Timestamp,Station,Address,Fuel Type,Price", lines.get(0));
        assertEquals(3, lines.size()); // header + 2 data rows
    }

    @Test
    void normalFlow_secondScrapeAppendsWithoutDuplicatingHeader() throws Exception {
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(SAMPLE_HTML);
        scraper.saveToCsv(entries);
        scraper.saveToCsv(entries); // scrape again, e.g. a second run

        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertEquals(5, lines.size()); // header + 2 + 2
        assertEquals(1, lines.stream().filter(l -> l.startsWith("Timestamp,")).count());
    }

    // alternate flow

    @Test
    void alternateFlow_adSlotsWithoutH5AreSkipped() {
        // SAMPLE_HTML includes a <li class="listing-inline-ad"> with no h5
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(SAMPLE_HTML);
        // only the real station's two fuel rows should appear, ad slot contributes nothing
        assertEquals(2, entries.size());
    }

    // exceptions

    @Test
    void exception_noPricesFoundReturnsEmptyList() {
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(EMPTY_HTML);
        assertTrue(entries.isEmpty());
    }

    @Test
    void exception_missingFuelTypeRowIsSkipped() {
        String html = "<html><body><ul class='no-bullets'><li>" +
                "<h5>Circle K Cranley</h5>" +
                "<div class='fuel-price-item'>" +
                "  <div class='price'><span class='digit'>1</span><span class='digit'>7</span>" +
                "  <span class='digit'>8</span><span class='digit'>.</span><span class='digit'>8</span></div>" +
                "</div>" + // no span.fuel-type present
                "</li></ul></body></html>";
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(html);
        assertTrue(entries.isEmpty());
    }

    @Test
    void exception_missingPriceDivIsSkipped() {
        String html = "<html><body><ul class='no-bullets'><li>" +
                "<h5>Circle K Cranley</h5>" +
                "<div class='fuel-price-item'><span class='fuel-type'>Diesel</span></div>" +
                "</li></ul></body></html>";
        ArrayList<FuelScraper.FuelEntry> entries = scraper.scrapePrices(html);
        assertTrue(entries.isEmpty());
    }

    // precondition

    @Test
    void precondition_csvFilePathIsAbsolute() {
        assertTrue(new File(scraper.getCsvFilePath()).isAbsolute());
    }
}