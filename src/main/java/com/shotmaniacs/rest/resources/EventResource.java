package com.shotmaniacs.rest.resources;

import com.shotmaniacs.dao.AssignmentDao;
import com.shotmaniacs.dao.EventDao;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.event.EventStatus;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/events/all/{id}")
@PermitAll
public class EventResource extends RestResource {

    private final AssignmentDao assignmentDao = new AssignmentDao();
    private final EventDao eventDao = new EventDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Event getEvent(@PathParam("id") long id) {
        if (!eventDao.existById(id)) throw new BadRequestException("Such event does not exist");
        authorize(id, "get this event");
        return eventDao.getById(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"client", "admin"})
    public Response updateEvent(@PathParam("id") long id,
                                @QueryParam("status") String statusValue) {
        authorize(id, "update this event");
        EventStatus status = EventStatus.valueOfOrNull(statusValue);
        boolean updated = eventDao.updateStatus(id, status);
        if (!updated) throw new BadRequestException("Failed to update an event");
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({"client", "admin"})
    public Response deleteEvent(@PathParam("id") long id) {
        authorize(id, "delete this event");
        boolean deleted = eventDao.deleteById(id);
        if (!deleted) throw new BadRequestException("Failed to delete an event");
        return Response.ok().build();
    }

    @GET
    @Path("/photos")
    public InputStream getPhotos(@PathParam("id") long id) {
        authorize(id, "get event photos");
        // TODO: Complete this step.
        return null;
    }

    @POST
    @Path("/kaakjes")
    @RolesAllowed({"crew"})
    public Response incrementKaakjes(@PathParam("id") long id) {
        authorize(id, "increment kaakjes");
        assignmentDao.incrementKaakjes(getUserId(), id);
        return Response.ok().build();
    }

    private void authorize(long eventId, String action) {
        long userId = getUserId();
        if (context.isUserInRole("admin")) return;
        if (context.isUserInRole("client") && eventDao.getClientIdById(eventId) == userId) return;
        if (context.isUserInRole("crew") && assignmentDao.getById(userId, eventId).size()>0) return;
        throw new NotAuthorizedException("Not authorized to " + action);
    }
}
