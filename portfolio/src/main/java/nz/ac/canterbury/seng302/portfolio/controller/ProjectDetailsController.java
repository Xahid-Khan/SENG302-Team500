package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectDetailsController {
  @GetMapping("/project-details")
  public String projectDetails(
      @AuthenticationPrincipal AuthState principal,
      Model model
  ) {
    return "project_details";
  }
}
