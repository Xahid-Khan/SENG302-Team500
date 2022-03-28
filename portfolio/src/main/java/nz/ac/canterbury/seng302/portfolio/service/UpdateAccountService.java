package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UpdateAccountService {
    final String INVALID = " is Invalid";

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub grpcAccountService;

    @Autowired
    private AccountDetailsValidationService validationService;

    public Object updateAccount(int userId, User user) {
        if (!validationService.validateName(user.getFirstName())) {
            return user.getFirstName() + INVALID;
        }
        if (!validationService.optionalFields(user.getMiddleName())) {
            return user.getMiddleName() + INVALID;
        }
        if (!validationService.validateName(user.getLastName())) {
            return user.getLastName() + INVALID;
        }
        if (!validationService.optionalFields(user.getNickname())) {
            return user.getNickname() + INVALID;
        }
        if (!validationService.validateName(user.getEmail())) {
            return user.getEmail() + INVALID;
        }
        if (!validationService.optionalFields(user.getPronouns())) {
            return user.getFirstName() + INVALID;
        }
        if (!validationService.optionalFields(user.getBio())) {
            return user.getBio() + INVALID;
        }

        EditUserRequest updateRequest = EditUserRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(user.getFirstName())
                .setMiddleName(user.getMiddleName())
                .setLastName(user.getLastName())
                .setNickname(user.getNickname())
                .setEmail(user.getEmail())
                .setBio(user.getBio())
                .setPersonalPronouns(user.getPronouns())
                .build();

        EditUserResponse updatedUser = grpcAccountService.editUser(updateRequest);

        return updatedUser;
    }
}
