package nz.ac.canterbury.seng302.identityprovider.database;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserModel, Integer> {

<<<<<<< HEAD
    UserModel findByUsername(String username);

    UserModel findById(int id);
=======
  UserModel findByUsername(String username);

  UserModel findById(long id);
>>>>>>> U2_T12/registration_backend_field_validation
}
