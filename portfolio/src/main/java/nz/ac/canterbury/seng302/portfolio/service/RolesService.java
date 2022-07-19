package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This service will take the Authentication Principal and look for roles in the token
 */
@Service
public class RolesService {

    /**
     * this method will take the authenticated Principal and retrieve the role of the user and reutrn a Array of type string
     * containing all the roles.
     * @param principal Authentication principal of type AuthState
     * @return Array of roles
     */
    public ArrayList<String> getRolesByToken(PortfolioPrincipal principal) {
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");
        return new ArrayList<String>(List.of((role.toUpperCase()).split(",")));
    }
}
