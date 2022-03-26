package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyRoleOfUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This service handles adding and removing roles to/from users, as according to the
 * user_accounts.proto file.
 */
@GrpcService
public class RoleService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

  @Autowired
  private UserRepository repository;

  // Helper function to reduce duplicate code.
  // Gets the user, checks if modification is legal (returns false if not), modifies, saves.
  // If addingRole is true, role will be added. If false, role will be deleted.
  private boolean modifyRoleOfUser(
      ModifyRoleOfUserRequest modificationRequest, boolean addingRole) {
    UserModel user = repository.findById(modificationRequest.getUserId());
    UserRole roleToChange = modificationRequest.getRole();

    // If the role is being added, the user should not have the role.
    // If the role is being deleted, the user should have the role.
    if (addingRole != user.getRoles().contains(roleToChange)) {
      if (addingRole) {
        user.addRole(roleToChange);
      } else {
        user.deleteRole(roleToChange);
      }
      repository.save(user);
      return true;
    }
    return false;
  }

  /**
   * Adds a role to the user if the user does not already have the role.
   *
   * @param modificationRequest The modification request for the role
   * @param responseObserver An observer to send back a response
   */
  @Override
  public void addRoleToUser(
      ModifyRoleOfUserRequest modificationRequest,
      StreamObserver<UserRoleChangeResponse> responseObserver) {
    // Generate a response based on if modifyRoleOfUser was successful or not
    var response =
        modifyRoleOfUser(modificationRequest, true)
            ? UserRoleChangeResponse.newBuilder().setIsSuccess(true).setMessage(true)
            : UserRoleChangeResponse.newBuilder().setIsSuccess(false).setMessage(false);
    // Return the response message
    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  /**
   * Removes a role to the user if the user does not already have the role.
   *
   * @param modificationRequest The modification request for the role
   * @param responseObserver An observer to send back a response
   */
  @Override
  public void removeRoleFromUser(
      ModifyRoleOfUserRequest modificationRequest,
      StreamObserver<UserRoleChangeResponse> responseObserver) {
    // Generate a response based on if modifyRoleOfUser was successful or not
    var response =
        modifyRoleOfUser(modificationRequest, false)
            ? UserRoleChangeResponse.newBuilder().setIsSuccess(true).setMessage(true)
            : UserRoleChangeResponse.newBuilder().setIsSuccess(false).setMessage(false);
    // Return the response message
    responseObserver.onNext(response.build());
    responseObserver.onCompleted();
  }

  public UserRepository getRepository() {
    return repository;
  }
}
