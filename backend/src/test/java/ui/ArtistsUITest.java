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

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArtistsUITest {
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
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	@Test
	void testSearchAndClickArtist() {
		String baseUrl = System.getProperty("baseUrl", "https://localhost");

	    // 1) Navigate to the artists page
		driver.get(baseUrl + "/artists");

	    // 2) Wait until the search input field is present in the DOM, then enter the search query "Velazquez"
	    WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("wp-block-search__input-1")));
	    searchInput.sendKeys("Velazquez");

	    // 3) Wait until the search button is clickable, then click it to submit the search
	    WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit'].wp-block-search__button")));
	    searchBtn.click();

	    // 4) After clicking, wait for the search results to load (you can wait for presence of first result)
	    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.artist-card a")));

	    // 5) Now find the artist detail link again (fresh element reference)
	    WebElement detailLink = driver.findElement(By.cssSelector("div.artist-card a"));

	    // 6) Scroll the detail link element into view to ensure it is visible on the screen
	    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", detailLink);

	    // 7) Wait until the detail link element is clickable (visible and enabled)
	    wait.until(ExpectedConditions.elementToBeClickable(detailLink));

	    // 8) Click the detail link using JavaScript to avoid any potential overlay or click interception issues
	    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", detailLink);

	    // 9) Wait until the URL contains "/artists/" to confirm navigation to the artist details page
	    wait.until(ExpectedConditions.urlContains("/artists/"));

	    // 10) Assert that the current URL indeed contains "/artists/", indicating the user is on the details page
	    assertTrue(driver.getCurrentUrl().contains("/artists/"), "Should navigate to the artists details page");
	}

	@AfterAll
	static void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
