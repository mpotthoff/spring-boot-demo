package it.fb5.imgshare.imgshare.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetAddress;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AlbumIT {

    private static WebDriver driver;
    private static String hostName;

    @LocalServerPort
    private int serverPort;

    @BeforeAll
    static void beforeAll() {
        var options = new ChromeOptions();

        options.addArguments(
                "--headless",
                "--disable-gpu",
                "--no-sandbox",
                "--window-size=1920,1080"
        );

        AlbumIT.driver = new ChromeDriver(options);
        AlbumIT.hostName = "http://" + InetAddress.getLoopbackAddress().getHostName() + ":";
    }

    @AfterAll
    static void afterAll() {
        var driver = AlbumIT.driver;

        driver.quit();
    }

    @BeforeEach
    void setUp() {
        var driver = AlbumIT.driver;
        var serverName = AlbumIT.hostName + this.serverPort;

        driver.get(serverName + "/a/1");
    }

    @Test
    void testTitle() {
        var driver = AlbumIT.driver;
        var title = driver.getTitle();

        assertEquals("Not Found - imgshare", title);
    }
}
