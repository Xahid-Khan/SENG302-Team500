package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseGroupContract;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedGroupsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Handles the GET request on the /groups endpoint. */
@Controller
public class GroupsController {
  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private AuthStateService authStateService;

  @Autowired
  private GroupsClientService groupsClientService;

  @Autowired
  private RolesService rolesService;


  private static final String TEACHER = "TEACHER";
  private static final String COURSE_ADMINISTRATOR = "COURSE_ADMINISTRATOR";
  /**
   * GET /groups fetches the groups page. The groups page shows all groups
   *
   * @param principal Principal for the currently logged-in user, used to get userId
   * @param model     Parameters sent to thymeleaf template to be rendered into HTML
   * @return The groups html page
   */
  @GetMapping(value = "/groups")
  public String getGroups(@AuthenticationPrincipal PortfolioPrincipal principal, Model model) {
    Integer userId = authStateService.getId(principal);

    UserResponse userDetails = userAccountService.getUserById(userId);
    List<UserRole> roles = userDetails.getRolesList();

    model.addAttribute("userId", userId);
    model.addAttribute(
            "canEdit",
            roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR));
    model.addAttribute("username", userDetails.getUsername());

    return "groups";
  }

  /**
   * This method will be invoked when API receives a GET request, and will produce a list of all the groups.
   * @return List of groups converted into project contract (JSON) type.
   */
  @GetMapping(value = "/groups/all", produces = "application/json")
  public ResponseEntity<?> getAll() {
    try {
      PaginatedGroupsResponse groupsResponse = groupsClientService.getAllGroupDetails();
      List<GroupDetailsResponse> groups = groupsResponse.getGroupsList();
      return ResponseEntity.ok(groups);
    } catch (NoSuchElementException error) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }


  /**
   * This method will be invoked when API receives a POST request to delete members. Invokes the client service to delete the members
   * @param groupId The group id to delete members from
   * @param members The members to delete from the group
   */
  @PostMapping(value = "/groups/{groupId}/delete-members", produces = "application/json")
  public ResponseEntity<?> deleteMembers(@AuthenticationPrincipal PortfolioPrincipal principal, @PathVariable("groupId") String groupId,
                                         @RequestBody List<Integer> members) {

    ArrayList<String> roles = rolesService.getRolesByToken(principal);
    if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
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

    ArrayList<String> roles = rolesService.getRolesByToken(principal);
    if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
      try {
        groupsClientService.addGroupMembers(Integer.parseInt(groupId), members);
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

    ArrayList<String> roles = rolesService.getRolesByToken(principal);
    if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
      try {
        groupsClientService.deleteGroup(Integer.parseInt(groupId));
        return ResponseEntity.ok().build();
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
  @PostMapping(value = "/groups/", produces = "application/json")
  public ResponseEntity<?> createGroup(@AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody BaseGroupContract newGroup) {

    ArrayList<String> roles = rolesService.getRolesByToken(principal);
    if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
      try {
        groupsClientService.createGroup(newGroup);
        return ResponseEntity.ok().build();
      } catch (Exception error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }


  }
