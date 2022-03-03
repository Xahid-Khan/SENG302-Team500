package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.ProjectMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * A project service, which implements all the CRUD (Create, Read, Update, Delete) processes.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    /**
     * Create a Project Contract, return it with project ID number.
     * @param contract a contract received from application.
     */
    public ProjectContract create(ProjectContract contract){
        var project = projectMapper.toEntity(contract);
        projectRepository.save(project);
        return contract;
    }


    /**
     * Delete a Project Entity, return void.
     * @param projectId project ID of the project that needs to be deleted.
     */

    public void delete(long projectId) {
        projectRepository.deleteById(projectId);
    }
}