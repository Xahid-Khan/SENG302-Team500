package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.entity.NotificationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface NotificationRepository extends CrudRepository<NotificationEntity, String> {
    ArrayList<NotificationEntity> findAllByUserId(int userId);
}
