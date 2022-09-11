package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.sql.Timestamp;
import java.util.List;

/**
 * A contract for a post. Used for sending and retrieving posts from the database.
 *
 * @param postId this post's ID
 * @param groupId the ID of the group this post is associated with
 * @param userId the ID of the user this post is associated with
 * @param postContent the content of the post
 * @param created the timestamp on which this post was created
 * @param updated the timestamp on which this post was updated (can be null if not updated)
 * @param comments all of this post's comments
 */
public record PostContract(
    String postId,
    int groupId,
    int userId,
    String postContent,
    String postTitle,
    Timestamp created,
    Timestamp updated,
    List<CommentContract> comments
) implements Contractable {}
