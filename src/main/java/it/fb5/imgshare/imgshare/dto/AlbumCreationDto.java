package it.fb5.imgshare.imgshare.dto;

import it.fb5.imgshare.imgshare.entity.Album.Visibility;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class AlbumCreationDto {

    private String title;
    private String description;
    private int visibility;
    private String tags;
    private long coverImage;
    private List<AlbumImageDto> images;

    public AlbumCreationDto() {
        this.title = "";
        this.description = "";
        this.visibility = Visibility.PUBLIC.ordinal();
        this.tags = "";
        this.images = new ArrayList<>();
    }

    public static AlbumCreationDto fromAlbumDto(AlbumDto albumDto,
            List<AlbumImageDto> albumImages) {
        var creationDto = new AlbumCreationDto();

        creationDto.setTitle(albumDto.getTitle());
        creationDto.setDescription(albumDto.getDescription());
        creationDto.setVisibility(albumDto.getVisibility().ordinal());
        creationDto.setTags(String.join(",", albumDto.getTags()));
        creationDto.setCoverImage(albumDto.getCoverImageId());
        creationDto.setImages(albumImages);

        return creationDto;
    }

    @NotNull
    @Length(max = 63)
    @Pattern(regexp = "^[A-Za-z0-9- ]+$",
            message = "Title should only contain letters, numbers, dashes and white spaces")
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Length(max = 255)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVisibility() {
        return this.visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9,]+$", message = "Tags should only contain letters and numbers")
    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public long getCoverImage() {
        return this.coverImage;
    }

    public void setCoverImage(long coverImage) {
        this.coverImage = coverImage;
    }

    public List<AlbumImageDto> getImages() {
        return this.images;
    }

    public void setImages(List<AlbumImageDto> images) {
        this.images = images;
    }
}
