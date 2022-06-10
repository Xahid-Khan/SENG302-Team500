package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


public class AuthorisationParamsHelper {

    public static void setParams(String name, String role) {

        ClaimDTO.Builder newClaim = ClaimDTO.newBuilder();
        newClaim.setIssuer("Local Auths");
        newClaim.setType(name);
        newClaim.setValue(role);

        AuthState.Builder newState = AuthState.newBuilder();
        newState.addClaims(newClaim)
                .setIsAuthenticated(true)
                .setNameClaimType("name")
                .setRoleClaimType("role")
                .setAuthenticationType("AuthenticationTypes.Federation");


        // With complements to: https://stackoverflow.com/a/46631015
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.setContext(securityContext);

        AuthState authState = newState.build();
        PortfolioPrincipal principal = new PortfolioPrincipal(authState);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

    }
}
