package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.time.Instant;
import java.util.List;

public record ProjectContract(
        String id,
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        List<SprintContract> sprints
) { }
