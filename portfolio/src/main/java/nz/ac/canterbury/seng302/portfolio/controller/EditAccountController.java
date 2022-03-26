package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class EditAccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @GetMapping(value="/edit_account")
    public String getPage(Model model){
        //TODO Get the user's object from the database instead of making a preset user
        //TODO Field Validation
        User currentDetails = new User("abc", "", "John", "Jane", "Doe", "Jonny", "hi im john", "he/him", "test@gmail.com");

        String registrationDate = "Member since: 2 April 2021 (10 months)"; // TODO fix this

        //Prefill the form with the user's details
        model.addAttribute("user",currentDetails);
        model.addAttribute("registration_date", registrationDate);

        return "edit_account";
    }

    @PostMapping(value="/edit_account")
    public String postPage(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
        //TODO User object needs to be passed to DB
        if (bindingResult.hasErrors()) {
            return "edit_account";
        }

        try {
            registerClientService.updateDetails(user);
        } catch (StatusRuntimeException e){
            model.addAttribute("registerMessage", "Error connecting to Identity Provider...");
            return "edit_account";
        }


        return "account_details";
    }

}
