package nz.ac.canterbury.seng302.portfolio.model.contract.basecontract;

import javax.validation.constraints.NotBlank;
import nz.ac.canterbury.seng302.portfolio.model.contract.Contractable;

public record BaseImageContract(
        @NotBlank(message = "Image is required")
        String croppedImage
) implements Contractable {}
