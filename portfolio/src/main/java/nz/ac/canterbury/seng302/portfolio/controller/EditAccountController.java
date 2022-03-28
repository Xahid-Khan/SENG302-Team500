package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.DTO.EditedUserValidation;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationToken;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.ViewAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class EditAccountController {

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private ViewAccountService viewAccountService;

    @Autowired
    private UserAccountController userAccountController;

    @GetMapping(value="/edit_account")
    public String getPage(Model model, @AuthenticationPrincipal AuthState principal){
        Integer userId = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse userDetails = viewAccountService.getUserById(userId);
        String registrationDate = "2 April 2021 (10 months)"; // TODO fix this

        //Prefill the form with the user's details
        model.addAttribute("user", userDetails);
        model.addAttribute("registration_date", registrationDate);

        return "edit_account";
    }

    @PostMapping(value="/edit_account")
    public String postPage(@ModelAttribute @Validated(EditedUserValidation.class) User user, BindingResult bindingResult, Model model, @AuthenticationPrincipal AuthState principal) {
        if (bindingResult.hasErrors()) {
            return "edit_account";
        }
        try {
            Integer userId = Integer.valueOf(principal.getClaimsList().stream()
                    .filter(claim -> claim.getType().equals("nameid"))
                    .findFirst()
                    .map(ClaimDTO::getValue)
                    .orElse("-100"));

            registerClientService.updateDetails(user, userId);

        } catch (StatusRuntimeException e){
            model.addAttribute("registerMessage", "Error connecting to Identity Provider..."); // TODO fix
            return "edit_account";
        }
        return "redirect:/my_account";
    }

}
