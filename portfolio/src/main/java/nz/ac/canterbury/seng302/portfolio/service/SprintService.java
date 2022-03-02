package nz.ac.canterbury.seng302.portfolio.service;

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
public class SprintService {
    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private SprintMapper sprintMapper;

    /**
     * Creates a SprintContract, returning it with its ID and order number.
     */
    public SprintContract create(SprintContract sprint) {
        var project = projectRepository.findById(sprint.projectId()).orElseThrow();

        var entity = sprintMapper.toEntity(sprint, project.getSprints().size() + 1);
        project.addSprint(entity);
        sprintRepository.save(entity);

        return sprintMapper.toContract(entity);
    }
}