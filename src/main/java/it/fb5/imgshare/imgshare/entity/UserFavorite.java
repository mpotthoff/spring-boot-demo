package it.fb5.imgshare.imgshare.entity;

import java.time.ZonedDateTime;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class UserFavorite {

    private Album album;
    private ZonedDateTime favoriteTimestamp;

    public UserFavorite() {

    }

    public UserFavorite(Album album, ZonedDateTime favoriteTimestamp) {
        this.album = album;
        this.favoriteTimestamp = favoriteTimestamp;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id", nullable = false)
    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public ZonedDateTime getFavoriteTimestamp() {
        return this.favoriteTimestamp;
    }

    public void setFavoriteTimestamp(ZonedDateTime favoriteTimestamp) {
        this.favoriteTimestamp = favoriteTimestamp;
    }
}
