package com.mycompany.davidssimplecrawler;

import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-6 SearchProducts
 * Tests ProductScraper against the confirmed selectors for each retailer:
 *   Aldi:   div.product-tile / product-tile__name / base-price--product-tile span.digit
 *   Tesco:  h2...heading a  +  p...price__text  (paired by index)
 *   Lidl:   .product-grid-box / .product-grid-box__title / .ods-price__value
 *   SuperValu: [class*='ProductCardWrapper'] etc.
 *
 * @author david
 */
class SearchProductsTest {

    private ProductScraper scraper;
    private File csvFile;

    @BeforeEach
    void setUp() {
        scraper = new ProductScraper();
        csvFile = new File(scraper.getCsvFilePath());
    }

    @AfterEach
    void tearDown() {
        if (csvFile.exists()) csvFile.delete();
    }

    // normal flow

    @Test
    void normalFlow_buildSearchUrlReplacesQueryPlaceholder() {
        String url = scraper.buildSearchUrl("https://www.tesco.ie/groceries/en-IE/search?query={QUERY}", "brown bread");
        assertEquals("https://www.tesco.ie/groceries/en-IE/search?query=brown+bread", url);
    }

    @Test
    void normalFlow_aldiTileParsedWithBrandNameSizeAndJoinedPrice() {
        String html =
            "<div class='product-tile'>" +
            "  <div class='product-tile__brandname'><p>Roma</p></div>" +
            "  <div class='product-tile__name'><p>Tomatoes</p></div>" +
            "  <div class='product-tile__unit-of-measurement'><p>500g</p></div>" +
            "  <div class='base-price--product-tile'>" +
            "    <span class='digit'>€</span><span class='digit'>1</span>" +
            "    <span class='digit'>.</span><span class='digit'>2</span><span class='digit'>9</span>" +
            "  </div>" +
            "</div>";
        ArrayList<ProductScraper.ProductEntry> entries = scraper.scrapeProducts(html, "Aldi Ireland", "tomatoes");
        assertEquals(1, entries.size());
        assertEquals("Roma - Tomatoes (500g)", entries.get(0).productName);
        assertEquals("€1.29", entries.get(0).price);
    }

    @Test
    void normalFlow_tescoNamesAndPricesPairedByIndex() {
        String html =
            "<h2 class='online-components-product-tile-product-heading__heading'><a>Tesco Brown Bread 800g</a></h2>" +
            "<p class='online-components-product-tile-price__text'>€1.90</p>" +
            "<h2 class='online-components-product-tile-product-heading__heading'><a>Tesco Sourdough 400g</a></h2>" +
            "<p class='online-components-product-tile-price__text'>€2.50</p>";
        ArrayList<ProductScraper.ProductEntry> entries = scraper.scrapeProducts(html, "Tesco Ireland", "bread");
        assertEquals(2, entries.size());
        assertEquals("Tesco Brown Bread 800g", entries.get(0).productName);
        assertEquals("€1.90", entries.get(0).price);
    }

    @Test
    void normalFlow_lidlPriceValueGetsEuroSignPrefixed() {
        String html =
            "<div class='product-grid-box'>" +
            "  <div class='product-grid-box__title'>Lidl Wholemeal Bread</div>" +
            "  <div class='ods-price__value'>1.79</div>" +
            "</div>";
        ArrayList<ProductScraper.ProductEntry> entries = scraper.scrapeProducts(html, "Lidl Ireland", "bread");
        assertEquals(1, entries.size());
        assertEquals("€1.79", entries.get(0).price);
    }

    @Test
    void normalFlow_superValuTileParsed() {
        String html =
            "<div class='ProductCardWrapper'>" +
            "  <h3 class='ProductName'>SuperValu Milk 2L</h3>" +
            "  <span class='Price'>€2.99</span>" +
            "</div>";
        ArrayList<ProductScraper.ProductEntry> entries = scraper.scrapeProducts(html, "SuperValu", "milk");
        assertEquals(1, entries.size());
        assertEquals("SuperValu Milk 2L", entries.get(0).productName);
    }

    @Test
    void normalFlow_saveToCsvPersistsResultsWithHeader() throws Exception {
        String html =
            "<div class='product-tile'>" +
            "  <div class='product-tile__name'><p>Milk</p></div>" +
            "  <div class='base-price--product-tile'><span class='digit'>€</span><span class='digit'>1</span></div>" +
            "</div>";
        ArrayList<ProductScraper.ProductEntry> entries = scraper.scrapeProducts(html, "Aldi Ireland", "milk");
        scraper.saveToCsv(entries);

        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertEquals("Timestamp,Supermarket,Search Query,Product Name,Price", lines.get(0));
        assertEquals(2, lines.size());
    }

    // alternate flow

    @Test
    void alternateFlow_aldiUrlUsesQueryParamThatJsBrowserFetcherTypesIntoSearchBox() {
        // aldi.ie redirects url-based search to the homepage, so JsBrowserFetcher
        // extracts {QUERY} from this url and types it into the on-page search box instead
        String urlTemplate = ProductScraper.SUPERMARKETS[0][1];
        assertTrue(urlTemplate.contains("aldi.ie"));
        assertTrue(urlTemplate.contains("{QUERY}"));
    }

    // exceptions

    @Test
    void exception_noResultsWhenAldiSelectorsChanged() {
        String html = "<html><body><div class='some-other-layout'>no tiles here</div></body></html>";
        ArrayList<ProductScraper.ProductEntry> entries = scraper.scrapeProducts(html, "Aldi Ireland", "milk");
        assertTrue(entries.isEmpty());
    }

    @Test
    void exception_tescoRowSkippedWhenPriceMissing() {
        // more name headings than price paragraphs - only the paired one is kept
        String html =
            "<h2 class='online-components-product-tile-product-heading__heading'><a>Bread</a></h2>" +
            "<h2 class='online-components-product-tile-product-heading__heading'><a>Sourdough</a></h2>" +
            "<p class='online-components-product-tile-price__text'>€1.90</p>";
        ArrayList<ProductScraper.ProductEntry> entries = scraper.scrapeProducts(html, "Tesco Ireland", "bread");
        assertEquals(1, entries.size());
    }

    @Test
    void exception_emptyAldiNameIsSkipped() {
        String html =
            "<div class='product-tile'>" +
            "  <div class='base-price--product-tile'><span class='digit'>€</span><span class='digit'>1</span></div>" +
            "</div>"; // no product-tile__name element
        ArrayList<ProductScraper.ProductEntry> entries = scraper.scrapeProducts(html, "Aldi Ireland", "milk");
        assertTrue(entries.isEmpty());
    }

    // precondition

    @Test
    void precondition_fiveSupermarketsConfigured() {
        /
        assertEquals(5, ProductScraper.SUPERMARKETS.length);
    }

    @Test
    void precondition_csvFilePathIsAbsolute() {
        assertTrue(new File(scraper.getCsvFilePath()).isAbsolute());
    }
}
