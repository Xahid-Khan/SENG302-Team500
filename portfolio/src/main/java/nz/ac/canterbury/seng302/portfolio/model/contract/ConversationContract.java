package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.sql.Timestamp;
import java.util.List;

/**
 * A contract for a conversation. The most recent message only is included for previewing.
 *
 * @param conversationId the conversation's ID
 * @param userIds the users in the conversation
 * @param creationDate the creation date of the conversation
 * @param mostRecentMessage the most recent message in the conversation for previewing
 */
public record ConversationContract(
    String conversationId,
    List<Integer> userIds,
    Timestamp creationDate,
    MessageContract mostRecentMessage
) implements Contractable {}
