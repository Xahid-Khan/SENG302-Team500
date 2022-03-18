package nz.ac.canterbury.seng302.portfolio.controller;


import io.grpc.netty.shaded.io.netty.handler.codec.json.JsonObjectDecoder;
import nz.ac.canterbury.seng302.portfolio.model.entity.UserEntity;
import nz.ac.canterbury.seng302.portfolio.service.ViewAccountService;
import org.apache.tomcat.util.json.JSONParser;
import org.h2.util.json.JSONBoolean;
import org.h2.util.json.JSONObject;
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



    @GetMapping(value = "/account/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserById(@PathVariable int userId) {
        try {
            System.out.println("11111111111111111111111111111111");
            var userById = viewAccountService.getUserById(userId);
            var responseUser = "User : {" +
                    "First Name : " + userById.getFirstName() +
                    "Middle Name : " + userById.getMiddleName() +
                    "Last Name : " + userById.getLastName() +
                    "User Name : " + userById.getUsername() +
                    "Email : " + userById.getEmail() +
                    "Bio : " + userById.getBio() +
                    "}";
            return ResponseEntity.ok(responseUser);
        } catch (Exception e) {
            System.out.println("RRRRRRRRRRRRRRRRRRRR");
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllUsers() {
        try {
            System.out.println("22222222222222222222222222222222222");
            var users = viewAccountService.getAllUsers();
            return ResponseEntity.ok(users.toString());
        } catch (Exception e) {
            System.out.println("EEEEEEEEEEEEEEEEEEEE");
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
