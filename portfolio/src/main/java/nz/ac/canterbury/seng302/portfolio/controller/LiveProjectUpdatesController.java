package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling and dispatching live project update notifications
 */
@Controller
public class LiveProjectUpdatesController {

    @MessageMapping("/ping")
    @SendTo("/topic/pongs")
    public String ping(String message) throws Exception {
        return String.format("Pong %s", message);
    }
}
