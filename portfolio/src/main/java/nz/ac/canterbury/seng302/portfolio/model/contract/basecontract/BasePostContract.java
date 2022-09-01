package nz.ac.canterbury.seng302.portfolio.model.contract.basecontract;

import nz.ac.canterbury.seng302.portfolio.model.contract.Contractable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record BasePostContract(
    @NotBlank(message = "Must provide a valid Group ID")
    int groupId,
    @Size(max = 2047, message = "Post content should be less than 2048 characters")
    @NotBlank(message = "Post Cannot be empty")
    String postContent
) implements Contractable {}
