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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = app.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterUITest {
	
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
	void testUserRegisterAndRedirect() {
	    // 1) Navigate to the register page
		driver.get("https://localhost:" + port + "/register");
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		WebElement userField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
		userField.sendKeys("testUser" + System.currentTimeMillis());
		driver.findElement(By.id("email")).sendKeys("testUser" + System.currentTimeMillis() + "@arteum.com");
		driver.findElement(By.id("password")).sendKeys("qwerty123456789");
		driver.findElement(By.id("password1")).sendKeys("qwerty123456789");
		driver.findElement(By.id("location")).sendKeys("Madrid");
		driver.findElement(By.id("biography")).sendKeys("Usuario en pruebas!");

		WebElement submitSpan = wait
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.primary-btn2.btn-hover span")));
		submitSpan.click();

		assertTrue(driver.getCurrentUrl().contains("login"));
	}

	@AfterAll
	static void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
