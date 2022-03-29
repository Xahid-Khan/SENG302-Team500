package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthStateService {

    public Integer getId(AuthState authState){
        return Integer.valueOf(getClaimByType(authState, "nameid", "-100"));
    }

    public String getRole(AuthState authState){
        return getClaimByType(authState, "role", "NOT FOUND");
    }

    private String getClaimByType(AuthState authState, String claimType, String defaultValue){
        return authState.getClaimsList().stream()
                .filter(claim -> claim.getType().equals(claimType))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse(defaultValue);
    }
}
