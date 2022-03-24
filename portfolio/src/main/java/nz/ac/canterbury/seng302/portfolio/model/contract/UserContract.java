package nz.ac.canterbury.seng302.portfolio.model.contract;

public record UserContract(
        String firstName,
        String middleName,
        String lastName,
        String nickName,
        String username,
        String email,
        String pronouns,
        String bio
) {}
