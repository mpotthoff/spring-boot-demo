package it.fb5.imgshare.imgshare.dto;

import it.fb5.imgshare.imgshare.entity.User;

public class FullUserDto extends UserDto {

    private String email;

    public static FullUserDto fromUser(User user) {
        var userDto = new FullUserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
