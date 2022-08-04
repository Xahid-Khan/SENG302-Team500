package nz.ac.canterbury.seng302.identityprovider.database;

import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<GroupModel, Integer> {
  GroupModel findByLongName(String longName);
  GroupModel findByShortName(String shortName);
}
