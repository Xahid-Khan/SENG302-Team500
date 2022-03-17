package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

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
            @Valid @ModelAttribute User user, BindingResult result, Model model
    ){//return data to the view using model
//        UserRegisterResponse registerReply;
//        try {
//            registerReply = registerClientService.register(user.getUsername(), user.getPassword(), user.getFirstName(), user.getMiddleName(), user.getLastName(),
//                    user.getNickname(), user.getBio(), user.getPronouns(), user.getEmail());
//        } catch (StatusRuntimeException e){
//            model.addAttribute("registerMessage", "Error connecting to Identity Provider...");
//            return "registration_form";
//
//        }
//        model.addAttribute("registerMessage",registerReply.getMessage());//add data to the model
        if(result.hasErrors()){
            return "registration_form";
        }
        System.out.println("GOT HERE");
        return "greeting";//return the template in templates folder
    }
}
