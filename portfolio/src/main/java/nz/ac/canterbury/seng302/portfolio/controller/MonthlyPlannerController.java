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

    @GetMapping("/monthly-planner/{projectId}")
    public String getMonthlyPlanner(@AuthenticationPrincipal AuthState principal, Model model, @PathVariable String projectId) {
        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);
        List<UserRole> roles = userDetails.getRolesList();

        model.addAttribute("canEdit", roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR));
        model.addAttribute("username", userDetails.getUsername());

        return "monthly_planner";
    }
}
