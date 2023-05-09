package com.shotmaniacs.rest.security;

import com.shotmaniacs.dao.PersonDao;

import com.shotmaniacs.dao.SessionDao;
import com.shotmaniacs.models.Session;
import com.shotmaniacs.models.user.ServerRole;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

@PreMatching
@Priority(Priorities.AUTHORIZATION)
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    private final PersonDao personDao = new PersonDao();
    private final SessionDao sessionDao = new SessionDao();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Check if the request contains the token cookie
        Cookie tokenCookie = requestContext.getCookies().get("token");
        if (tokenCookie == null) {
            System.out.println("[ERROR] Could not find token cookie");
            return;
        }
        // Try to find a session for the retrieved token
        String token = tokenCookie.getValue();
        for (Session session : sessionDao.getAll()) {
            if (session.getToken().equals(token) && session.isActive()) {
                System.out.println("Active session id = " + session.getName());
                String scheme = requestContext.getUriInfo().getRequestUri().getScheme();
                ServerRole role = personDao.getRoleById(session.getUserId());
                var context = new SessionSecurityContext(role, session, scheme);
                requestContext.setSecurityContext(context);
                return;
            }
        }
        // Happens if either the token is expired, or the user tried to change it
        System.out.println("[ERROR] Failed to join session: " + token);
    }
}