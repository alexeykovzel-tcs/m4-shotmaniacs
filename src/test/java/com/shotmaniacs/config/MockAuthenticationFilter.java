package com.shotmaniacs.config;

import com.shotmaniacs.models.Session;
import com.shotmaniacs.models.user.ServerRole;
import com.shotmaniacs.rest.security.SessionSecurityContext;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

@PreMatching
@Priority(Priorities.AUTHORIZATION)
@Provider
public class MockAuthenticationFilter implements ContainerRequestFilter {

    public static final long USER_ID = 567;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        MultivaluedMap<String, String> headers = requestContext.getHeaders();
        if (headers.containsKey("auth")) {
            ServerRole role = ServerRole.valueOfOrNull(headers.get("auth").get(0));
            SecurityContext context = new SessionSecurityContext(role, new Session(USER_ID), "http");
            requestContext.setSecurityContext(context);
        }
    }
}
