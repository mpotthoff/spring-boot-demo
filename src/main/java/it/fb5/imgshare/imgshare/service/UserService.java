package it.fb5.imgshare.imgshare.service;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.FullUserDto;
import it.fb5.imgshare.imgshare.dto.UserDto;
import it.fb5.imgshare.imgshare.dto.UserEditDto;
import it.fb5.imgshare.imgshare.dto.UserRegistrationDto;
import it.fb5.imgshare.imgshare.exception.ValidationException;

public interface UserService {

    UserDto getUser(long userId);

    UserDto getUserByUsername(String username);

    FullUserDto getOwnUser(SecurityPrincipal principal);

    FullUserDto registerUser(UserRegistrationDto registrationDto) throws ValidationException;

    FullUserDto updateUser(UserEditDto userEditDto, SecurityPrincipal principal)
            throws ValidationException;
}
