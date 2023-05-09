package com.shotmaniacs.rest.resources;

import com.shotmaniacs.dao.PersonDao;

import com.shotmaniacs.dao.SessionDao;
import com.shotmaniacs.models.Credentials;
import com.shotmaniacs.models.Session;
import com.shotmaniacs.models.user.Person;
import com.shotmaniacs.models.user.ServerRole;
import com.shotmaniacs.utils.EmailSender;
import com.shotmaniacs.utils.FormValidator;
import com.shotmaniacs.utils.PasswordHasher;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("/auth")
@PermitAll
public class AuthenticationResource extends RestResource {

    private final PersonDao personDao = new PersonDao();
    private final SessionDao sessionDao = new SessionDao();

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(Credentials credentials) {
        // Verify that credentials are valid
        boolean valid = new FormValidator().validateCredentials(credentials);
        if (!valid) throw new BadRequestException("Invalid credentials");

        // Verify that such users exists
        String email = credentials.getEmail();
        Person person = personDao.getByEmail(email);
        if (person == null) throw new BadRequestException("Such user does not exist");

        // Verify that passwords match
        String passwordHash = new PasswordHasher().hash(credentials.getPassword());
        boolean matched = Objects.equals(person.getPassword(), passwordHash);
        if (!matched) throw new BadRequestException("Invalid email or password");

        // Find existing or establish a new session for the user
        Session session = sessionDao.establish(person.getId());
        if (session == null) throw new InternalServerErrorException("Could not establish a connection with the server");

        // Send a response with an attached token
        System.out.println("Successfully logged in: " + email);
        NewCookie tokenCookie = getTokenCookie(session.getToken(), Session.EXPIRES_IN_SECONDS);
        return Response.ok().cookie(tokenCookie).entity(person.getRole().name()).build();
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(Person person) {
        String email = person.getEmail();

        // Verify that provided data is valid
        boolean valid = new FormValidator().validatePerson(person);
        if (!valid) throw new BadRequestException("Invalid data");

        // Set default role
        person.setRole(ServerRole.CLIENT);

        // Verify that such user does not exist
        boolean exists = personDao.existsByEmail(email);
        if (exists) throw new BadRequestException("Such user already exists");

        // Hash user password for security reasons
        String hashedPassword = new PasswordHasher().hash(person.getPassword());
        person.setPassword(hashedPassword);

        // Verify that user is successfully saved
        boolean saved = personDao.save(person);
        if (!saved) throw new InternalServerErrorException("Could not save the user");

        // Establish a new session for the user
        long userId = personDao.getIdByEmail(email);
        Session session = sessionDao.establish(userId);

        // Send confirmation e-mail
        new EmailSender().sendConfirmSignup(person.getFullname(), email);

        // Send a response with an attached token
        NewCookie tokenCookie = getTokenCookie(session.getToken(), Session.EXPIRES_IN_SECONDS);
        return Response.ok().cookie(tokenCookie).entity(person.getRole().name()).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@CookieParam("token") Cookie cookie) {
        boolean isTokenPresent = (cookie != null) && !cookie.getValue().equals("");
        if (!isTokenPresent) throw new BadRequestException("User is not logged in");
        // Remove the token cookie in the user's browser
        NewCookie tokenCookie = getTokenCookie(null, 0);
        return Response.ok().cookie(tokenCookie).build();
    }

    /**
     * Builds a cookie for a token. As this token is applied to almost all server resources,
     * it has a default path set to '/'. Also, it has an expiration date defined by a 'duration' variable,
     * after which this cookie is deleted in the user's browser.
     *
     * @param token    given token value (e.g. UUID)
     * @param duration how much time a token remains valid
     * @return a cookie instance that can be attached to a server response
     */
    public NewCookie getTokenCookie(String token, int duration) {
        return new NewCookie("token", token, "/", null,
                null, duration, false, true);
    }
}
