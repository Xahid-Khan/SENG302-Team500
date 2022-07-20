package nz.ac.canterbury.seng302.portfolio.model.contract;

import nz.ac.canterbury.seng302.portfolio.model.entity.DeadlineEntity;

import java.time.Instant;

public record BaseDeadlineContract( 
        String name,
        String description,
        Instant startDate
) {}
