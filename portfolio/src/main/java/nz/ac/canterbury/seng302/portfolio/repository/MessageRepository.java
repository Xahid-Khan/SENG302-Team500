package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.entity.MessageEntity;
import org.springframework.data.repository.CrudRepository;

/** This is the repository for messages. */
public interface MessageRepository extends CrudRepository<MessageEntity, String> {}
