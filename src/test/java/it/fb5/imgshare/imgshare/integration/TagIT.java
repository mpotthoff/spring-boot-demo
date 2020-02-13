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
public class TagIT {

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

        TagIT.driver = new ChromeDriver(options);
        TagIT.hostName = "http://" + InetAddress.getLoopbackAddress().getHostName() + ":";
    }

    @AfterAll
    static void afterAll() {
        var driver = TagIT.driver;

        driver.quit();
    }

    @BeforeEach
    void setUp() {
        var driver = TagIT.driver;
        this.serverName = TagIT.hostName + this.serverPort;

        driver.get(serverName + "/t/beautiful");
    }

    @Test
    void testTitle() {
        var driver = TagIT.driver;
        var title = driver.getTitle();

        assertEquals("beautiful - imgshare", title);
    }

    @Test
    void testHeading() {
        var driver = TagIT.driver;
        var heading = driver.findElement(By.tagName("h1"));

        assertTrue(heading.isDisplayed());
        assertEquals("beautiful", heading.getText());
    }

    @Test
    void testSortButtonGroup() {
        var driver = TagIT.driver;
        var buttonGroup = driver.findElement(By.id("sortButtonGroup"));

        assertTrue(buttonGroup.isDisplayed());
        assertEquals("btn-group btn-group-toggle", buttonGroup.getAttribute("class"));
    }

    @Test
    void testSortNewButton() {
        var driver = TagIT.driver;
        var reference = driver.findElement(By.id("sortNewButtonRef"));
        var button = driver.findElement(By.id("sortNewButton"));

        assertTrue(button.isDisplayed());
        assertEquals("input", button.getTagName());
        assertEquals("radio", button.getAttribute("type"));

        assertEquals("a", reference.getTagName());
        assertEquals("btn btn-secondary active", reference.getAttribute("class"));
        assertEquals(this.serverName + "/t/beautiful?orderBy=new", reference.getAttribute("href"));
    }

    @Test
    void testSortTopButton() {
        var driver = TagIT.driver;
        var reference = driver.findElement(By.id("sortTopButtonRef"));
        var button = driver.findElement(By.id("sortTopButton"));

        assertTrue(button.isDisplayed());
        assertEquals("input", button.getTagName());
        assertEquals("radio", button.getAttribute("type"));

        assertEquals("a", reference.getTagName());
        assertEquals("btn btn-secondary", reference.getAttribute("class"));
        assertEquals(this.serverName + "/t/beautiful?orderBy=top", reference.getAttribute("href"));
    }
}
