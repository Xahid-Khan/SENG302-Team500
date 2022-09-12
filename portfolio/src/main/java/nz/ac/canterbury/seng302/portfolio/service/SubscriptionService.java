package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.SubscriptionMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.SubscriptionEntity;
import nz.ac.canterbury.seng302.portfolio.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles subscribing and unsubscribing people to and from groups
 */
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

    /**
     * Retrieves a list of groupIds of the groups the user is subscribed to
     * @return a list of groupIds of the groups the user is subscribed to
     */
    public List<Integer> getAll(int userId) {
        return subscriptionRepository.findByUserId(userId).stream().map(SubscriptionEntity::getGroupId).toList();
    }
}
