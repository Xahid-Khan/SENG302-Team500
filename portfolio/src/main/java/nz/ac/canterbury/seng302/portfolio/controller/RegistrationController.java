package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
//import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.portfolio.DTO.User;

@Controller
public class RegistrationController {

    //
    @GetMapping(value = "/register")//Mapped to GET
    public String registerForm(Model model) { //model turns the view into a thymeleaf object
        model.addAttribute("user", new User());//creates the DTO object which captures the inpuitd
        return "registration_form"; //returns the view which renders the HTML content
    }


    @Autowired
    private RegisterClientService registerClientService;

    @PostMapping("/register")//set path
    public String register(
            @ModelAttribute User user,  Model model
    ){//return data to the view using model
        UserRegisterResponse registerReply;
        try {
            registerReply = registerClientService.register(user.getUsername(), user.getPassword(), user.getFirstName(), user.getMiddleName(), user.getLastName(),
                    user.getNickname(), user.getBio(), user.getPronouns(), user.getEmail());
        } catch (StatusRuntimeException e){
            model.addAttribute("registerMessage", "Error connecting to Identity Provider...");
            return "register";
        }
        model.addAttribute("registerMessage",registerReply.getMessage());//add data to the model
        return "complete";//return the template in templates folder
    }
}
