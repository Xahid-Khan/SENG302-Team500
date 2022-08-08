package nz.ac.canterbury.seng302.identityprovider.service;

import java.util.ArrayList;
import java.util.List;
import nz.ac.canterbury.seng302.identityprovider.database.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.database.GroupRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** This service handles all requests on the server side to manage groups. */
@Service
public class  GroupsServerService {
  @Autowired private GroupRepository groupRepository;

  /**
   * Handles the functionality on the server side for creating groups. It will ensure that both the
   * short name and the long name of the group are unique. If they are, it will add the group to the
   * database. Otherwise, it will fail and return with validation errors.
   *
   * @param groupRequest the CreateGroupRequest gRPC message
   * @return a CreateGroupResponse gRPC message with potential errors
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

  /**
   * Handles the functionality on the server side for deleting groups. If the database does not have
   * a group with the corresponding ID, then a failure will be returned. Otherwise, the group will
   * be deleted, and a success will be returned.
   *
   * @param groupRequest the DeleteGroupRequest gRPC message
   * @return a DeleteGroupResponse gRPC message
   */
  public DeleteGroupResponse deleteGroup(DeleteGroupRequest groupRequest) {
    if (groupRepository.findById(groupRequest.getGroupId()).isEmpty()) {
      return DeleteGroupResponse.newBuilder()
          .setIsSuccess(false)
          .setMessage("Error: Group does not exist")
          .build();
    } else {
      groupRepository.deleteById(groupRequest.getGroupId());
      return DeleteGroupResponse.newBuilder()
          .setIsSuccess(true)
          .setMessage("Group successfully deleted")
          .build();
    }
  }
}
