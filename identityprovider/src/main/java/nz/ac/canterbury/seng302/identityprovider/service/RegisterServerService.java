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
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordService passwordService;

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        // TODO: TEST!
        try {
            var passwordHash = passwordService.hashPassword(request.getPassword());

            UserModel user = new UserModel(
                    request.getUsername(),
                    passwordHash,
                    request.getFirstName(),
                    request.getMiddleName(),
                    request.getLastName(),
                    request.getNickname(),
                    request.getBio(),
                    request.getPersonalPronouns(),
                    request.getEmail()
            );

            if (repository.findByUsername(request.getUsername()) != null) {
                reply.setIsSuccess(false).setNewUserId(-1).setMessage("Error: Username in use")
                        .setValidationErrors(
                                0,
                                ValidationError.newBuilder()
                                        .setFieldName("username")
                                        .setErrorText("Error: Username in use")
                        );
            } else {
                repository.save(user);
                reply.setIsSuccess(true).setNewUserId(user.getId()).setMessage("registered new user: " + user);
            }

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }
}
