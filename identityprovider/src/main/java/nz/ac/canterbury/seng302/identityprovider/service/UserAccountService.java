package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.hibernate.Criteria;
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
    var orderByFields = request.getOrderBy().split("\\|", 2);

    if (orderByFields.length != 2) {
      throw new IllegalArgumentException("Please provide an orderBy field name, pipe symbol, followed by 'asc' or 'desc'.");
    }
    boolean ascending = orderByFields[1].equals("asc");
    var orderByField = orderByFields[0];

    var limit = request.getLimit();
    var offset = request.getOffset();

    // Validate inputs
    if (!validOrderByFieldNames.contains(orderByField) || limit <= 0 || offset < 0) {
      responseObserver.onError(new IllegalArgumentException());
      responseObserver.onCompleted();
      return;
    }

    try (Session session = sessionFactory.openSession()) {
      Query countQuery = session.createQuery("SELECT COUNT(u.id) FROM UserModel u");
      int totalCount = (int) (long) countQuery.getSingleResult();

      List<UserModel> resultList;
      if (orderByField.equals("roles")) {
        // Receive IDs in order by roles
        var query = session.createQuery("SELECT u.id FROM UserModel u JOIN u.roles r GROUP BY u.id ORDER BY string_agg((case when r = ?1 then 'student' else (case when r=?2 then 'teacher' else 'course_administrator' end) end), ',') " + ((ascending) ? "ASC" : "DESC"), Integer.class)
            .setParameter(1, UserRole.STUDENT)
            .setParameter(2, UserRole.TEACHER)
            .setFirstResult(offset)
            .setMaxResults(limit);

        // Get the UserModels for the IDs
        var idList = query.getResultList();
        session.close();  // Close the session early so the repository query will work.

        // findAllById doesn't guarantee result order, so we need to manually re-order the data to match idList.
        Map<Integer, UserModel> resultsById = new HashMap<>();
        repository.findAllById(idList).forEach(user -> resultsById.put(user.getId(), user));
        resultList = new ArrayList<>(idList.size());
        for (var userId : idList) {
          resultList.add(resultsById.get(userId));
        }
      }
      else {
        var queryOrderByComponent = switch (orderByField) {
          case "name" -> "firstName, middleName, lastName";
          case "username" -> "username";
          case "nickname" -> "nickname";
          default -> throw new IllegalArgumentException("Unsupported orderBy field");
        };

        var query = session.createQuery("FROM UserModel ORDER BY " + queryOrderByComponent + ((ascending) ? " ASC" : " DESC"), UserModel.class)
            .setFirstResult(offset)
            .setMaxResults(limit);
        resultList = query.getResultList();
      }

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
