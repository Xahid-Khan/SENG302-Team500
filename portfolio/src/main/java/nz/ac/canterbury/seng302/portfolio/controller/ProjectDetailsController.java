package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Handles loading the project details page. */
@Controller
public class ProjectDetailsController extends AuthenticatedController {

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
