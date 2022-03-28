package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    UserDataValidationServerService dataValidation;


    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        try {
            var passwordHash = passwordService.hashPassword(request.getPassword());

            UserModel user = new UserModel(request.getUsername(), passwordHash, request.getFirstName(), request.getMiddleName(), request.getLastName(), request.getNickname(), request.getBio(), request.getPersonalPronouns(), request.getEmail());
            repository.save(user);
            reply.setIsSuccess(true).setNewUserId(user.getId()).setMessage("registered new user: " + user);

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
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



    @Override
    public void editUser(EditUserRequest request, StreamObserver<EditUserResponse> response) {
        UserModel user = repository.findById(request.getUserId());
        EditUserResponse.Builder update = EditUserResponse.newBuilder().setIsSuccess(false);

        if (!dataValidation.validateName(request.getFirstName())) {
            response.onNext(update.setMessage("Invalid First Name").build());
            response.onCompleted();
        }

        if (!dataValidation.validateName(request.getLastName())) {
            response.onNext(update.setMessage("Invalid Last Name").build());
            response.onCompleted();
        }

        if (!dataValidation.optionalFields(request.getNickname())) {
            response.onNext(update.setMessage("Invalid Nick Name").build());
            response.onCompleted();
        }

        if (!dataValidation.optionalFields(request.getMiddleName())) {
            response.onNext(update.setMessage("Invalid Middle Name").build());
            response.onCompleted();
        }

        if (!dataValidation.validateEmail(request.getEmail())) {
            response.onNext(update.setMessage("Invalid Email Address").build());
            response.onCompleted();
        }

        if (!dataValidation.optionalFields(request.getBio())) {
            response.onNext(update.setMessage("Invalid Characters in Bio").build());
            response.onCompleted();
        }

        if (!dataValidation.pronounsValidation(request.getPersonalPronouns())) {
            response.onNext(update.setMessage("Invalid Pronoun").build());
            response.onCompleted();
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPronouns(request.getPersonalPronouns());
        user.setBio(request.getBio());

        update.setIsSuccess(true);
        response.onNext(update.build());
        response.onCompleted();

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
