package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;



@Controller
public class MonthlyPlannerController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthStateService authStateService;

    /**
     * GET /monthly-planner/{projectId} fetches monthly planner view for a specific project projectId, this page
     * shows a calendar view with all sprints showing from the related project
     *
     * @param principal Principal for the currently logged in user, used to get userId
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The monthly_planner html page
     */
    @GetMapping("/monthly-planner/{projectId}")
    public String getMonthlyPlanner(@AuthenticationPrincipal AuthState principal, Model model) {

        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);
        List<UserRole> roles = userDetails.getRolesList();

        model.addAttribute("userId", userId);
        model.addAttribute("canEdit", roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR));
        model.addAttribute("username", userDetails.getUsername());

        return "monthly_planner";
    }
}
