package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.mapping.UserMapper;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


@Controller
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    RegisterClientService registerClientService;

    @Autowired
    private AuthStateService authStateService;

    @Autowired
    UserMapper mapper;


    /**
     * The register client service. Gives the user the edit account page
     * with their current information prefilled.
     * @param model The view model
     * @return user account page
     */
    @GetMapping(value="/my_account")
    public String getPage(Model model, @AuthenticationPrincipal AuthState principal){

        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        String dateString = getFormattedDate(userDetails.getCreated());

        //Prefill the form with the user's details
        model.addAttribute("user", userDetails);
        model.addAttribute("registration_date", dateString);

        return "account_details";
    }

    @GetMapping(value="/account/{id}")
    public String getUserAccount(@PathVariable int id, Model model, UserResponse user){
        var userById = userAccountService.getUserById(id);

        User currentDetails= mapper.UserResponseToUserDTO(userById);
        model.addAttribute("user",currentDetails);
        model.addAttribute("registration_date", currentDetails.created());

        return "account_details";
    }

    //TODO find me a new home
    public String getFormattedDate(Timestamp created) {
        // TODO timezone?
        LocalDate date = Instant
                .ofEpochSecond( created.getSeconds() , created.getNanos() )
                .atZone( ZoneId.of( "Pacific/Auckland" ) )
                .toLocalDate();

        String dateString = date.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG));
        return dateString;
    }

}
