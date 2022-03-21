package nz.ac.canterbury.seng302.portfolio.service;;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * A service that manages CRUD operations for View Account Controller.
 */

@Service
public class ViewAccountService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountServiceBlockingStub;


    public UserResponse getUserById(int userId) {
        GetUserByIdRequest userRequest = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();
        return userAccountServiceBlockingStub.getUserAccountById(userRequest);
    }

    public PaginatedUsersResponse getAllUsers() {
        GetPaginatedUsersRequest allUsers = GetPaginatedUsersRequest.newBuilder().build();
        return userAccountServiceBlockingStub.getPaginatedUsers(allUsers);
    }

}