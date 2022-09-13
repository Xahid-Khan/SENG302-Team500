package nz.ac.canterbury.seng302.portfolio.model.entity;

import nz.ac.canterbury.seng302.portfolio.model.entity.GroupRepositoryEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.SubscriptionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * this interface extends the CRUD repository and makes use of the function/methods provided by the library.
 */
public interface GroupRepositoryRepository extends CrudRepository<GroupRepositoryEntity, String> {
    List<GroupRepositoryEntity> findGroupRepositoryEntityByGroupId(int groupId);
}

