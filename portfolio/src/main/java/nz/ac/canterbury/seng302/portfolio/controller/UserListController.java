package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.GetPaginatedUsersOrderingElement;
import nz.ac.canterbury.seng302.portfolio.model.entity.SortingParameterEntity;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.SortingParametersService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
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

    @Autowired
    private GroupsClientService groupsClientService;


    @GetMapping("/user-list")
    public String listUsers(
            @AuthenticationPrincipal PortfolioPrincipal principal,
            @RequestParam("page") Optional<Integer> pageMaybe,
            @RequestParam("sortBy") Optional<String> sortAttributeMaybe,
            @RequestParam("asc") Optional<Boolean> ascendingMaybe,
            Model model
    ) {

        Integer userId = authStateService.getId(principal);
        model.addAttribute("userId", userId);

        UserResponse userDetails = userAccountService.getUserById(userId);
        List<UserRole> roles = userDetails.getRolesList();

        model.addAttribute("isAdmin", hasAdmin(roles));

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
        model.addAttribute("totalUserCount", response.getPaginationResponseOptions().getResultSetSize());
        model.addAttribute("pageOffset", offset);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortDir", ascending);
        model.addAttribute("sortBy", sortAttributeString);
        model.addAttribute("delegate", this);
        model.addAttribute("pageSize", PAGE_SIZE);
        return "user_list";
    }

    private void modifyRole(PortfolioPrincipal principal, Model model, Integer id, Integer roleNumber, boolean adding) {
        Integer userId = authStateService.getId(principal);
        UserResponse userDetails = userAccountService.getUserById(userId);
        List<UserRole> roles = userDetails.getRolesList();
        model.addAttribute("roleMessageTarget", id);

        if (hasAdmin(roles)) {
            if (!userId.equals(id)) {
                UserRoleChangeResponse response = adding ?
                        userAccountService.addRole(id, UserRole.forNumber(roleNumber)) :
                        userAccountService.removeRole(id, UserRole.forNumber(roleNumber));
                if (!response.getIsSuccess()) {
                    model.addAttribute("roleMessage", response.getMessage());
                }
            } else {
                model.addAttribute("roleMessage", "Cannot modify roles for yourself");
            }
        } else {
            model.addAttribute("roleMessage", "Error: insufficient permission");
        }
    }

    @PostMapping("/user-list")
    public String addRole(@AuthenticationPrincipal PortfolioPrincipal principal,
                          Model model,
                          @RequestParam(name="id") Integer id,
                          @RequestParam(name="roleNumber") Integer roleNumber) {

        modifyRole(principal, model, id, roleNumber, true);
        if (roleNumber == 1 || roleNumber == 2) {
            PaginatedGroupsResponse allGroupDetails = groupsClientService.getAllGroupDetails();
            boolean inNoOtherGroup = false;
            Integer nonGroupID = null;
            for (GroupDetailsResponse group : allGroupDetails.getGroupsList()) {
                if (group.getShortName().equals("Non Group")) {
                    nonGroupID = group.getGroupId();
                    for (UserResponse user: group.getMembersList()) {
                        if (user.getId() == id) {
                            inNoOtherGroup = true;
                        }
                    }
                }
                if (group.getShortName().equals("Teachers")) {
                    groupsClientService.addGroupMembers(group.getGroupId(), List.of(id));
                }
            }
            if (inNoOtherGroup) {
                groupsClientService.removeGroupMembers(nonGroupID, List.of(id));
            }
        }

        return listUsers(principal, Optional.empty(), Optional.empty(), Optional.empty(), model);
    }

    @DeleteMapping("/user-list")
    public String deleteRole(@AuthenticationPrincipal PortfolioPrincipal principal,
                             Model model,
                             @RequestParam(name="id") Integer id,
                             @RequestParam(name="roleNumber") Integer roleNumber) {

        UserResponse user = userAccountService.getUserById(id);
        modifyRole(principal, model, id, roleNumber, false);

        if (roleNumber == 1 || roleNumber == 2) {
            if (!user.getRolesList().contains(UserRole.TEACHER) || !user.getRolesList().contains(UserRole.COURSE_ADMINISTRATOR)) {
                PaginatedGroupsResponse allGroupDetails = groupsClientService.getAllGroupDetails();
                boolean inOtherGroup = false;
                Integer nonGroupID = null;
                for (GroupDetailsResponse group : allGroupDetails.getGroupsList()) {
                    if (group.getShortName().equals("Non Group")) {
                        nonGroupID = group.getGroupId();
                    }
                    if (group.getShortName().equals("Teachers")) {
                        groupsClientService.removeGroupMembers(group.getGroupId(), List.of(id));
                    } else {
                        for (UserResponse otherUser: group.getMembersList()) {
                            if (otherUser.getId() == id) {
                                inOtherGroup = true;
                            }
                        }
                    }
                }
                if (!inOtherGroup) {
                    groupsClientService.addGroupMembers(nonGroupID, List.of(id));
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

    private boolean hasAdmin(List<UserRole> roles){
        return (roles.contains(UserRole.COURSE_ADMINISTRATOR) || roles.contains(UserRole.TEACHER));
    }
}
