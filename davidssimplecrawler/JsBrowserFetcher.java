/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.davidssimplecrawler;

/**
 *
 * @author david
 */
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class JsBrowserFetcher {

    /**
     * Opens Chrome, loads the url, waits for content, returns rendered html.
     * For Aldi the url contains the search query as a parameter we extract and type.
     * For all other sites we load the url directly and wait.
     */
    public String fetchWithJs(String url, int waitSeconds) {
        if (url.contains("pickapump")) {
            return scrapePickapump(url, waitSeconds);
        }
        if (url.contains("aldi.ie")) {
            // extract the query from the url parameter e.g. ?q=olive+oil -> "olive oil"
            String query = extractAldiQuery(url);
            return scrapeAldi(query, waitSeconds);
        }
        if (url.contains("tesco.ie")) {
            return scrapeTesco(url, waitSeconds);
        }
        if (url.contains("lidl.ie")) {
            return scrapeLidl(url, waitSeconds);
        }
        // dunnes and supervalu - generic timed wait
        return scrapeGeneric(url, waitSeconds);
    }

    /**
     * Pulls the search query out of an aldi url.
     * e.g. https://www.aldi.ie/search?q=olive+oil -> "olive oil"
     * Falls back to empty string if the url doesnt have a query param.
     */
    private String extractAldiQuery(String url) {
        if (url.contains("?q=")) {
            String query = url.substring(url.indexOf("?q=") + 3);
            if (query.contains("&")) query = query.substring(0, query.indexOf("&"));
            return query.replace("+", " ").replace("%20", " ").trim();
        }
        // if the url is already a category url just return empty - not needed
        return "";
    }

    // ── shared chrome setup ──────────────────────────────────────────

    private WebDriver makeDriver() {
        ChromeOptions options = new ChromeOptions();
        // headless disabled - sites detect and block headless chrome
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1280,800");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        ((JavascriptExecutor) driver).executeScript(
            "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
        );
        return driver;
    }

    // ── aldi: type into search box ───────────────────────────────────

    /**
     * Scrapes aldi.ie by actually typing the search query into the search box.
     * This is required because aldi does not support url-based search queries.
     *
     * Steps:
     *   1. Load aldi.ie homepage
     *   2. Dismiss the OneTrust cookie banner
     *   3. Click the search icon to open the search box
     *   4. Type the query and press Enter
     *   5. Wait for product tiles to appear
     *   6. Grab and return the html
     */
    private String scrapeAldi(String query, int waitSeconds) {
        WebDriver driver = null;

        try {
            driver = makeDriver();

            // load the aldi homepage
            driver.get("https://www.aldi.ie");
            System.out.println("Aldi: loaded homepage");
            Thread.sleep(3000);

            // dismiss the onetrust cookie banner
            try {
                WebDriverWait bannerWait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement acceptBtn = bannerWait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.id("onetrust-accept-btn-handler")
                    )
                );
                acceptBtn.click();
                System.out.println("Aldi: dismissed OneTrust banner.");
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("Aldi: no cookie banner.");
            }

            // find and click the search icon/button to open the search box
            // aldi uses a button or input with aria-label containing "search"
            try {
                WebElement searchBtn = driver.findElement(
                    By.cssSelector("button[aria-label*='Search'], button[aria-label*='search'], " +
                                   ".n-header__search-trigger, [class*='search-trigger'], " +
                                   "[class*='searchTrigger']")
                );
                searchBtn.click();
                System.out.println("Aldi: clicked search trigger.");
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Aldi: no search trigger found, trying input directly.");
            }

            // find the search input and type the query
            WebElement searchInput = null;
            try {
                WebDriverWait inputWait = new WebDriverWait(driver, Duration.ofSeconds(8));
                searchInput = inputWait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.cssSelector("input[type='search'], input[placeholder*='Search'], " +
                                       "input[aria-label*='Search'], input[aria-label*='search'], " +
                                       ".n-header__search-input, [class*='search-input'] input, " +
                                       "[class*='searchInput']")
                    )
                );
                searchInput.clear();
                searchInput.sendKeys(query);
                System.out.println("Aldi: typed query '" + query + "'");
                Thread.sleep(500);
                searchInput.sendKeys(Keys.ENTER);
                System.out.println("Aldi: pressed Enter.");
            } catch (Exception e) {
                System.out.println("Aldi: could not find search input: " + e.getMessage());
                return "ERROR: Could not find Aldi search box. The page structure may have changed.";
            }

            // wait for the search results page to load product tiles
            System.out.println("Aldi: waiting for product tiles...");
            try {
                WebDriverWait contentWait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
                contentWait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.product-tile"))
                );
                System.out.println("Aldi: product tiles detected.");
            } catch (Exception e) {
                System.out.println("Aldi: timed out waiting for product tiles.");
            }

            // scroll to trigger lazy loading of all products
            ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);"
            );
            Thread.sleep(1500);
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            Thread.sleep(1000);

            String html = driver.getPageSource();
            System.out.println("Aldi: html length=" + html.length() +
                               " title=" + driver.getTitle());
            return html;

        } catch (InterruptedException e) {
            return "ERROR: Interrupted.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ── tesco ────────────────────────────────────────────────────────

    /**
     * Scrapes tesco.ie search results.
     * Confirmed selector: h2.online-components-product-tile-product-heading__heading
     */
    private String scrapeTesco(String url, int waitSeconds) {
        WebDriver driver = null;

        try {
            driver = makeDriver();
            driver.get(url);
            System.out.println("Tesco: loaded " + url);

            // wait for product headings
            try {
                WebDriverWait contentWait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
                contentWait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(
                        "h2.online-components-product-tile-product-heading__heading"
                    ))
                );
                System.out.println("Tesco: product headings detected.");
            } catch (Exception e) {
                System.out.println("Tesco: timed out waiting for products.");
            }

            // scroll to load lazy content
            ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);"
            );
            Thread.sleep(1500);
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            Thread.sleep(1000);

            String html = driver.getPageSource();
            System.out.println("Tesco: html length=" + html.length() +
                               " title=" + driver.getTitle());
            return html;

        } catch (InterruptedException e) {
            return "ERROR: Interrupted.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ── lidl ─────────────────────────────────────────────────────────

    /**
     * Scrapes lidl.ie search results.
     * Confirmed selector from real saved html: .product-grid-box
     */
    private String scrapeLidl(String url, int waitSeconds) {
        WebDriver driver = null;

        try {
            driver = makeDriver();
            driver.get(url);
            System.out.println("Lidl: loaded " + url);

            // wait for product grid boxes
            try {
                WebDriverWait contentWait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
                contentWait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".product-grid-box")
                    )
                );
                System.out.println("Lidl: product grid detected.");
            } catch (Exception e) {
                System.out.println("Lidl: timed out waiting for products.");
            }

            Thread.sleep(2000);
            String html = driver.getPageSource();
            System.out.println("Lidl: html length=" + html.length() +
                               " title=" + driver.getTitle());
            return html;

        } catch (InterruptedException e) {
            return "ERROR: Interrupted.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ── pickapump ────────────────────────────────────────────────────

    private String scrapePickapump(String url, int waitSeconds) {
        WebDriver driver = null;

        try {
            driver = makeDriver();

            driver.get("https://pickapump.com");
            Thread.sleep(2000);

            driver.manage().addCookie(new Cookie(
                "cookieconsent_status", "dismiss", "pickapump.com", "/", null));
            ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('cookieconsent_status', 'dismiss');" +
                "localStorage.setItem('FCCDCF', '[1,1,1,[],[],[1,1,1,1,1,1,1,1,1,1],[],{}]');"
            );
            Thread.sleep(500);

            driver.get(url);
            Thread.sleep(3000);

            // dismiss simple cookie banner
            try {
                List<WebElement> simpleButtons = driver.findElements(
                    By.cssSelector("a.cc-btn.cc-allow, a.cc-btn.cc-dismiss, .cc-allow")
                );
                if (!simpleButtons.isEmpty()) {
                    simpleButtons.get(0).click();
                    System.out.println("Pickapump: clicked simple cookie banner.");
                    Thread.sleep(1500);
                }
            } catch (Exception e) {
                System.out.println("Pickapump: no simple banner.");
            }

            // dismiss gdpr wall
            try {
                List<WebElement> gdprButtons = driver.findElements(
                    By.xpath("//*[contains(text(),'Accept all') and not(contains(@class,'vendor'))]")
                );
                if (!gdprButtons.isEmpty()) {
                    gdprButtons.get(0).click();
                    System.out.println("Pickapump: clicked GDPR accept all.");
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                System.out.println("Pickapump: no GDPR wall.");
            }

            // wait for station list
            try {
                WebDriverWait contentWait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
                contentWait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("ul.no-bullets li h5")
                    )
                );
                System.out.println("Pickapump: station list loaded.");
            } catch (Exception e) {
                System.out.println("Pickapump: timed out waiting for stations.");
            }

            Thread.sleep(2000);
            return driver.getPageSource();

        } catch (InterruptedException e) {
            return "ERROR: Interrupted.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ── generic fallback ─────────────────────────────────────────────

    private String scrapeGeneric(String url, int waitSeconds) {
        WebDriver driver = null;

        try {
            driver = makeDriver();
            driver.get(url);
            System.out.println("Generic: loaded " + url);
            Thread.sleep(waitSeconds * 1000L);
            return driver.getPageSource();

        } catch (InterruptedException e) {
            return "ERROR: Interrupted.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        } finally {
            if (driver != null) driver.quit();
        }
    }
}