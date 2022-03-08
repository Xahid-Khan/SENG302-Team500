package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.portfolio.DTO.User;

@Controller
public class RegistrationController {

    //
    @GetMapping(value="/registration")//Mapped to GET
    public String registerForm(Model model){ //model turns the view into a thymeleaf object
        model.addAttribute("user",new User());//creates the DTO object which captures the inpuitd
        return "registration"; //returns the view which renders the HTML content
    }
    //Method which revieves the User object from form. Can be referenced in done
    @PostMapping//Mapped to post
    public String registerSubmit(@ModelAttribute User user,  Model model){
        model.addAttribute("user", user);//adds the User object to the view
        return "complete";
    }
    @Autowired
    private RegisterClientService registerClientService;

    @GetMapping("/register")//set path
    public String register(
            @RequestParam(name="username", required=false, defaultValue="abc123") String username,
            @RequestParam(name="password", required=false, defaultValue="Password123!") String password,
            @RequestParam(name="firstName", required=false, defaultValue="joe") String firstName,
            @RequestParam(name="middleName", required=false, defaultValue="danger") String middleName,
            @RequestParam(name="lastName", required=false, defaultValue="soap") String lastName,
            @RequestParam(name="nickname", required=false, defaultValue="jojo") String nickname,
            @RequestParam(name="bio", required=false, defaultValue="") String bio,
            @RequestParam(name="pronouns", required=false, defaultValue="") String pronouns,
            @RequestParam(name="email", required=false, defaultValue="") String email,
            Model model
    ){//return data to the view using model
        UserRegisterResponse registerReply;
        try {
            registerReply = registerClientService.register(username, password, firstName, middleName, lastName,
                    nickname, bio, pronouns, email);
        } catch (StatusRuntimeException e){
            model.addAttribute("registerMessage", "Error connecting to Identity Provider...");
            return "register";
        }
        model.addAttribute("registerMessage",registerReply.getMessage());//add data to the model
        return "register";//return the template in templates folder
    }
}
