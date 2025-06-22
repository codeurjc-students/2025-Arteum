package ui;

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
class LoginUITest {
	
	@LocalServerPort
	private int port;
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
		options.addArguments("--headless=new");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.setAcceptInsecureCerts(true);
		options.addArguments("--ignore-certificate-errors");
		driver = new ChromeDriver(options);
	}

	@Test
	void testUserLoginAndDashboard() {
	    // 1) Navigate to the login page
		driver.get("https://localhost:" + port + "/login");
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		// 2) Wait for the username field to be present and enter credentials
		WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
		emailField.sendKeys("test");
		
		WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
		passwordField.sendKeys("admin");

		// 3) Wait for the login button’s span element to be clickable and click it
		WebElement submitSpan = wait
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.primary-btn2.btn-hover span")));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitSpan);
	    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitSpan);

		// 4) Directly navigate to the dashboard profile page
		driver.get("https://localhost:" + port + "/dashboard-profile");

		// 5) Verify that the current URL contains 'dashboard'
		wait.until(ExpectedConditions.urlContains("dashboard-profile"));
	}

	@AfterAll
	static void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
