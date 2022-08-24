package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Handles loading the project details page. */
@Controller
public class ProjectDetailsController extends AuthenticatedController {

  @Autowired
  public ProjectDetailsController(
      AuthStateService authStateService, UserAccountService userAccountService) {
    super(authStateService, userAccountService);
  }

  /**
   * Loads the project details page.
   *
   * @return the project details page
   */
  @GetMapping("/project-details")
  public String projectDetails() {
    return "project_details";
  }
}
