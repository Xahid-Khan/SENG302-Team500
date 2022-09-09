package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.GroupRepositoryMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.GroupRepositoryContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.GroupRepositoryEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.GroupRepositoryRepository;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
/**
 * A service that manages CRUD operations for group repository settings.
 */
public class GroupRepositoryService {
    @Autowired
    private GroupRepositoryRepository groupRepositoryRepository;

    @Autowired
    private GroupRepositoryMapper groupRepositoryMapper;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieve the group repository with the given ID.
     *
     * @param id of the contract to get
     * @throws NoSuchElementException if the id is invalid
     * @return GroupRepository with the given ID
     */
    public GroupRepositoryEntity get(String id) {
        var groupRepository = groupRepositoryRepository.findById(id).orElseThrow();
        return groupRepository;
    }

    /**
     * Retrieve all group repositories.
     * @return List of all group repositories
     */
    public List<GroupRepositoryContract> getAll() {
        Iterable<GroupRepositoryEntity> result = groupRepositoryRepository.findAll();

        ArrayList<GroupRepositoryContract> allRepos = new ArrayList<GroupRepositoryContract>();

        for(GroupRepositoryEntity repo : result) {
            allRepos.add(groupRepositoryMapper.toContract(repo));
        }

        return allRepos;
    }

    /**
     * Adds a group repository to the database with the given ID.
     * @param id to associate with the group repository (this should be done on group creation)
     */
    public void add(int id) {
        //checks if the group repository already exists
        if (groupRepositoryRepository.existsById(Integer.toString(id))) {
            throw new IllegalArgumentException("Group repository already exists");
        }
        var groupRepository = new GroupRepositoryEntity(id);
        groupRepositoryRepository.save(groupRepository);
    }

    /**
     * Deletes a group repository from the database with the given ID.
     */
    public void delete(int id) {
        //checks if the group repository exists
        if (!groupRepositoryRepository.existsById(Integer.toString(id))) {
            throw new IllegalArgumentException("Group repository does not exist");
        }
        groupRepositoryRepository.deleteById(Integer.toString(id));
    }

    /**
     * Updates a group repository in the database with the given ID. Sets the repositoryID and token
     */
    public void update(int id, int repositoryID, String token){
        //checks if the group repository exists
        if (!groupRepositoryRepository.existsById(Integer.toString(id))) {
            throw new IllegalArgumentException("Group repository does not exist");
        }
        var groupRepository = groupRepositoryRepository.findById(Integer.toString(id)).orElseThrow();
        groupRepository.setRepositoryID(repositoryID);
        groupRepository.setToken(token);
        groupRepositoryRepository.save(groupRepository);
    }
}
