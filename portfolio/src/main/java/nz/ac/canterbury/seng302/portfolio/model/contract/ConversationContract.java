package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.time.Instant;
import java.util.List;

/**
 * A contract for a conversation.
 */
public record ConversationContract(
        String conversationId,
        List<Integer> userIds,
        Instant creationDate,
        MessageContract mostRecentMessage
) implements Contractable {}