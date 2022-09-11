package nz.ac.canterbury.seng302.portfolio.repository;

import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
import org.springframework.data.repository.CrudRepository;

/** CRUD Repository for a post. */
public interface PostRepository extends CrudRepository<PostEntity, String> {
  List<PostEntity> findPostByGroupId(int groupId);

  List<PostEntity> findPostByUserId(int userId);
}
