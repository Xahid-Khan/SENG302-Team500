package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * CRUD Repository for a comment on a post
 */
public interface CommentModelRepository extends CrudRepository<CommentModel, Integer> {
    boolean deleteById (int commentId);

    ArrayList<CommentModel> findAllCommentByPostId(int postId);

    boolean deleteCommentsByPostId(int postId);
}
