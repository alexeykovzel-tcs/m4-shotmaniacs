package com.shotmaniacs.rest.resources;

import com.shotmaniacs.dao.AssignmentDao;
import com.shotmaniacs.dao.CrewDao;
import com.shotmaniacs.dao.EventDao;
import com.shotmaniacs.models.Assignment;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.dto.CrewMemberDTO;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.models.user.ProjectRole;
import com.shotmaniacs.utils.DateUtils;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/crew/all")
@RolesAllowed({"admin", "crew"})
public class CrewResource extends RestResource {

    private final AssignmentDao assignmentDao = new AssignmentDao();
    private final EventDao eventDao = new EventDao();
    private final CrewDao crewDao = new CrewDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin"})
    public List<CrewMember> getAllMembers() {
        return crewDao.getAll();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin"})
    public Response updateMember(CrewMemberDTO dto) {
        crewDao.update(dto);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CrewMember getMemberById(@PathParam("id") long id) {
        authorize(id, "view member profile");
        return (CrewMember) crewDao.getById(id);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin"})
    public Response deleteMemberById(@PathParam("id") long id) {
        crewDao.deleteById(id);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWorkingHours(@PathParam("id") long id) {
        authorize(id, "view stats");

        int hoursPerMonth = 0;
        int hoursPerWeek = 0;
        int hoursPerDay = 0;

        // Count working hours per each assignment
        for (Assignment assignment : assignmentDao.getByMemberId(id)) {
            Event event = eventDao.getById(assignment.getEventId());
            int duration = assignment.getDuration();
            if (event != null) {
                int days = DateUtils.getPassedDays(event.getEndDate());
                if (days <= 1) hoursPerDay += duration;
                if (days <= 7) hoursPerWeek += duration;
                if (days <= 30) hoursPerMonth += duration;
            }
        }

        // Return working hours in JSON format
        return String.format("{ month: %d, week: %d, day: %d }",
                hoursPerMonth, hoursPerWeek, hoursPerDay);
    }

    private void authorize(long memberId, String action) {
        if (!context.isUserInRole("admin") && getUserId() != memberId) {
            throw new NotAuthorizedException("Not authorized to " + action);
        }
    }
}
