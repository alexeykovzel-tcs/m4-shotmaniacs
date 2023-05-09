package com.shotmaniacs.rest.resources;

import com.shotmaniacs.dao.CrewDao;
import com.shotmaniacs.dao.EventDao;
import com.shotmaniacs.dao.PersonDao;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.event.BookingType;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.event.EventStatus;
import com.shotmaniacs.models.event.EventType;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.models.user.Person;
import com.shotmaniacs.utils.EventXSSFConverter;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/events")
@PermitAll
public class EventsResource extends RestResource {

    private final CrewDao crewDao = new CrewDao();
    private final EventDao eventDao = new EventDao();
    private final PersonDao personDao = new PersonDao();

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getAllEvents(@QueryParam("status") String statusVal) {
        EventStatus status = EventStatus.valueOfOrNull(statusVal);
        if (context.isUserInRole("admin")) {
            return (status != null) ? eventDao.getByStatus(status) : eventDao.getAll();
        }

        if (context.isUserInRole("client")) {
            return eventDao.getAll().stream()
                    .filter(e -> e.getClient().getId() == getUserId())
                    .collect(Collectors.toList());
        }

        // otherwise get events for a specific member
        Person person = crewDao.getById(getUserId());
        if (!(person instanceof CrewMember))  {
            throw new InternalServerErrorException("Could not retrieve todo events");
        }

        // get events defined by permissions
        CrewMember member = (CrewMember) person;
        List<Event> events = eventDao.getByStatus(EventStatus.TODO);
        System.out.println("TODO events: " + events.size());
        List<Event> allowedEvents = new ArrayList<>();
        for (Event event : events) {
            Department department = member.getDepartment();
            BookingType booking = event.getBookingType();
            if (booking == BookingType.FILM && department != Department.EVENT_FILMMAKING) continue;
            if (booking == BookingType.MARKETING && department != Department.MARKETING) continue;
            if (booking == BookingType.PHOTOGRAPHY
                    && (department != Department.EVENT_PHOTOGRAPHY
                    && (department != Department.CLUB_PHOTOGRAPHY
                    && event.getEventType() != EventType.CLUB))) continue;
            allowedEvents.add(event);
        }
        System.out.println("Allowed events: " + events.size());
        return allowedEvents;
    }

    @POST
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveEvent(Event event) {
        validateEvent(event);
        eventDao.save(event);
        return Response.ok().build();
    }

    @PUT
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"client", "admin"})
    public Response updateEvent(Event event) {
        if (context.isUserInRole("client") && eventDao.getClientIdById(event.getId()) != getUserId())
            throw new ForbiddenException("Not authorized to update this event");
        eventDao.update(event);
        return Response.ok().build();
    }

    @POST
    @Path("/xssf")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response saveEventsByFile(@FormDataParam("file") InputStream in,
                                     @FormDataParam("file") FormDataContentDisposition fileDetails) {
        List<Event> events = new EventXSSFConverter().getByInputStream(in);
        events.forEach(this::validateEvent);
        eventDao.saveAll(events);
        return Response.ok().build();
    }

    private void validateEvent(Event event) {
        if (event.getName() == null || "".equals(event.getName())) {
            throw new BadRequestException("Event should contain a name");
        }
        if (event.getLocation() == null || "".equals(event.getLocation())) {
            throw new BadRequestException("Event should contain a location");
        }
        if (event.getStartDate() == null || event.getEndDate() == null) {
            throw new BadRequestException("Event should contain dates");
        }
        long timeDifference = event.getEndDate().getTime() - event.getStartDate().getTime();
        if (timeDifference <= 0) {
            throw new BadRequestException("End date should be after the start date");
        }
        int duration = (int) (timeDifference / 1000 / 60 / 60);
        if (event.getDuration() == 0) {
            event.setDuration(duration);
        }
        if (event.getDuration() > duration) {
            throw new BadRequestException("Duration exceeds the time difference");
        }
        // if the user has admin rights, there is no point in approving
        event.setStatus(context.isUserInRole("admin") ? EventStatus.TODO : EventStatus.TO_APPROVE);
    }
}
