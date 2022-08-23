package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.service.HomePageService;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Handles the GET request on the /home-page endpoint. */
@Controller
@RequestMapping("/api/v1")
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @PostMapping(value = "/home-page/subscribe", produces = "application/json")
    public ResponseEntity<?> subscribe(@AuthenticationPrincipal PortfolioPrincipal principal,
                                       @RequestBody SubscriptionContract subscription) {
        try{
            homePageService.subscribe(subscription);
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/home-page/subscribe", produces = "application/json")
    public ResponseEntity<?> unsubscribe(@AuthenticationPrincipal PortfolioPrincipal principal,
                                       @RequestBody SubscriptionContract subscription) {
        try{
            homePageService.unsubscribe(subscription);
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
}
