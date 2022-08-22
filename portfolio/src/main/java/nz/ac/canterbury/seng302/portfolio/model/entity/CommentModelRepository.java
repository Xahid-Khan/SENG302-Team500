package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.springframework.data.repository.CrudRepository;

/**
 * CRUD Repository for a comment on a post
 */
public interface CommentModelRepository extends CrudRepository<CommentModel, Integer> {
}
