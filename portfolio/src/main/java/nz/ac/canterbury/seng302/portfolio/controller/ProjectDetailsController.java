package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectDetailsController {

  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private AuthStateService authStateService;

  @GetMapping("/project-details")
  public String projectDetails(
      @AuthenticationPrincipal AuthState principal,
      Model model
  ) {
    try {
        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        model.addAttribute("username", userDetails.getUsername());
        return "project_details";
    } catch (NullPointerException e) {
      return "redirect:/login?notLoggedIn=true";
    }

  }
}
