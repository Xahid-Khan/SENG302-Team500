package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.Query;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.mapping.UserMapper;
import nz.ac.canterbury.seng302.shared.identityprovider.GetPaginatedUsersRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.GetUserByIdRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This gRPC service handles registration from the server side. If the user is valid, it will add it
 * into the UserRepository and return a success. Otherwise an error will be returned.
 */
@GrpcService
public class UserAccountService extends UserAccountServiceGrpc.UserAccountServiceImplBase {
    private static HashSet<String> validOrderByFieldNames = new HashSet<String>(
        List.of(new String[]{"name", "username", "nickname", "roles"}));

  @Autowired private UserRepository repository;

  @Autowired private PasswordService passwordService;

  @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserMapper userMapper;

    @Override
  public void register(
      UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
    UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

    try {
      var passwordHash = passwordService.hashPassword(request.getPassword());

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
              new ArrayList<>()
          );

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

      responseObserver.onNext(reply.build());
      responseObserver.onCompleted();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
      responseObserver.onError(e);
    }
  }

  /**
   * This is a GRPC user serivce method that is beign over-ridden to get the user details and encase
   * them into a User Response body. if the user is not found the User response is set to null
   *
   * @param request
     * @param responseObserver
     */
    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        int userId = request.getId();
        var userFound = repository.findById(userId);
        if (userFound != null) {
            responseObserver.onNext(userMapper.toUserResponse(userFound));
      responseObserver.onCompleted();
    } else {
      responseObserver.onNext(null);
      responseObserver.onCompleted();
    }
  }

    /**
     * GRPC service method that provides a list of user details with a caller-supplied sort order,
     * maximum length, and offset.
     *
   *
   * @param request parameters from the caller
     * @param responseObserver to receive results or errors
   */
  @Override
  public void getPaginatedUsers(
      GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
    var orderByField = request.getOrderBy();
        var limit = request.getLimit();
        var offset = request.getOffset();

        // Validate inputs
    if (!validOrderByFieldNames.contains(orderByField) || limit <= 0 || offset < 0) {
            responseObserver.onError(new IllegalArgumentException());
            responseObserver.onCompleted();
            return;
        }

        try (Session session = sessionFactory.openSession()) {
            var queryOrderByComponent = switch (orderByField) {
                case "name" -> "firstName, middleName, lastName";
                case "username" -> "username";
                case "nickname" -> "nickname";
                case "roles" -> throw new Exception("Roles support hasn't been implemented yet");
                default -> throw new Exception("Unsupported orderBy field");
            };

            Query query = session.createQuery("FROM UserModel ORDER BY " + queryOrderByComponent, UserModel.class)
                .setFirstResult(offset)
        .setMaxResults(limit);
            List<UserModel> resultList = query.getResultList();

            Query countQuery = session.createQuery("SELECT COUNT(u.id) FROM UserModel u");
            int totalCount = (int) (long) countQuery.getSingleResult();

            var response = PaginatedUsersResponse.newBuilder()
                .addAllUsers(resultList.stream().map(userMapper::toUserResponse).toList())
                .setResultSetSize(totalCount)
        .build();

      responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (Exception e) {
      responseObserver.onError(e);
            responseObserver.onCompleted();
        }
  }

  public UserRepository getRepository() {
    return repository;
  }
}
