package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

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
            return "login";
        }
        model.addAttribute("registerMessage",registerReply.getMessage());//add data to the model
        return "register";//return the template in templates folder
    }
}
