package it.fb5.imgshare.imgshare.unit.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.ImageDto;
import it.fb5.imgshare.imgshare.entity.Image;
import it.fb5.imgshare.imgshare.entity.User;
import it.fb5.imgshare.imgshare.repository.ImageRepository;
import it.fb5.imgshare.imgshare.repository.UserRepository;
import it.fb5.imgshare.imgshare.service.ImageService;
import it.fb5.imgshare.imgshare.service.impl.ImageServiceImpl;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestInstance(Lifecycle.PER_CLASS)
public class ImageServiceTests {

    private User user;
    private SecurityPrincipal principal;

    private Image image;

    private ImageRepository imageRepository;
    private UserRepository userRepository;

    private ImageService imageService;

    @BeforeAll
    void beforeAll() {
        this.user = new User();
        this.user.setId(1);
        this.user.setUsername("test");
        this.user.setEmail("test@test.de");
        this.user.setPassword(new BCryptPasswordEncoder().encode("test"));
        this.user.setAlbums(Collections.emptyList());
        this.user.setFavorites(Collections.emptyList());
        this.principal = new SecurityPrincipal(this.user);

        this.image = new Image();
        this.image.setId(1);
        this.image.setUser(this.user);
        this.image.setUploadTimestamp(ZonedDateTime.now());
        this.image.setData(new byte[256]);

        this.user.setImages(Collections.singletonList(this.image));

        this.imageRepository = Mockito.mock(ImageRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);

        this.imageService = new ImageServiceImpl(this.imageRepository, this.userRepository);
    }

    @Test
    void testGetImageReturnsImage() {
        Mockito.when(this.imageRepository.findById(this.image.getId())).thenReturn(Optional.of(this.image));

        ImageDto imageDto = this.imageService.getImage(this.image.getId());

        assertNotNull(imageDto);
        assertEquals(imageDto.getId(), this.image.getId());
    }

    @Test
    void testGetImageReturnsNull() {
        Mockito.when(this.imageRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        ImageDto imageDto = this.imageService.getImage(123L);

        assertNull(imageDto);
    }

    @Test
    void testGetOwnImagesReturnsImages() {
        Mockito.when(this.userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(this.user));

        List<ImageDto> images = this.imageService.getOwnImages(0, this.principal);

        assertNotNull(images);
        assertEquals(images.size(), 1);
        assertEquals(images.get(0).getId(), this.image.getId());
    }

    @Test
    void testGetImageDataReturnsData() throws IOException {
        Mockito.when(this.imageRepository.findById(this.image.getId())).thenReturn(Optional.of(this.image));

        byte[] data = this.imageService.getImageData(this.image.getId(), 0);

        assertNotNull(data);
        assertArrayEquals(data, this.image.getData());
    }

    @Test
    void testGetImageDataReturnsNull() throws IOException {
        Mockito.when(this.imageRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        byte[] data = this.imageService.getImageData(123L, 0);

        assertNull(data);
    }

}
