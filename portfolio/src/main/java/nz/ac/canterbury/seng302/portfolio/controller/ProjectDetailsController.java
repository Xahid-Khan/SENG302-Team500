package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.List;
import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectDetailsController {
  @Autowired private RolesService rolesService;

  @Autowired private UserAccountService userAccountService;

  @Autowired private AuthStateService authStateService;

  @Value("${nz.ac.canterbury.seng302.portfolio.urlPathPrefix}")
  private String urlPathPrefix;

  @GetMapping("/project-details")
  public String projectDetails(@AuthenticationPrincipal PortfolioPrincipal principal, Model model) {
    List<UserRole> roles = rolesService.getRolesByToken(principal);

    model.addAttribute("isStudent", roles.size() == 1 && roles.contains(UserRole.STUDENT));

    Integer userId = authStateService.getId(principal);

    UserResponse userDetails = userAccountService.getUserById(userId);

    model.addAttribute("userId", userId);
    model.addAttribute("username", userDetails.getUsername());
    model.addAttribute("relativePath", urlPathPrefix);

    return "project_details";
  }
}
