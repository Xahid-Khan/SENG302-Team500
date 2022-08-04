package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.ArrayList;
import java.util.List;
import nz.ac.canterbury.seng302.identityprovider.database.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.database.GroupRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service handles all requests on the server side to manage groups.
 */
@Service
public class GroupsServerService {
  @Autowired private GroupRepository groupRepository;

  /**
   * Handles the functionality on the server side for creating groups. It will ensure that both the
   *  short name and the long name of the group are unique. If they are, it will add the group to
   *  the database. Otherwise, it will fail and return with validation errors.
   *
   * @param groupRequest  the CreateGroupRequest gRPC message
   * @return              a CreateGroupResponse gRPC message with potential errors
   */
  public CreateGroupResponse createGroup(CreateGroupRequest groupRequest) {
    List<ValidationError> validationErrors = new ArrayList<>();
    // Ensure that both the short name and long name are unique
    if (groupRepository.findByLongName(groupRequest.getLongName()) != null) {
      validationErrors.add(
          ValidationError.newBuilder()
              .setFieldName("longName")
              // Potentially add which group it's clashing with here?
              .setErrorText("Error: Long name must be unique")
              .build());
    }

    if (groupRepository.findByShortName(groupRequest.getShortName()) != null) {
      validationErrors.add(
          ValidationError.newBuilder()
              .setFieldName("shortName")
              // Potentially add which group it's clashing with here?
              .setErrorText("Error: Short name must be unique")
              .build());
    }

    if (validationErrors.isEmpty()) {
      GroupModel group = new GroupModel(groupRequest.getShortName(), groupRequest.getLongName());
      groupRepository.save(group);
      return CreateGroupResponse.newBuilder()
          .setIsSuccess(true)
          .setNewGroupId(group.getId())
          .setMessage("Group successfully created")
          .build();
    } else {
      return CreateGroupResponse.newBuilder()
          .setIsSuccess(false)
          .setNewGroupId(-1)
          .setMessage("Validation error(s)")
          .addAllValidationErrors(validationErrors)
          .build();
    }
  }
}
