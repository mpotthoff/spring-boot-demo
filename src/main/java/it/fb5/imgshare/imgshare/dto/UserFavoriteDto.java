package it.fb5.imgshare.imgshare.dto;

import it.fb5.imgshare.imgshare.entity.User;
import it.fb5.imgshare.imgshare.entity.UserFavorite;
import java.time.ZonedDateTime;

public class UserFavoriteDto {

    private AlbumDto album;
    private ZonedDateTime favoriteTimestamp;

    public static UserFavoriteDto fromUserFavorite(UserFavorite userFavorite, User user) {
        var userFavoriteDto = new UserFavoriteDto();

        userFavoriteDto.setAlbum(AlbumDto.fromAlbum(userFavorite.getAlbum(), user));
        userFavoriteDto.setFavoriteTimestamp(userFavorite.getFavoriteTimestamp());

        return userFavoriteDto;
    }

    public AlbumDto getAlbum() {
        return album;
    }

    public void setAlbum(AlbumDto album) {
        this.album = album;
    }

    public ZonedDateTime getFavoriteTimestamp() {
        return favoriteTimestamp;
    }

    public void setFavoriteTimestamp(ZonedDateTime favoriteTimestamp) {
        this.favoriteTimestamp = favoriteTimestamp;
    }
}
