package nz.ac.canterbury.seng302.identityprovider.database;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserModel, Long> {

    List<UserModel> findByUsername(String username);

    UserModel findById(long id);
}
