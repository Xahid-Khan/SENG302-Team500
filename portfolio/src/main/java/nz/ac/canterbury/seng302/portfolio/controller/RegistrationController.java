package nz.ac.canterbury.seng302.portfolio.controller;

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

    //Method which revieves the User object from form. Can be referenced in done
    @PostMapping("/register")//Mapped to post
    public String registerSubmit(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);//reads post data to user object
        System.out.println(user);
        System.out.println(user.getEmail());

        return "registered";
    }
}