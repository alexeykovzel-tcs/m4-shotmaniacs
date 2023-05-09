package com.shotmaniacs.rest;

import com.shotmaniacs.config.MockAuthenticationFilter;
import com.shotmaniacs.config.Path;
import com.shotmaniacs.dao.PersonDao;
import com.shotmaniacs.models.Credentials;
import com.shotmaniacs.models.user.Person;
import com.shotmaniacs.models.user.ServerRole;
import com.shotmaniacs.rest.resources.AuthenticationResource;
import com.shotmaniacs.rest.resources.ProfileResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationResourceTest extends SimpleJerseyTest {

    private final long testId = MockAuthenticationFilter.USER_ID;
    private final String tokenId = "token";

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        post(null, to(Path.LOGOUT)).close();
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
    public void givenValidData_whenSignUp_thenSaveUser() {
        assertNull(getProfile());
        try (Response response = post(buildProfile(), to(Path.SIGN_UP))) {
            assertEquals(OK, response.getStatus());
            assertNotNull(getProfile());
        }
    }

    @Test
    public void givenInvalidFullname_whenSignUp_thenThrowBadRequest() {
        Person person = buildProfile();
        person.setFullname("123");
        try (Response response = post(person, to(Path.SIGN_UP))) {
            assertEquals(BAD_REQUEST, response.getStatus());
        }
    }

    @Test
    public void givenUserExists_whenSignUp_thenThrowBadRequest() {
        Person person = buildProfile();
        try (Response r1 = post(person, to(Path.SIGN_UP));
             Response r2 = post(person, to(Path.SIGN_UP))) {
            assertEquals(OK, r1.getStatus());
            assertEquals(BAD_REQUEST, r2.getStatus());
        }
    }

    @Test
    public void givenWrongPassword_whenLogin_thenThrowBadRequest() {
        Credentials credentials = buildCredentials();
        credentials.setPassword("321");
        try (Response r1 = post(buildProfile(), to(Path.SIGN_UP));
             Response r2 = post(credentials, to(Path.LOGIN))) {
            assertEquals(OK, r1.getStatus());
            assertEquals(BAD_REQUEST, r2.getStatus());
        }
    }

    @Test
    public void givenNoSuchUser_whenLogin_thenThrowBadRequest() {
        try (Response response = post(buildProfile(), to(Path.LOGIN))) {
            assertEquals(BAD_REQUEST, response.getStatus());
        }
    }

    @Test
    public void givenValidCredentials_whenLogin_thenReturnToken() {
        NewCookie tokenCookie = post(buildProfile(), to(Path.SIGN_UP)).getCookies().get(tokenId);
        post(null, to(Path.LOGOUT).cookie(tokenCookie));
        try (Response response = post(buildCredentials(), to(Path.LOGIN).cookie(tokenCookie))) {
            assertEquals(OK, response.getStatus());
            assertTrue(response.getCookies().containsKey(tokenId));
            String token = response.getCookies().get(tokenId).getValue();
            assertFalse(token == null || token.equals(""));
        }
    }

    @Test
    public void givenLoggedIn_whenLogOut_thenDeleteToken() {
        NewCookie tokenCookie = post(buildProfile(), to(Path.SIGN_UP)).getCookies().get(tokenId);
        try (Response response = post(null, to(Path.LOGOUT).cookie(tokenCookie))) {
            String token = response.getCookies().get(tokenId).getValue();
            assertEquals(OK, response.getStatus());
            assertEquals("", token);
        }
    }

    private Person getProfile() {
        return getSingle(Person.class, to(Path.PROFILE + testId, ServerRole.ADMIN));
    }

    private Person buildProfile() {
        return new Person.builder().id(testId).fullname("Test").credentials(buildCredentials()).build();
    }

    private Credentials buildCredentials() {
        return new Credentials("test0@gmail.com", "123");
    }
}
