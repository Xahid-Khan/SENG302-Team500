package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseGroupContract;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles all services of groups on the client side. This includes: - Creating a group - Deleting a
 * group - Getting a group's details - Modifying a group's details - Adding group members - Removing
 * group members as specified in the gRPC.
 */
@Service
public class GroupsClientService {
  @GrpcClient(value = "identity-provider-grpc-server")
  private GroupsServiceGrpc.GroupsServiceBlockingStub groupBlockingStub;

  /**
   * Handles creating a group when given a BaseGroupContract.
   *
   * @param groupContract the base contract to use for establishing a new group
   * @return a CreateGroupResponse with either a success or error(s)
   */
  public CreateGroupResponse createGroup(BaseGroupContract groupContract) {
    CreateGroupRequest groupRequest = CreateGroupRequest.newBuilder()
        .setLongName(groupContract.longName())
        .setShortName(groupContract.shortName())
        .build();

    return groupBlockingStub.createGroup(groupRequest);
  }

  /**
   * Handles deleting a group when given a group ID.
   *
   * @param groupId the ID of the group to delete
   * @return        a DeleteGroupResponse with either a success or errors(s)
   */
  public DeleteGroupResponse deleteGroup(int groupId) {
    return groupBlockingStub.deleteGroup(
        DeleteGroupRequest.newBuilder().setGroupId(groupId).build());
  }

    /**
     * Handles adding a group's members when given a group id
     * @param groupId the ID of the group to add to
     * @param userIds the user ids of the updated group
     * @return a AddGroupMembersResponse with either a success or errors(s)
     */
    public AddGroupMembersResponse addGroupMembers(int groupId, List<Integer> userIds) {
        return groupBlockingStub.addGroupMembers(
                AddGroupMembersRequest.newBuilder()
                        .setGroupId(groupId)
                        .addAllUserIds(userIds)
                        .build());
    }

  /**
   * Handles deleting a group's members when given a group id
   * @param groupId the ID of the group to add to
   * @param userIds the user ids of the updated group
   * @return a AddGroupMembersResponse with either a success or errors(s)
   */
    public RemoveGroupMembersResponse removeGroupMembers(int groupId, List<Integer> userIds) {
      return groupBlockingStub.removeGroupMembers(
              RemoveGroupMembersRequest.newBuilder()
                      .setGroupId(groupId)
                      .addAllUserIds(userIds)
                      .build());
    }

  /**
   * Handles getting a group's details when given a group id
   * @param groupId the ID of the group to get the details of
   * @return a GetGroupDetailsResponse which has the groups details
   */
  public GetGroupDetailsResponse getGroupDetails(int groupId) {
      return groupBlockingStub.getGroupDetails(
              GetGroupDetailsRequest.newBuilder().setGroupId(groupId).build());
    }
}
