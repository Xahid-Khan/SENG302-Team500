package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.MilestoneMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseMilestoneContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.MilestoneContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.MilestoneEntity;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Get the milestone with the milestone ID
     *
     * @param milestoneId The milestone ID
     * @throws IllegalArgumentException If the milestone ID is invalid
     * @return The milestone contract with the milestone ID
     */
    public MilestoneContract get(String milestoneId) {
        var milestone= milestoneRepository.findById(milestoneId).orElseThrow(() -> new IllegalArgumentException("Invalid milestone ID"));
        return milestoneMapper.toContract(milestone);
    }

    /**
     * Creates an milestone within a project and puts it in a sprint
     * if it falls within the sprint's start and end dates
     *
     * @return
     */
    public MilestoneContract createMilestone(String projectId, BaseMilestoneContract milestoneContract) {

        var project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        var milestone = milestoneMapper.toEntity(milestoneContract);
        project.addMilestone(milestone);
        projectRepository.save(project);
        milestoneRepository.save(milestone);

        return milestoneMapper.toContract(milestone);
    }


    /**
     * Deletes an milestone, including removing it from its parent project.
     *
     * @param milestoneId to delete
     * @throws NoSuchElementException if the id is invalid
     */
    public void delete(String milestoneId) {
        var milestone = milestoneRepository.findById(milestoneId).orElseThrow(() -> new NoSuchElementException("Invalid milestone ID"));
        var project = milestone.getProject();

        project.removeMilestone(milestone);
        milestoneRepository.deleteById(milestoneId);
        projectRepository.save(project);
    }

    /**
     * Updates a milestone using the MilestoneContract provided
     * @param milestoneId to update
     * @param milestone to update, with the new values
     */
    public void update(String milestoneId, BaseMilestoneContract milestone) {

        MilestoneEntity milestoneEntity = milestoneRepository.findById(milestoneId).orElseThrow(() -> new NoSuchElementException("Invalid milestone ID"));

        milestoneEntity.setName(milestone.name());
        milestoneEntity.setDescription(milestone.description());
        milestoneEntity.setStartDate(milestone.startDate());
        milestoneEntity.setEndDate(milestone.endDate());

        milestoneRepository.save(milestoneEntity);
    }


}