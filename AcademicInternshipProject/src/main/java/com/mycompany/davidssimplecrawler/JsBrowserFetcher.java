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
     * Opens Chrome, navigates to the url, waits for a specific css selector
     * to appear in the DOM, then returns the fully rendered html.
     *
     * waitForSelector controls what element we wait for before grabbing html.
     * If null or empty, falls back to a fixed time wait.
     *
     * Runs without headless mode because some sites block headless Chrome.
     * A Chrome window will briefly appear and close.
     */
    public String fetchWithJs(String url, int waitSeconds) {
        // for pickapump pages wait for station list
        if (url.contains("pickapump")) {
            return fetchWithWaitForSelector(url, waitSeconds, "ul.no-bullets li h5");
        }
        // for aldi search pages wait for product tiles
        if (url.contains("aldi.ie")) {
            return fetchWithWaitForSelector(url, waitSeconds, "div.product-tile");
        }
        // for lidl search pages wait for product grid
        if (url.contains("lidl.ie")) {
            return fetchWithWaitForSelector(url, waitSeconds, "article, [class*='product-grid']");
        }
        // for tesco wait for product list
        if (url.contains("tesco.ie")) {
            return fetchWithWaitForSelector(url, waitSeconds, "[data-auto='product-tile'], li.product-list--list-item");
        }
        // default - just use a fixed wait
        return fetchWithWaitForSelector(url, waitSeconds, null);
    }

    /**
     * Core fetch method.
     * Opens Chrome, loads the url, optionally bypasses cookie consent,
     * waits for waitForSelector to appear, then returns the page source.
     */
    public String fetchWithWaitForSelector(String url, int waitSeconds, String waitForSelector) {
        WebDriver driver = null;

        try {
            ChromeOptions options = new ChromeOptions();
            // headless disabled - some sites detect and block headless chrome
            // options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--window-size=1280,800");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-extensions");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            driver = new ChromeDriver(options);

            // remove webdriver detection flag
            ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
            );

            // ── pickapump: inject consent cookies before loading ──────
            if (url.contains("pickapump")) {
                driver.get("https://pickapump.com");
                Thread.sleep(2000);
                driver.manage().addCookie(new Cookie(
                    "cookieconsent_status", "dismiss", "pickapump.com", "/", null));
                ((JavascriptExecutor) driver).executeScript(
                    "localStorage.setItem('cookieconsent_status', 'dismiss');" +
                    "localStorage.setItem('FCCDCF', '[1,1,1,[],[],[1,1,1,1,1,1,1,1,1,1],[],{}]');"
                );
                Thread.sleep(500);
            }

            // ── aldi: inject consent before loading search ────────────
            if (url.contains("aldi.ie")) {
                driver.get("https://www.aldi.ie");
                Thread.sleep(2000);
                // try to dismiss the onetrust cookie banner
                try {
                    WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(6));
                    WebElement acceptBtn = popupWait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.id("onetrust-accept-btn-handler")
                        )
                    );
                    acceptBtn.click();
                    System.out.println("Aldi: dismissed OneTrust cookie banner.");
                    Thread.sleep(1500);
                } catch (Exception e) {
                    System.out.println("Aldi: no cookie banner found or already dismissed.");
                }
            }

            // ── load the target url ───────────────────────────────────
            driver.get(url);
            System.out.println("Loaded: " + url);

            // ── wait for the target content to appear ─────────────────
            if (waitForSelector != null && !waitForSelector.isEmpty()) {
                System.out.println("Waiting for: " + waitForSelector);
                try {
                    WebDriverWait contentWait = new WebDriverWait(driver,
                        Duration.ofSeconds(waitSeconds));
                    contentWait.until(
                        ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(waitForSelector)
                        )
                    );
                    System.out.println("Content detected: " + waitForSelector);
                    // extra small wait for remaining content to settle
                    Thread.sleep(1500);
                } catch (Exception e) {
                    System.out.println("Timed out waiting for: " + waitForSelector);
                    // still grab what loaded
                    Thread.sleep(2000);
                }
            } else {
                // no selector specified - just wait the full time
                Thread.sleep(waitSeconds * 1000L);
            }

            // ── for pickapump: dismiss consent popup if still showing ──
            if (url.contains("pickapump")) {
                try {
                    List<WebElement> simpleButtons = driver.findElements(
                        By.cssSelector("a.cc-btn.cc-allow, a.cc-btn.cc-dismiss, .cc-allow")
                    );
                    if (!simpleButtons.isEmpty()) {
                        simpleButtons.get(0).click();
                        System.out.println("Pickapump: clicked simple cookie banner.");
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    System.out.println("Pickapump: no simple banner.");
                }
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

                // re-wait for station list after consent
                try {
                    WebDriverWait reWait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
                    reWait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("ul.no-bullets li h5")
                    ));
                    System.out.println("Pickapump: station list loaded.");
                    Thread.sleep(1500);
                } catch (Exception e) {
                    System.out.println("Pickapump: timed out waiting for stations.");
                }
            }

            return driver.getPageSource();

        } catch (InterruptedException e) {
            return "ERROR: Interrupted.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}