package com.shotmaniacs.rest.security;

import com.shotmaniacs.models.Session;
import com.shotmaniacs.models.user.ServerRole;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class SessionSecurityContext implements SecurityContext {
    private final ServerRole role;
    private final Session session;
    private final String scheme;

    public SessionSecurityContext(ServerRole role, Session session, String scheme) {
        this.role = role;
        this.session = session;
        this.scheme = scheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return session;
    }

    @Override
    public boolean isUserInRole(String roleValue) {
        if (session == null) return false;
        return role == ServerRole.valueOfOrNull(roleValue);
    }

    @Override
    public boolean isSecure() {
        return "https".equals(this.scheme);
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}