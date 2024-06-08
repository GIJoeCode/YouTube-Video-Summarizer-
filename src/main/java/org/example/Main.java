package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        String filePath = "C:\\Test Projects\\AWSProject\\How to use Cracking The Coding Interview Effectively.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            List<Section> sections = navigateSite(driver, wait);
            int sectionNumber = 1;
            for (Section section : sections) {
                writer.write("Section " + sectionNumber + " Summary:\n");

                List<String> individualSummaries = summarizeDescriptions(section.getDescriptions());
                String sectionSummary = String.join(" ", individualSummaries);
                writer.write(sectionSummary + "\n\n");

                // Convert the section summary to speech
                String outputFilePath = "output_section_" + sectionNumber + ".mp3";
                AWSTextToSpeech.convertTextToSpeech(sectionSummary, outputFilePath);

                sectionNumber++;
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }

    private static List<Section> navigateSite(WebDriver driver, WebDriverWait wait) {
        driver.get("https://www.summarize.tech/www.youtube.com/watch?v=yG0RhKFTonw");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section")));

        List<WebElement> webSections = driver.findElements(By.cssSelector("section"));
        List<Section> sections = new ArrayList<>();
        for (WebElement webSection : webSections) {
            try {
                List<String> descriptions = new ArrayList<>();

                WebElement list = webSection.findElement(By.tagName("ul"));
                List<WebElement> items = list.findElements(By.tagName("li"));
                for (WebElement item : items) {
                    String description = (String) ((JavascriptExecutor) driver).executeScript(
                            "return Array.from(arguments[0].childNodes).reduce((acc, node) => acc + (node.nodeType === 3 ? node.textContent.trim() : ''), '');",
                            item);
                    descriptions.add(description.trim());
                }
                sections.add(new Section("", "", descriptions));
            } catch (NoSuchElementException e) {
                System.out.println("A necessary element was not found in this section: " + e.getMessage());
            }
        }
        return sections;
    }

    private static List<String> summarizeDescriptions(List<String> descriptions) {
        List<String> summaries = new ArrayList<>();
        for (String description : descriptions) {
            try {
                String summary = ChatGPTSummarizer.summarize(description);
                summaries.add(summary);
            } catch (IOException e) {
                System.err.println("Failed to summarize description: " + e.getMessage());
                summaries.add("Error summarizing description.");
            }
        }
        return summaries;
    }
}
