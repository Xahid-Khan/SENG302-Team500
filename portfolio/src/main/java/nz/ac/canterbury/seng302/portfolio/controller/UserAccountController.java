package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.mapping.UserMapper;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;


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
    public String getPage(Model model,
                          @AuthenticationPrincipal AuthState principal,
                          @RequestParam Optional<String> edited){
        if (edited.isPresent()) {
            if (edited.get().equals("password")) {
                model.addAttribute("editMessage", "Password changed successfully");
            } else if (edited.get().equals("details")) {
                model.addAttribute("editMessage", "User details updated successfully");
            }
        }

        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        String dateString = getFormattedDate(userDetails.getCreated());

        //Prefill the form with the user's details
        model.addAttribute("delegate", this);
        model.addAttribute("user", userDetails);
        model.addAttribute("registration_date", dateString);


        return "account_details";
    }

    @GetMapping(value="/account/{id}")
    public String getUserAccount(@PathVariable int id, Model model){

        UserResponse userDetails = userAccountService.getUserById(id);

        String dateString = getFormattedDate(userDetails.getCreated());

        //Prefill the form with the user's details
        model.addAttribute("delegate", this);
        model.addAttribute("user", userDetails);
        model.addAttribute("registration_date", dateString);

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

        long years = ChronoUnit.YEARS.between(date, LocalDate.now());
        long months = ChronoUnit.MONTHS.between(date, LocalDate.now()) % 12;

        return dateString +
                " (" +
                ((years == 0) ? "" : years + " years ") +
                ((years != 0 && months == 0) ? "" : months + " months") +
                ")";
    }

    /**
     * This function is used by Thymeleaf whenever a list of roles must be displayed to the user.
     * It converts the roles into a human readable list, seperated by commas if need be.
     *
     * @param roles a list of roles of the user
     * @return      the human friendly readable output of the roles
     */
    public String formatUserRoles(List<UserRole> roles) {
        return roles.stream().map(role -> switch (role) {
            case STUDENT -> "Student";
            case TEACHER -> "Teacher";
            case COURSE_ADMINISTRATOR -> "Course Administrator";
            default -> "Student";
        }).collect(Collectors.joining(", "));
    }
}
