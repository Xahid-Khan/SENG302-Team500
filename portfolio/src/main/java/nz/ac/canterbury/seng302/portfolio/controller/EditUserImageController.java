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

//    @GetMapping(value="/edit_user_image")
//    public String getPage(
//            Model model, @AuthenticationPrincipal PortfolioPrincipal principal) {
//        Integer userId = authStateService.getId(principal);
//
//        model.addAttribute("userId", userId);
//
//        return "edit_user_image";
//    }
}
