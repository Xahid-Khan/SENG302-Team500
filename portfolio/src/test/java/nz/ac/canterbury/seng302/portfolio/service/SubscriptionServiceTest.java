package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.SubscriptionMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.SubscriptionEntity;
import nz.ac.canterbury.seng302.portfolio.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    private final int USER_ID = 1;
    private final int GROUP_ID = 1;
    private final SubscriptionContract contract = new SubscriptionContract(USER_ID, GROUP_ID);
    private final SubscriptionEntity entity = new SubscriptionEntity(USER_ID, GROUP_ID);

    @BeforeEach
    public void beforeEach() {
        subscriptionRepository.deleteAll();
    }

    /**
     * Tests that you can successfully subscribe to a group that you are not already subscribed to
     */
    @Test
    public void subscribe() {
        Mockito.when(subscriptionMapper.toEntity(contract)).thenReturn(entity);
        subscriptionService.subscribe(contract);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).save(entity);
    }

    /**
     * Tests that you can successfully unsubscribe from a group, you are already subscribed to
     */
    @Test
    public void unsubscribe() {
        Mockito.when(subscriptionMapper.toEntity(contract)).thenReturn(entity);
        subscriptionService.subscribe(contract);
        subscriptionService.unsubscribe(contract);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).delete(entity);
    }
}
