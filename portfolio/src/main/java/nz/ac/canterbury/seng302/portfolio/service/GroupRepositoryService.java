package nz.ac.canterbury.seng302.portfolio.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nz.ac.canterbury.seng302.portfolio.mapping.GroupRepositoryMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.GroupRepositoryContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.GroupRepositoryEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.GroupRepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/** A service that manages CRUD operations for group repository settings. */
public class GroupRepositoryService {
  @Autowired private GroupRepositoryRepository groupRepositoryRepository;

  @Autowired private GroupRepositoryMapper groupRepositoryMapper;

  /**
   * Retrieve the group repository with the given ID.
   *
   * @param id of the contract to get
   * @return GroupRepository with the given ID
   * @throws NoSuchElementException if the id is invalid
   */
  public GroupRepositoryContract get(String id) {
    if (groupRepositoryRepository.existsById(id)) {
      return null;
    }
    var groupRepository = groupRepositoryRepository.findById(id).get();
    return groupRepositoryMapper.toContract(groupRepository);
  }

  /**
   * Retrieve all group repositories.
   *
   * @return List of all group repositories
   */
  public List<GroupRepositoryContract> getAll() {
    Iterable<GroupRepositoryEntity> result = groupRepositoryRepository.findAll();

    ArrayList<GroupRepositoryContract> allRepos = new ArrayList<>();

    for (GroupRepositoryEntity repo : result) {
      allRepos.add(groupRepositoryMapper.toContract(repo));
    }

    return allRepos;
  }

  /**
   * Adds a group repository to the database with the given ID.
   *
   * @param id to associate with the group repository (this should be done on group creation)
   */
  public GroupRepositoryContract add(int id) {
    // checks if the group repository already exists
    if (groupRepositoryRepository.existsById(Integer.toString(id))) {
      return null;
    }
    var groupRepository = new GroupRepositoryEntity(id);
    var result = groupRepositoryRepository.save(groupRepository);

    return groupRepositoryMapper.toContract(result);
  }

  /** Deletes a group repository from the database with the given ID. */
  public boolean delete(int id) {
    // checks if the group repository exists
    if (!groupRepositoryRepository.existsById(Integer.toString(id))) {
      return false;
    }
    groupRepositoryRepository.deleteById(Integer.toString(id));
    return true;
  }

  /**
   * Updates a group repository in the database with the given ID. Sets the repositoryID and token
   */
  public boolean update(int id, int repositoryId, String token) {
    GroupRepositoryEntity groupRepository;
    List<GroupRepositoryEntity> groupRepositories =
        groupRepositoryRepository.findGroupRepositoryEntityByGroupId(id);
    // checks if the group repository exists
    if (!groupRepositories.isEmpty()) {
      groupRepository = groupRepositories.get(0);
      groupRepository.setRepositoryID(repositoryId);
      groupRepository.setToken(token);
      groupRepositoryRepository.save(groupRepository);

      return true;
    }
    return false;
  }
}
