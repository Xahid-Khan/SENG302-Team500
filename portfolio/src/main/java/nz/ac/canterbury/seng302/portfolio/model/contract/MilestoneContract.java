package nz.ac.canterbury.seng302.portfolio.model.contract;

import nz.ac.canterbury.seng302.portfolio.model.entity.EventEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.MilestoneEntity;

import java.time.Instant;

/**
 * A contract for an milestone. Used for sending and retrieving sprints from the database
 * @param projectId The id of the project this event is associated with.
 * @param name The name of the milestone.
 * @param description The description of the milestone.
 * @param startDate The start date of the milestone.
 * @param endDate The end date of the milestone.
 */
public record  MilestoneContract (
        String projectId,
        String milestoneId,
        String name,
        String description,
        Instant startDate,
        Instant endDate
) {}