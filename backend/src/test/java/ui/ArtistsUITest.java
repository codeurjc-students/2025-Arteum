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
		options.setAcceptInsecureCerts(true);
		options.addArguments("--ignore-certificate-errors");
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	@Test
	void testSearchAndClickArtist() {
		// 1) Navigate to the artists page
		driver.get("https://localhost/artists");

		// 2) Wait for the search input to be present and enter a query
		WebElement searchInput = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.id("wp-block-search__input-1")));
		searchInput.sendKeys("Velazquez");

		// 3) Wait for the search button to be clickable and click it
		WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(
				By.cssSelector("button[type='submit'].wp-block-search__button")));
		searchBtn.click();

		// 4) Wait for the first artist “Details” link to become clickable
		WebElement detailLink = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.artist-card a")));

		// 5) Click the “Details” link via JavaScript to avoid any overlay issues
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", detailLink);

		// 6) Wait until the URL contains the artist detail path and then verify it
		wait.until(ExpectedConditions.urlContains("/artists/"));
		assertTrue(driver.getCurrentUrl().contains("/artists/"), "Should navigate to the artists details page");
	}

	@AfterAll
	static void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
