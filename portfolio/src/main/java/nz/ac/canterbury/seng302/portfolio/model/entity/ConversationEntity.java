package nz.ac.canterbury.seng302.portfolio.model.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/** The database representation of a Conversation. */
@Entity
@Table(name = "conversation")
public class ConversationEntity extends PortfolioEntity {
  @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
  @Fetch(value = FetchMode.SUBSELECT)
  // This ensures that some collection operations do not trigger collection initialization
  //  Read more:
  // https://sites.google.com/a/pintailconsultingllc.com/java/hibernate-extra-lazy-collection-fetching
  @LazyCollection(LazyCollectionOption.EXTRA)
  private final List<MessageEntity> messages = new ArrayList<>();
  // Eager since loading all IDs always is vital
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(nullable = false)
  private List<Integer> userIds = new ArrayList<>();
  // Makes the database automatically create the timestamp when the user is inserted
  @CreationTimestamp
  @Column(name = "creationDate", nullable = false, updatable = false)
  private Timestamp creationDate;

  protected ConversationEntity() {}

  /**
   * Creates a conversation out of an existing amount of user IDs.
   *
   * @param userIds the user IDs to add to the conversation initially
   */
  public ConversationEntity(List<Integer> userIds) {
    this.userIds.addAll(userIds);
  }

  public List<Integer> getUserIds() {
    return userIds;
  }

  public void setUserIds(List<Integer> userIds) {
    this.userIds = userIds;
  }

  /**
   * Adds a user to the conversation.
   *
   * @param userId the user to add to the conversation
   */
  public void addUser(Integer userId) {
    userIds.add(userId);
  }

  /**
   * Removes a user from the conversation.
   *
   * @param userId the user to remove from the conversation
   */
  public void removeUser(Integer userId) {
    userIds.remove(userId);
  }

  /**
   * Returns the most recent message, used for previewing.
   *
   * @return the most recent message for previewing, or null if there are no messages
   */
  public MessageEntity getMostRecentMessage() {
    return !messages.isEmpty() ? messages.get(messages.size() - 1) : null;
  }

  /**
   * Adds a message into the conversation.
   *
   * @param message the message to add to the conversation
   */
  public void addMessage(MessageEntity message) {
    messages.add(message);
    message.setConversation(this);
  }

  /**
   * Removes a message from the conversation.
   *
   * @param message the message to remove from the conversation
   */
  public void removeMessage(MessageEntity message) {
    messages.remove(message);
    message.setConversation(null);
  }

  public Timestamp getCreationDate() {
    return creationDate;
  }
}
