package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.exceptions.IrremovableRoleException;
import nz.ac.canterbury.seng302.identityprovider.exceptions.UserDoesNotExistException;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyRoleOfUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service handles adding and removing roles to/from users, as according to the
 * user_accounts.proto file.
 */
@Service
public class RoleService {

  @Autowired private UserRepository repository;

  // Helper function to reduce duplicate code.
  // Gets the user, checks if modification is legal (returns false if not), modifies, saves.
  // If addingRole is true, role will be added. If false, role will be deleted.
  private boolean modifyRoleOfUser(ModifyRoleOfUserRequest modificationRequest, boolean addingRole)
      throws UserDoesNotExistException {
    UserModel user = repository.findById(modificationRequest.getUserId());
    if (user == null) {
      throw new UserDoesNotExistException("Error: User does not exist.");
    }
    UserRole roleToChange = modificationRequest.getRole();

    // If the role is being added, the user should not have the role.
    // If the role is being deleted, the user should have the role.
    if (addingRole != user.getRoles().contains(roleToChange)) {
      if (addingRole) {
        user.addRole(roleToChange);
      } else {
        if (user.getRoles().size() == 1) {
          throw new IrremovableRoleException("Error: User must have at least 1 role.");
        }
        user.deleteRole(roleToChange);
      }
      repository.save(user);
      return true;
    }
    return false;
  }

  /**
   * Adds a role to the user if the user does not already have the role.
   *
   * @param modificationRequest The modification request for the role
   * @return UserRoleChangeResponse The status of how successful the modification was
   */
  public UserRoleChangeResponse addRoleToUser(ModifyRoleOfUserRequest modificationRequest)
      throws UserDoesNotExistException {
    // Generate a response based on if modifyRoleOfUser was successful or not
    var response =
        modifyRoleOfUser(modificationRequest, true)
            ? UserRoleChangeResponse.newBuilder().setIsSuccess(true).setMessage("Role added successfully")
            : UserRoleChangeResponse.newBuilder().setIsSuccess(false).setMessage("Unable to add role");

    return response.build();
  }

  /**
   * Removes a role to the user if the user does not already have the role.
   *
   * @param modificationRequest The modification request for the role
   * @return UserRoleChangeResponse The status of how successful the modification was
   */
  public UserRoleChangeResponse removeRoleFromUser(ModifyRoleOfUserRequest modificationRequest)
      throws UserDoesNotExistException, IrremovableRoleException {
    // Generate a response based on if modifyRoleOfUser was successful or not
    var response =
        modifyRoleOfUser(modificationRequest, false)
            ? UserRoleChangeResponse.newBuilder().setIsSuccess(true).setMessage("Role removed successfully")
            : UserRoleChangeResponse.newBuilder().setIsSuccess(false).setMessage("Unable to remove role");

    return response.build();
  }
}
