package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.ViewAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@Controller
public class UserAccountController {

    @Autowired
    private ViewAccountService viewAccountService;

    @Autowired
    RegisterClientService registerClientService;


    /**
     * The register client service. Gives the user the edit account page
     * with their current information prefilled.
     * @param model The view model
     * @return user account page
     */
    @GetMapping(value="/my_account")
    public String getPage(Model model, @AuthenticationPrincipal AuthState principal){

        Integer userId = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse userDetails = viewAccountService.getUserById(userId);

        String registrationDate = "2 April 2021 (10 months)"; //TODO Sort this out

        //Prefill the form with the user's details
        model.addAttribute("user", userDetails);
        model.addAttribute("registration_date", registrationDate);

        return "account_details";
    }

    @GetMapping(value="/account/{id}")
    public String getUserAccount(@PathVariable int id, Model model, UserResponse user){
        var userById = viewAccountService.getUserById(id);

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

}
