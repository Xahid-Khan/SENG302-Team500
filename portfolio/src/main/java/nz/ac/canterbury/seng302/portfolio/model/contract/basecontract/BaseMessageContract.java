package nz.ac.canterbury.seng302.portfolio.model.contract.basecontract;

import nz.ac.canterbury.seng302.portfolio.model.contract.Contractable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record BaseMessageContract(
        @Size(min = 1, max = 4096, message = "Message length should be between 1 - 4096 characters")
        @NotBlank(message = "Cannot post an empty message")
        String content
) implements Contractable{}
