package it.fb5.imgshare.imgshare.entity;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AlbumImage {

    private Image image;
    private String description;

    public AlbumImage() {

    }

    public AlbumImage(Image image, String description) {
        this.image = image;
        this.description = description;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id", nullable = false)
    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
