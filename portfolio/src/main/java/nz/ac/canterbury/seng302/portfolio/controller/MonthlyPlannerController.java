package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MonthlyPlannerController {
    @GetMapping("/monthly-planner/{projectId}")
    public String getMonthlyPlanner(Model model, @PathVariable String projectId) {
        return "monthly_planner";
    }
}
