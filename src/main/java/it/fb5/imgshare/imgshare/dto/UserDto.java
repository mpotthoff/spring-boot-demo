package it.fb5.imgshare.imgshare.dto;

import it.fb5.imgshare.imgshare.entity.User;

public class UserDto {

    private long id;
    private String username;

    public static UserDto fromUser(User user) {
        var userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());

        return userDto;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
