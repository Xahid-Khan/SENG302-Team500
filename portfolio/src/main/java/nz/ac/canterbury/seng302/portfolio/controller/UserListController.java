package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nz.ac.canterbury.seng302.portfolio.model.GetPaginatedUsersOrderingElement;
import nz.ac.canterbury.seng302.portfolio.model.entity.SortingParameterEntity;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import nz.ac.canterbury.seng302.portfolio.service.SortingParametersService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserListController {
    private static final int PAGE_SIZE = 20;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthStateService authStateService;

    @Autowired
    private SortingParametersService sortingParametersService;


    @GetMapping("/user-list")
    public String listUsers(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam("page") Optional<Integer> pageMaybe,
            @RequestParam("sortBy") Optional<String> sortAttributeMaybe,
            @RequestParam("asc") Optional<Boolean> ascendingMaybe,
            Model model
    ) {

        Integer userId = authStateService.getId(principal);
        model.addAttribute("userId", userId);

        UserResponse userDetails = userAccountService.getUserById(userId);
        List<UserRole> roles = userDetails.getRolesList();

        if ((roles.contains(UserRole.COURSE_ADMINISTRATOR) || roles.contains(UserRole.TEACHER))) {
            model.addAttribute("isAdmin", true);
        }


        String sortAttributeString;
        boolean ascending = ascendingMaybe.orElse(true);

        if (sortingParametersService.checkExistance(userId) && sortAttributeMaybe.isEmpty()) {
            SortingParameterEntity sortingParams = sortingParametersService.getSortingParams(userId);
            sortAttributeString = sortingParams.getSortAttribute();
            ascending = sortingParams.isSortOrder();

        } else if (!sortAttributeMaybe.isEmpty()) {
            sortAttributeString = sortAttributeMaybe.get();

            sortingParametersService.saveSortingParams(userId, sortAttributeString, ascending);
        } else {
            sortAttributeString = "name";
        }

        model.addAttribute("username", userDetails.getUsername());

        // Supply defaults
        int page = pageMaybe.orElse(1);

        // Validate inputs
        if (page < 1) {
            page = 1;
        }

        var sortAttribute = switch (sortAttributeString) {
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
        model.addAttribute("userId", userId);
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

    @PostMapping("/user-list")
    public String updateRoles(@AuthenticationPrincipal AuthState principal,
                              Model model,
                              @RequestParam(name="action") String action,
                              @RequestParam(name="id") Integer id,
                              @RequestParam(name="roleNumber") Integer roleNumber) {
        Integer userId = authStateService.getId(principal);
        UserResponse userDetails = userAccountService.getUserById(userId);
        List<UserRole> roles = userDetails.getRolesList();

        model.addAttribute("roleMessageTarget", id);

        if (roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR)) {

            if (userId.equals(id)) {
                model.addAttribute("roleMessage", "Cannot add roles for yourself");
            }

            if (action.equals("remove")) {
                if (userId.equals(id)) {
                    model.addAttribute("roleMessage", "Cannot remove roles for yourself");
                }
                try {
                    UserRoleChangeResponse response = userAccountService.removeRole(id, UserRole.forNumber(roleNumber));
                    if (!response.getIsSuccess()) {
                        model.addAttribute("roleMessage", "Error adding role");
                    }
                } catch (Exception e) { // TODO: Fix this Exception to be IrremovableRoleException
                    model.addAttribute("roleMessage", "User must have at least one role");
                }
            } else if (action.equals("add")) {
                UserRoleChangeResponse response = userAccountService.addRole(id, UserRole.forNumber(roleNumber));
                if (!response.getIsSuccess()) {
                    model.addAttribute("roleMessage", "Error adding role");
                }
            }
        }

        return listUsers(principal, Optional.empty(), Optional.empty(), Optional.empty(), model);


    }



    public String formatUrl(int page, String sortBy, boolean sortDir) {
        return String.format("?page=%d&sortBy=%s&asc=%b", page, sortBy, sortDir);
    }

    public String formatUserRole(UserRole role) {
        switch (role) {
            case STUDENT: return "Student";
            case TEACHER: return "Teacher";
            case COURSE_ADMINISTRATOR: return "Course Administrator";
            default: return "Role not found";
        }
    }

    public List<UserRole> getAvailableRoles(UserResponse user) {
        List<UserRole> list = new ArrayList<>();
        for(UserRole role : UserRole.values()){
            if(role != UserRole.UNRECOGNIZED && !user.getRolesList().contains(role)){
                list.add(role);
            }
        }
        return list;
    }
}
