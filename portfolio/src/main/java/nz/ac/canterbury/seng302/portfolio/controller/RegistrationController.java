package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import javax.validation.Valid;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

  /**
   * Calling the /register endpoint with a GET request will return the user a form to fill out for
   * registration.
   *
   * @param model
   * @return
   */
  @GetMapping(value = "/register")
  public String registerForm(Model model) {
    // TODO: Will this mean `Null` can never occur? Test this!
    model.addAttribute("user", new User("", "", "", "", "", "", "", "", ""));
    return "registration_form";
  }

  @Autowired private RegisterClientService registerClientService;

  @PostMapping("/register")
  public String register(
      @ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
    System.out.println(user.toString());
    // If there are errors in the validation of the user, display them
    if (bindingResult.hasErrors()) {
      return "registration_form";
    }

    UserRegisterResponse registerReply;
    try {
      registerReply = registerClientService.register(user);
      model.addAttribute("registerMessage", registerReply.getMessage());
    } catch (StatusRuntimeException e) {
      model.addAttribute("registerMessage", "Error connecting to Identity Provider...");
      return "registration_form";
    }
    return "registered"; // return the template in templates folder
  }
}
