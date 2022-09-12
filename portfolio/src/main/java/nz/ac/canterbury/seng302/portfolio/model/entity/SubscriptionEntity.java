package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Represents a subscription of a user to a group in the database
 */
@Entity
@Table(name = "subscription")
public class SubscriptionEntity extends PortfolioEntity {

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "group_id", nullable = false)
    private int groupId;

    @CreationTimestamp
    @Column(name = "time_subscribed")
    private Timestamp timeSubscribed;

    protected SubscriptionEntity() {

    }

    public SubscriptionEntity(int userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Timestamp getTimeSubscribed() {
        return timeSubscribed;
    }

    public void setTimeSubscribed(Timestamp timeSubscribed) {
        this.timeSubscribed = timeSubscribed;
    }
}