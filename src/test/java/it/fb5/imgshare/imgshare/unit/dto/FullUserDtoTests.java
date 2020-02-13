package it.fb5.imgshare.imgshare.unit.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.fb5.imgshare.imgshare.dto.FullUserDto;
import it.fb5.imgshare.imgshare.entity.User;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestInstance(Lifecycle.PER_CLASS)
public class FullUserDtoTests {

    private User user;

    @BeforeAll
    void beforeAll() {
        this.user = new User();
        this.user.setId(1);
        this.user.setUsername("test");
        this.user.setEmail("test@test.de");
        this.user.setPassword(new BCryptPasswordEncoder().encode("test"));
        this.user.setAlbums(Collections.emptyList());
        this.user.setFavorites(Collections.emptyList());
    }

    @Test
    void testFromUser() {
        FullUserDto userDto = FullUserDto.fromUser(this.user);

        assertNotNull(userDto);
        assertEquals(userDto.getId(), this.user.getId());
        assertEquals(userDto.getUsername(), this.user.getUsername());
        assertEquals(userDto.getEmail(), this.user.getEmail());
    }
}
