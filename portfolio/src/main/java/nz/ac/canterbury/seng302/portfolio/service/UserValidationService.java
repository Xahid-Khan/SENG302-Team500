package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService {

    public String checkRegisterUser(User user) {
        return "not Okay";
    }

    public String checkEditUser(User user) {
        return "not Okay";
    }
}
