package nz.ac.canterbury.seng302.identityprovider.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDataValidationServerService {
    public boolean validateName(String name) {
        var nameRegex = "^[-a-zA-Z\s]+$";
        return (name.strip().length() > 0 &&  name.matches(nameRegex));
    }

    public boolean validateEmail(String email) {
        var emailRegex = "^(.+)@(\\S+)$"; // ^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$
        return email.matches(emailRegex);
    }

    public boolean optionalFields(String data) {
        var regexPattern = "^[a-zA-Z0-9\s]*$";
        return data.matches(regexPattern);
    }

    public boolean pronounsValidation(String data) {
        ArrayList<String> allowedNouns = new ArrayList<String>();
        allowedNouns.add("SHE");
        allowedNouns.add("HER");
        allowedNouns.add("HERS");
        allowedNouns.add("HE");
        allowedNouns.add("HIM");
        allowedNouns.add("HIS");
        allowedNouns.add("THEY");
        allowedNouns.add("THEM");
        return allowedNouns.contains(data.split("/"));
    }



}
