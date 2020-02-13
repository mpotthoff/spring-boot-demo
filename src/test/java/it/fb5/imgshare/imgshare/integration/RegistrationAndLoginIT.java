package it.fb5.imgshare.imgshare.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetAddress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class RegistrationAndLoginIT {

    private static WebDriver driver;
    private static String hostName;

    @LocalServerPort
    private int serverPort;
    private String serverName;

    @BeforeAll
    static void beforeAll() {
        var options = new ChromeOptions();

        options.addArguments(
                "--headless",
                "--disable-gpu",
                "--no-sandbox",
                "--window-size=1920,1080"
        );

        RegistrationAndLoginIT.driver = new ChromeDriver(options);
        RegistrationAndLoginIT.hostName =
                "http://" + InetAddress.getLoopbackAddress().getHostName() + ":";
    }

    @BeforeEach
    void setUp() {
        this.serverName = RegistrationAndLoginIT.hostName + this.serverPort;
    }

    @AfterAll
    static void afterAll() {
        var driver = RegistrationAndLoginIT.driver;

        driver.quit();
    }

    @Test
    @Order(1)
    void testRegistration() {
        var driver = RegistrationAndLoginIT.driver;
        driver.get(this.serverName + "/register");

        var username = driver.findElement(By.id("username"));
        var email = driver.findElement(By.id("email"));
        var password = driver.findElement(By.id("password"));
        var confirmPassword = driver.findElement(By.id("confirmPassword"));
        var signUp = driver.findElement(By.id("signUp"));

        username.sendKeys("test");
        email.sendKeys("test@test.de");
        password.sendKeys("test123");
        confirmPassword.sendKeys("test123");

        var signIn = driver.findElement(By.id("signIn"));
        assertEquals(this.serverName + "/login", signIn.getAttribute("href"));

        signUp.click();
        assertEquals(this.serverName + "/login", driver.getCurrentUrl());
    }

    @Test
    @Order(2)
    void testLogin() {
        var driver = RegistrationAndLoginIT.driver;
        driver.get(this.serverName + "/login");

        var username = driver.findElement(By.id("username"));
        var password = driver.findElement(By.id("password"));
        var signIn = driver.findElement(By.id("signIn"));

        username.sendKeys("test");
        password.sendKeys("test123");

        var signUp = driver.findElement(By.id("signUp"));
        assertEquals(this.serverName + "/register", signUp.getAttribute("href"));

        signIn.click();
        assertEquals(this.serverName + "/", driver.getCurrentUrl());
    }
}
