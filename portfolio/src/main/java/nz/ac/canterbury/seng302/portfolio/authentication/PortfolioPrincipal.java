package nz.ac.canterbury.seng302.portfolio.authentication;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

import java.security.Principal;


public record PortfolioPrincipal(AuthState authState) implements Principal {
    @Override
    public String getName() {
        return authState.getName();
    }

    @Override
    public String toString() {
        return getName();
    }
}
