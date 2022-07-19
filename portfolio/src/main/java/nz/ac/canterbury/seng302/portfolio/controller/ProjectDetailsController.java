package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.ArrayList;
import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectDetailsController {
  @Autowired
  private RolesService rolesService;


  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private AuthStateService authStateService;

  @GetMapping("/project-details")
  public String projectDetails(
      @AuthenticationPrincipal PortfolioPrincipal principal,
      Model model
  ) {
    ArrayList<String> roles = rolesService.getRolesByToken(principal);

    model.addAttribute("isStudent", roles.size() == 1 && roles.contains("STUDENT"));

    Integer userId = authStateService.getId(principal);

    UserResponse userDetails = userAccountService.getUserById(userId);

    model.addAttribute("userId", userId);
    model.addAttribute("username", userDetails.getUsername());

    return "project_details";


  }
}
