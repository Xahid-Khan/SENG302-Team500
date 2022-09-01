package nz.ac.canterbury.seng302.portfolio.model.contract.basecontract;

import nz.ac.canterbury.seng302.portfolio.model.contract.Contractable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record BaseCommentContract(
        @NotBlank(message = "User ID must be provided")
        int userId,

        @NotBlank(message = "Group ID must be provided")
        int postId,

        @Size(min = 1, max = 1023, message = "Comment length should be between 1 - 1023 characters")
        @NotBlank(message = "Cannot post an empty comment")
        String comment
) implements Contractable{}
