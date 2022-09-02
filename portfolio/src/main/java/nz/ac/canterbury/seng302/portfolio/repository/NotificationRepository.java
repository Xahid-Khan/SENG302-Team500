package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.entity.NotificationEntity;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<NotificationEntity, String> {}
