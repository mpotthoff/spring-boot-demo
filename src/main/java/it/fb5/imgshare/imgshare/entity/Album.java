package it.fb5.imgshare.imgshare.entity;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Album {

    private long id;
    private User user;
    private Image coverImage;
    private ZonedDateTime creationTimestamp;
    private ZonedDateTime editTimestamp;
    private String title;
    private String description;
    private Visibility visibility;
    private List<AlbumImage> images;
    private List<String> tags;
    private List<AlbumVote> votes;
    private List<Comment> comments;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_album")
    @SequenceGenerator(name = "gen_album", sequenceName = "seq_album", allocationSize = 1)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cover_image_id", nullable = false)
    public Image getCoverImage() {
        return this.coverImage;
    }

    public void setCoverImage(Image coverImage) {
        this.coverImage = coverImage;
    }

    public ZonedDateTime getCreationTimestamp() {
        return this.creationTimestamp;
    }

    public void setCreationTimestamp(ZonedDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public ZonedDateTime getEditTimestamp() {
        return this.editTimestamp;
    }

    public void setEditTimestamp(ZonedDateTime editTimestamp) {
        this.editTimestamp = editTimestamp;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Enumerated()
    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "album_image", joinColumns = @JoinColumn(name = "album_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderColumn(name = "position")
    public List<AlbumImage> getImages() {
        return this.images;
    }

    public void setImages(List<AlbumImage> images) {
        this.images = images;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "album_tag", joinColumns = @JoinColumn(name = "album_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    @Column(name = "tag")
    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "album_vote", joinColumns = @JoinColumn(name = "album_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    public List<AlbumVote> getVotes() {
        return this.votes;
    }

    public void setVotes(List<AlbumVote> votes) {
        this.votes = votes;
    }

    @OneToMany(mappedBy = "album", fetch = FetchType.LAZY)
    public List<Comment> getComments() {
        return this.comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Album album = (Album) o;

        return id == album.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum Visibility {
        PUBLIC, PRIVATE
    }
}
