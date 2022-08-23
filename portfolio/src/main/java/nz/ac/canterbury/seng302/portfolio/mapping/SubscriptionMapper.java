package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.SubscriptionEntity;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {
    public SubscriptionEntity toEntity(SubscriptionContract subscription) {
        return new SubscriptionEntity(
                subscription.userId(),
                subscription.groupId()
        );
    }
}
