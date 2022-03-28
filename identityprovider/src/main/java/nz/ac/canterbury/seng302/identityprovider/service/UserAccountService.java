package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.exceptions.IrremovableRoleException;
import nz.ac.canterbury.seng302.identityprovider.exceptions.UserDoesNotExistException;
import nz.ac.canterbury.seng302.shared.identityprovider.GetPaginatedUsersRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.GetUserByIdRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyRoleOfUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This base service contains multiple other services, and is used as a hub so GRPC does not
 * complain about multiple services extending UserAccountServiceGrpc.UserAccountServiceImplBase.
 */
@GrpcService
public class UserAccountService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

  @Autowired private RegisterServerService registerServerService;

  @Autowired private GetUserService getUserService;

  @Autowired private RoleService roleService;

  @Override
  public void register(
      UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
    try {
      var response = registerServerService.register(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

  @Override
  public void getUserAccountById(
      GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
    getUserService.getUserAccountById(request);
  }

  @Override
  public void getPaginatedUsers(
      GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
    getUserService.getPaginatedUsers(request);
  }

  @Override
  public void addRoleToUser(
      ModifyRoleOfUserRequest modificationRequest,
      StreamObserver<UserRoleChangeResponse> responseObserver) {
    try {
      var response = roleService.addRoleToUser(modificationRequest);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (UserDoesNotExistException e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

  @Override
  public void removeRoleFromUser(
      ModifyRoleOfUserRequest modificationRequest,
      StreamObserver<UserRoleChangeResponse> responseObserver) {
    try {
      var response = roleService.removeRoleFromUser(modificationRequest);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (UserDoesNotExistException | IrremovableRoleException e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }
}
