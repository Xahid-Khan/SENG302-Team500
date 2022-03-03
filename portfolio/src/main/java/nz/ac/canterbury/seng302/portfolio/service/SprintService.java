package nz.ac.canterbury.seng302.portfolio.service;

import java.util.NoSuchElementException;
import javax.transaction.Transactional;
import nz.ac.canterbury.seng302.portfolio.mapping.SprintMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service that manages CRUD operations for sprints.
 */
@Service
@Transactional
public class SprintService {
    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SprintMapper sprintMapper;

    /**
     * Retrieve the sprint with the given ID.
     *
     * @param id of the contract to get
     * @throws NoSuchElementException if the id is invalid
     * @return Sprint with the given ID
     */
    public SprintContract get(long id) {
        return sprintMapper.toContract(sprintRepository.findById(id).orElseThrow());
    }

    /**
     * Creates a SprintContract, returning it with its ID and order number.
     *
     * @param sprint to create. orderNumber and sprintId are ignored.
     * @throws NoSuchElementException if the project id is invalid
     * @return Sprint that was created, including the orderNumber generated.
     */
    public SprintContract create(SprintContract sprint) {
        var project = projectRepository.findById(sprint.projectId()).orElseThrow();

        var entity = sprintMapper.toEntity(sprint, project.getSprints().size() + 1);
        project.addSprint(entity);
        sprintRepository.save(entity);
        projectRepository.save(project);

        return sprintMapper.toContract(entity);
    }

    /**
     * Deletes a sprint, including removing it from its parent project.
     *
     * @param sprintId to delete
     * @throws NoSuchElementException if the id is invalid
     */
    public void delete(long sprintId) {
        var sprint = sprintRepository.findById(sprintId).orElseThrow();
        var project = sprint.getProject();

        project.removeSprint(sprint);
        sprintRepository.save(sprint);
        projectRepository.save(project);
    }

    /**
     * Updates a sprint using the SprintContract data provided.
     *
     * @param sprint to update, with the update fields filled. projectId and orderNumber is ignored.
     * @throws NoSuchElementException if the id is invalid
     */
    public void update(SprintContract sprint) {
        var sprintEntity = sprintRepository.findById(sprint.sprintId()).orElseThrow();

        sprintEntity.setName(sprint.name());
        sprintEntity.setDescription(sprint.description());
        sprintEntity.setStartDate(sprint.startDate());
        sprintEntity.setEndDate(sprint.endDate());

        sprintRepository.save(sprintEntity);
    }
}