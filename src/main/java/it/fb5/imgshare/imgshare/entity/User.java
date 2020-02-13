package it.fb5.imgshare.imgshare.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class User {

    private long id;
    private String username;
    private String email;
    private String password;
    private List<UserFavorite> favorites;
    private List<Image> images;
    private List<Album> albums;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_user")
    @SequenceGenerator(name = "gen_user", sequenceName = "seq_user", allocationSize = 1)
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(columnDefinition = "char(60)")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_favorite", joinColumns = @JoinColumn(name = "user_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    public List<UserFavorite> getFavorites() {
        return this.favorites;
    }

    public void setFavorites(List<UserFavorite> favorites) {
        this.favorites = favorites;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public List<Image> getImages() {
        return this.images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
