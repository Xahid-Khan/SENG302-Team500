package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.sql.Timestamp;

/**
 * A contract for a comment. Used for sending and retrieving comments from the database.
 *
 * @param commentId this comment's ID
 * @param userId the user the comment is associated with
 * @param postId the post this comment is under
 * @param comment the comment's contents
 * @param created the timestamp on which this comment was created
 */
public record CommentContract(
    String commentId,
    int userId,
    String postId,
    String comment,
    Timestamp created
) implements Contractable {}
