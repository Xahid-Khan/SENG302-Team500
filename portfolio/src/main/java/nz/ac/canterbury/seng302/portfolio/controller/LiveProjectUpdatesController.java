package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling and dispatching live project update notifications.
 */
@Controller
public class LiveProjectUpdatesController {

  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private AuthStateService authStateService;

  /**
   * STOMP endpoint that receives a message string and echos it back with metadata attached.
   *
   * <p>
   *   This can be used in conjunction with the socket_test page to verify that the WebSocket setup
   *   is working as expected.
   * </p>
   */
  @MessageMapping("/alert")
  @SendTo("/topic/edit-project")
  public Map<String, String> alert(@AuthenticationPrincipal PreAuthenticatedAuthenticationToken principal, String alert) {
    var authState = (PortfolioPrincipal) principal.getPrincipal();
    Map<String, String> message = new HashMap<>();
    message.put("username", alert.split("~")[2]);
    message.put("action", alert.split("~")[1]);
    message.put("location", alert.split("~")[0]);
    message.put("name", authState.getName());
    return message;
  }
}
