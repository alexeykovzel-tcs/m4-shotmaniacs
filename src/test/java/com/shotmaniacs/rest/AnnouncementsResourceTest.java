package com.shotmaniacs.rest;

import com.shotmaniacs.config.MockAuthenticationFilter;
import com.shotmaniacs.config.Path;
import com.shotmaniacs.dao.AnnouncementDao;
import com.shotmaniacs.dao.CrewDao;
import com.shotmaniacs.dao.PersonDao;
import com.shotmaniacs.models.Announcement;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.user.*;
import com.shotmaniacs.rest.resources.AnnouncementsResource;
import org.junit.jupiter.api.*;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
//TODO reset the database if test doesn't go through
//TODO make sure the crewMember has MockAuthenticationFilter.USER_ID id and not the client.
public class AnnouncementsResourceTest extends SimpleJerseyTest {
    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final List<Announcement> data = List.of(
            new Announcement.builder().id(1).receiverId(1).build(),
            new Announcement.builder().id(2).receiverId(MockAuthenticationFilter.USER_ID).build(),
            new Announcement.builder().id(3).department(Department.EVENT_FILMMAKING).build(),
            new Announcement.builder().id(4).department(Department.CLUB_PHOTOGRAPHY).build(),
            new Announcement.builder().id(5).department(Department.CLUB_PHOTOGRAPHY).build(),
            new Announcement.builder().id(6).department(Department.MARKETING).build(),
            new Announcement.builder().id(7).build(),
            new Announcement.builder().id(8).build()
    );

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        data.forEach(e -> to(Path.ANNOUNCEMENTS + e.getId(), ServerRole.ADMIN).delete().close());
    }

    @Override
    protected Application configure() {
        return configureBasic().registerClasses(
                AnnouncementsResource.class,
                CrewDao.class
        );
    }

    @Test
    public void givenNotAuthorized_whenPost_thenThrowForbiddenError() {
        try (Response response = post(data.get(0), to(Path.ANNOUNCEMENTS))) {
            assertEquals(FORBIDDEN, response.getStatus());
        }
    }

    @Test
    public void givenNotAuthorized_whenDelete_thenThrowForbiddenError() {
        addMockCrewMember(Department.CLUB_PHOTOGRAPHY,ProjectRole.FILMMAKER);
        post(data.get(0), to(Path.ANNOUNCEMENTS, ServerRole.ADMIN)).close();
        try (Response response = to(Path.ANNOUNCEMENTS + data.get(0).getId()).delete()) {
            assertEquals(FORBIDDEN, response.getStatus());
        }
        for (int i = 1; i < 9; i++) {
            announcementDao.deleteById(i);
        }
        deleteMockCrewMember();
    }

    @Test
    public void givenNotAuthorized_whenGet_thenThrowForbiddenError() {
        assertThrows(ForbiddenException.class, () -> get(Announcement.class, to(Path.ANNOUNCEMENTS)));
    }

    @Test
    public void givenAsAdmin_whenGet_thenReturnAllAnnouncements() {
        addMockCrewMember(Department.CLUB_PHOTOGRAPHY, ProjectRole.PHOTOGRAPHER);
        int initSize = get(Announcement.class, to(Path.ANNOUNCEMENTS, ServerRole.ADMIN)).size();
        data.forEach(announcement -> post(announcement, to(Path.ANNOUNCEMENTS, ServerRole.ADMIN)).close());
        List<Announcement> announcements = get(Announcement.class, to(Path.ANNOUNCEMENTS, ServerRole.ADMIN));
        assertEquals(data.size() + initSize, announcements.size());
        for (int i = 1; i < 9; i++) {
            announcementDao.deleteById(i);
        }
        deleteMockCrewMember();
    }

    @Test
    public void givenAsCrew_whenGet_thenReturnOnlyAllowedAnnouncements() {
        addMockCrewMember(Department.CLUB_PHOTOGRAPHY, ProjectRole.PHOTOGRAPHER);
        int initSize = get(Announcement.class, to(Path.ANNOUNCEMENTS, ServerRole.CREW)).size();
        data.forEach(announcement -> post(announcement, to(Path.ANNOUNCEMENTS, ServerRole.ADMIN)).close());
        List<Announcement> announcements = get(Announcement.class, to(Path.ANNOUNCEMENTS, ServerRole.CREW));
        announcements.sort(Comparator.comparing(Announcement::getId));

        assertEquals(5 + initSize, announcements.size());
        assertEquals(567, announcements.get(0).getReceiverId());
        assertEquals(Department.CLUB_PHOTOGRAPHY, announcements.get(1).getDepartment());
        assertEquals(Department.CLUB_PHOTOGRAPHY, announcements.get(2).getDepartment());
        assertTrue(isForEveryone(announcements.get(3)));
        assertTrue(isForEveryone(announcements.get(4)));
        for (int i = 1; i < 9; i++) {
            announcementDao.deleteById(i);
        }
        deleteMockCrewMember();
    }

    private List<Announcement> getAll() {
        return get(Announcement.class, to(Path.ANNOUNCEMENTS, ServerRole.ADMIN));
    }

    private boolean isForEveryone(Announcement a) {
        return a.getReceiverId() == 0 && a.getDepartment() == null;
    }
}
