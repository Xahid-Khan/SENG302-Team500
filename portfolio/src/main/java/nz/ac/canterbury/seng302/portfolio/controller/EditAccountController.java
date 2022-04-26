package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.DTO.EditedUserValidation;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class EditAccountController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthStateService authStateService;

    @GetMapping(value="/edit_account")
    public String getPage(Model model, @AuthenticationPrincipal AuthState principal){


        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        //Prefill the form with the user's details
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("user", userDetails);
//        model.addAttribute("userImagePath", userDetails.getProfileImagePath());

        return "edit_account";

    }

    @PostMapping(value="/edit_account")
    public String postPage(@ModelAttribute @Validated(EditedUserValidation.class) User user, BindingResult bindingResult, Model model, @AuthenticationPrincipal AuthState principal) {

        if (bindingResult.hasErrors()) {
            return "edit_account";
        }
        try {
            var imageData = model.getAttribute("userImagePath");


            Integer userId = authStateService.getId(principal);

            registerClientService.updateDetails(user, userId);

        } catch (StatusRuntimeException e){
            model.addAttribute("registerMessage", "Error connecting to Identity Provider...");
            return "edit_account";
        }
        return "redirect:/my_account";
    }

}
