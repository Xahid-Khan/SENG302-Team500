package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/** Handles the post and delete requests on the /subscribe endpoint. */
@Controller
@RequestMapping("/api/v1")
public class HomePageController {

    @Autowired
    private SubscriptionService subscriptionService;

    /** Handles post requests on the /subscribe endpoint to subscribe a user to a group. */
    @PostMapping(value = "/subscribe", produces = "application/json")
    public ResponseEntity<?> subscribe(@AuthenticationPrincipal PortfolioPrincipal principal,
                                       @RequestBody SubscriptionContract subscription) {
        try{
            subscriptionService.subscribe(subscription);
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    /** Handles delete requests on the /subscribe endpoint to unsubscribe a user from a group. */
    @DeleteMapping(value = "/subscribe", produces = "application/json")
    public ResponseEntity<?> unsubscribe(@AuthenticationPrincipal PortfolioPrincipal principal,
                                       @RequestBody SubscriptionContract subscription) {
        try{
            subscriptionService.unsubscribe(subscription);
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}
