package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Handles loading the notifications page. */
@Controller
public class NotificationsController extends AuthenticatedController {

    @Value("${nz.ac.canterbury.seng302.portfolio.urlPathPrefix}")
    private String urlPathPrefix;

    @Autowired
    protected NotificationsController(AuthStateService authStateService, UserAccountService userAccountService) {
        super(authStateService, userAccountService);
    }

    /**
     * Loads the notifications page.
     *
     * @param model the model to load the relative path into
     * @return the notifications html page
     */
    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("relativePath", urlPathPrefix);
        return "notifications";
    }
}
