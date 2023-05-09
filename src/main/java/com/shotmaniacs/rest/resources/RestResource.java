package com.shotmaniacs.rest.resources;

import com.shotmaniacs.models.Session;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class RestResource {

    @Context
    protected SecurityContext context;

    protected long getUserId() {
        Session session = (Session) context.getUserPrincipal();
        if (session == null) return 0;
        return session.getUserId();
    }
}
