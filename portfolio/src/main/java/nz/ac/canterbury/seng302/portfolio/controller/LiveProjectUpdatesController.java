package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller for handling and dispatching live project update notifications.
 */
@Controller
public class LiveProjectUpdatesController {

  @Autowired
  private ProjectDetailsController projectDetailsController;

  /**
   * STOMP endpoint that receives a message string and echos it back with metadata attached.
   *
   * <p>
   *   This can be used in conjunction with the socket_test page to verify that the WebSocket setup
   *   is working as expected.
   * </p>
   */
  @MessageMapping("/show")
  @SendTo("/topic/pongs")
  public String show(@AuthenticationPrincipal PreAuthenticatedAuthenticationToken principal,
                 String location) {
    var authState = (PortfolioPrincipal) principal.getPrincipal();
    return "show~" + location + "~" + authState.getName();
  }

  @MessageMapping("/cancel")
  @SendTo("/topic/pongs")
  public String cancel(@AuthenticationPrincipal PreAuthenticatedAuthenticationToken principal,
                     String location) {
    var authState = (PortfolioPrincipal) principal.getPrincipal();
    return "cancel~" + location + "~" + authState.getName();
  }

  @MessageMapping("/save")
  @SendTo("/topic/pongs")
  public String save(@AuthenticationPrincipal PreAuthenticatedAuthenticationToken principal,
                     String location) {
    var authState = (PortfolioPrincipal) principal.getPrincipal();
    return "save~" + location + "~" + authState.getName();
  }
}
