package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.time.Instant;

public record SprintContract(
    String projectId,
    String sprintId,
    String name,
    String description,
    Instant startDate,
    Instant endDate,
    Long orderNumber  // Should only be present in responses
) {}