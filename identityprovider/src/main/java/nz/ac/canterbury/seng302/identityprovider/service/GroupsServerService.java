package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.ArrayList;
import java.util.List;

import nz.ac.canterbury.seng302.identityprovider.database.*;
import nz.ac.canterbury.seng302.identityprovider.mapping.UserMapper;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service handles all requests on the server side to manage groups.
 */
@Service
public class GroupsServerService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    /**
     * Handles the functionality on the server side for creating groups. It will ensure that both the
     * short name and the long name of the group are unique. If they are, it will add the group to the
     * database. Otherwise, it will fail and return with validation errors.
     *
     * @param groupRequest the CreateGroupRequest gRPC message
     * @return a CreateGroupResponse gRPC message with potential errors
     */
    public CreateGroupResponse createGroup(CreateGroupRequest groupRequest) {
        List<ValidationError> validationErrors = new ArrayList<>();
        // Ensure that both the short name and long name are unique
        if (groupRepository.findByLongName(groupRequest.getLongName()) != null) {
            validationErrors.add(
                    ValidationError.newBuilder()
                            .setFieldName("longName")
                            // Potentially add which group it's clashing with here?
                            .setErrorText("Error: Long name must be unique")
                            .build());
        }

        if (groupRepository.findByShortName(groupRequest.getShortName()) != null) {
            validationErrors.add(
                    ValidationError.newBuilder()
                            .setFieldName("shortName")
                            // Potentially add which group it's clashing with here?
                            .setErrorText("Error: Short name must be unique")
                            .build());
        }

        if (validationErrors.isEmpty()) {
            GroupModel group = new GroupModel(groupRequest.getShortName(), groupRequest.getLongName());
            groupRepository.save(group);
            return CreateGroupResponse.newBuilder()
                    .setIsSuccess(true)
                    .setNewGroupId(group.getId())
                    .setMessage("Group successfully created")
                    .build();
        } else {
            return CreateGroupResponse.newBuilder()
                    .setIsSuccess(false)
                    .setNewGroupId(-1)
                    .setMessage("Validation error(s)")
                    .addAllValidationErrors(validationErrors)
                    .build();
        }
    }

    /**
     * Handles the functionality on the server side for deleting groups. If the database does not have
     * a group with the corresponding ID, then a failure will be returned. Otherwise, the group will
     * be deleted, and a success will be returned.
     *
     * @param groupRequest the DeleteGroupRequest gRPC message
     * @return a DeleteGroupResponse gRPC message
     */
    public DeleteGroupResponse deleteGroup(DeleteGroupRequest groupRequest) {
        if (groupRepository.findById(groupRequest.getGroupId()).isEmpty()) {
            return DeleteGroupResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Error: Group does not exist")
                    .build();
        } else {
            groupRepository.deleteById(groupRequest.getGroupId());
            return DeleteGroupResponse.newBuilder()
                    .setIsSuccess(true)
                    .setMessage("Group successfully deleted")
                    .build();
        }
    }

    /**
     * Handles the functionality on the server side for adding members to a group. If the database
     * does not have a group with the corresponding ID, then a failure will be returned. If the database
     * does have a group with the corresponding ID, then the members will be updated. If members cannot
     * be updated, then a failure will be returned. Otherwise, a success will be returned.
     *
     * @param groupRequest the AddGroupMembersRequest gRPC message
     * @return a AddGroupMembersResponse gRPC message
     */
    public AddGroupMembersResponse addGroupMembers(AddGroupMembersRequest groupRequest) {
        //Checks groups existence
        if (groupRepository.findById(groupRequest.getGroupId()).isEmpty()) {
            return AddGroupMembersResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Error: Group does not exist")
                    .build();
        } else {
            //Checks if group exists in groupMember table
            if (groupMemberRepository.findById(groupRequest.getGroupId()).isEmpty()) {
                //create new entry in groupMember table
                GroupMemberModel groupMember = new GroupMemberModel(groupRequest.getGroupId(), groupRequest.getUserIdsList());
                groupMemberRepository.save(groupMember);
            } else {
                //update groupMember table
                GroupMemberModel groupMember = groupMemberRepository.findById(groupRequest.getGroupId()).get();
                String message = groupMember.addUserIds(groupRequest.getUserIdsList());
                groupMemberRepository.save(groupMember);
            }

            return AddGroupMembersResponse.newBuilder()
                    .setIsSuccess(true)
                    .setMessage("Members successfully added")
                    .build();
        }

    }

    /**
     * Handles the functionality on the server side for removing members from a group. If the database
     * does not have a group with the corresponding ID, then a failure will be returned. If the database
     * does have a group with the corresponding ID, then the members will be updated. If members cannot
     * be updated, then a failure will be returned. Otherwise, a success will be returned.
     *
     * @param groupRequest the AddGroupMembersRequest gRPC message
     * @return a AddGroupMembersResponse gRPC message
     */
    public RemoveGroupMembersResponse removeGroupMembers(RemoveGroupMembersRequest groupRequest) {
        //Checks groups existence
        if (groupRepository.findById(groupRequest.getGroupId()).isEmpty()) {
            return RemoveGroupMembersResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Error: Group does not exist")
                    .build();
        } else {
            //Gets the group from the repository and adds the user Ids to the group
            var group = groupMemberRepository.findById(groupRequest.getGroupId()).orElseThrow();
            String message = group.removeUserIds(groupRequest.getUserIdsList());

            // If the message is not success, then there was an error
            if (!message.equals("Success")) {
                return RemoveGroupMembersResponse.newBuilder()
                        .setIsSuccess(false)
                        .setMessage(message)
                        .build();
            } else {

                //Success,  save and return response
                groupMemberRepository.save(group);
                return RemoveGroupMembersResponse.newBuilder()
                        .setIsSuccess(true)
                        .setMessage("Group members successfully removed")
                        .build();
            }
        }
    }

//    public GetGroupDetailsResponse getGroupDetails(GetGroupDetailsRequest groupRequest) {
//        var group = groupRepository.findById(groupRequest.getGroupId()).orElseThrow();
//        var groupMembers = groupMemberRepository.findById(groupRequest.getGroupId()).orElseThrow();
//        List<UserResponse> userList = new ArrayList<>();
//        for (Integer userId : groupMembers.getUserId()) {
//          var userFound = userRepository.findById(userId);
//          return userMapper.toUserResponse(userFound.orElseThrow());
//        }
//        return GetGroupDetailsResponse.newBuilder()
//                .setShortName(group.getShortName())
//                .setLongName(group.getLongName())
//                .addMembers();
//
//    }
}

