package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.List;
import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.GroupContract;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/** Handles the GET request on the /groups endpoint. */
@Controller
public class GroupsController {
  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private AuthStateService authStateService;

  @Autowired
  private GroupsClientService groupsClientService;

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

//  PATCH api/v1/groups/{groupID}
//  Request Parameters: groupId:string
//  Content-Type: application/json
//  Body: GroupContract object
//  Response: GroupContract
//  Authentication: Teacher
//  Status: 200 OK
  //In the body gets all the group members in groupID, sends them to the groupclientcontroller

  @PatchMapping(value = "/groups/{groupId}")
  public String updateGroup(@RequestBody GroupContract groupContract, @PathVariable("groupId") String groupId) {
    //convert groupContract.users() list of string to int

//    groupsClientService.updateGroupMembers(Integer.parseInt(groupId), groupContract.users());
    return "";
  }
}
