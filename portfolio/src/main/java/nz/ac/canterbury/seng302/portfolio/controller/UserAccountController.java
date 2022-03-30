package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;


@Controller
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    RegisterClientService registerClientService;

    @Autowired
    private AuthStateService authStateService;


    /**
     * The register client service. Gives the user the edit account page
     * with their current information prefilled.
     * @param model The view model
     * @return user account page
     */
    @GetMapping(value="/my_account")
    public String getPage(Model model, @AuthenticationPrincipal AuthState principal){

        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        String registrationDate = "2 April 2021 (10 months)"; //TODO Sort this out

        //Prefill the form with the user's details
        model.addAttribute("delegate", this);
        model.addAttribute("user", userDetails);
        model.addAttribute("registration_date", registrationDate);

        return "account_details";
    }

    @GetMapping(value="/account/{id}")
    public String getUserAccount(@PathVariable int id, Model model, UserResponse user){
        var userById = userAccountService.getUserById(id);

        String registrationDate = "Member since: 2 April 2021 (10 months)"; // TODO implement this

        User currentDetails= UserResponseToUserDTO(userById);
        model.addAttribute("user",currentDetails);
        model.addAttribute("registration_date", registrationDate);

        return "account_details";
    }


    /**
     * This method gets a userResponse object and returns a user DTO object for use with Thymeleaf.
     * @param userResponse
     * @return User
     */
    public User UserResponseToUserDTO(UserResponse userResponse) {
        return new User(userResponse.getUsername(),
                "",
                userResponse.getFirstName(),
                userResponse.getMiddleName(),
                userResponse.getLastName(),
                userResponse.getNickname(),
                userResponse.getPersonalPronouns(),
                userResponse.getEmail(),
                userResponse.getEmail());
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
