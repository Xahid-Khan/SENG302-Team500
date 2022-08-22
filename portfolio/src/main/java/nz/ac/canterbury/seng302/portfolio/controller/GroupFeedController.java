package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class GroupFeedController {
    @Autowired
    AuthStateService authStateService;

    @Autowired
    UserAccountService userAccountService;
    @Autowired
    private GroupsClientService groupsClientService;

    @GetMapping(value = "/group_feed/{groupId}", produces = "application/json")
    public String getGroupFeed(@AuthenticationPrincipal PortfolioPrincipal principal, Model model,
                               @PathVariable Integer groupId) {
        Integer userId = authStateService.getId(principal);
        UserResponse userDetails = userAccountService.getUserById(userId);
        GroupDetailsResponse groupDetailsResponse = groupsClientService.getGroupById(groupId);
        List<UserRole> roles = userDetails.getRolesList();

        model.addAttribute("userId", userId);
        model.addAttribute(
                "isStudent",
                !roles.contains(UserRole.TEACHER) && !roles.contains(UserRole.COURSE_ADMINISTRATOR));
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("groupName", groupDetailsResponse.getShortName());
        return "group_feed";
    }
}
