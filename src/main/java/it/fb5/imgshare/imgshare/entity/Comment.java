package it.fb5.imgshare.imgshare.entity;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Comment {

    private long id;
    private Album album;
    private User user;
    private ZonedDateTime creationTimestamp;
    private String text;
    private List<CommentVote> votes;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_comment")
    @SequenceGenerator(name = "gen_comment", sequenceName = "seq_comment", allocationSize = 1)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "album_id", nullable = false)
    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ZonedDateTime getCreationTimestamp() {
        return this.creationTimestamp;
    }

    public void setCreationTimestamp(ZonedDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comment_vote", joinColumns = @JoinColumn(name = "comment_id"))
    @Fetch(value = FetchMode.SUBSELECT)
    public List<CommentVote> getVotes() {
        return this.votes;
    }

    public void setVotes(List<CommentVote> votes) {
        this.votes = votes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Comment comment = (Comment) o;

        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
