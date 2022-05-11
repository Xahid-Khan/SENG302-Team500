package nz.ac.canterbury.seng302.portfolio.model.contract;

import nz.ac.canterbury.seng302.portfolio.model.entity.EventEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.MilestoneEntity;

import java.time.Instant;

public record BaseMilestoneContract(
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        MilestoneEntity.MilestoneType type
) {}