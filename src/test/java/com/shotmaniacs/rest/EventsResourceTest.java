package com.shotmaniacs.rest;

import com.shotmaniacs.config.MockAuthenticationFilter;
import com.shotmaniacs.config.Path;
import com.shotmaniacs.dao.EventDao;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.event.BookingType;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.event.EventStatus;
import com.shotmaniacs.models.event.EventType;
import com.shotmaniacs.models.user.Client;
import com.shotmaniacs.models.user.ProjectRole;
import com.shotmaniacs.models.user.ServerRole;
import com.shotmaniacs.rest.resources.EventResource;
import com.shotmaniacs.rest.resources.EventsResource;
import com.shotmaniacs.utils.EventXSSFConverter;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventsResourceTest extends SimpleJerseyTest {

    private final long testId = 567;

    private final Client client = new Client(testId, null, "06-234", null, null,null);
    private final EventDao eventDao = new EventDao();
    private final List<Event> events = List.of(
            Event.builder().id(1).client(client).status(EventStatus.TODO).eventType(EventType.BUSINESS).bookingType(BookingType.MARKETING).build(),
            Event.builder().id(2).client(client).status(EventStatus.TODO).eventType(EventType.BUSINESS).bookingType(BookingType.PHOTOGRAPHY).build(),
            Event.builder().id(3).client(client).status(EventStatus.TODO).eventType(EventType.BUSINESS).bookingType(BookingType.OTHER).build(),
            Event.builder().id(4).client(client).status(EventStatus.TODO).eventType(EventType.CLUB).bookingType(BookingType.PHOTOGRAPHY).build(),
            Event.builder().id(5).client(client).status(EventStatus.IN_PROGRESS).eventType(EventType.COMMERCIAL).bookingType(BookingType.FILM).build(),
            Event.builder().id(6).client(client).status(EventStatus.REVIEW).eventType(EventType.COMMERCIAL).bookingType(BookingType.OTHER).build(),
            Event.builder().id(7).client(client).status(EventStatus.DONE).eventType(EventType.WEDDING).bookingType(BookingType.PHOTOGRAPHY).build()
    );

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        for (int i = 1; i <= events.size(); i++) {
            to(Path.ALL_EVENTS + i, ServerRole.ADMIN).delete();
        }
    }

    @Override
    protected Application configure() {
        return configureBasic().registerClasses(
                MultiPartFeature.class,
                EventsResource.class,
                EventResource.class,
                EventDao.class
        );
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }

    @Test
    public void givenAsCrew_whenGet_returnAvailableEvents() {
        //TODO for the tester make sure that the crew has the MockAuthenticationFilter.USER_ID id.
        //and the client has the crew idea.
        addMockClient();
        addMockEvents();
        addMockCrewMember(Department.EVENT_PHOTOGRAPHY, ProjectRole.PHOTOGRAPHER);
        List<Event> events = get(Event.class, to(Path.TODO_EVENTS, ServerRole.CREW));
        assertEquals(3, events.size());

        for (Event event : events) {
            assertEquals(EventStatus.TODO, event.getStatus());
            BookingType booking = event.getBookingType();
            assertTrue(booking == BookingType.PHOTOGRAPHY || booking == BookingType.OTHER);
        }
        for (int i = 1; i < 8; i++) {
            eventDao.deleteById(i);
        }
        deleteMockClient();
        deleteMockCrewMember();
    }

    @Test
    public void givenAsAdmin_whenGet_returnAllEvents() {
        addMockClient();
        addMockEvents();
        List<Event> events = get(Event.class, to(Path.ALL_EVENTS, ServerRole.ADMIN));
        assertEquals(7, events.size());
        deleteMockClient();
    }

    @Test
    public void givenNotAuthorized_whenGet_throwForbiddenError() {
        assertThrows(ForbiddenException.class, () -> get(Event.class, to(Path.ALL_EVENTS)));
    }

    @Test
    public void givenAsClient_whenPost_thenSaveEvent() {
        addMockClient();
        Event event = events.get(0);
        event.setStartDate(new Date(0));
        event.setEndDate(new Date(0));
        try (Response response = post(event, to(Path.ALL_EVENTS, ServerRole.CLIENT))) {
            assertEquals(OK, response.getStatus());
            Event savedEvent = getSingle(Event.class, to(Path.ALL_EVENTS + event.getId(), ServerRole.CLIENT));
            assertEquals(event.getId(),savedEvent.getId());
            //assertEquals(event, savedEvent);
        }
        for (int i = 1; i < 8; i++) {
            eventDao.deleteById(i);
        }
        deleteMockClient();

    }

    @Test
    public void givenAsClient_whenPut_thenUpdateEvent() {
        Event event = events.get(0);
        post(event, to(Path.ALL_EVENTS, ServerRole.CLIENT)).close();
        String eventUrl = Path.ALL_EVENTS + event.getId();
        assertEquals(EventStatus.TODO, event.getStatus());

        try (Response response = put("", to(eventUrl + "?status=done", ServerRole.CLIENT))) {
            assertEquals(OK, response.getStatus());
            Event modified = getSingle(Event.class, to(eventUrl, ServerRole.CLIENT));
            assertEquals(EventStatus.DONE, modified.getStatus());
        }
    }

    @Test
    public void givenAsClient_whenDelete_thenDeleteEvent() {
        Event event = events.get(0);
        post(event, to(Path.ALL_EVENTS, ServerRole.CLIENT)).close();
        String eventUrl = Path.ALL_EVENTS + event.getId();

        try (Response response = to(eventUrl, ServerRole.CLIENT).delete()) {
            assertEquals(OK, response.getStatus());
            assertThrows(BadRequestException.class,
                    () -> getSingle(Event.class, to(eventUrl, ServerRole.CLIENT)));
        }
    }

    @Test
    public void givenAsClient_whenPostXSSF_thenSaveMultipleEvents() throws FileNotFoundException {
        FormDataMultiPart fileData = new FormDataMultiPart();
        File file = new File("src/test/resources/Test_Events.xlsx");
        fileData.bodyPart(new FileDataBodyPart("file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        try (Response response = postFile(fileData, to(Path.UPLOAD_XSSF, ServerRole.CLIENT))) {
            assertEquals(OK, response.getStatus());
            List<Event> savedEvents = get(Event.class, to(Path.ALL_EVENTS, ServerRole.ADMIN));
            int eventCount = new EventXSSFConverter().getByInputStream(new FileInputStream(file)).size();
            assertEquals(eventCount, savedEvents.size());
        }
    }
    private void addMockEvents() {
        events.forEach(event -> post(event, to(Path.ALL_EVENTS, ServerRole.ADMIN)).close());
    }
}
