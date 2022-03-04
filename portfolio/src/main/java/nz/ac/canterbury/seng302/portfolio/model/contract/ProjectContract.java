package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record ProjectContract(
        Long id,
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        List<SprintContract> allSprints
) { }
