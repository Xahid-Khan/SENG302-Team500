package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.database.GroupMemberRepository;
import nz.ac.canterbury.seng302.identityprovider.database.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AddGroupMembersRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
        assertTrue(addGroupMembersResponse.getIsSuccess());

        var groupMembers = groupMemberRepository.findById(createGroupResponse.getNewGroupId());
        for (Integer id : groupMembers.get().getUserId()) {
            assertTrue(userRepository.findById(id).isPresent());
        }

    }



}
