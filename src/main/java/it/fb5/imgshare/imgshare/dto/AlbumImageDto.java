package it.fb5.imgshare.imgshare.dto;

import it.fb5.imgshare.imgshare.entity.AlbumImage;

public class AlbumImageDto {

    private long id;
    private String description;

    public static AlbumImageDto fromAlbumImage(AlbumImage albumImage) {
        var albumImageDto = new AlbumImageDto();

        albumImageDto.setId(albumImage.getImage().getId());
        albumImageDto.setDescription(albumImage.getDescription());

        return albumImageDto;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
