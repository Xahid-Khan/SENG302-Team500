package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditUserService {

    @Autowired
    private UserRepository repository;

    public EditUserResponse editUser(
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
                            request.getEmail(),
                            existingUser.getRoles(),
                            existingUser.getCreated());
            newUser.setId(request.getUserId());
            repository.save(newUser);
            reply
                    .setIsSuccess(true)
                    .setMessage("Updated details for user: " + newUser);
        }

        return reply.build();
    }
}
