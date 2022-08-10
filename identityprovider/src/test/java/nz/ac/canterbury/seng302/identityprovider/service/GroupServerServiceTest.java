//package nz.ac.canterbury.seng302.identityprovider.service;
//
//import nz.ac.canterbury.seng302.identityprovider.database.GroupMemberRepository;
//import nz.ac.canterbury.seng302.identityprovider.database.GroupRepository;
//import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
//import nz.ac.canterbury.seng302.shared.identityprovider.*;
//import nz.ac.canterbury.seng302.shared.util.PaginationRequestOptions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Tests for the GroupServerService.
// */
//@SpringBootTest
//public class GroupServerServiceTest {
//
//    @Autowired
//    private GroupsServerService groupsServerService;
//
//    @Autowired
//    private GroupRepository groupRepository;
//
//    @Autowired
//    private GroupMemberRepository groupMemberRepository;
//
//    @Autowired
//    private RegisterServerService registerServerService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private final CreateGroupRequest.Builder createGroupOneRequest =
//            CreateGroupRequest.newBuilder()
//                    .setShortName("TG")
//                    .setLongName("Test Group");
//
//    private final CreateGroupRequest.Builder createGroupTwoRequest =
//            CreateGroupRequest.newBuilder()
//                    .setShortName("TGTwo")
//                    .setLongName("Test GroupTwo");
//
//    private final UserRegisterRequest.Builder createUserOneRequest =
//            UserRegisterRequest.newBuilder()
//                    .setUsername("Username")
//                    .setPassword("Password")
//                    .setFirstName("FirstName")
//                    .setMiddleName("Middle Names")
//                    .setLastName("LastName")
//                    .setNickname("Nickname")
//                    .setBio("Bio")
//                    .setPersonalPronouns("Pronoun1/Pronoun2")
//                    .setEmail("email@email.email");
//
//    private final UserRegisterRequest.Builder createUserTwoRequest =
//            UserRegisterRequest.newBuilder()
//                    .setUsername("UsernameTwo")
//                    .setPassword("PasswordTwo")
//                    .setFirstName("FirstNameTwo")
//                    .setMiddleName("Middle NamesTwo")
//                    .setLastName("LastNameTwo")
//                    .setNickname("NicknameTwo")
//                    .setBio("BioTwo")
//                    .setPersonalPronouns("Pronoun1/Pronoun2")
//                    .setEmail("emailTwo@email.email");
//
//    private final PaginationRequestOptions.Builder paginationRequestOptions =
//            PaginationRequestOptions.newBuilder()
//                    .setOffset(0)//skip none
//                    .setLimit(1000)//get all the groups (a high number)
//                    .setOrderBy("id")
//                    .setIsAscendingOrder(true);
//
//    private GetPaginatedGroupsRequest getAllGroupsRequest =
//            GetPaginatedGroupsRequest.newBuilder()
//            .setPaginationRequestOptions(paginationRequestOptions.build())
//            .build();
//
//
//    @BeforeEach
//    private void clearDatabase() {
//        groupRepository.deleteAll();
//        groupMemberRepository.deleteAll();
//        userRepository.deleteAll();
//    }
//
//    private void populateGroups() {
//        groupsServerService.createGroup(createGroupOneRequest.build());
//    }
//
//    /**
//     * Tests creating group
//     */
//    @Test
//    public void createGroup() {
//        var response = groupsServerService.createGroup(createGroupOneRequest.build());
//
//        assertTrue(response.getIsSuccess());
//
//        assertNotNull(groupRepository.findByShortName("TG"));
//    }
//
//    /**
//     * Tests adding group members to a group
//     * @throws NoSuchAlgorithmException if the algorithm does not exist
//     * @throws InvalidKeySpecException if the key spec is invalid
//     */
//    @Test
//    public void addValidUsersToGroup() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //Creates group, adds two users. Makes service request to add both users to group
//        var createGroupResponse = groupsServerService.createGroup(createGroupOneRequest.build());
//        var userOneResponse = registerServerService.register(createUserOneRequest.build());
//        var userTwoResponse = registerServerService.register(createUserTwoRequest.build());
//        AddGroupMembersRequest.Builder addGroupMembersRequest =
//                AddGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupResponse.getNewGroupId())
//                        .addUserIds(userTwoResponse.getNewUserId())
//                        .addUserIds(userOneResponse.getNewUserId());
//
//        //Uses the service to add the users to the group
//        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
//        assertTrue(addGroupMembersResponse.getIsSuccess());
//
//        //Checks that the users one and two are in the group
//        var groupMembers = groupMemberRepository.findById(createGroupResponse.getNewGroupId());
//        for (Integer id : groupMembers.get().getUserIds()) {
//            assertTrue(id==userOneResponse.getNewUserId() || id==userTwoResponse.getNewUserId());
//        }
//    }
//    /**
//     * Tests adding group members to a group that does not exist
//     */
//    @Test
//    public void addUsersToGroupThatDoesNotExist() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //Creates group, adds two users. Makes service request to add both users to group
//        var createGroupResponse = groupsServerService.createGroup(createGroupOneRequest.build());
//        var userOneResponse = registerServerService.register(createUserOneRequest.build());
//        var userTwoResponse = registerServerService.register(createUserTwoRequest.build());
//        AddGroupMembersRequest.Builder addGroupMembersRequest =
//                AddGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupResponse.getNewGroupId()+1)
//                        .addUserIds(userTwoResponse.getNewUserId())
//                        .addUserIds(userOneResponse.getNewUserId());
//
//        //Adds the members to the group
//        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
//
//        assertFalse(addGroupMembersResponse.getIsSuccess());
//        assert(addGroupMembersResponse.getMessage().equals("Error: Group does not exist"));
//
//    }
//
//    /**
//     * Tests adding a member to a group twice
//     */
//    @Test
//    public void addUserToGroupTwice() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //Creates group, adds two users. Makes service request to add both users to group
//        var createGroupResponse = groupsServerService.createGroup(createGroupOneRequest.build());
//        var userOneResponse = registerServerService.register(createUserOneRequest.build());
//        var userTwoResponse = registerServerService.register(createUserTwoRequest.build());
//        AddGroupMembersRequest.Builder addGroupMembersRequest =
//                AddGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupResponse.getNewGroupId())
//                        .addUserIds(userTwoResponse.getNewUserId())
//                        .addUserIds(userOneResponse.getNewUserId());
//        //Adds the user to the group twice
//        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
//        var addGroupMembersTwiceResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
//        assertFalse(addGroupMembersTwiceResponse.getIsSuccess());
//        assert(addGroupMembersTwiceResponse.getMessage().equals("Error: User already in group"));
//    }
//
//    /**
//     * Tests deleting group members from a group
//     * @throws NoSuchAlgorithmException if the algorithm does not exist
//     * @throws InvalidKeySpecException if the key spec is inval
//     */
//    @Test
//    public void deleteUsersFromGroup() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //Creates group, adds two users. Makes service request to add both users to group
//        var createGroupResponse = groupsServerService.createGroup(createGroupOneRequest.build());
//        var userOneResponse = registerServerService.register(createUserOneRequest.build());
//        var userTwoResponse = registerServerService.register(createUserTwoRequest.build());
//        AddGroupMembersRequest.Builder addGroupMembersRequest =
//                AddGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupResponse.getNewGroupId())
//                        .addUserIds(userTwoResponse.getNewUserId())
//                        .addUserIds(userOneResponse.getNewUserId());
//
//        //Uses the service to add the users to the group
//        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
//        assertTrue(addGroupMembersResponse.getIsSuccess());
//
//        //Checks that the users one and two are in the group
//        var groupMembers = groupMemberRepository.findById(createGroupResponse.getNewGroupId());
//        for (Integer id : groupMembers.get().getUserIds()) {
//            assertTrue(id==userOneResponse.getNewUserId() || id==userTwoResponse.getNewUserId());
//        }
//
//        // Removes user one from the group
//        RemoveGroupMembersRequest.Builder removeGroupMembersRequest =
//                RemoveGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupResponse.getNewGroupId())
//                        .addUserIds(userOneResponse.getNewUserId());
//
//        RemoveGroupMembersResponse removeGroupMembersResponse = groupsServerService.removeGroupMembers(removeGroupMembersRequest.build());
//
//        // Checks that the user one is not in the group
//        groupMembers = groupMemberRepository.findById(createGroupResponse.getNewGroupId());
//        assertTrue(removeGroupMembersResponse.getIsSuccess());
//        for (Integer id : groupMembers.get().getUserIds()) {
//            assertNotEquals(id,userOneResponse.getNewUserId());
//        }
//    }
//
//    /**
//     * Tests deleting group member twice from a group
//     */
//    @Test
//    public void deleteUsersTwice() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //Creates group, adds two users. Makes service request to add both users to group
//        var createGroupResponse = groupsServerService.createGroup(createGroupOneRequest.build());
//        var userOneResponse = registerServerService.register(createUserOneRequest.build());
//        var userTwoResponse = registerServerService.register(createUserTwoRequest.build());
//        AddGroupMembersRequest.Builder addGroupMembersRequest =
//                AddGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupResponse.getNewGroupId())
//                        .addUserIds(userTwoResponse.getNewUserId())
//                        .addUserIds(userOneResponse.getNewUserId());
//
//        //Uses the service to add the users to the group
//        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
//        assertTrue(addGroupMembersResponse.getIsSuccess());
//
//        //Checks that the users one and two are in the group
//        var groupMembers = groupMemberRepository.findById(createGroupResponse.getNewGroupId());
//        for (Integer id : groupMembers.get().getUserIds()) {
//            assertTrue(id==userOneResponse.getNewUserId() || id==userTwoResponse.getNewUserId());
//        }
//
//        // Removes user one from the group
//        RemoveGroupMembersRequest.Builder removeGroupMembersRequest =
//                RemoveGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupResponse.getNewGroupId())
//                        .addUserIds(userOneResponse.getNewUserId());
//
//        RemoveGroupMembersResponse removeGroupMembersResponse = groupsServerService.removeGroupMembers(removeGroupMembersRequest.build());
//
//        // Removes user one from the group again
//        removeGroupMembersResponse = groupsServerService.removeGroupMembers(removeGroupMembersRequest.build());
//        assertFalse(removeGroupMembersResponse.getIsSuccess());
//        assert(removeGroupMembersResponse.getMessage().equals("Error: User not in group"));
//
//    }
//
//
//    /**
//     * Tests that the info from getting all groups is correct
//     */
//    @Test
//    public void getAllGroups() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //Creates group, adds two users
//        CreateGroupResponse createGroupOneResponse = groupsServerService.createGroup(createGroupOneRequest.build());
//        CreateGroupResponse createGroupTwoResponse = groupsServerService.createGroup(createGroupTwoRequest.build());
//        UserRegisterResponse userOneResponse = registerServerService.register(createUserOneRequest.build());
//        UserRegisterResponse userTwoResponse = registerServerService.register(createUserTwoRequest.build());
//
//        //Adds both users to group one
//        AddGroupMembersRequest.Builder addGroupMembersRequest =
//                AddGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupOneResponse.getNewGroupId())
//                        .addUserIds(userTwoResponse.getNewUserId())
//                        .addUserIds(userOneResponse.getNewUserId());
//
////       Adds user one to group two
//        AddGroupMembersRequest.Builder addGroupMembersRequestTwo =
//                AddGroupMembersRequest.newBuilder()
//                        .setGroupId(createGroupTwoResponse.getNewGroupId())
//                        .addUserIds(userOneResponse.getNewUserId());
//
//        //Uses the service to add the users to the group
//        var addGroupMembersResponse = groupsServerService.addGroupMembers(addGroupMembersRequest.build());
//        var addGroupMembersResponseTwo = groupsServerService.addGroupMembers(addGroupMembersRequestTwo.build());
//        assertTrue(addGroupMembersResponse.getIsSuccess());
//        assertTrue(addGroupMembersResponseTwo.getIsSuccess());
//
//        //Gets all groups
//        PaginatedGroupsResponse allGroupsResponse = groupsServerService.getAllGroupDetails(getAllGroupsRequest);
//
//        //Checks that the two groups are in the list
//        assertEquals(2,allGroupsResponse.getPaginationResponseOptions().getResultSetSize());
//
//        //Checks that the two users are in group one of the allGroupsResponse and that there are two members
//        assert(allGroupsResponse.getGroups(0).getMembersList().size()==2);
//        allGroupsResponse.getGroups(0).getMembersList().forEach(member -> {
//            assertTrue(member.getId() == userOneResponse.getNewUserId() || member.getId()== userTwoResponse.getNewUserId());
//        } );
//
//        //Checks that user one is in the group two and the user two is not are in group two of the allGroupsResponse
//        assert(allGroupsResponse.getGroups(1).getMembersList().size()==1);
//        allGroupsResponse.getGroups(1).getMembersList().forEach(member -> {
//            assertTrue(member.getId() == userOneResponse.getNewUserId() || member.getId()!= userTwoResponse.getNewUserId());
//        } );
//    }
//
//    /**
//     * Tests getting all groups when there are no groups
//     */
//    @Test
//    public void getAllGroupsNoGroups() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //Gets all groups
//        PaginatedGroupsResponse allGroupsResponse = groupsServerService.getAllGroupDetails(getAllGroupsRequest);
//
//        //Checks that the groups are in the list
//        assertEquals(0,allGroupsResponse.getPaginationResponseOptions().getResultSetSize());
//    }
//
//
//
//
//}
