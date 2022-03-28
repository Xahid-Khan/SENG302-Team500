package nz.ac.canterbury.seng302.portfolio.model.contract;

import com.google.protobuf.Timestamp;

public record UserContract(
        String firstName,
        String middleName,
        String lastName,
        String nickName,
        String username,
        String email,
        String personalPronouns,
        String bio,
        Timestamp dateJoined
) {}
