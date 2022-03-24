package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.stereotype.Service;

@Service
public class RegisterClientService {

  @GrpcClient(value = "identity-provider-grpc-server")
  private UserAccountServiceGrpc.UserAccountServiceBlockingStub registrationStub;

  public UserRegisterResponse register(User user) {
    UserRegisterRequest regRequest =
        UserRegisterRequest.newBuilder()
            .setUsername(user.username())
            .setPassword(user.password())
            .setFirstName(user.firstName())
            .setMiddleName(user.middleName())
            .setLastName(user.lastName())
            .setNickname(user.nickname())
            .setBio(user.bio())
            .setPersonalPronouns(user.pronouns())
            .setEmail(user.email())
            .build();
    return registrationStub.register(regRequest);
  }
}
