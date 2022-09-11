package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.entity.CommentEntity;
import org.springframework.data.repository.CrudRepository;

/** CRUD Repository for a comment on a post. */
public interface CommentRepository extends CrudRepository<CommentEntity, String> {}
