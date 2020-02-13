package it.fb5.imgshare.imgshare.service.impl;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.CommentDto;
import it.fb5.imgshare.imgshare.entity.Comment;
import it.fb5.imgshare.imgshare.entity.CommentVote;
import it.fb5.imgshare.imgshare.entity.User;
import it.fb5.imgshare.imgshare.repository.AlbumRepository;
import it.fb5.imgshare.imgshare.repository.CommentRepository;
import it.fb5.imgshare.imgshare.repository.UserRepository;
import it.fb5.imgshare.imgshare.service.CommentService;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, AlbumRepository albumRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CommentDto> getComments(long albumId, int limit, CommentOrder order,
            SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        var album = this.albumRepository.findById(albumId).orElse(null);

        if (album == null) {
            return Collections.emptyList();
        }

        var stream = album.getComments().stream()
                .map(c -> CommentDto.fromComment(c, currentUser));

        stream = this.getSortedStream(order, stream);

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(long albumId, String text, SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        var album = this.albumRepository.findById(albumId).orElse(null);

        if (album == null) {
            return null;
        }

        var comment = new Comment();

        comment.setAlbum(album);
        comment.setUser(currentUser);
        comment.setCreationTimestamp(ZonedDateTime.now());
        comment.setText(text);
        comment.setVotes(Collections.emptyList());

        comment = this.commentRepository.save(comment);

        return CommentDto.fromComment(comment, currentUser);
    }

    @Override
    public CommentDto voteComment(long commentId, int vote, SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        var comment = this.commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            return null;
        }

        var votes = comment.getVotes();
        var currentVote = votes.stream()
                .filter(v -> v.getUser().equals(currentUser))
                .findAny();

        if (currentVote.isPresent()) {
            if (currentVote.get().getVote() == vote) {
                votes.remove(currentVote.get());
            } else {
                currentVote.get().setVote(vote);
            }
        } else {
            votes.add(new CommentVote(currentUser, vote));
        }

        comment.setVotes(votes);

        comment = this.commentRepository.save(comment);

        return CommentDto.fromComment(comment, currentUser);
    }

    private User getCurrentUser(SecurityPrincipal principal) {
        if (principal == null) {
            return null;
        }

        return this.userRepository.findById(principal.getUserId()).orElseThrow();
    }

    private Stream<CommentDto> getSortedStream(CommentOrder order, Stream<CommentDto> stream) {
        switch (order) {
            default:
            case TIMESTAMP:
                stream = stream
                        .sorted(Comparator.comparing(CommentDto::getCreationTimestamp).reversed());
                break;
            case VOTE_SCORE:
                stream = stream.sorted(Comparator.comparing(CommentDto::getVoteScore).reversed());
                break;
        }

        return stream;
    }
}
