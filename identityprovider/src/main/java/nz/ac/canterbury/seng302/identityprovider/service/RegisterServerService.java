package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
                        roles,
                        currentTimestamp());

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

        return reply.build();
    }

    public static Timestamp currentTimestamp() {
        return Timestamp
                .newBuilder()
                .setSeconds(Instant.now().getEpochSecond())
                .build();
    }
}

