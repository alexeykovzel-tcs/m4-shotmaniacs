package com.shotmaniacs.rest.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ServerExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException e) {
        Response.Status status = e.getResponse().getStatusInfo().toEnum();
        return Response.status(status)
                .entity(status + ": " + e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}