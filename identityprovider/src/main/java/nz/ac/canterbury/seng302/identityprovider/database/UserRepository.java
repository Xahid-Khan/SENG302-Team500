package nz.ac.canterbury.seng302.identityprovider.database;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserModel, Long> {

  UserModel findByUsername(String username);

  UserModel findById(long id);
}
