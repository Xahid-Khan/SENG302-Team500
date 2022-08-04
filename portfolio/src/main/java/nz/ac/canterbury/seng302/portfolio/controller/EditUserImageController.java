package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EditUserImageController {

    @GetMapping(value="/edit_user_image")
    public String getPage(
            Model model, @AuthenticationPrincipal PortfolioPrincipal principal) {
        model.addAttribute("test", true);

        return "edit_user_image";
    }
}
