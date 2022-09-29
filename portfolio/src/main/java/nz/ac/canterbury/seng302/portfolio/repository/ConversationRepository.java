package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.entity.ConversationEntity;
import org.springframework.data.repository.CrudRepository;

/** This is the repository for conversations. */
public interface ConversationRepository extends CrudRepository<ConversationEntity, String> {}
