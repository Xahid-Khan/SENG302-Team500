package nz.ac.canterbury.seng302.portfolio.model.contract.basecontract;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import nz.ac.canterbury.seng302.portfolio.model.contract.Contractable;

/**
 * Denotes the most basic a post can be, I.E., before all calculations of additions are made.
 *
 * @param groupId the group ID to tie this post to
 * @param userId the user's ID to tie this post to
 * @param postContent the post's content
 */
public record BasePostContract(
    int groupId,
    int userId,
    @Size(min = 1, max = 4096, message = "Post content should be between 1 and 4096 characters")
    @NotBlank(message = "Post cannot be empty")
    String postContent,

    @Size(min = 1, max = 128, message = "Post title should be between 1 and 128 characters")
    @NotBlank(message = "Post must have a title")
    String postTitle
) implements Contractable {}
