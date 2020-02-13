package it.fb5.imgshare.imgshare.dto;

import it.fb5.imgshare.imgshare.entity.Album;
import it.fb5.imgshare.imgshare.entity.Album.Visibility;
import it.fb5.imgshare.imgshare.entity.User;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.lang.Nullable;

public class AlbumDto {

    private long id;
    private UserDto user;
    private ZonedDateTime creationTimestamp;
    private ZonedDateTime editTimestamp;
    private long coverImageId;
    private String title;
    private String description;
    private Visibility visibility;
    private List<String> tags;
    private int upVotes;
    private int downVotes;
    private boolean upVoted;
    private boolean downVoted;
    private boolean favored;
    private int commentCount;

    public static AlbumDto fromAlbum(Album album, @Nullable User user) {
        var albumDto = new AlbumDto();

        albumDto.setId(album.getId());
        albumDto.setUser(UserDto.fromUser(album.getUser()));
        albumDto.setCreationTimestamp(album.getCreationTimestamp());
        albumDto.setEditTimestamp(album.getEditTimestamp());
        albumDto.setCoverImageId(album.getCoverImage().getId());
        albumDto.setTitle(album.getTitle());
        albumDto.setDescription(album.getDescription());
        albumDto.setVisibility(album.getVisibility());
        albumDto.setTags(album.getTags());
        albumDto.setCommentCount(album.getComments().size());

        var votes = album.getVotes();

        albumDto.setUpVotes((int) votes.stream().filter(v -> v.getVote() == 1).count());
        albumDto.setDownVotes((int) votes.stream().filter(v -> v.getVote() == -1).count());

        if (user != null) {
            var vote = votes.stream().filter(v -> v.getUser().equals(user)).findAny();

            if (vote.isPresent()) {
                albumDto.setUpVoted(vote.get().getVote() == 1);
                albumDto.setDownVoted(vote.get().getVote() == -1);
            }

            albumDto.setFavored(
                    user.getFavorites().stream()
                            .anyMatch(f -> f.getAlbum().equals(album))
            );
        }

        return albumDto;
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

    public long getCoverImageId() {
        return this.coverImageId;
    }

    public void setCoverImageId(long coverImageId) {
        this.coverImageId = coverImageId;
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

    public Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getUpVotes() {
        return this.upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return this.downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public int getVoteScore() {
        return this.upVotes - this.downVotes;
    }

    public boolean isUpVoted() {
        return this.upVoted;
    }

    public void setUpVoted(boolean upVoted) {
        this.upVoted = upVoted;
    }

    public boolean isDownVoted() {
        return this.downVoted;
    }

    public void setDownVoted(boolean downVoted) {
        this.downVoted = downVoted;
    }

    public boolean isFavored() {
        return this.favored;
    }

    public void setFavored(boolean favored) {
        this.favored = favored;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
