package ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = app.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtistsUITest {
	
	@LocalServerPort
    private int port;
	private static WebDriver driver;
	private static WebDriverWait wait;

	@BeforeAll
	void setupDriver() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	void setup() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.addArguments("--no-default-browser-check");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--disable-notifications");
		options.addArguments("--disable-extensions");
		options.addArguments("--headless=new");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.setAcceptInsecureCerts(true);
		options.addArguments("--ignore-certificate-errors");
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	}

	@Test
	void testSearchAndClickArtist() {
	    // 1) Navigate to the artists page
		driver.get("https://localhost:" + port + "/artists");

	    // 2) Wait until the search input field is present in the DOM, then enter the search query "Velazquez"
	    WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("wp-block-search__input-1")));
	    searchInput.sendKeys("da Vinci");

	    // 3) Wait until the search button is clickable, then click it to submit the search
	    WebElement searchBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
    	    By.cssSelector("button[type='submit'].wp-block-search__button")
    	));
    	((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchBtn);
    	((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchBtn);

    	driver.get("https://localhost:" + port + "/artists/1");
	    wait.until(ExpectedConditions.urlContains("/artists/1"));
	    assertTrue(driver.getCurrentUrl().contains("/artists/1"), "Should navigate to the artists details page");
	}

	@AfterAll
	static void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
