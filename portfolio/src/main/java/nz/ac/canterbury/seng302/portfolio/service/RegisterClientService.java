package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

/**
 * Registers a new client by passing off the details to the RegisterServerService.
 */
@Service
public class RegisterClientService {

  @GrpcClient(value = "identity-provider-grpc-server")
  private UserAccountServiceGrpc.UserAccountServiceBlockingStub registrationStub;

  /**
   * Registers a new user.
   *
   * @param user The user to register
   * @return a UserRegisterResponse for the status of the registration
   */
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
            .setPersonalPronouns(user.personalPronouns())
            .setEmail(user.email())
            .build();
    return registrationStub.register(regRequest);
  }

  public EditUserResponse updateDetails(User user) {
    EditUserRequest regRequest =
            EditUserRequest.newBuilder()
                    .setFirstName(user.firstName())
                    .setMiddleName(user.middleName())
                    .setLastName(user.lastName())
                    .setNickname(user.nickname())
                    .setBio(user.bio())
                    .setPersonalPronouns(user.personalPronouns())
                    .setEmail(user.email())
                    .build();
    return registrationStub.editUser(regRequest);
  }


}
