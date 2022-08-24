package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.GroupContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.UserContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseGroupContract;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/** Handles the GET request on the /groups endpoint. */
@Controller
@RequestMapping("/api/v1")
public class GroupsController {

  @Autowired
  private GroupsClientService groupsClientService;

  @Autowired
  private SubscriptionService subscriptionService;

  @Autowired
  private RolesService rolesService;

  /**
   * This method will be invoked when API receives a GET request, and will produce a list of all the groups.
   * @return List of groups converted into project contract (JSON) type.
   */
  @GetMapping(value = "/groups/all", produces = "application/json")
  public ResponseEntity<?> getAll() {
    try {
      PaginatedGroupsResponse groupsResponse = groupsClientService.getAllGroupDetails();
      List<GroupDetailsResponse> groupsList = groupsResponse.getGroupsList();
      ArrayList<GroupContract> groups = new ArrayList<>();
      for (GroupDetailsResponse groupDetails: groupsList) {
        groups.add(new GroupContract(groupDetails.getGroupId(),
                groupDetails.getShortName(),
                groupDetails.getLongName(),
                getUsers(groupDetails.getMembersList())
                ));
      }
      return ResponseEntity.ok(groups);
    } catch (NoSuchElementException error) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  private ArrayList<UserContract> getUsers(List<UserResponse> userList) {
    ArrayList<UserContract> users = new ArrayList<>();
    for (UserResponse user: userList) {
      users.add(new UserContract(
              user.getId(),
              user.getFirstName(),
              user.getMiddleName(),
              user.getLastName(),
              user.getNickname(),
              user.getUsername(),
              user.getEmail(),
              user.getPersonalPronouns(),
              user.getBio(),
              user.getRolesList()
              ));
    }
    return users;
  }

  /**
   * This method will be invoked when API receives a POST request to delete members. Invokes the client service to delete the members
   * @param groupId The group id to delete members from
   * @param members The members to delete from the group
   */
  @PostMapping(value = "/groups/{groupId}/delete-members", produces = "application/json")
  public ResponseEntity<?> deleteMembers(@AuthenticationPrincipal PortfolioPrincipal principal, @PathVariable("groupId") String groupId,
                                         @RequestBody List<Integer> members) {
    List<UserRole> roles = rolesService.getRolesByToken(principal);
    if (roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR)) {
      try {
        groupsClientService.removeGroupMembers(Integer.parseInt(groupId), members);
        return ResponseEntity.ok().build();
      } catch (Exception error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }


  /**
   * This method will be invoked when API receives a POST request to add members. Invokes the client service to add the members
   * @param groupId The group id to delete members from
   * @param members The members to delete from the group
   */
  @PostMapping(value = "/groups/{groupId}/add-members", produces = "application/json")
  public ResponseEntity<?> addMembers(@AuthenticationPrincipal PortfolioPrincipal principal, @PathVariable("groupId") String groupId,
                                         @RequestBody List<Integer> members) {

    List<UserRole> roles = rolesService.getRolesByToken(principal);
    if (roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR)) {
      try {
        groupsClientService.addGroupMembers(Integer.parseInt(groupId), members);
        members.stream().forEach(member -> subscriptionService.subscribe(new SubscriptionContract(member, Integer.parseInt(groupId))));
        return ResponseEntity.ok().build();
      } catch (Exception error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  /**
   * This method will be invoked when API receives a POST request to delete the group. Invokes the client service to delete the group
   * @param groupId The group id to delete
   */
  @DeleteMapping(value = "/groups/{groupId}", produces = "application/json")
  public ResponseEntity<?> deleteGroup(@AuthenticationPrincipal PortfolioPrincipal principal, @PathVariable("groupId") String groupId) {
    List<UserRole> roles = rolesService.getRolesByToken(principal);
    if (roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR)) {
      try {
        DeleteGroupResponse response = groupsClientService.deleteGroup(Integer.parseInt(groupId));
        if (response.getIsSuccess()) {
          return ResponseEntity.ok().build();
        } else {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
      } catch (Exception error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  /**
   * This method will be invoked when API receives a POST request to create a group. Invokes the client service to create the group
   * @param newGroup The contract for the group to create
   */
  @PostMapping(value = "/groups", produces = "application/json")
  public ResponseEntity<?> createGroup(@AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody BaseGroupContract newGroup) {
    List<UserRole> roles = rolesService.getRolesByToken(principal);
    if (roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR)) {
      try {
        CreateGroupResponse response = groupsClientService.createGroup(newGroup);
        if (response.getIsSuccess()) {
          return ResponseEntity.ok().build();
        } else {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
        }
      } catch (Exception error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

}
