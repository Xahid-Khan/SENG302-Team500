package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class RegisterServerService extends UserAccountServiceGrpc.UserAccountServiceImplBase {

    @Autowired
    private UserRepository repository;

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        UserModel user = new UserModel(request.getUsername(), request.getPassword(), request.getFirstName(), request.getMiddleName(), request.getLastName(), request.getNickname(), request.getBio(), request.getPersonalPronouns(), request.getEmail());
        repository.save(user);
        reply.setIsSuccess(true).setNewUserId(user.getId()).setMessage("registered new user: " + user);

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }
}
