package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.UserContract;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserContract toContract(UserResponse userResponse) {
        return new UserContract(
                userResponse.getFirstName(),
                userResponse.getMiddleName(),
                userResponse.getLastName(),
                userResponse.getNickname(),
                userResponse.getUsername(),
                userResponse.getEmail(),
                userResponse.getPersonalPronouns(),
                userResponse.getBio(),
                userResponse.getRolesList()
        );
    }
}
