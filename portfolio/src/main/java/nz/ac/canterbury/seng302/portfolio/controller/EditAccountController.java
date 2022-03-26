package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EditAccountController {

    @GetMapping(value="/edit_account")
    public String getPage(Model model){
        //TODO Get the user's object from the database instead of making a preset user
        //TODO Field Validation
        User currentDetails = new User();
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

        return "edit_account";
    }

    @PostMapping(value="/edit_account")
    public String postPage(@ModelAttribute User user, Model model) {
        //TODO User object needs to be passed to DB

        return "account_details";
    }

}
