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
     * Opens Chrome, bypasses the pickapump consent wall using cookies and
     * javascript injection, then waits for the station list to load.
     *
     * Runs WITHOUT headless mode because pickapump detects and blocks
     * headless browsers. A Chrome window will briefly appear and close.
     */
    public String fetchWithJs(String url, int waitSeconds) {
        WebDriver driver = null;

        try {
            ChromeOptions options = new ChromeOptions();

            // do NOT use headless - pickapump blocks headless browsers
            // a chrome window will briefly appear on screen while scraping
            // options.addArguments("--headless"); // intentionally disabled

            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--window-size=1280,800");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-extensions");

            // make chrome look as much like a real browser as possible
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            driver = new ChromeDriver(options);

            // remove the webdriver property that sites use to detect automation
            ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
            );

            // step 1: visit pickapump home page first to establish the domain
            driver.get("https://pickapump.com");
            Thread.sleep(3000);

            // step 2: set consent cookies before loading the county page
            driver.manage().addCookie(new Cookie(
                "cookieconsent_status", "dismiss", "pickapump.com", "/", null));

            // step 3: set localStorage consent flags via javascript
            ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('cookieconsent_status', 'dismiss');" +
                "localStorage.setItem('FCCDCF', '[1,1,1,[],[],[1,1,1,1,1,1,1,1,1,1],[],{}]');" +
                "localStorage.setItem('eupubconsent-v2', 'accepted');"
            );

            Thread.sleep(1000);

            // step 4: navigate to the target county page
            driver.get(url);
            Thread.sleep(3000);

            // step 5: dismiss any consent popups that still appear
            // try the simple cookieconsent banner
            try {
                WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement allowBtn = popupWait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.cc-btn.cc-allow")
                    )
                );
                allowBtn.click();
                System.out.println("Dismissed simple cookie banner.");
                Thread.sleep(1500);
            } catch (Exception e) {
                System.out.println("No simple banner or already dismissed.");
            }

            // try the full gdpr consent wall - click accept all
            try {
                List<WebElement> acceptBtns = driver.findElements(
                    By.xpath("//button[normalize-space()='Accept all']")
                );
                if (acceptBtns.isEmpty()) {
                    acceptBtns = driver.findElements(
                        By.xpath("//a[normalize-space()='Accept all']")
                    );
                }
                if (!acceptBtns.isEmpty()) {
                    acceptBtns.get(0).click();
                    System.out.println("Clicked GDPR Accept all.");
                    Thread.sleep(2000);
                } else {
                    System.out.println("No GDPR wall visible.");
                }
            } catch (Exception e) {
                System.out.println("GDPR click failed: " + e.getMessage());
            }

            // step 6: wait for station data to appear
            System.out.println("Waiting for station list...");
            try {
                WebDriverWait contentWait = new WebDriverWait(driver,
                    Duration.ofSeconds(waitSeconds));
                contentWait.until(
                    ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("ul.no-bullets li h5")
                    )
                );
                System.out.println("Station list loaded.");
            } catch (Exception e) {
                System.out.println("Timed out waiting for stations.");
            }

            Thread.sleep(2000);

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
