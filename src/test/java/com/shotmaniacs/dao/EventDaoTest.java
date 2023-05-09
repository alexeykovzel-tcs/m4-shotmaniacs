package com.shotmaniacs.dao;

import com.shotmaniacs.models.event.BookingType;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.event.EventStatus;
import com.shotmaniacs.models.event.EventType;
import com.shotmaniacs.models.user.Client;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.utils.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventDaoTest {
	EventDao eventDao;
	Event event2;
	Event event3;
	Event event4;
	Event event5;
	Client client1;
	Client client2;
	List<Event> eventList;
	PersonDao personDao;
	@BeforeEach
	void setUp() {
		eventDao = new EventDao();
		personDao = new PersonDao();
		eventList = new ArrayList<>();
		client1 = new Client(-2, "Test Client -2", "+121", null, null,null);
		client2 = new Client(-3, "Test Client -3", "+131", null, null,null);
		personDao.save(client1);
		personDao.save(client2);
		event2 = new Event(-2, "Event 2", BookingType.FILM, EventType.CLUB, "Some location 2",
				DateUtils.format("18-01-2022 08:00"), DateUtils.format("18-01-2022 10:00"),
				1, "Some notes 2", client2);
		event3 = new Event(-3, "Event 3", BookingType.FILM, EventType.CLUB, "Some location 3",
				DateUtils.format("20-01-2022 12:00"), DateUtils.format("20-01-2022 20:00"),
				3, "Some notes 3", client1);
		event4 = new Event(-4, "Event 4", BookingType.FILM, EventType.CLUB, "Some location 4",
				DateUtils.format("20-01-2022 12:00"), DateUtils.format("20-01-2022 20:00"),
				3, "Some notes 4", client1);
		event5 = new Event(-5, "Event 5", BookingType.FILM, EventType.CLUB, "Some location 5",
				DateUtils.format("17-02-2022 16:00"), DateUtils.format("17-01-2022 18:00"),
				2, "Some notes 5", client1);
		eventList.add(event2);
		eventList.add(event3);
		eventList.add(event4);
		eventList.add(event5);
	}

	@Test
	void getAll() {
		int size = eventDao.getAll().size();
		eventDao.saveAll(eventList);
		assertEquals(size+ 4,eventDao.getAll().size());
		eventDao.deleteById(-2);
		assertEquals(size+ 3,eventDao.getAll().size());
		eventDao.deleteById(-3);
		assertEquals(size+ 2,eventDao.getAll().size());
		eventDao.deleteById(-4);
		assertEquals(size+ 1,eventDao.getAll().size());
		eventDao.deleteById(-5);
	}

	@Test
	void getByStatus() {
		int size = eventDao.getByStatus(EventStatus.TO_APPROVE).size();
		eventDao.saveAll(eventList);
		assertEquals(size+ 4,eventDao.getByStatus(EventStatus.TO_APPROVE).size());
		eventDao.deleteById(-2);
		eventDao.deleteById(-3);
		eventDao.deleteById(-4);
		eventDao.deleteById(-5);

	}

	@Test
	void getById() {
		assertFalse(eventDao.getById(-2) != null);
		eventDao.save(event2);
		assertTrue(eventDao.getById(-2) != null);
		eventDao.deleteById(-2);
	}

	@Test
	void existById() {
		assertFalse(eventDao.existById(-1));
		assertFalse(eventDao.existById(-2));
		eventDao.save(event2);
		eventDao.deleteById(-2);
	}

	@Test
	void getClientIdById(){
		assertEquals(-1, eventDao.getClientIdById(-2));
		eventDao.save(event2);
		assertEquals(-3,eventDao.getClientIdById(-2));
		eventDao.deleteById(-2);
		assertEquals(-1, eventDao.getClientIdById(-3));
		eventDao.save(event3);
		assertEquals(-2,eventDao.getClientIdById(-3));
		eventDao.deleteById(-3);
	}

	@Test
	void save() {
		assertFalse(eventDao.getById(-2) != null);
		eventDao.save(event2);
		assertTrue(eventDao.getById(-2) != null);
		assertFalse(eventDao.getById(-3) != null);
		eventDao.save(event3);
		assertTrue(eventDao.getById(-3) != null);
		assertFalse(eventDao.getById(-4) != null);
		eventDao.save(event4);
		assertTrue(eventDao.getById(-4) != null);
		assertFalse(eventDao.getById(-5) != null);
		eventDao.save(event5);
		assertTrue(eventDao.getById(-5) != null);
		eventDao.deleteById(-2);
		eventDao.deleteById(-3);
		eventDao.deleteById(-4);
		eventDao.deleteById(-5);
	}

	@Test
	void saveAll() {
		eventDao.saveAll(eventList);
		assertTrue(eventDao.getById(-2) != null);
		assertTrue(eventDao.getById(-3) != null);
		assertTrue(eventDao.getById(-4) != null);
		assertTrue(eventDao.getById(-5) != null);
		eventDao.deleteById(-2);
		eventDao.deleteById(-3);
		eventDao.deleteById(-4);
		eventDao.deleteById(-5);
	}


	@Test
	void update() {
		Event event = new Event();
		assertFalse(eventDao.update(event));
	}

	@Test
	void updateStatus() {
		eventDao.save(event2);
		assertEquals(EventStatus.TO_APPROVE,eventDao.getById(-2).getStatus());
		eventDao.updateStatus(-2,EventStatus.TODO);
		assertEquals(EventStatus.TODO,eventDao.getById(-2).getStatus());
		eventDao.updateStatus(-2,EventStatus.DONE);
		assertEquals(EventStatus.DONE,eventDao.getById(-2).getStatus());
		eventDao.updateStatus(-2,EventStatus.IN_PROGRESS);
		assertEquals(EventStatus.IN_PROGRESS,eventDao.getById(-2).getStatus());
		eventDao.updateStatus(-2,EventStatus.REVIEW);
		assertEquals(EventStatus.REVIEW,eventDao.getById(-2).getStatus());
		eventDao.updateStatus(-2,EventStatus.TODO);
		assertEquals(EventStatus.TODO,eventDao.getById(-2).getStatus());
		eventDao.deleteById(-2);
	}

	@Test
	void getAllowedEventsFor() {
		CrewMember crew = new CrewMember();
		assertEquals(0,eventDao.getAllowedEventsFor(crew).size());
	}

	@Test
	void deleteById() {

		eventDao.save(event2);
		assertTrue(eventDao.getById(-2) != null);
		eventDao.deleteById(-2);
		assertFalse(eventDao.getById(-2) != null);
	}

	@AfterEach
	void afterEach() {
		personDao.deleteByPerson(client1);
		personDao.deleteByPerson(client2);
	}
}