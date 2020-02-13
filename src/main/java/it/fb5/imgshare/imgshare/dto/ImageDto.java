package it.fb5.imgshare.imgshare.dto;

import it.fb5.imgshare.imgshare.entity.Image;
import java.time.ZonedDateTime;

public class ImageDto {

    private long id;
    private UserDto user;
    private ZonedDateTime uploadTimestamp;

    public static ImageDto fromImage(Image image) {
        var imageDto = new ImageDto();

        imageDto.setId(image.getId());
        imageDto.setUser(UserDto.fromUser(image.getUser()));
        imageDto.setUploadTimestamp(image.getUploadTimestamp());

        return imageDto;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserDto getUser() {
        return this.user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public ZonedDateTime getUploadTimestamp() {
        return this.uploadTimestamp;
    }

    public void setUploadTimestamp(ZonedDateTime uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }
}
