package it.fb5.imgshare.imgshare.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.AlbumCreationDto;
import it.fb5.imgshare.imgshare.dto.AlbumImageDto;
import it.fb5.imgshare.imgshare.entity.Album;
import it.fb5.imgshare.imgshare.entity.Album.Visibility;
import it.fb5.imgshare.imgshare.entity.AlbumImage;
import it.fb5.imgshare.imgshare.entity.Image;
import it.fb5.imgshare.imgshare.entity.User;
import it.fb5.imgshare.imgshare.exception.ValidationException;
import it.fb5.imgshare.imgshare.repository.AlbumRepository;
import it.fb5.imgshare.imgshare.repository.UserRepository;
import it.fb5.imgshare.imgshare.service.AlbumService;
import it.fb5.imgshare.imgshare.service.impl.AlbumServiceImpl;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestInstance(Lifecycle.PER_CLASS)
public class AlbumServiceTests {

    private SecurityPrincipal principal;
    private Album expectedAlbum;
    private AlbumService albumService;

    @BeforeAll
    void beforeAll() {
        var userRepository = Mockito.mock(UserRepository.class);
        var albumRepository = Mockito.mock(AlbumRepository.class);
        this.albumService = new AlbumServiceImpl(
                albumRepository,
                userRepository
        );

        var user = new User();
        user.setUsername("test");
        user.setPassword(new BCryptPasswordEncoder().encode("test"));
        user.setFavorites(Collections.emptyList());
        this.principal = new SecurityPrincipal(user);

        var images = new Image[3];

        for (var i = 0; i < images.length; i++) {
            images[i] = new Image();
            images[i].setId(i + 1);
            images[i].setUser(user);
        }

        user.setId(1);
        user.setEmail("admin@imgshare.fb5.it");
        user.setUsername("admin");
        user.setPassword(new BCryptPasswordEncoder().encode("admin"));
        user.setImages(Arrays.asList(images));

        Mockito.when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));

        var albumImages = new AlbumImage[3];

        for (var i = 0; i < albumImages.length; i++) {
            albumImages[i] = new AlbumImage();
            albumImages[i].setImage(images[i]);
        }

        this.expectedAlbum = new Album();

        this.expectedAlbum.setId(1);
        this.expectedAlbum.setTitle("Test album");
        this.expectedAlbum.setUser(user);
        this.expectedAlbum.setCoverImage(images[1]);
        this.expectedAlbum.setImages(Arrays.asList(albumImages));
        this.expectedAlbum.setVisibility(Visibility.PUBLIC);
        this.expectedAlbum.setTags(Arrays.asList("nice", "nice2"));

        Mockito.when(albumRepository.save(Mockito.any(Album.class)))
                .thenAnswer((Answer<Album>) invocation -> {
                    Album album = invocation.getArgument(0);
                    album.setId(1);
                    album.setVotes(Collections.emptyList());
                    album.setComments(Collections.emptyList());
                    return album;
                });
    }

    @Test
    void testValidDto() throws ValidationException {
        var expectedAlbum = this.expectedAlbum;

        expectedAlbum.setDescription("Test description");

        var albumImageDtoArray = new AlbumImageDto[3];

        for (var index = 0; index < albumImageDtoArray.length; index++) {
            albumImageDtoArray[index] = new AlbumImageDto();
            albumImageDtoArray[index].setId(index + 1);
        }

        var listAlbums = Arrays.asList(albumImageDtoArray);

        var albumDto = new AlbumCreationDto();

        albumDto.setTitle("Test album");
        albumDto.setDescription("Test description");
        albumDto.setImages(listAlbums);
        albumDto.setCoverImage(2);
        albumDto.setVisibility(Visibility.PUBLIC.ordinal());
        albumDto.setTags("nice,nice2");

        var created = this.albumService.createAlbum(albumDto, this.principal);

        assertNotNull(created);
        assertEquals(expectedAlbum.getId(), created.getId());
        assertEquals(expectedAlbum.getTitle(), created.getTitle());
        assertEquals(expectedAlbum.getDescription(), created.getDescription());
        assertEquals(expectedAlbum.getCoverImage().getId(), created.getCoverImageId());
        assertEquals(ZonedDateTime.class, created.getCreationTimestamp().getClass());
        assertNull(created.getEditTimestamp());
        assertEquals(expectedAlbum.getVisibility(), created.getVisibility());
        assertEquals(expectedAlbum.getTags(), created.getTags());
    }

    @Test
    void testDescriptionIsNull()
            throws ValidationException {
        var expectedAlbum = this.expectedAlbum;

        expectedAlbum.setDescription(null);

        var albumImageDtoArray = new AlbumImageDto[3];

        for (var index = 0; index < albumImageDtoArray.length; index++) {
            albumImageDtoArray[index] = new AlbumImageDto();
            albumImageDtoArray[index].setId(index + 1);
        }

        var listAlbums = Arrays.asList(albumImageDtoArray);

        var albumDto = new AlbumCreationDto();

        albumDto.setTitle("Test album");
        albumDto.setDescription(null);
        albumDto.setImages(listAlbums);
        albumDto.setCoverImage(2);
        albumDto.setVisibility(Visibility.PUBLIC.ordinal());
        albumDto.setTags("nice,nice2");

        var created = this.albumService.createAlbum(albumDto, this.principal);

        assertNotNull(created);
        assertEquals(expectedAlbum.getId(), created.getId());
        assertEquals(expectedAlbum.getTitle(), created.getTitle());
        assertNull(created.getDescription());
        assertEquals(expectedAlbum.getCoverImage().getId(), created.getCoverImageId());
        assertEquals(ZonedDateTime.class, created.getCreationTimestamp().getClass());
        assertNull(created.getEditTimestamp());
        assertEquals(expectedAlbum.getVisibility(), created.getVisibility());
        assertEquals(expectedAlbum.getTags(), created.getTags());
    }

    @Test
    void testDuplicateImagesSelected()
            throws ValidationException {
        var expectedAlbum = this.expectedAlbum;

        expectedAlbum.setDescription("Test");

        var albumImageDtoArray = new AlbumImageDto[3];

        for (var index = 0; index < albumImageDtoArray.length; index++) {
            albumImageDtoArray[index] = new AlbumImageDto();

            if (index < 2) {
                albumImageDtoArray[index].setId(1);
            } else {
                albumImageDtoArray[index].setId(index);
            }
        }

        var listAlbums = Arrays.asList(albumImageDtoArray);

        var albumDto = new AlbumCreationDto();

        albumDto.setTitle("Test album");
        albumDto.setDescription("Test");
        albumDto.setImages(listAlbums);
        albumDto.setCoverImage(2);
        albumDto.setVisibility(Visibility.PUBLIC.ordinal());
        albumDto.setTags("nice,nice2");

        var created = this.albumService.createAlbum(albumDto, this.principal);

        assertNotNull(created);
        assertEquals(expectedAlbum.getId(), created.getId());
        assertEquals(expectedAlbum.getTitle(), created.getTitle());
        assertEquals(expectedAlbum.getDescription(), created.getDescription());
        assertEquals(expectedAlbum.getCoverImage().getId(), created.getCoverImageId());
        assertEquals(ZonedDateTime.class, created.getCreationTimestamp().getClass());
        assertNull(created.getEditTimestamp());
        assertEquals(expectedAlbum.getVisibility(), created.getVisibility());
        assertEquals(expectedAlbum.getTags(), created.getTags());
    }

    @Test
    void testNoValidImagesSelected() {
        var albumImageDtoArray = new AlbumImageDto[3];

        for (var index = 0; index < albumImageDtoArray.length; index++) {
            albumImageDtoArray[index] = new AlbumImageDto();
            albumImageDtoArray[index].setId(index + 4);
        }

        var listAlbums = Arrays.asList(albumImageDtoArray);

        var albumDto = new AlbumCreationDto();

        albumDto.setTitle("Test album");
        albumDto.setDescription("Test description");
        albumDto.setImages(listAlbums);
        albumDto.setCoverImage(2);
        albumDto.setVisibility(Visibility.PUBLIC.ordinal());
        albumDto.setTags("nice,nice2");

        assertThrows(ValidationException.class,
                () -> this.albumService.createAlbum(albumDto, this.principal));
    }

    @Test
    void testNoValidCoverImageSelected() {
        var albumImageDtoArray = new AlbumImageDto[3];

        for (var index = 0; index < albumImageDtoArray.length; index++) {
            albumImageDtoArray[index] = new AlbumImageDto();
            albumImageDtoArray[index].setId(index + 1);
        }

        var listAlbums = Arrays.asList(albumImageDtoArray);

        var albumDto = new AlbumCreationDto();

        albumDto.setTitle("Test album");
        albumDto.setDescription("Test description");
        albumDto.setImages(listAlbums);
        albumDto.setCoverImage(5);
        albumDto.setVisibility(Visibility.PUBLIC.ordinal());
        albumDto.setTags("nice,nice2");

        assertThrows(ValidationException.class,
                () -> this.albumService.createAlbum(albumDto, this.principal));
    }
}
