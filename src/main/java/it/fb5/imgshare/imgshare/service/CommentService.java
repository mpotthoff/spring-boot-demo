package it.fb5.imgshare.imgshare.service;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.CommentDto;
import java.util.List;

public interface CommentService {

    List<CommentDto> getComments(long albumId, int limit, CommentOrder order,
            SecurityPrincipal principal);

    CommentDto createComment(long albumId, String text, SecurityPrincipal principal);

    CommentDto voteComment(long commentId, int vote, SecurityPrincipal principal);

    enum CommentOrder {
        TIMESTAMP,
        VOTE_SCORE
    }
}
