package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.model.GetPaginatedUsersOrderingElement;
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

import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserListController {
    private static final int PAGE_SIZE = 20;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthStateService authStateService;

    @GetMapping("/user-list")
    public String listUsers(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam("page") Optional<Integer> pageMaybe,
            @RequestParam("sortBy") Optional<String> sortAttributeMaybe,
            @RequestParam("asc") Optional<Boolean> ascendingMaybe,
            Model model
    ) {

        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        model.addAttribute("username", userDetails.getUsername());
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

        // Make Request
        var response = userAccountService.getPaginatedUsers(
            offset,
            PAGE_SIZE,
            sortAttribute,
            ascending
        );

        // Construct response
        model.addAttribute("users", response.getUsersList());
        model.addAttribute("totalUserCount", response.getResultSetSize());
        model.addAttribute("pageOffset", offset);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortDir", ascending);
        model.addAttribute("sortBy", sortAttributeString);
        model.addAttribute("delegate", this);
        model.addAttribute("pageSize", PAGE_SIZE);
        return "user_list";
    }

    public String formatUrl(int page, String sortBy, boolean sortDir) {
        return String.format("?page=%d&sortBy=%s&asc=%b", page, sortBy, sortDir);
    }

    public String formatUserRoles(List<UserRole> roles) {
        return roles.stream().map(role -> switch (role) {
            case STUDENT -> "Student";
            case TEACHER -> "Teacher";
            case COURSE_ADMINISTRATOR -> "Course Administrator";
            default -> "Student";
        }).collect(Collectors.joining(", "));
    }
}
