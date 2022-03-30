package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.ArrayList;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectDetailsController {
  @Autowired
  private RolesService rolesService;

  @GetMapping("/project-details")
  public String projectDetails(
      @AuthenticationPrincipal AuthState principal,
      Model model
  ) {
    ArrayList<String> roles = rolesService.getRolesByToken(principal);

    model.addAttribute("isStudent", roles.size() == 1 && roles.contains("STUDENT"));
    return "project_details";
  }
}
