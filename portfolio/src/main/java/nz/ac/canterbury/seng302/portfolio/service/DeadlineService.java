package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.DeadlineMapper;
import nz.ac.canterbury.seng302.portfolio.mapping.SprintMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseDeadlineContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.DeadlineContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.DeadlineEntity;
import nz.ac.canterbury.seng302.portfolio.repository.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class DeadlineService {
    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DeadlineRepository deadlineRepository;

    @Autowired
    private DeadlineMapper deadlineMapper;

    /**
     * Get the deadline with the deadline ID
     *
     * @param deadlineId The deadline ID
     * @throws IllegalArgumentException If the deadline ID is invalid
     * @return The deadline contract with the deadline ID
     */
    public DeadlineContract get(String deadlineId) {
        var deadline= deadlineRepository.findById(deadlineId).orElseThrow(() -> new IllegalArgumentException("Invalid deadline ID"));
        return deadlineMapper.toContract(deadline);
    }

    /**
     * Creates an deadline within a project and puts it in a sprint
     * if it falls within the sprint's start and end dates
     *
     * @return
     */
    public DeadlineContract createDeadline(String projectId, BaseDeadlineContract deadline) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        var entity = deadlineMapper.toEntity(deadline);

        project.newDeadline(entity);
        projectRepository.save(project);
        deadlineRepository.save(entity);

        return deadlineMapper.toContract(entity);
    }


    /**
     * Deletes an deadline, including removing it from its parent project.
     *
     * @param deadlineId to delete
     * @throws NoSuchElementException if the id is invalid
     */
    public void delete(String deadlineId) {
        var deadline = deadlineRepository.findById(deadlineId).orElseThrow(() -> new NoSuchElementException("Invalid deadline ID"));
        var project = deadline.getProject();


        project.removeDeadline(deadline);
        deadlineRepository.deleteById(deadlineId);
        projectRepository.save(project);
    }

    /**
     * Updates an deadline using the DeadlineContract provided
     * @param deadlineId to update
     * @param deadline to update, with the new values
     */
    public void update(String deadlineId, BaseDeadlineContract deadline) {
        DeadlineEntity deadlineEntity = deadlineRepository.findById(deadlineId).orElseThrow(() -> new NoSuchElementException("Invalid deadline ID"));

        deadlineEntity.setName(deadline.name());
        deadlineEntity.setDescription(deadline.description());
        deadlineEntity.setStartDate(deadline.startDate());
        deadlineEntity.setEndDate(deadline.endDate());

        deadlineRepository.save(deadlineEntity);
    }

}