package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/


import nz.ac.canterbury.seng302.portfolio.mapping.ProjectMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseProjectContract;
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
    // Code from initial commit
//    @Autowired
//    private ProjectRepository repository;
//
//    /**
//     * Get list of all projects
//     */
//    public List<Project> getAllProjects() {
//        List<Project> list = (List<Project>) repository.findAll();
//        return list;
//    }
//
//    /**
//     * Get project by id
//     */
//    public Project getProjectById(Integer id) throws Exception {
//
//        Optional<Project> project = repository.findById(id);
//        if(project!=null) {
//            return project.get();
//        }
//        else
//        {
//            throw new Exception("Project not found");
//        }
//    }
    /**
     * Create a Project Contract, return it with project ID number.
     * @param contract a contract received from application.
     * @return contart of the newly created project
     */
    public ProjectContract create(BaseProjectContract contract){
        var project = projectMapper.toEntity(contract);
        projectRepository.save(project);

        return projectMapper.toContract(project);
    }


    /**
     * Delete a Project Entity, return void.
     * @param projectId project ID of the project that needs to be deleted.
     */
    public void delete(String projectId) {
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
    public ProjectContract getById(String id) {
        return projectMapper.toContract(projectRepository.findById(id).orElseThrow());
    }


    /**
     * This method will update the current project details.
     * @param project contract of the new project
     * @param  id Type String of the project
     * @throws NoSuchElementException if the project does not exist
     */
    public void update(ProjectContract project, String id) {
        var projectEntity = projectRepository.findById(id).orElseThrow();
        projectEntity.setDescription(project.description());
        projectEntity.setName(project.name());
        projectEntity.setStartDate(project.startDate());
        projectEntity.setEndDate(project.endDate());

        projectRepository.save(projectEntity);
    }

}

