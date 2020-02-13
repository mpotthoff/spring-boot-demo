package it.fb5.imgshare.imgshare.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class IndexIT {

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

        IndexIT.driver = new ChromeDriver(options);
        IndexIT.hostName = "http://" + InetAddress.getLoopbackAddress().getHostName() + ":";
    }

    @AfterAll
    static void afterAll() {
        var driver = IndexIT.driver;

        driver.quit();
    }

    @BeforeEach
    void setUp() {
        var driver = IndexIT.driver;
        this.serverName = IndexIT.hostName + this.serverPort;

        driver.get(serverName + "/");
    }

    @Test
    void testTitle() {
        var driver = IndexIT.driver;
        var title = driver.getTitle();

        assertEquals("imgshare", title);
    }

    @Test
    void testBrand() {
        var driver = IndexIT.driver;
        var brand = driver.findElement(By.className("navbar-brand"));
        var linkedUrl = brand.getAttribute("href");

        assertTrue(brand.isDisplayed());
        assertEquals("a", brand.getTagName());
        assertEquals("imgshare", brand.getText());
        assertEquals(this.serverName + "/", linkedUrl);
    }

    @Test
    void testSignIn() {
        var driver = IndexIT.driver;
        var signIn = driver.findElement(By.id("sign-in"));
        var linkedUrl = signIn.getAttribute("href");

        assertTrue(signIn.isDisplayed());
        assertEquals("a", signIn.getTagName());
        assertEquals("Sign in", signIn.getText());
        assertEquals("btn btn-outline-success", signIn.getAttribute("class"));
        assertEquals(this.serverName + "/login?redirect=/", linkedUrl);
    }

    @Test
    void testSortButtonGroup() {
        var driver = IndexIT.driver;
        var buttonGroup = driver.findElement(By.id("sortButtonGroup"));

        assertTrue(buttonGroup.isDisplayed());
        assertEquals("btn-group btn-group-toggle", buttonGroup.getAttribute("class"));
    }

    @Test
    void testSortNewButton() {
        var driver = IndexIT.driver;
        var reference = driver.findElement(By.id("sortNewButtonRef"));
        var button = driver.findElement(By.id("sortNewButton"));

        assertTrue(button.isDisplayed());
        assertEquals("input", button.getTagName());
        assertEquals("radio", button.getAttribute("type"));

        assertEquals("a", reference.getTagName());
        assertEquals("btn btn-secondary active", reference.getAttribute("class"));
        assertEquals(this.serverName + "/?orderBy=new", reference.getAttribute("href"));
    }

    @Test
    void testSortTopButton() {
        var driver = IndexIT.driver;
        var reference = driver.findElement(By.id("sortTopButtonRef"));
        var button = driver.findElement(By.id("sortTopButton"));

        assertTrue(button.isDisplayed());
        assertEquals("input", button.getTagName());
        assertEquals("radio", button.getAttribute("type"));

        assertEquals("a", reference.getTagName());
        assertEquals("btn btn-secondary", reference.getAttribute("class"));
        assertEquals(this.serverName + "/?orderBy=top", reference.getAttribute("href"));
    }
}
