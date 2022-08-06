package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseGroupContract;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupsServiceGrpc;
import org.springframework.stereotype.Service;

/**
 * Handles all services of groups on the client side. This includes:
 *  - Creating a group
 *  - Deleting a group
 *  - Getting a group's details
 *  - Modifying a group's details
 *  - Adding group members
 *  - Removing group members
 * as specified in the gRPC.
 */
@Service
public class GroupsClientService {
  @GrpcClient(value = "identity-provider-grpc-server")
  private GroupsServiceGrpc.GroupsServiceBlockingStub groupBlockingStub;

  /**
   * Handles creating a group when given a BaseGroupContract.
   *
   * @param groupContract the base contract to use for establishing a new group
   * @return              a CreateGroupResponse with either a success or error
   */
  public CreateGroupResponse createGroup(BaseGroupContract groupContract) {
    // Ensures that the user is authorized to perform the request

    CreateGroupRequest groupRequest = CreateGroupRequest.newBuilder()
        .setLongName(groupContract.longName())
        .setShortName(groupContract.shortName())
        .build();
    return groupBlockingStub.createGroup(groupRequest);
  }
}
