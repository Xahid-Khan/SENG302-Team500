package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.database.GroupMemberRepository;
import nz.ac.canterbury.seng302.identityprovider.database.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GroupServerServiceTest {

    @Autowired
    private GroupsServerService groupsServerService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private RegisterServerService registerServerService;

    @Autowired
    private UserRepository userRepository;

    private CreateGroupRequest.Builder createGroupRequest =
            CreateGroupRequest.newBuilder()
                    .setShortName("TG")
                    .setLongName("Test Group");

    private UserRegisterRequest.Builder createUserOneRequest =
            UserRegisterRequest.newBuilder()
                    .setUsername("Username")
                    .setPassword("Password")
                    .setFirstName("FirstName")
                    .setMiddleName("Middle Names")
                    .setLastName("LastName")
                    .setNickname("Nickname")
                    .setBio("Bio")
                    .setPersonalPronouns("Pronoun1/Pronoun2")
                    .setEmail("email@email.email");

    private UserRegisterRequest.Builder createUserTwoRequest =
            UserRegisterRequest.newBuilder()
                    .setUsername("UsernameTwo")
                    .setPassword("PasswordTwo")
                    .setFirstName("FirstNameTwo")
                    .setMiddleName("Middle NamesTwo")
                    .setLastName("LastNameTwo")
                    .setNickname("NicknameTwo")
                    .setBio("BioTwo")
                    .setPersonalPronouns("Pronoun1/Pronoun2")
                    .setEmail("emailTwo@email.email");


    @BeforeEach
    private void clearDatabase() {
        groupRepository.deleteAll();
        groupMemberRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void populateGroups() {
        groupsServerService.createGroup(createGroupRequest.build());
    }

    /**
     * Tests creating group
     */
    @Test
    public void createGroup() {
        var response = groupsServerService.createGroup(createGroupRequest.build());

        assertTrue(response.getIsSuccess());

        assertNotNull(groupRepository.findByShortName("TG"));
    }

    /**
     * Tests adding group members to a group
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Test
    public void addValidUsersToGroup() throws NoSuchAlgorithmException, InvalidKeySpecException {
        //Creates group, adds two users. Makes service request to add both users to group
        var createGroupResponse = groupsServerService.createGroup(createGroupRequest.build());
        var userOneResponse = registerServerService.register(createUserOneRequest.build());
        var userTwoResponse = registerServerService.register(createUserTwoRequest.build());
        AddGroupMembersRequest.Builder addGroupMembersRequest =
                AddGroupMembersRequest.newBuilder()
                        .setGroupId(createGroupResponse.getNewGroupId())
                        .addUserIds(userTwoResponse.getNewUserId())
                        .addUserIds(userOneResponse.getNewUserId());

        //Uses the service to add the users to the group
        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
        assertTrue(addGroupMembersResponse.getIsSuccess());

        //Checks that the users one and two are in the group
        var groupMembers = groupMemberRepository.findById(createGroupResponse.getNewGroupId());
        for (Integer id : groupMembers.get().getUserId()) {
            assertTrue(id==userOneResponse.getNewUserId() || id==userTwoResponse.getNewUserId());
        }
    }

    /**
     * Tests adding group members to a group
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Test
    public void addDeleteUsersFromGroup() throws NoSuchAlgorithmException, InvalidKeySpecException {
        //Creates group, adds two users. Makes service request to add both users to group
        var createGroupResponse = groupsServerService.createGroup(createGroupRequest.build());
        var userOneResponse = registerServerService.register(createUserOneRequest.build());
        var userTwoResponse = registerServerService.register(createUserTwoRequest.build());
        AddGroupMembersRequest.Builder addGroupMembersRequest =
                AddGroupMembersRequest.newBuilder()
                        .setGroupId(createGroupResponse.getNewGroupId())
                        .addUserIds(userTwoResponse.getNewUserId())
                        .addUserIds(userOneResponse.getNewUserId());

        //Uses the service to add the users to the group
        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
        assertTrue(addGroupMembersResponse.getIsSuccess());

        //Checks that the users one and two are in the group
        var groupMembers = groupMemberRepository.findById(createGroupResponse.getNewGroupId());
        for (Integer id : groupMembers.get().getUserId()) {
            assertTrue(id==userOneResponse.getNewUserId() || id==userTwoResponse.getNewUserId());
        }

        // Removes user one from the group
        RemoveGroupMembersRequest.Builder removeGroupMembersRequest =
                RemoveGroupMembersRequest.newBuilder()
                        .setGroupId(createGroupResponse.getNewGroupId())
                        .addUserIds(userOneResponse.getNewUserId());

        RemoveGroupMembersResponse removeGroupMembersResponse = groupsServerService.removeGroupMembers(removeGroupMembersRequest.build());

        // Checks that the user one is not in the group
        groupMembers = groupMemberRepository.findById(createGroupResponse.getNewGroupId());
        assertTrue(removeGroupMembersResponse.getIsSuccess());
        for (Integer id : groupMembers.get().getUserId()) {
            assertFalse(id==userOneResponse.getNewUserId());
        }
    }



}
