package it.fb5.imgshare.imgshare.service.impl;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.FullUserDto;
import it.fb5.imgshare.imgshare.dto.UserDto;
import it.fb5.imgshare.imgshare.dto.UserEditDto;
import it.fb5.imgshare.imgshare.dto.UserRegistrationDto;
import it.fb5.imgshare.imgshare.entity.User;
import it.fb5.imgshare.imgshare.exception.ValidationException;
import it.fb5.imgshare.imgshare.repository.UserRepository;
import it.fb5.imgshare.imgshare.service.UserService;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto getUser(long userId) {
        var user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        return UserDto.fromUser(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        return UserDto.fromUser(userRepository.findByUsername(username).orElseThrow());
    }

    @Override
    public FullUserDto getOwnUser(SecurityPrincipal principal) {
        return FullUserDto
                .fromUser(this.userRepository.findById(principal.getUserId()).orElseThrow());
    }

    @Override
    public FullUserDto registerUser(UserRegistrationDto registrationDto)
            throws ValidationException {

        var foundUser = this.userRepository.findByEmail(registrationDto.getEmail());
        if (foundUser.isPresent()) {
            throw new ValidationException("email",
                    "There is already an account with that email address");
        }

        foundUser = this.userRepository.findByUsername(registrationDto.getUsername());
        if (foundUser.isPresent()) {
            throw new ValidationException("username",
                    "There is already an account with that username");
        }

        if (!registrationDto.getPassword().equals(registrationDto.getMatchingPassword())) {
            throw new ValidationException("matchingPassword", "The passwords don't match");
        }

        var user = new User();

        user.setUsername(registrationDto.getUsername());
        user.setPassword(this.passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());

        user = this.userRepository.save(user);

        return FullUserDto.fromUser(user);
    }

    @Override
    public FullUserDto updateUser(UserEditDto userEditDto, SecurityPrincipal principal)
            throws ValidationException {
        var currentUser = this.userRepository.findById(principal.getUserId()).orElseThrow();

        if (!userEditDto.getPassword().isEmpty()) {
            if (userEditDto.getPassword().length() < 5) {
                throw new ValidationException("password",
                        "The Password must be a minimum of five Characters");
            }

            if (!userEditDto.getPassword().equals(userEditDto.getMatchingPassword())) {
                throw new ValidationException("matchingPassword", "The passwords don't match");
            }

            currentUser.setPassword(this.passwordEncoder.encode(userEditDto.getPassword()));
        }

        if (!userEditDto.getEmail().isEmpty()) {
            if (!userEditDto.getEmail().matches("^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$")) {
                throw new ValidationException("email", "Invalid email address");
            }

            var foundUser = this.userRepository.findByEmail(userEditDto.getEmail());

            if (foundUser.isPresent() && !foundUser.get().equals(currentUser)) {
                throw new ValidationException("email",
                        "There is already an account with that email address");
            }

            currentUser.setEmail(userEditDto.getEmail());
        }

        currentUser = this.userRepository.save(currentUser);

        return FullUserDto.fromUser(currentUser);
    }
}
