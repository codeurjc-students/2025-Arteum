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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = app.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeUITest {

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
	void testHomeAndBasics() {
		String baseurl = "https://localhost:" + port;

		driver.get(baseurl + "/");
		assertTrue(driver.getCurrentUrl().equals(baseurl + "/"));

		driver.get(baseurl + "/login");
		assertTrue(driver.getCurrentUrl().equals(baseurl + "/login"));

		driver.get(baseurl + "/register");
		assertTrue(driver.getCurrentUrl().equals(baseurl + "/register"));

		driver.get(baseurl + "/contact");
		assertTrue(driver.getCurrentUrl().equals(baseurl + "/contact"));

		driver.get(baseurl + "/faq");
		assertTrue(driver.getCurrentUrl().equals(baseurl + "/faq"));

		driver.get(baseurl + "/about");
		assertTrue(driver.getCurrentUrl().equals(baseurl + "/about"));

		driver.get(baseurl + "/terms-condition");
		assertTrue(driver.getCurrentUrl().equals(baseurl + "/terms-condition"));

		driver.get(baseurl + "/privacy-policy");
		assertTrue(driver.getCurrentUrl().equals(baseurl + "/privacy-policy"));
	}

	@AfterAll
	static void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
