package it.fb5.imgshare.imgshare.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.FullUserDto;
import it.fb5.imgshare.imgshare.dto.UserDto;
import it.fb5.imgshare.imgshare.dto.UserEditDto;
import it.fb5.imgshare.imgshare.dto.UserRegistrationDto;
import it.fb5.imgshare.imgshare.entity.Image;
import it.fb5.imgshare.imgshare.entity.User;
import it.fb5.imgshare.imgshare.exception.ValidationException;
import it.fb5.imgshare.imgshare.repository.ImageRepository;
import it.fb5.imgshare.imgshare.repository.UserRepository;
import it.fb5.imgshare.imgshare.service.UserService;
import it.fb5.imgshare.imgshare.service.impl.UserServiceImpl;
import java.time.ZonedDateTime;
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
public class UserServiceTests {

    private User user;
    private SecurityPrincipal principal;

    private Image image;

    private BCryptPasswordEncoder encoder;

    private ImageRepository imageRepository;
    private UserRepository userRepository;

    private UserService userService;

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

        this.encoder = new BCryptPasswordEncoder();

        this.imageRepository = Mockito.mock(ImageRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);

        this.userService = new UserServiceImpl(this.userRepository, this.encoder);

        Mockito.when(this.userRepository.save(Mockito.any(User.class)))
                .thenAnswer((Answer<User>) invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1);
                    return user;
                });
    }

    @Test
    void testGetUserReturnsUser() {
        Mockito.when(this.userRepository.findById(this.user.getId()))
                .thenReturn(Optional.of(this.user));

        UserDto userDto = this.userService.getUser(user.getId());

        assertNotNull(userDto);
        assertEquals(userDto.getId(), this.user.getId());
    }

    @Test
    void testGetUserByUsernameReturnsUser() {
        Mockito.when(this.userRepository.findByUsername(this.user.getUsername()))
                .thenReturn(Optional.of(this.user));

        UserDto userDto = this.userService.getUserByUsername(user.getUsername());

        assertNotNull(userDto);
        assertEquals(userDto.getId(), this.user.getId());
    }

    @Test
    void testGetUserReturnsNull() {
        Mockito.when(this.userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        UserDto userDto = this.userService.getUser(user.getId());

        assertNull(userDto);
    }

    @Test
    void testGetOwnUserReturnsUser() {
        Mockito.when(this.userRepository.findById(this.user.getId()))
                .thenReturn(Optional.of(this.user));

        UserDto userDto = this.userService.getOwnUser(principal);

        assertNotNull(userDto);
        assertEquals(userDto.getId(), this.user.getId());
    }

    @Test
    void testRegisterUser() throws ValidationException {
        Mockito.when(this.userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(this.userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty());

        UserRegistrationDto registration = new UserRegistrationDto();
        registration.setEmail(this.user.getEmail());
        registration.setMatchingPassword("01234");
        registration.setPassword("01234");
        registration.setUsername(this.user.getUsername());

        FullUserDto regUser = this.userService.registerUser(registration);
        assertEquals(this.user.getUsername(), regUser.getUsername());
        assertEquals(this.user.getEmail(), regUser.getEmail());
    }

    @Test
    void testRegisterUserExistingEmailException() {
        Mockito.when(this.userRepository.findByEmail(this.user.getEmail()))
                .thenReturn(Optional.of(this.user));
        Mockito.when(this.userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty());

        UserRegistrationDto registration = new UserRegistrationDto();
        registration.setEmail(this.user.getEmail());
        registration.setMatchingPassword("01234");
        registration.setPassword("01234");
        registration.setUsername(this.user.getUsername());

        assertThrows(ValidationException.class, () -> this.userService.registerUser(registration));
    }

    @Test
    void testRegisterUserExistingUsernameException() {
        Mockito.when(this.userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(this.userRepository.findByUsername(this.user.getUsername()))
                .thenReturn(Optional.of(this.user));

        UserRegistrationDto registration = new UserRegistrationDto();
        registration.setEmail(this.user.getEmail());
        registration.setMatchingPassword("01234");
        registration.setPassword("01234");
        registration.setUsername(user.getUsername());

        assertThrows(ValidationException.class, () -> this.userService.registerUser(registration));
    }

    @Test
    void testRegisterUserMatchingPasswordException() {
        Mockito.when(this.userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(this.userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty());

        UserRegistrationDto registration = new UserRegistrationDto();
        registration.setEmail(this.user.getEmail());
        registration.setMatchingPassword("56789");
        registration.setPassword("01234");
        registration.setUsername(this.user.getUsername());

        assertThrows(ValidationException.class, () -> this.userService.registerUser(registration));
    }

    @Test
    void testUpdateUser() throws ValidationException {
        Mockito.when(this.userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(this.userRepository.findById(this.user.getId()))
                .thenReturn(Optional.of(this.user));

        UserEditDto editDto = new UserEditDto();
        editDto.setEmail(this.user.getEmail());
        editDto.setMatchingPassword("01234");
        editDto.setPassword("01234");

        FullUserDto editUser = this.userService.updateUser(editDto, principal);
        assertEquals(this.user.getUsername(), editUser.getUsername());
        assertEquals(this.user.getEmail(), editUser.getEmail());
    }

    @Test
    void testUpdateUserSameEmail() throws ValidationException {
        Mockito.when(this.userRepository.findByEmail(this.user.getEmail()))
                .thenReturn(Optional.of(this.user));
        Mockito.when(this.userRepository.findById(this.user.getId()))
                .thenReturn(Optional.of(this.user));

        UserEditDto editDto = new UserEditDto();
        editDto.setEmail(this.user.getEmail());
        editDto.setMatchingPassword("");
        editDto.setPassword("");

        FullUserDto editUser = this.userService.updateUser(editDto, principal);
        assertEquals(this.user.getUsername(), editUser.getUsername());
        assertEquals(this.user.getEmail(), editUser.getEmail());
    }

    @Test
    void testUpdateUserExistingEmailException() {
        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setUsername("test");
        otherUser.setEmail("test@test.de");
        otherUser.setPassword(new BCryptPasswordEncoder().encode("test"));
        otherUser.setAlbums(Collections.emptyList());
        otherUser.setFavorites(Collections.emptyList());

        Mockito.when(this.userRepository.findByEmail(otherUser.getEmail()))
                .thenReturn(Optional.of(otherUser));
        Mockito.when(this.userRepository.findById(this.user.getId()))
                .thenReturn(Optional.of(this.user));

        UserEditDto editDto = new UserEditDto();
        editDto.setEmail(otherUser.getEmail());
        editDto.setMatchingPassword("");
        editDto.setPassword("");

        assertThrows(ValidationException.class,
                () -> this.userService.updateUser(editDto, principal));
    }

    @Test
    void testUpdateUserInvalidEmailException() {
        Mockito.when(this.userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(this.userRepository.findById(this.user.getId()))
                .thenReturn(Optional.of(this.user));

        UserEditDto editDto = new UserEditDto();
        editDto.setEmail("NotAnEmail");
        editDto.setMatchingPassword("");
        editDto.setPassword("");

        assertThrows(ValidationException.class,
                () -> this.userService.updateUser(editDto, principal));
    }

    @Test
    void testUpdateUserShortPasswordException() {
        Mockito.when(this.userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(this.userRepository.findById(this.user.getId()))
                .thenReturn(Optional.of(this.user));

        UserEditDto editDto = new UserEditDto();
        editDto.setEmail("");
        editDto.setMatchingPassword("0");
        editDto.setPassword("0");

        assertThrows(ValidationException.class,
                () -> this.userService.updateUser(editDto, principal));
    }

    @Test
    void testUpdateUserMatchingPasswordException() {
        Mockito.when(this.userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(this.userRepository.findById(this.user.getId()))
                .thenReturn(Optional.of(this.user));

        UserEditDto editDto = new UserEditDto();
        editDto.setEmail("");
        editDto.setMatchingPassword("56789");
        editDto.setPassword("01234");

        assertThrows(ValidationException.class,
                () -> this.userService.updateUser(editDto, principal));
    }
}
