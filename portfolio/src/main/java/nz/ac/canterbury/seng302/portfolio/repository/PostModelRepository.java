package nz.ac.canterbury.seng302.portfolio.repository;

import java.util.Collection;
import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * CRUD Repository for a post.
 */
public interface PostModelRepository extends CrudRepository<PostModel, Integer> {

  List<PostModel> findPostModelByGroupId(int groupId);

  List<PostModel> findPostModelByUserId(int userId);

  @Query(value = "SELECT * FROM POSTMODEL P WHERE P.group_id IN (?1) ORDER BY P.created DESC",
      nativeQuery = true)
  List<PostModel> findPostModelByGroupIdOrderByCreatedDesc(Collection<Integer> groupIds);
}
