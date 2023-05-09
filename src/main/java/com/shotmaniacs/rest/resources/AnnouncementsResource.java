package com.shotmaniacs.rest.resources;

import com.shotmaniacs.dao.AnnouncementDao;
import com.shotmaniacs.dao.CrewDao;
import com.shotmaniacs.models.Announcement;
import com.shotmaniacs.models.dto.AnnouncementDTO;
import com.shotmaniacs.models.user.CrewMember;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/announcements")
@RolesAllowed({"admin", "crew"})
public class AnnouncementsResource extends RestResource {

    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final CrewDao crewDao = new CrewDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Announcement> getAnnouncements(@CookieParam(value = "token") String token) {
        // Return all announcements for admins
        if (context.isUserInRole("admin")) {
            return announcementDao.getAll();
        }
        // Otherwise specifically for crew members
        CrewMember member = (CrewMember) crewDao.getById(getUserId());
        return announcementDao.getForCrewMember(member);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin"})
    public Response editAnnouncement(AnnouncementDTO dto) {
        announcementDao.edit(dto);
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"admin"})
    public Response saveAnnouncement(Announcement announcement) {
        announcement.setSenderId(getUserId());
        long id = announcementDao.save(announcement);
        return Response.ok().entity(id).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin"})
    public Response deleteAnnouncementById(@PathParam("id") int id) {
        boolean success = announcementDao.deleteById(id);
        if (!success) throw new BadRequestException("Failed to delete an announcement");
        return Response.ok().build();
    }
}
