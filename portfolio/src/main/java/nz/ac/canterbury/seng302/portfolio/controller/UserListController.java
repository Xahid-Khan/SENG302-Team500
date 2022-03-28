package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class UserListController {

    @GetMapping("/user-list")
    public String listUsers(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        ArrayList<User> users = new ArrayList<>();
        model.addAttribute("users", users);
        return "user_list";
    }
}
