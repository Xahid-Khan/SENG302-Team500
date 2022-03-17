package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.model.entity.UserEntity;
import nz.ac.canterbury.seng302.portfolio.service.ViewAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

/**
 * This view account controller allows users to make API calls, such as GET, POST, PUT, DELETE requests.
 * User will be view all the details and update them.
 */

@RestController
@RequestMapping("/api/v1")
public class ViewAccountController {
    @Autowired
    private ViewAccountService viewAccountService;

    /**
     *
     */
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDetailsById(@PathVariable String userId) {
        try {
            var userDetails = viewAccountService.get(userId);
            return ResponseEntity.ok(userDetails);
        }
        catch (NoSuchElementException e ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
