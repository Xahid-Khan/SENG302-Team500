package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.ViewAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String getPage(Model model){
        //@TODO Get the user's object from the database instead of making a preset user
        User currentDetails = new User();
        var userById = viewAccountService.getUserById(1);
        currentDetails.setFirstName("John");
        currentDetails.setLastName("Doe");
        currentDetails.setEmail("test@gmail.com");
        currentDetails.setPassword("password");
        currentDetails.setUsername("test");
        currentDetails.setNickname("test");
        currentDetails.setBio("I am a test user");
        currentDetails.setPronouns("They/Them");

        String registrationDate = "Member since: 2 April 2021 (10 months)";

        //Prefill the form with the user's details
        model.addAttribute("user",currentDetails);
        model.addAttribute("registration_date", registrationDate);

        return "account_details";
    }

    @GetMapping(value="/account/{id}")
    public String getUserAccount(@PathVariable int id, Model model, UserResponse user){
        var userById = viewAccountService.getUserById(id);

        String registrationDate = "Member since: 2 April 2021 (10 months)";

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
        User user = new User();
        user.setUsername(userResponse.getUsername());
        user.setFirstName(userResponse.getFirstName());
        user.setLastName(userResponse.getLastName());
        user.setEmail(userResponse.getEmail());
        user.setNickname(userResponse.getNickname());
        user.setBio(userResponse.getBio());
        user.setPronouns(userResponse.getPersonalPronouns());

        return user;
    }



}
