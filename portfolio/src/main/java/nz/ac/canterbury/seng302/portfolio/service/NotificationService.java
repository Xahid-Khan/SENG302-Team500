package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.NotificationMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.NotificationContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseNotificationContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.NotificationEntity;
import nz.ac.canterbury.seng302.portfolio.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository repository;

    @Autowired
    NotificationMapper mapper;

    /**
     * Inserts a new notification into the database and returns the entry
     * @param baseContract
     * @return
     */
    public NotificationContract create(BaseNotificationContract baseContract) {
        NotificationEntity entity = repository.save(mapper.toEntity(baseContract ));
        return mapper.toContract(entity);
    }

    /**
     * Retrieves all the notification for a particular user
     * @param userId The id of the user whose notification will be retrieved
     * @return An arraylist of the users notifications
     */
    public ArrayList<NotificationContract> getAll(int userId) {
        Iterable<NotificationEntity> entities = repository.findAllByUserIdOrderByTimeNotifiedDesc(userId);

        ArrayList<NotificationContract> contracts = new ArrayList<>();
        for(NotificationEntity entity : entities){
            contracts.add(mapper.toContract(entity));
        }
        return contracts;
    }
}
