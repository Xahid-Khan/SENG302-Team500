package nz.ac.canterbury.seng302.portfolio.service;
import nz.ac.canterbury.seng302.portfolio.model.contract.NotificationContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseNotificationContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.NotificationEntity;
import nz.ac.canterbury.seng302.portfolio.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the NotificationService class
 */
@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService service;

    @Autowired
    private NotificationRepository repository;

    @BeforeEach
    private void clear() {
        repository.deleteAll();
    }

    /**
     * Tests that a notification can be created and stored in the database, with all the correct information
     */
    @Test
    public void createNotificationTest(){
        final int USER_ID = 1;
        final String NOTIFIED_FROM = "Shaylin";
        final String DESCRIPTION = "Shaylin high-fived your last post!";
        BaseNotificationContract contract = new BaseNotificationContract(USER_ID, NOTIFIED_FROM, DESCRIPTION);

        service.create(contract);

        assertEquals(1, repository.findAllByUserId(1).size());
        assertEquals(USER_ID, repository.findAllByUserId(1).get(0).getUserId());
        assertEquals(NOTIFIED_FROM, repository.findAllByUserId(1).get(0).getNotificationFrom());
        assertEquals(DESCRIPTION, repository.findAllByUserId(1).get(0).getDescription());
    }

    /**
     * Tests that all notifications are retrieved for a user and that they all belong to that user
     */
    @Test
    public void retrieveAllNotificationsForUserTest(){
        final int USER_ID = 1;
        final String NOTIFIED_FROM = "Shaylin";
        final String DESCRIPTION = "Shaylin high-fived your last post!";

        for(int i = 0; i < 5; i++){
            service.create(new BaseNotificationContract(USER_ID, NOTIFIED_FROM, DESCRIPTION + '#' + i));
        }
        for(int i = 0; i < 10; i++){
            service.create(new BaseNotificationContract(2, "Cody", "This notification is not for you" + '#' + i));
        }

        ArrayList<NotificationEntity> notifications = repository.findAllByUserId(1);
        assertEquals(5, notifications.size());
        for(NotificationEntity notification : notifications) {
            assertEquals(USER_ID, notification.getUserId());
        }
    }

}
