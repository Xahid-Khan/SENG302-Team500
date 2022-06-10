package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;

/**
 * Controller for handling and dispatching live project update notifications.
 */
@Controller
public class LiveProjectUpdatesController {

  /**
   * STOMP endpoint that receives a message string and echos it back with metadata attached.
   *
   * <p>
   *   This can be used in conjunction with the socket_test page to verify that the WebSocket setup
   *   is working as expected.
   * </p>
   */
  @MessageMapping("/ping")
  @SendTo("/topic/pongs")
  public String ping(@AuthenticationPrincipal PreAuthenticatedAuthenticationToken principal,
      String message) {
    var authState = (PortfolioPrincipal) principal.getPrincipal();
    return String.format("Pong %s (Also, hi %s!)", message, authState.getName());
  }
}
