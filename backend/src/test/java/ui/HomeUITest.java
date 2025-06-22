package ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HomeUITest {
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
	void testHomeAndBasics() {

		driver.get("https://localhost/");
		assertTrue(driver.getCurrentUrl().equals("https://localhost/"));

		driver.get("https://localhost/login");
		assertTrue(driver.getCurrentUrl().equals("https://localhost/login"));

		driver.get("https://localhost/register");
		assertTrue(driver.getCurrentUrl().equals("https://localhost/register"));

		driver.get("https://localhost/contact");
		assertTrue(driver.getCurrentUrl().equals("https://localhost/contact"));

		driver.get("https://localhost/faq");
		assertTrue(driver.getCurrentUrl().equals("https://localhost/faq"));

		driver.get("https://localhost/about");
		assertTrue(driver.getCurrentUrl().equals("https://localhost/about"));

		driver.get("https://localhost/terms-condition");
		assertTrue(driver.getCurrentUrl().equals("https://localhost/terms-condition"));

		driver.get("https://localhost/privacy-policy");
		assertTrue(driver.getCurrentUrl().equals("https://localhost/privacy-policy"));
	}

	@AfterAll
	static void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
