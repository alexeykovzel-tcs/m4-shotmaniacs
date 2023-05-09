package com.shotmaniacs.rest.resources;

import com.shotmaniacs.dao.PersonDao;
import com.shotmaniacs.models.dto.PersonDTO;
import com.shotmaniacs.models.user.Person;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/profile")
@PermitAll
public class ProfileResource extends RestResource {

    private final PersonDao personDao;

    public ProfileResource() {
        personDao = new PersonDao();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin"})
    public Person getProfileById(@PathParam("id") long id) {
        return personDao.getById(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PersonDTO getProfile() {
        long userId = getUserId();
        if (userId == 0) throw new BadRequestException("Failed to load profile");
        Optional<PersonDTO> person = personDao.getDtoById(userId);
        return person.orElseThrow(() -> new BadRequestException("Profile was not found"));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProfile(Person person) {
        authorize(person.getId(), "update this profile");
        personDao.update(person);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProfile(@PathParam("id") long id) {
        authorize(id, "delete this profile");
        if (!personDao.deleteById(id)) throw new BadRequestException("Such user does not exist");
        return Response.ok().build();
    }

    private void authorize(long id, String action) {
        boolean authorized = context.isUserInRole("admin") || getUserId() == id;
        if (!authorized) throw new ForbiddenException("Not authorized to " + action);
    }
}
