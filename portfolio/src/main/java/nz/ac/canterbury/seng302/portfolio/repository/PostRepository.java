package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/** CRUD Repository for a post. */
public interface PostRepository extends PagingAndSortingRepository<PostEntity, Integer> {
  Page<PostEntity> getPaginatedPostsByGroupId(int groupId, Pageable pageable);
}
