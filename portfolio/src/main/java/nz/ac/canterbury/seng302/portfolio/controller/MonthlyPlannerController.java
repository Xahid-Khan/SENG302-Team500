package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Handles the GET request on the /monthly-planner endpoint. */
@Controller
public class MonthlyPlannerController extends AuthenticatedController {

  @Autowired
  public MonthlyPlannerController(
      AuthStateService authStateService, UserAccountService userAccountService) {
    super(authStateService, userAccountService);
  }

  /**
   * GET /monthly-planner/{projectId} fetches monthly planner view for a specific project projectId,
   * this page shows a calendar view with all sprints showing from the related project.
   *
   * @return The monthly_planner html page
   */
  @GetMapping("/monthly-planner/{projectId}")
  public String getMonthlyPlanner() {
    return "monthly_planner";
  }
}
