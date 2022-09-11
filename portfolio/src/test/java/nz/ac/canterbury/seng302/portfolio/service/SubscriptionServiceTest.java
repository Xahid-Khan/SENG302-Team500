package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the functionality of SubscriptionService
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class SubscriptionServiceTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired private SubscriptionService subscriptionService;

    @BeforeEach
    public void beforeEach() {
        subscriptionRepository.deleteAll();
    }

    /**
     * Tests that you can successfully subscribe to a group that you are not already subscribed to
     */
    @Test
    public void subscribe() {
        final int USER_ID = 1;
        final int GROUP_ID = 1;
        subscriptionService.subscribe(
                new SubscriptionContract(1, 1)
        );
        assertNotNull(subscriptionRepository.findByUserIdAndGroupId(USER_ID, GROUP_ID));
    }


    /**
     * Tests that you cannot overwrite a subscription to a group, you are already subscribed to
     */
    @Test
    public void subscribeToAlreadySubscribed() {
        final int USER_ID = 1;
        final int GROUP_ID = 1;
        subscriptionService.subscribe(
                new SubscriptionContract(USER_ID, GROUP_ID)
        );

        //The time that the first subscription was made
        Timestamp originalSubscriptionTime = subscriptionRepository.findByUserIdAndGroupId(USER_ID, GROUP_ID).getTimeSubscribed();

        subscriptionService.subscribe(
                new SubscriptionContract(USER_ID, GROUP_ID)
        );

        //The timestamp of the subscription after a second insertion was attempted
        System.out.println(subscriptionRepository.findByUserIdAndGroupId(USER_ID, GROUP_ID).getTimeSubscribed());
        Timestamp newSubscriptionTime = subscriptionRepository.findByUserIdAndGroupId(USER_ID, GROUP_ID).getTimeSubscribed();
        assertEquals(originalSubscriptionTime, newSubscriptionTime);
    }


    /**
     * Tests that you can successfully unsubscribe from a group, you are already subscribed to
     */
    @Test
    public void unsubscribe() {
        final int USER_ID = 1;
        final int GROUP_ID = 1;
        subscriptionService.subscribe(
                new SubscriptionContract(USER_ID, GROUP_ID)
        );
        subscriptionService.unsubscribe(
                new SubscriptionContract(USER_ID, GROUP_ID)
        );
        assertNull(subscriptionRepository.findByUserIdAndGroupId(USER_ID, GROUP_ID));
    }

    /**
     * Tests that you cannot unsubscribe from a group, you are not subscribed to
     */
    @Test
    public void unsubscribeFromNotSubscribed() {
        final int USER_ID = 1;
        final int GROUP_ID = 1;
        subscriptionService.unsubscribe(
                new SubscriptionContract(USER_ID, GROUP_ID)
        );
        assertNull(subscriptionRepository.findByUserIdAndGroupId(USER_ID, GROUP_ID));
    }
}
