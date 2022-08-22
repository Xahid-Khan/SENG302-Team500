package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** This controller controls the portion of the groups page which is visible to the user. */
@Controller
public class GroupsPageController extends AuthenticatedController {

  /**
   * GET /groups fetches the groups page. The groups page shows all groups
   *
   * @return The groups html page
   */
  @GetMapping(value = "/groups")
  public String getGroups() {
    return "groups";
  }
}
