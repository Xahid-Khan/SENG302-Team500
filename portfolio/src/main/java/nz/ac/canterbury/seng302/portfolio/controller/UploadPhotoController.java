package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UploadPhotoController {

    @Autowired
    private AuthStateService authStateService;

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/uploadphoto")
    public String index(Model model, @AuthenticationPrincipal AuthState principal) {
        int userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("user", userDetails);

        return "upload_photo";
    }
}
