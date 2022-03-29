package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.GetPaginatedUsersRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.GetUserByIdRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserService {

  @Autowired private UserRepository repository;

  /**
   * This is a GRPC user serivce method that is beign over-ridden to get the user details and encase
   * them into a User Response body. if the user is not found the User response is set to null
   *
   * @param request the request containing the user ID to get
   * @return a UserResponse with the correct information if found
   */
  public void getUserAccountById(GetUserByIdRequest request) {
    int userId = request.getId();
    var userFound = repository.findById(userId);
    return userFound != null ? userMapper.toUserResponse(userFound) : null;
  }

  /**
   * GRPC service method that provides a list of user details with a caller-supplied sort order,
   * maximum length, and offset.
   *
   *
   * @param request parameters from the caller
   * @return a PaginatedUsersResponse filled in
   */
  @Override
  public void getPaginatedUsers(GetPaginatedUsersRequest request) throws Exception {
    var orderByField = request.getOrderBy();
    var limit = request.getLimit();
    var offset = request.getOffset();

    // Validate inputs
    if (!validOrderByFieldNames.contains(orderByField) || limit <= 0 || offset < 0) {
      throw new IllegalArgumentException("Error: Inputs not valid for request");
    }

    try (Session session = sessionFactory.openSession()) {
      Query countQuery = session.createQuery("SELECT COUNT(u.id) FROM UserModel u");
      int totalCount = (int) (long) countQuery.getSingleResult();

      List<UserModel> resultList;
      if (orderByField.equals("roles")) {
        // Receive IDs in order by roles
        var query = session.createQuery("SELECT u.id FROM UserModel u JOIN u.roles r GROUP BY u.id ORDER BY string_agg((case when r = ?1 then 'student' else (case when r=?2 then 'teacher' else 'course_administrator' end) end), ',')", Integer.class)
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

        var query = session.createQuery("FROM UserModel ORDER BY " + queryOrderByComponent, UserModel.class)
            .setFirstResult(offset)
            .setMaxResults(limit);
        resultList = query.getResultList();
      }

      return PaginatedUsersResponse.newBuilder()
          .addAllUsers(resultList.stream().map(userMapper::toUserResponse).toList())
          .setResultSetSize(totalCount)
          .build();
    }
  }
}
