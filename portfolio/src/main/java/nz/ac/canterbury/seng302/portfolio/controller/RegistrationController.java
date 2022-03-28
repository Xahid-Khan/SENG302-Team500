package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
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

/**
 * Handles the /register endpoint for either GET requests or POST requests.
 */
@Controller
public class RegistrationController {

  /**
   * Calling the /register endpoint with a GET request will return the user a form to fill out for
   * registration.
   *
   * @param model Adds a blank user for the user to fill in
   * @return      The registration_form thymeleaf page
   */
  @GetMapping(value = "/register")
  public String registerForm(Model model) {
    model.addAttribute("user", new User("", "", "", "", "", "", "", "", ""));
    return "registration_form";
  }

  @Autowired private RegisterClientService registerClientService;

  /**
   * Calling the /register endpoint with a POST request will validate the user, and send the user to
   *  the RegisterClientService to be sent up for database validation and saving. If an error occurs
   *  along the way, it will be caught here. If all is successful, the registered thymeleaf page
   *  will be loaded.
   *
   * @param user            The user passed to the controller
   * @param bindingResult   The result of validation on the user
   * @param model           The model to update for errors for thymeleaf
   * @return                The registration_form thymeleaf page if unsuccessful,
   *     the registered thymeleaf page otherwise.
   */
  @PostMapping("/register")
  public String register(
      @ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
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
    return "redirect:/login"; // return the template in templates folder
  }
}
