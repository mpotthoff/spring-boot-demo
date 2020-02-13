package it.fb5.imgshare.imgshare.dto;

import it.fb5.imgshare.imgshare.entity.Comment;
import it.fb5.imgshare.imgshare.entity.User;
import java.time.ZonedDateTime;
import org.springframework.lang.Nullable;

public class CommentDto {

    private long id;
    private UserDto user;
    private ZonedDateTime creationTimestamp;
    private String text;
    private int upVotes;
    private int downVotes;
    private boolean upVoted;
    private boolean downVoted;

    public static CommentDto fromComment(Comment comment, @Nullable User user) {
        var commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setUser(UserDto.fromUser(comment.getUser()));
        commentDto.setCreationTimestamp(comment.getCreationTimestamp());
        commentDto.setText(comment.getText());

        var votes = comment.getVotes();

        commentDto.setUpVotes((int) votes.stream().filter(v -> v.getVote() == 1).count());
        commentDto.setDownVotes((int) votes.stream().filter(v -> v.getVote() == -1).count());

        if (user != null) {
            var vote = votes.stream().filter(v -> v.getUser().equals(user)).findAny();

            if (vote.isPresent()) {
                commentDto.setUpVoted(vote.get().getVote() == 1);
                commentDto.setDownVoted(vote.get().getVote() == -1);
            }
        }

        return commentDto;
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

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int getVoteScore() {
        return this.upVotes - this.downVotes;
    }
}
