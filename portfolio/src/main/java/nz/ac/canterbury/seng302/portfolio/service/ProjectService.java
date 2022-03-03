package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.ProjectMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.NoSuchElementException;


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


    /**
     * This method will fetch all the projects and return them in json file
     * @return a iterable list containing all the projects
     */
    public ArrayList<ProjectContract> allProjects(){
        Iterable<ProjectEntity> result = projectRepository.findAll();
        ArrayList<ProjectContract> allProjects = new ArrayList<ProjectContract>();

        for(ProjectEntity project : result) {
            allProjects.add(projectMapper.toContract(project));
        }

        return allProjects;
    }

    /**
     * This method will get a specific project given the id, if it exist in the database
     * else, it will throw an Exception
     * @param id which is a long
     * @throws NoSuchElementException is raised if project ID is not in database
     * @return project entity
     */
    public ProjectContract getById(long id) {
        return projectMapper.toContract(projectRepository.findById(id).orElseThrow());
    }



}