package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.GroupRepositoryMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.GroupRepositoryContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseGroupRepositoryContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.GroupRepositoryEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.GroupRepositoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to test the GroupRepositoryService class using mocking
 */
@SpringBootTest
public class GroupRepositoryServiceTest {
    @InjectMocks
    private GroupRepositoryService groupRepositoryService;

    @Mock
    private GroupRepositoryRepository groupRepositoryRepository;

    @Mock
    private GroupRepositoryMapper groupRepositoryMapper;

    private GroupRepositoryContract repoContract1;
    private GroupRepositoryEntity repoEntity1;
    private BaseGroupRepositoryContract emptyRepoContract1;
    private GroupRepositoryEntity emptyRepoEntity1;
    private GroupRepositoryContract repo2;
    private BaseGroupRepositoryContract emptyRepo2;

    private List<GroupRepositoryEntity> repoList;

    private List<GroupRepositoryContract> contractList;

    @BeforeEach
    void setup() {
        groupRepositoryRepository.deleteAll();
        repoContract1 = new GroupRepositoryContract(1, 1, "ABCTOKEN");
        repoEntity1 = new GroupRepositoryEntity(1, 1, "ABCTOKEN");
        emptyRepoContract1 = new BaseGroupRepositoryContract(2);
        emptyRepoEntity1 = new GroupRepositoryEntity(2);
//        repo2 = new GroupRepositoryContract(5,1340,"BACTOKEN");
//        emptyRepo2 = new BaseGroupRepositoryContract(20);
        repoList = new ArrayList<>();
        repoList.add(new GroupRepositoryEntity(repoContract1.groupId(), repoContract1.groupId(), repoContract1.token()));
//        repoList.add(new GroupRepositoryEntity(emptyRepo1.groupId()));
//        repoList.add(new GroupRepositoryEntity(repo2.groupId(), repo2.groupId(), repo2.token()));
//        repoList.add(new GroupRepositoryEntity(emptyRepo2.groupId()));

        repoList.forEach(repo -> {
            groupRepositoryRepository.save(repo);
            //convert to contract and add to list

        });
    }
//    /**
//     * Tests the add method of the GroupRepositoryService class
//     */
//    public void testAdd() {
//
//
//    }
    //Create unit tests for all methods in GroupRepositoryService

//    @Test
//    void getRepositoryByIDAndExpectPass() {
//        //add a repository to the database
//        final int GROUP_ID = 5;
//        groupRepositoryService.add(GROUP_ID);
//
//        //retrieve the repository from the database
//        var repository = groupRepositoryService.get(Integer.toString(GROUP_ID));
//    }

//    @Test
//    void getAll() {
//        Mockito.when(groupRepositoryService.getAll()).thenReturn(repoList);
//
//
//
//        List<GroupRepositoryContract> result= groupRepositoryService.getAll();
////        assert(result.size() == 4);
//
//        for (int i=0; i<result.size(); i++) {
//            Assertions.assertEquals(repoList.get(i).getId(), result.get(i).groupId());;
//            Assertions.assertEquals(repoList.get(i).getRepositoryID(), result.get(i).repositoryId());
//            Assertions.assertEquals(repoList.get(i).getToken(), result.get(i).token());
//        }
//    }

    @Test
    void add() {
//        Mockito.when(groupRepositoryRepository.save(emptyRepoEntity1.getId())).thenReturn(emptyRepoEntity1);
        Mockito.when(groupRepositoryRepository.existsById(emptyRepoEntity1.getId())).thenReturn(false);
        doReturn(emptyRepoEntity1).when(groupRepositoryRepository).save(any());

        //checks that the entity is being added to the database
        var result = groupRepositoryService.add(emptyRepoContract1.groupId());
//        assertEquals(emptyRepoEntity1, result);
        //assert sizr of database is 1
        assertEquals(emptyRepoEntity1.getId(), result.getId());
    }

    /**
     * Tests adding the same repo twice
     */
    @Test
    void addTwice() {
        Mockito.when(groupRepositoryRepository.save(repoEntity1)).thenReturn(repoEntity1);
//        doReturn(repoEntity1).when(groupRepositoryRepository).save(any());

        //checks that the entity is being added to the database
        var result = groupRepositoryService.add(repoContract1.groupId());
        assertEquals(repoEntity1, result);

        //checks that the entity is being added to the database
        var result2 = groupRepositoryService.add(1);
        assertEquals(repoEntity1, result2);
        //check that IllegalArgumentException is thrown

    }

    /**
     * Tests deleting a repo with a group id that exists
     */
    @Test
    void deleteARepoThatExistsExpectPass() {
        //When the id exists in the database then mock that the statement returns true
        Mockito.when(groupRepositoryRepository.existsById("5")).thenReturn(true);
        assertTrue(groupRepositoryService.delete(5));
    }

    /**
     * Tests deleting a repo with twice, the first valid the 2nd invalid
     */
    @Test
    void deleteARepoTwiceExpectFail() {
        //When the id exists in the database then mock that the statement returns true
        Mockito.when(groupRepositoryRepository.existsById("5")).thenReturn(true).thenReturn(false);
        assertTrue(groupRepositoryService.delete(5));
        assertFalse(groupRepositoryService.delete(5));
    }

    @Test
    /**
     * Deletes a repository that doesn't exist
     */
    void deleteARepoThatDoesNotExistExpectFail() {
        assertFalse(groupRepositoryService.delete(2));
//
//    @Test
//    void update() {
//    }
    }
}
