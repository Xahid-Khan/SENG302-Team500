package nz.ac.canterbury.seng302.portfolio.controller;


import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.DTO.EditedUserValidation;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.ChangePasswordClientService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ChangePasswordResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class EditPasswordController {

    @Autowired
    private ChangePasswordClientService changePasswordClientService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthStateService authStateService;


    @GetMapping(value="/edit_password")
    public String getPage(Model model, @AuthenticationPrincipal AuthState principal){

        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        //Prefill the form with the user's details
        model.addAttribute("username", userDetails.getUsername());

        return "edit_password";

    }

    @PostMapping(value="/edit_password")
    public String postPage(Model model,
                           @AuthenticationPrincipal AuthState principal,
                           @RequestParam(value = "currentPassword") String currentPassword,
                           @RequestParam(value = "newPassword") String newPassword,
                           @RequestParam(value = "confirmPassword") String confirmPassword) {

        if (newPassword.length() < 8 || newPassword.length() > 512) {
            model.addAttribute("error", "New password must contain at least 8 and be no more than 512 characters");
            model.addAttribute("currentPass", currentPassword);
            model.addAttribute("newPass", newPassword);
            model.addAttribute("confirmPass", confirmPassword);
            return "edit_password";
        }

        if (!newPassword.matches("^(?!.*  )(.*)")) {
            model.addAttribute("error", "New password cannot contain more than one whitespace in a row");
            model.addAttribute("currentPass", currentPassword);
            model.addAttribute("newPass", newPassword);
            model.addAttribute("confirmPass", confirmPassword);
            return "edit_password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password does not match confirmed password");
            model.addAttribute("currentPass", currentPassword);
            model.addAttribute("newPass", newPassword);
            model.addAttribute("confirmPass", confirmPassword);
            return "edit_password";
        }

        if (newPassword.equals(currentPassword)) {
            model.addAttribute("error", "New password cannot be the same as current password");
            model.addAttribute("currentPass", currentPassword);
            model.addAttribute("newPass", newPassword);
            model.addAttribute("confirmPass", confirmPassword);
            return "edit_password";
        }

        try {

            Integer userId = authStateService.getId(principal);
            ChangePasswordResponse response = changePasswordClientService.updatePassword(userId, currentPassword, newPassword);

            if (!response.getIsSuccess()) {
                model.addAttribute("error", response.getMessage());
                model.addAttribute("currentPass", currentPassword);
                model.addAttribute("newPass", newPassword);
                model.addAttribute("confirmPass", confirmPassword);
                return "edit_password";
            }

        } catch (StatusRuntimeException e){
            model.addAttribute("error", "Error connecting to Identity Provider...");
            model.addAttribute("currentPass", currentPassword);
            model.addAttribute("newPass", newPassword);
            model.addAttribute("confirmPass", confirmPassword);
            return "edit_password";
        }
        return "redirect:/my_account?edited=password";
    }

}
