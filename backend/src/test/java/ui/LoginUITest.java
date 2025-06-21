package ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginUITest {
	private static WebDriver driver;

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
	}

	@Test
	void testUserLoginAndDashboard() {
		// 1) Navigate to the login page
		driver.get("https://localhost/login");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		// 2) Wait for the username field to be present and enter credentials
		WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
		emailField.sendKeys("admin");
		driver.findElement(By.id("password")).sendKeys("admin");

		// 3) Wait for the login button’s span element to be clickable and click it
		WebElement submitSpan = wait
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.primary-btn2.btn-hover span")));
		submitSpan.click();

		// 4) Directly navigate to the dashboard profile page
		driver.get("https://localhost/dashboard-profile");

		// 5) Verify that the current URL contains 'dashboard'
		assertTrue(driver.getCurrentUrl().contains("dashboard"), "Should navigate to the dashboard after login");
	}

	@AfterAll
	static void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
