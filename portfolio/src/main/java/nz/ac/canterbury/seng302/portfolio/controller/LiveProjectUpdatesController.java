package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Controller for handling and dispatching live project update notifications
 */
@Controller
public class LiveProjectUpdatesController {

    @MessageMapping("/ping")
    @SendTo("/topic/pongs")
    public String ping(@AuthenticationPrincipal Principal principal, String message) throws Exception {
        var authState = (AuthState) ((PreAuthenticatedAuthenticationToken) principal).getPrincipal();
        return String.format("Pong %s (Also, hi %s!)", message, authState.getName());
    }
}
