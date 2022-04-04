package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MonthlyPlannerController {
    @GetMapping("/monthly-planner")
    public String getMonthlyPlanner(Model model) {
        return "monthly_planner";
    }
}
