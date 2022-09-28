package nz.ac.canterbury.seng302.portfolio.repository;

import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.entity.ConversationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/** This is the repository for conversations. */
public interface ConversationRepository
    extends PagingAndSortingRepository<ConversationEntity, String> {

  /**
   * Paginates based on a list of user IDs. Note that this is primarily used by passing in a single
   * user ID.
   *
   * @param userIds the user IDs to grab conversations for (almost always only a single user)
   * @param request the pagination request
   * @return a Page of ConversationEntities
   */
  Page<ConversationEntity> getPaginatedPostsByUserIdsIn(List<Integer> userIds, Pageable request);
}
