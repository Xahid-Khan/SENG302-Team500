package nz.ac.canterbury.seng302.portfolio.service;;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.mapping.UserMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.UserContract;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * A service that manages CRUD operations for View Account Controller.
 */

@Service
public class ViewAccountService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userAccountServiceBlockingStub;

    @Autowired
    private UserMapper mapper;


    /**
     * This service is used by controller to request the details of a given user
     * @param userId Id of a user
     * @return details of user or null in case of no user found.
     */
    public UserContract getUserById(int userId) {
        GetUserByIdRequest userRequest = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();
        var user = userAccountServiceBlockingStub.getUserAccountById(userRequest);
        if (user.getUsername().length() > 0) {
            return mapper.toContract(user);
        } else {
            return null;
        }
    }


    /**
     * Skeleton -
     * @return
     */
    public PaginatedUsersResponse getAllUsers() {
        GetPaginatedUsersRequest allUsers = GetPaginatedUsersRequest.newBuilder().build();
        return userAccountServiceBlockingStub.getPaginatedUsers(allUsers);
    }

}