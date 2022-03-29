package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.Optional;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.model.GetPaginatedUsersOrderingElement;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserListController {
    private static final int PAGE_SIZE = 20;

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/user-list")
    public String listUsers(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam("page") Optional<Integer> pageMaybe,
            @RequestParam("sortBy") Optional<String> sortAttributeMaybe,
            @RequestParam("asc") Optional<Boolean> ascendingMaybe,
            Model model
    ) {
        // Supply defaults
        int page = pageMaybe.orElse(1);
        String sortAttributeString = sortAttributeMaybe.orElse("name");
        boolean ascending = ascendingMaybe.orElse(true);

        // Validate inputs
        if (page < 1) {
            page = 1;
        }

        var sortAttribute = switch (sortAttributeString) {
            case "name" -> GetPaginatedUsersOrderingElement.NAME;
            case "username" -> GetPaginatedUsersOrderingElement.USERNAME;
            case "alias" -> GetPaginatedUsersOrderingElement.NICKNAME;
            case "roles" -> GetPaginatedUsersOrderingElement.ROLES;
            default -> GetPaginatedUsersOrderingElement.NAME;
        };

        var offset = (page - 1) * PAGE_SIZE;

        var response = userAccountService.getPaginatedUsers(
            offset,
            PAGE_SIZE,
            sortAttribute,
            ascending
        );

        model.addAttribute("users", response.getUsersList());
        model.addAttribute("totalUserCount", response.getResultSetSize());
        model.addAttribute("pageOffset", offset);
        return "user_list";
    }
}
