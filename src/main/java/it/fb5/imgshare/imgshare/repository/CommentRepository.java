package it.fb5.imgshare.imgshare.repository;

import it.fb5.imgshare.imgshare.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

}
