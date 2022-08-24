package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.SubscriptionMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    /**
     * Creates a subscription in the database of a user to a group
     * @param subscription SubscriptionContract containing the user and group
     */
    public void subscribe(SubscriptionContract subscription) {
        subscriptionRepository.save(subscriptionMapper.toEntity(subscription));
    }

    /**
     * Removes a subscription in the database
     * @param subscription SubscriptionContract containing the user and group
     */
    public void unsubscribe(SubscriptionContract subscription) {
        subscriptionRepository.delete(subscriptionMapper.toEntity(subscription));
    }
}
