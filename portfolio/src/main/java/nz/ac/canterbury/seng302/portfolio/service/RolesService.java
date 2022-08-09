package nz.ac.canterbury.seng302.portfolio.service;

import java.util.Arrays;
import java.util.List;
import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.stereotype.Service;

/** This service will take the Authentication Principal and look for roles in the token. */
@Service
public class RolesService {

  /**
   * Handles obtaining the roles from the token. It will parse them into their correct UserRole
   * equivalents such that they can be handled as enum values.
   *
   * @param principal the PortfolioPrincipal to get the roles from
   * @return a list of UserRoles containing whatever is in the token
   */
  public List<UserRole> getRolesByToken(PortfolioPrincipal principal) {
    // Get roles as String array. I.E.: "STUDENT", or "STUDENT, TEACHER"
    String roles =
        principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("role"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse(UserRole.UNRECOGNIZED.toString());

    // Maps each role to UserRole enum if possible.
    return roles.contains(" ")
        ? Arrays.stream(roles.split(", ")).map(String::toUpperCase).map(UserRole::valueOf).toList()
        : Arrays.stream(roles.split(",")).map(String::toUpperCase).map(UserRole::valueOf).toList();
  }
}
