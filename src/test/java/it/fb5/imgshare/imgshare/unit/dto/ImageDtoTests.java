package it.fb5.imgshare.imgshare.unit.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.fb5.imgshare.imgshare.dto.ImageDto;
import it.fb5.imgshare.imgshare.entity.Image;
import it.fb5.imgshare.imgshare.entity.User;
import java.time.ZonedDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestInstance(Lifecycle.PER_CLASS)
public class ImageDtoTests {

    private User user;
    private Image image;

    @BeforeAll
    void beforeAll() {
        this.user = new User();
        this.user.setId(1);
        this.user.setUsername("test");
        this.user.setEmail("test@test.de");
        this.user.setPassword(new BCryptPasswordEncoder().encode("test"));
        this.user.setAlbums(Collections.emptyList());
        this.user.setFavorites(Collections.emptyList());

        this.image = new Image();
        this.image.setId(1);
        this.image.setUser(this.user);
        this.image.setUploadTimestamp(ZonedDateTime.now());
        this.image.setData(new byte[256]);

        this.user.setImages(Collections.singletonList(this.image));
    }

    @Test
    void testFromImage() {
        ImageDto imageDto = ImageDto.fromImage(this.image);

        assertNotNull(imageDto);
        assertEquals(imageDto.getId(), this.image.getId());
        assertEquals(imageDto.getUploadTimestamp(), this.image.getUploadTimestamp());
        assertNotNull(imageDto.getUser());
        assertEquals(imageDto.getUser().getId(), this.user.getId());
    }
}
