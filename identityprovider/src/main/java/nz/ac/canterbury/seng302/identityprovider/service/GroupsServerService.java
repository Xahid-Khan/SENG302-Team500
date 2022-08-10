package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.ArrayList;
import java.util.List;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.database.*;
import nz.ac.canterbury.seng302.identityprovider.mapping.GroupMapper;
import nz.ac.canterbury.seng302.identityprovider.mapping.UserMapper;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.PaginationResponseOptions;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service handles all requests on the server side to manage groups.
 */
@GrpcService
public class GroupsServerService extends GroupsServiceGrpc.GroupsServiceImplBase {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GetUserService getUserService;

    private String groupDoesntExistMessage = "Error: Group does not exist";

    /**
     * Handles the functionality on the server side for creating groups. It will ensure that both the
     * short name and the long name of the group are unique. If they are, it will add the group to the
     * database. Otherwise, it will fail and return with validation errors.
     *
     * @param request the CreateGroupRequest gRPC message
     * @return a CreateGroupResponse gRPC message with potential errors
     */
    @Override
    public void createGroup(CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
        List<ValidationError> validationErrors = new ArrayList<>();
        // Ensure that both the short name and long name are unique
        if (groupRepository.findByLongName(request.getLongName()) != null) {
            validationErrors.add(
                    ValidationError.newBuilder()
                            .setFieldName("longName")
                            // Potentially add which group it's clashing with here?
                            .setErrorText("Error: Long name must be unique")
                            .build());
        }

        if (groupRepository.findByShortName(request.getShortName()) != null) {
            validationErrors.add(
                    ValidationError.newBuilder()
                            .setFieldName("shortName")
                            // Potentially add which group it's clashing with here?
                            .setErrorText("Error: Short name must be unique")
                            .build());
        }

        if (validationErrors.isEmpty()) {
            GroupModel group = new GroupModel(request.getShortName(), request.getLongName());
            groupRepository.save(group);

            //Creates a groupMember entity with the same id as the group
            GroupMemberModel groupMember = new GroupMemberModel(group.getId(), new ArrayList<>());
            groupMemberRepository.save(groupMember);

            responseObserver.onNext(CreateGroupResponse.newBuilder()
                    .setIsSuccess(true)
                    .setNewGroupId(group.getId())
                    .setMessage("Group successfully created")
                    .build());
        } else {
            responseObserver.onNext(CreateGroupResponse.newBuilder()
                    .setIsSuccess(false)
                    .setNewGroupId(-1)
                    .setMessage("Validation error(s)")
                    .addAllValidationErrors(validationErrors)
                    .build());
        }
        responseObserver.onCompleted();
    }

    /**
     * Handles the functionality on the server side for deleting groups. If the database does not have
     * a group with the corresponding ID, then a failure will be returned. Otherwise, the group will
     * be deleted, and a success will be returned.
     *
     * @param request the DeleteGroupRequest gRPC message
     * @return a DeleteGroupResponse gRPC message
     */
    @Override
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        if (groupRepository.findById(request.getGroupId()).isEmpty()) {
            responseObserver.onNext(DeleteGroupResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage(groupDoesntExistMessage)
                    .build());
        } else {
            groupRepository.deleteById(request.getGroupId());
            groupMemberRepository.deleteById(request.getGroupId());

            responseObserver.onNext(DeleteGroupResponse.newBuilder()
                    .setIsSuccess(true)
                    .setMessage("Group successfully deleted")
                    .build());
        }
        responseObserver.onCompleted();
    }

    /**
     * Handles the functionality on the server side for adding members to a group. If the database
     * does not have a group with the corresponding ID, then a failure will be returned. If the database
     * does have a group with the corresponding ID, then the members will be updated. If members cannot
     * be updated, then a failure will be returned. Otherwise, a success will be returned.
     *
     * @param request the AddGroupMembersRequest gRPC message
     * @return a AddGroupMembersResponse gRPC message
     */
    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {
        //Checks groups existence
        if (groupRepository.findById(request.getGroupId()).isEmpty() || groupMemberRepository.findById(request.getGroupId()).isEmpty()) {
            responseObserver.onNext(AddGroupMembersResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage(groupDoesntExistMessage)
                    .build());
        } else {

            //update groupMember table
            GroupMemberModel groupMember = groupMemberRepository.findById(request.getGroupId()).get();
            String message = groupMember.addUserIds(request.getUserIdsList());

            if (!message.equals("Success")) {
                responseObserver.onNext(AddGroupMembersResponse.newBuilder()
                        .setIsSuccess(false)
                        .setMessage(message)
                        .build());
            } else {

                for (Integer userId : request.getUserIdsList()) {
                    if (groupRepository.findById(request.getGroupId()).get().getShortName().equals("Teachers")) {
                        roleService.addRoleToUser(ModifyRoleOfUserRequest.newBuilder()
                                .setUserId(userId)
                                .setRole(UserRole.TEACHER)
                                .build());
                    }
                }

                groupMemberRepository.save(groupMember);
                responseObserver.onNext(AddGroupMembersResponse.newBuilder()
                        .setIsSuccess(true)
                        .setMessage("Members successfully added")
                        .build());
            }

        }
        responseObserver.onCompleted();
    }

    /**
     * Handles the functionality on the server side for removing members from a group. If the database
     * does not have a group with the corresponding ID, then a failure will be returned. If the database
     * does have a group with the corresponding ID, then the members will be updated. If members cannot
     * be updated, then a failure will be returned. Otherwise, a success will be returned.
     *
     * @param request the AddGroupMembersRequest gRPC message
     * @return a AddGroupMembersResponse gRPC message
     */
    @Override
    public void removeGroupMembers(RemoveGroupMembersRequest request, StreamObserver<RemoveGroupMembersResponse> responseObserver) {
        //Checks groups existence
        if (groupRepository.findById(request.getGroupId()).isEmpty()) {
            responseObserver.onNext(RemoveGroupMembersResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage(groupDoesntExistMessage)
                    .build());
        } else {
            //Gets the group from the repository and adds the user Ids to the group
            var group = groupMemberRepository.findById(request.getGroupId()).orElseThrow();
            String message = group.removeUserIds(request.getUserIdsList());

            // If the message is not success, then there was an error
            if (!message.equals("Success")) {
                responseObserver.onNext(RemoveGroupMembersResponse.newBuilder()
                        .setIsSuccess(false)
                        .setMessage(message)
                        .build());
            } else {

                Integer nonGroupId = null;
                for (Integer userId: request.getUserIdsList()) {
                    boolean inOtherGroup = false;
                    for (GroupModel otherGroup: groupRepository.findAll()) {
                        if (otherGroup.getShortName().equals("Non Group")) {
                            nonGroupId = otherGroup.getId();
                        }
                        if (otherGroup.getId() != request.getGroupId()) {
                            GroupMemberModel groupInfo = groupMemberRepository.findById(otherGroup.getId()).orElseThrow();
                            for (Integer id : groupInfo.getUserIds()) {
                                if (id == userId) {
                                    inOtherGroup = true;
                                }
                            }
                        }
                    }
                    if (!inOtherGroup) {
                        GroupMemberModel nonGroup = groupMemberRepository.findById(nonGroupId).orElseThrow();
                        nonGroup.addNewMember(userId);
                        groupMemberRepository.save(nonGroup);
                    }
                }

                for (Integer userId : request.getUserIdsList()) {
                    if (groupRepository.findById(request.getGroupId()).get().getShortName().equals("Teachers")) {
                        UserResponse user = getUserService.getUserAccountById(GetUserByIdRequest.newBuilder()
                                .setId(userId)
                                .build());
                        if (user.getRolesList().contains(UserRole.TEACHER)) {
                            roleService.removeRoleFromUser(ModifyRoleOfUserRequest.newBuilder()
                                    .setUserId(userId)
                                    .setRole(UserRole.TEACHER)
                                    .build());
                        }
                    }
                }

                //Success,  save and return response
                groupMemberRepository.save(group);
                responseObserver.onNext(RemoveGroupMembersResponse.newBuilder()
                        .setIsSuccess(true)
                        .setMessage("Group members successfully removed")
                        .build());
            }
        }
        responseObserver.onCompleted();
    }


    /**
     * Handles the functionality on the server side for getting all groups. If the database does not have
     * any groups, then a failure will be returned. Otherwise, the all the groups in the database will be returned.
     *
     * @return a GetAllGroupsResponse gRPC message with all the groups
     */
    @Override
    public void getPaginatedGroups(GetPaginatedGroupsRequest request, StreamObserver<PaginatedGroupsResponse> responseObserver) {
        //all groups
        var allGroupsIterator = groupRepository.findAll();
        List<GroupDetailsResponse> allGroups = new ArrayList<>();
        for (GroupModel group : allGroupsIterator) {
            allGroups.add(groupMapper.toGroupDetailsResponse(group));
        }

        //if groups repository is empty
        if (allGroups.isEmpty()) {
            responseObserver.onNext(PaginatedGroupsResponse.newBuilder()
                    .setPaginationResponseOptions(PaginationResponseOptions.newBuilder()
                            .setResultSetSize(0).build())
                    .build());
        } else {
            responseObserver.onNext(PaginatedGroupsResponse.newBuilder()
                    .setPaginationResponseOptions(PaginationResponseOptions.newBuilder()
                            .setResultSetSize(allGroups.size()).build())
                    .addAllGroups(allGroups)
                    .build());
        }
        responseObserver.onCompleted();
    }
}

