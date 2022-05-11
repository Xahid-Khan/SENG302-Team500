package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.EventMapper;
import nz.ac.canterbury.seng302.portfolio.mapping.MilestoneMapper;
import nz.ac.canterbury.seng302.portfolio.mapping.SprintMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.*;
import nz.ac.canterbury.seng302.portfolio.model.entity.EventEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.MilestoneEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Service
public class MilestoneService {
    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private MilestoneMapper milestoneMapper;


    /**
     * Get the event with the event ID
     *
     * @param eventId The event ID
     * @throws IllegalArgumentException If the event ID is invalid
     * @return The event contract with the event ID
     */
    public MilestoneContract get(String eventId) {
        var event= milestoneRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
        return milestoneMapper.toContract(event);
    }

    /**
     * Creates an event within a project and puts it in a sprint
     * if it falls within the sprint's start and end dates
     *
     * @return
     */
    public MilestoneContract createMilestone(String projectId, BaseMilestoneContract milestoneContract) {

        var project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        var milestone = milestoneMapper.toEntity(milestoneContract);
        project.newMilestone(milestone);
        projectRepository.save(project);
        milestoneRepository.save(milestone);

        return milestoneMapper.toContract(milestone);
    }


    /**
     * Deletes an event, including removing it from its parent project.
     *
     * @param milestoneId to delete
     * @throws NoSuchElementException if the id is invalid
     */
    public void delete(String milestoneId) {
        var milestone = milestoneRepository.findById(milestoneId).orElseThrow(() -> new NoSuchElementException("Invalid event ID"));
        var project = milestone.getProject();

        project.removeMilestone(milestone);
        milestoneRepository.deleteById(milestoneId);
        projectRepository.save(project);
    }

    /**
     * Updates an event using the EventContract provided
     * @param eventId to update
     * @param milestone to update, with the new values
     */
    public void update(String eventId, BaseMilestoneContract milestone) {

        MilestoneEntity milestoneEntity = milestoneRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("Invalid event ID"));

        milestoneEntity.setName(milestone.name());
        milestoneEntity.setDescription(milestone.description());
        milestoneEntity.setStartDate(milestone.startDate());
        milestoneEntity.setEndDate(milestone.endDate());

        milestoneRepository.save(milestoneEntity);
    }


}