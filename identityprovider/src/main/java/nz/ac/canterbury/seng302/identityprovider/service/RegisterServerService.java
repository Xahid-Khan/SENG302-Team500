package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This gRPC service handles registration from the server side.
 * If the user is valid, it will add it into the UserRepository and return a success. Otherwise an
 *  error will be returned.
 */
@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

  @Autowired private UserRepository repository;

  @Autowired private PasswordService passwordService;

  @Override
  public void register(
      UserRegisterRequest request,
      StreamObserver<UserRegisterResponse> responseObserver
  ) {
    UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

    try {
      var passwordHash = passwordService.hashPassword(request.getPassword());

      UserModel user =
          new UserModel(
              request.getUsername(),
              passwordHash,
              request.getFirstName(),
              request.getMiddleName(),
              request.getLastName(),
              request.getNickname(),
              request.getBio(),
              request.getPersonalPronouns(),
              request.getEmail());

      // If a username already exists in the database, return an error
      if (repository.findByUsername(request.getUsername()) != null) {
        reply
            .setIsSuccess(false)
            .setNewUserId(-1)
            .setMessage("Error: Username in use")
            .addValidationErrors(
                ValidationError.newBuilder()
                    .setFieldName("username")
                    .setErrorText("Error: Username in use"));
      } else {
        repository.save(user);
        reply
            .setIsSuccess(true)
            .setNewUserId(user.getId())
            .setMessage("Registered new user: " + user);
      }

      responseObserver.onNext(reply.build());
      responseObserver.onCompleted();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

    @Override
    public void editUser(
            EditUserRequest request,
            StreamObserver<EditUserResponse> responseObserver
    ) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        try {
            var passwordHash = passwordService.hashPassword(request.getPassword());

            UserModel user =
                    new UserModel(
                            request.getUsername(),
                            passwordHash,
                            request.getFirstName(),
                            request.getMiddleName(),
                            request.getLastName(),
                            request.getNickname(),
                            request.getBio(),
                            request.getPersonalPronouns(),
                            request.getEmail());


            if (repository.findByUsername(request.getUsername()) == null) {
                reply
                        .setIsSuccess(false)
                        .setNewUserId(-1)
                        .setMessage("Error: Username not in database")
                        .addValidationErrors(
                                ValidationError.newBuilder()
                                        .setFieldName("username")
                                        .setErrorText("Error: Username not in database"));
            } else {
                repository.save(user);
                reply
                        .setIsSuccess(true)
                        .setNewUserId(user.getId())
                        .setMessage("Updated details for user: " + user);
            }

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

  public UserRepository getRepository() {
    return repository;
  }

    /**
     * This is a GRPC user serivce method that is beign over-ridden to get the user details and encase them into a User Response
     * body. if the user is not found the User response is set to null
     * @param request
     * @param responseObserver
     */
    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        UserResponse.Builder reply = UserResponse.newBuilder();
        int userId = request.getId();
        var userFound = repository.findById(userId);
        if (userFound != null) {
            reply.setFirstName(userFound.getFirstName())
                    .setMiddleName(userFound.getMiddleName())
                    .setLastName(userFound.getLastName())
                    .setBio(userFound.getBio())
                    .setUsername(userFound.getUsername())
                    .setPersonalPronouns(userFound.getPronouns())
                    .setEmail(userFound.getEmail())
                    .setNickname(userFound.getNickname());

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
        }
        else {
            responseObserver.onNext(null);
            responseObserver.onCompleted();
        }
    }


    /**
     * Skeleton for pagination -
     * @param request
     * @param responseObserver
     */
    @Override
    public void getPaginatedUsers(GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
        PaginatedUsersResponse.Builder reply = PaginatedUsersResponse.newBuilder();
        Iterable<UserModel> userList = repository.findAll();
        if (userList != null) {
            for (UserModel user : userList) {
                UserResponse.Builder subUser = UserResponse.newBuilder();
                subUser.setFirstName(user.getFirstName())
                        .setMiddleName(user.getMiddleName())
                        .setLastName(user.getLastName())
                        .setBio(user.getBio())
                        .setUsername(user.getUsername())
                        .setPersonalPronouns(user.getPronouns())
                        .setEmail(user.getEmail())
                        .setNickname(user.getNickname());
                reply.addUsers(subUser);
            }
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onNext(null);
            responseObserver.onCompleted();
        }

    }
}
