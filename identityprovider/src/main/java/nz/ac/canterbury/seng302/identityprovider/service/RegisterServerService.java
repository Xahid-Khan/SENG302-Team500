package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterServerService {

  @Autowired private UserRepository repository;

  @Autowired private PasswordService passwordService;

    public UserRegisterResponse register(UserRegisterRequest request)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        var passwordHash = passwordService.hashPassword(request.getPassword());
        List<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.STUDENT);
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
                        request.getEmail(),
                        roles);
        repository.save(user);
        reply.setIsSuccess(true).setNewUserId(user.getId()).setMessage("registered new user: " + user);

        return reply.build();
    }

    public void editUser(
            EditUserRequest request,
            StreamObserver<EditUserResponse> responseObserver
    ) {
        EditUserResponse.Builder reply = EditUserResponse.newBuilder();
        UserModel existingUser = repository.findById(request.getUserId());
        if (existingUser == null) {
            reply
                    .setIsSuccess(false)
                    .setMessage("Error: User not in database")
                    .addValidationErrors(
                            ValidationError.newBuilder()
                                    .setFieldName("ID")
                                    .setErrorText("Error: User not in database"));
        } else {
            UserModel newUser =
                    new UserModel(
                            existingUser.getUsername(),
                            existingUser.getPasswordHash(),
                            request.getFirstName(),
                            request.getMiddleName(),
                            request.getLastName(),
                            request.getNickname(),
                            request.getBio(),
                            request.getPersonalPronouns(),
                            request.getEmail());
            newUser.setId(request.getUserId());
            repository.save(newUser);
            reply
                    .setIsSuccess(true)
                    .setMessage("Updated details for user: " + newUser);
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}

