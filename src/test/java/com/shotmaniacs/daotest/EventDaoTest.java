package com.shotmaniacs.daotest;

import com.shotmaniacs.models.event.BookingType;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.event.EventStatus;
import com.shotmaniacs.models.event.EventType;
import com.shotmaniacs.models.user.Client;
import com.shotmaniacs.utils.DateUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventDaoTest {
    private final List<Event> events = new LinkedList<>();

    private EventDaoTest() {
        Client client = new Client(1, "Test Client 1", "+111", null, null,null);

        events.add(new Event(1, "Event 1", BookingType.PHOTOGRAPHY, EventType.CLUB, "Some location 1",
                DateUtils.format("29-06-2022 16:00"), DateUtils.format("29-06-2022 18:00"),
                2, "Some notes 1", client, EventStatus.TO_APPROVE, null, null));

        events.add(new Event(2, "Event 2", BookingType.PHOTOGRAPHY, EventType.WEDDING, "Some location 2",
                DateUtils.format("30-06-2022 08:00"), DateUtils.format("30-06-2022 10:00"),
                1, "Some notes 2", client, EventStatus.TO_APPROVE, null, null));

        events.add(new Event(3, "Event 3", BookingType.PHOTOGRAPHY, EventType.COMMERCIAL, "Some location 3",
                DateUtils.format("28-06-2022 12:00"), DateUtils.format("28-06-2022 20:00"),
                3, "Some notes 3", client, EventStatus.IN_PROGRESS, null, null));

        events.add(new Event(4, "Event 4", BookingType.FILM, EventType.CLUB, "Some location 3",
                DateUtils.format("28-06-2022 12:00"), DateUtils.format("28-06-2022 20:00"),
                6, "Some notes 4", client, EventStatus.TODO, null, null));

        events.add(new Event(5, "Event 5", BookingType.PHOTOGRAPHY, EventType.CLUB, "Some location 3",
                DateUtils.format("02-07-2022 12:00"), DateUtils.format("02-07-2022 18:00"),
                3, "Some notes 5", client, EventStatus.TODO, null, null));

        events.add(new Event(6, "Event 6", BookingType.PHOTOGRAPHY, EventType.BUSINESS, "Some location 3",
                DateUtils.format("01-07-2022 12:00"), DateUtils.format("01-07-2022 20:00"),
                7, "Some notes 6", client, EventStatus.TODO, null, null));
    }

    public static EventDaoTest getInstance() {
        return EventDaoTest.EventDaoTestHolder.instance;
    }

    public List<Event> getAll() {
        return events;
    }

    public List<Event> getByStatus(EventStatus status) {
        return events.stream().filter(e -> e.getStatus() == status).collect(Collectors.toList());
    }

    public Optional<Event> getById(long id) {
        return events.stream().filter(e -> e.getId() == id).findFirst();
    }

    public void save(Event event) {
        if (event.getStatus() == null) event.setStatus(EventStatus.TODO);
        events.add(event);
    }

    public void saveAll(List<Event> events) {
        this.events.addAll(events);
    }

    public void update(Event event) {
        System.out.println("Update event: " + event);
        for (int i = 0; i < events.size(); i++) {
            Event oldEvent = events.get(i);
            if (oldEvent.getId() == event.getId()) {
                events.set(i, event);
            }
        }
    }

    public boolean updateStatus(long id, EventStatus status) {
        if (status == null) return false;
        Optional<Event> event = events.stream().filter(e -> e.getId() == id).findFirst();
        event.ifPresent(value -> value.setStatus(status));
        return event.isPresent();
    }

    public boolean deleteById(long id) {
        return events.removeIf(event -> event.getId() == id);
    }

    public boolean existsById(long id) {
        return events.stream().anyMatch(e -> e.getId() == id);
    }

    public long getClientIdById(long id) {
        for (Event event : events) {
            if (event.getId() == id) {
                Client client = event.getClient();
                System.out.println(client);
                return (client != null) ? client.getId() : 0;
            }
        }
        return 0;
    }

    private static final class EventDaoTestHolder {
        public static final EventDaoTest instance = new EventDaoTest();
    }
}
