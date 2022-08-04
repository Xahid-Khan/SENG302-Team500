package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EditUserImageController {
    @Autowired
    private AuthStateService authStateService;

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping(value="/edit_user_image")
    public String getPage(
            Model model, @AuthenticationPrincipal PortfolioPrincipal principal) {
        Integer userId = authStateService.getId(principal);

//        UserResponse userDetails = userAccountService.getUserById(userId);

        // Prefill the form with the user's details
        model.addAttribute("userId", userId);
//        model.addAttribute("username", userDetails.getUsername());
//        model.addAttribute("user", userDetails);

        return "edit_user_image";
    }
}
