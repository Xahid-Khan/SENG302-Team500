package nz.ac.canterbury.seng302.portfolio.model.contract.basecontract;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import nz.ac.canterbury.seng302.portfolio.model.contract.Contractable;

/**
 * Denotes the most basic a comment can be, I.E., before all calculations of additions are made.
 *
 * @param userId the user's ID to tie to the comment
 * @param comment the comment's content
 */
public record BaseCommentContract(
    int userId,
    @Size(min = 1, max = 4096, message = "Comment length should be between 1 - 4096 characters")
    @NotBlank(message = "Cannot post an empty comment")
    String comment
) implements Contractable{}
