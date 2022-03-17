package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface extends the CRUD repository and makes use of the function/methods provided by the library.
 */

public interface UserRepository extends CrudRepository<UserEntity, String> {}
