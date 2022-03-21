package nz.ac.canterbury.seng302.identityprovider.database;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserModel, Long> {

  @Query("SELECT count(user) FROM UserModel user WHERE UserModel.username = ?1")
  boolean usernameExists(String username);

  UserModel findById(long id);
}
