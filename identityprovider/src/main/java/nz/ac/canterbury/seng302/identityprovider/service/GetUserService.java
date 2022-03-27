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
   * @param request
   * @return
   */
  public UserResponse getUserAccountById(
      GetUserByIdRequest request) {
    UserResponse.Builder reply = UserResponse.newBuilder();
    int userId = request.getId();
    var userFound = repository.findById(userId);
    if (userFound != null) {
      reply
          .setFirstName(userFound.getFirstName())
          .setMiddleName(userFound.getMiddleName())
          .setLastName(userFound.getLastName())
          .setBio(userFound.getBio())
          .setUsername(userFound.getUsername())
          .setPersonalPronouns(userFound.getPronouns())
          .setEmail(userFound.getEmail())
          .setNickname(userFound.getNickname())
          .addAllRoles(userFound.getRoles());

      return reply.build();
    } else {
      return null;
    }
  }

  /**
   * Skeleton for pagination -
   *
   * @param request
   * @return
   */
  public PaginatedUsersResponse getPaginatedUsers(
      GetPaginatedUsersRequest request) {
    PaginatedUsersResponse.Builder reply = PaginatedUsersResponse.newBuilder();
    Iterable<UserModel> userList = repository.findAll();
    if (userList != null) {
      for (UserModel user : userList) {
        UserResponse.Builder subUser = UserResponse.newBuilder();
        subUser
            .setFirstName(user.getFirstName())
            .setMiddleName(user.getMiddleName())
            .setLastName(user.getLastName())
            .setBio(user.getBio())
            .setUsername(user.getUsername())
            .setPersonalPronouns(user.getPronouns())
            .setEmail(user.getEmail())
            .setNickname(user.getNickname())
            .addAllRoles(user.getRoles());
        reply.addUsers(subUser);
      }
      return reply.build();
    } else {
      return null;
    }
  }
}
