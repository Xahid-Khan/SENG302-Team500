package nz.ac.canterbury.seng302.portfolio.model.contract;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.util.List;

public record UserContract(
        String firstName,
        String middleName,
        String lastName,
        String nickName,
        String username,
        String email,
        String personalPronouns,
        String bio,
        List<UserRole> roles
) {}
