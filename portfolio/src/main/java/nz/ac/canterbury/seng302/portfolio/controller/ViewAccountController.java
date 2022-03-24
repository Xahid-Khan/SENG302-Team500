package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.model.contract.UserContract;
import nz.ac.canterbury.seng302.portfolio.service.ViewAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * This method will be invoked when API receives a GET request with User ID embedded in the URL.
     * @param userId This is an ID of the User
     * @return returns the details of user as String type
     */
    @GetMapping(value = "/account/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserContract> getUserById(@PathVariable int userId) {
        var userById = viewAccountService.getUserById(userId);
        if (userById != null) {
            return ResponseEntity.ok(userById);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}