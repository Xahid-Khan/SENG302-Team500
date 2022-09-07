package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * CRUD Repository for a post
 */
public interface PostModelRepository extends CrudRepository<PostModel, Integer> {

    List<PostModel> findPostModelByGroupId(int groupId);

    List<PostModel> findPostModelByUserId(int userId);
}
