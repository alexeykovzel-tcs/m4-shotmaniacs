package com.shotmaniacs.rest.resources;

import com.shotmaniacs.dao.AssignmentDao;
import com.shotmaniacs.dao.EventDao;
import com.shotmaniacs.models.Assignment;
import com.shotmaniacs.models.event.StatusPermission;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/events/{eventId}/assigned")
@RolesAllowed({"admin", "crew"})
public class AssignmentsResource extends RestResource {

    private final AssignmentDao assignmentDao = new AssignmentDao();
    private final EventDao eventDao = new EventDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Assignment> getAssignments(@PathParam("eventId") long eventId) {
        authorize(eventId, "view assignments");
        return assignmentDao.getByEventId(eventId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response assignEvent(Assignment assignment) {
        // Verify that the user is authorized to assign events
        if (!context.isUserInRole("admin") && getUserId() != assignment.getMemberId()) {
            throw new NotAuthorizedException("Not authorized to assign event");
        }

        // Set default values (if were not set by the user, or were set wrongly)
        assignment.setStatus(StatusPermission.REQUEST);
        assignment.setCounterKaakjes(0);

        // Save a received assignment to the database
        // Otherwise, throw an error (e.g. if there are invalid values)
        assignmentDao.save(assignment);
        return Response.ok().build();
    }

    @GET
    @Path("/assigned/{memberId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Assignment getAssignment(@PathParam("eventId") long eventId,
                                    @PathParam("memberId") long memberId) {
        authorize(eventId, "view assignment");
        List<Assignment> assignmentList = assignmentDao.getById(getUserId(),eventId);
        if( (assignmentList != null ? assignmentList.size() : 0) >0){
            return assignmentList.get(0);
        }
        throw new BadRequestException("No such assignment");
    }

    @DELETE
    @Path("/assigned/{memberId}")
    @RolesAllowed({"admin"})
    public Response deleteAssignment(@PathParam("eventId") long eventId,
                                     @PathParam("memberId") long memberId) {
        assignmentDao.deleteById(memberId, eventId);
        return Response.ok().build();
    }

    @PUT
    @Path("/assigned/{memberId}")
    @RolesAllowed({"admin"})
    public Response updateAssignment(@PathParam("eventId") long eventId,
                                     @PathParam("memberId") long memberId,
                                     @QueryParam("status") String statusValue) {
        if (statusValue != null) {
            StatusPermission status = StatusPermission.valueOfOrNull(statusValue);
            if (status == null) throw new BadRequestException("Invalid permission status");
            assignmentDao.updateStatusById(memberId, eventId, status);
        }
        return Response.ok().build();
    }

    private void authorize(long eventId, String action) {
        long userId = getUserId();
        if (context.isUserInRole("admin")) return;
        List<Assignment> assignmentList = assignmentDao.getById(userId,eventId);
        if (context.isUserInRole("crew") && assignmentList != null && assignmentDao.getById(userId, eventId).size()>0) return;
        throw new NotAuthorizedException("Not authorized to " + action);
    }
}
