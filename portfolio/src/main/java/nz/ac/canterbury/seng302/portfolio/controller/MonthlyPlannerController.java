package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Handles the GET request on the /monthly-planner endpoint. */
@Controller
public class MonthlyPlannerController extends AuthenticatedController {

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
