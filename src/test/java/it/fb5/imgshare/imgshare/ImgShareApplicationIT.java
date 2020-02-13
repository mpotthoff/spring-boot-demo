package it.fb5.imgshare.imgshare;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ImgShareApplicationIT {

    @Test
    void contextLoads() {
    }

}
