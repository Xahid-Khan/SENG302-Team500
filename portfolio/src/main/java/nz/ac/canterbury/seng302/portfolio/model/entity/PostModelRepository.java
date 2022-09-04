package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.springframework.data.repository.CrudRepository;

/**
 * CRUD Repository for a post
 */
public interface PostModelRepository extends CrudRepository<PostModel, Long> {
}
