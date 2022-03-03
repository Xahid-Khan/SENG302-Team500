package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.time.Instant;

public record ProjectContract(
        String name,
        String description,
        Instant startDate,
        Instant endDate
) { }
