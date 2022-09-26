package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.time.Instant;
import java.util.List;

/**
 * A contract for a conversation.
 * @param messageId The id of the message.
 * @param sentBy The id of the user who sent the message.
 */
public record MessageContract(
        String conversationId,
        String messageId,
        int sentBy,
        String messageContent,
        Instant timeSent
) implements Contractable {}