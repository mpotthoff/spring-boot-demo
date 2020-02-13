package it.fb5.imgshare.imgshare.dto;

public class UserEditDto {

    private String password;
    private String matchingPassword;
    private String email;

    public static UserEditDto fromFullUserDto(FullUserDto user) {
        var edit = new UserEditDto();

        edit.setEmail(user.getEmail());

        return edit;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
