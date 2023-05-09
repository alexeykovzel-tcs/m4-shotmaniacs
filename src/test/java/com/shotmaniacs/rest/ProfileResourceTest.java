package com.shotmaniacs.rest;

import com.shotmaniacs.config.MockAuthenticationFilter;
import com.shotmaniacs.config.Path;
import com.shotmaniacs.dao.PersonDao;
import com.shotmaniacs.models.Credentials;
import com.shotmaniacs.models.user.Person;
import com.shotmaniacs.models.user.ServerRole;
import com.shotmaniacs.rest.resources.AuthenticationResource;
import com.shotmaniacs.rest.resources.ProfileResource;
import com.shotmaniacs.utils.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProfileResourceTest extends SimpleJerseyTest {

    private final long testId = MockAuthenticationFilter.USER_ID;
    private final String tokenId = "token";

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        to(Path.PROFILE + testId, ServerRole.ADMIN).delete().close();
    }

    @Override
    protected Application configure() {
        return configureBasic().registerClasses(
                AuthenticationResource.class,
                ProfileResource.class,
                PersonDao.class
        );
    }

    @Test
    public void givenLoggedIn_whenGet_thenGetProfile() {
        //TODO probaly delete first all test instances in the database.
        Person profile = getTestProfile();
        NewCookie tokenCookie = post(profile, to(Path.SIGN_UP)).getCookies().get(tokenId);
        profile.setPassword(null);
        assertEquals(profile, getSingle(Person.class, to(Path.PROFILE, ServerRole.CLIENT).cookie(tokenCookie)));
    }

    @Test
    public void givenNotLoggedIn_whenGet_thenThrowForbiddenError() {
        assertThrows(BadRequestException.class, () -> getSingle(Person.class, to(Path.PROFILE)));
    }

    @Test
    public void givenLoggedIn_whenUpdate_thenUpdateProfile() {
        NewCookie tokenCookie = post(getTestProfile(), to(Path.SIGN_UP)).getCookies().get(tokenId);
        Person person = getTestProfile();
        person.setFullname("Test (Modified)");
        person.setEmail("modified0@gmail.com");
        try (Response response = put(person, to(Path.PROFILE, ServerRole.CLIENT).cookie(tokenCookie))) {
            assertEquals(OK, response.getStatus());
            Person modified = getSingle(Person.class, to(Path.PROFILE, person.getRole()));
            person.setPassword(null);
            assertEquals(person, modified);
        }
    }

    @Test
    public void givenNotAuthorized_whenGetById_thenThrowForbiddenError() {
        assertThrows(ForbiddenException.class, () -> getSingle(Person.class, to(Path.PROFILE + testId)));
    }

    @Test
    public void givenAsAdmin_whenGetById_thenGetProfile() {
        Person profile = getTestProfile();
        post(profile, to(Path.SIGN_UP));
        profile.setPassword(new PasswordHasher().hash(profile.getPassword()));
        assertEquals(profile, getSingle(Person.class, to(Path.PROFILE + testId, ServerRole.ADMIN)));
    }

    private Person getTestProfile() {
        return new Person.builder().id(testId).fullname("Test").role(ServerRole.CLIENT).credentials(getTestCredentials()).phone("06-123").build();
    }

    private Credentials getTestCredentials() {
        return new Credentials("test0@gmail.com", "123");
    }
}
