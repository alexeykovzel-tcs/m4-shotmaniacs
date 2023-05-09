package com.shotmaniacs.dao;

import com.shotmaniacs.models.Assignment;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.event.BookingType;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.event.EventType;
import com.shotmaniacs.models.event.StatusPermission;
import com.shotmaniacs.models.user.Availability;
import com.shotmaniacs.models.user.Client;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.models.user.ProjectRole;
import com.shotmaniacs.utils.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.BadRequestException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentDaoTest {
	AssignmentDao assignmentDao;
	CrewMember crewMember1;
	EventDao eventDao;
	CrewDao crewDao;
	PersonDao personDao;
	Event event1;
	Client client2;
	Assignment assignment;
	@BeforeEach
	void setUp() {
		assignmentDao = new AssignmentDao();
		crewDao = new CrewDao();
		eventDao = new EventDao();
		personDao = new PersonDao();
		crewMember1 = new CrewMember(-2, "Test Crew 2", "+222", "crew3@gmail.com",
				"pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", null, Availability.AVAILABLE,
				Department.MARKETING);
		client2 = new Client(-3, "Test Client -3", "+131", null, null,null);
		personDao.save(client2);
		event1 = new Event(-2, "Event 2", BookingType.FILM, EventType.CLUB, "Some location 2",
				DateUtils.format("18-01-2022 08:00"), DateUtils.format("18-01-2022 10:00"),
				1, "Some notes 2", client2);
		crewDao.save(crewMember1);
		eventDao.save(event1);
		assignment = new Assignment(0,0, ProjectRole.PHOTOGRAPHER,StatusPermission.REQUEST, -2, -2);
	}

	@AfterEach
	void tearDown() {
		eventDao.deleteById(-2);
		personDao.deleteByPerson(crewMember1);
		personDao.deleteByPerson(client2);
	}

	@Test
	void getAll() {
		assignmentDao.getAll();
		int size = assignmentDao.getAll().size();
		assignmentDao.save(assignment);
		assertEquals(size+1, assignmentDao.getAll().size());
		assignmentDao.deleteById(-2,-2);
	}

	@Test
	void getById() {
		assignmentDao.save(assignment);
		List<Assignment> assignmentList = assignmentDao.getById(-2,-2);
		assertEquals(1,assignmentList.size());
		assignmentDao.deleteById(-2,-2);
	}

	@Test
	void getByMemberId() {
		assignmentDao.save(assignment);
		List<Assignment> assignmentList = assignmentDao.getByMemberId(-2);
		assertEquals(1,assignmentList.size());
		assignmentDao.deleteById(-2,-2);
	}

	@Test
	void getByEventId() {
		assignmentDao.save(assignment);
		List<Assignment> assignmentList = assignmentDao.getByEventId(-2);
		assertEquals(1,assignmentList.size());
		assignmentDao.deleteById(-2,-2);
	}

	@Test
	void existsById() {
		assertFalse(assignmentDao.existsById(-2,-2));
		assertFalse(assignmentDao.existsById(-1,-2));
		assertFalse(assignmentDao.existsById(-2,-1));
		assignmentDao.save(assignment);
		assertTrue(assignmentDao.existsById(-2,-2));
		assertFalse(assignmentDao.existsById(-1,-2));
		assertFalse(assignmentDao.existsById(-2,-1));
		assignmentDao.deleteById(-2,-2);
	}

	@Test
	void save() {
		assertTrue(assignmentDao.save(assignment));
		assertThrows(BadRequestException.class,()->this.assignmentDao.save(assignment));
		assignmentDao.deleteById(-2,-2);

	}

	@Test
	void updateStatusById() {
		assertTrue(assignmentDao.save(assignment));
		assertTrue(assignmentDao.updateStatusById(-2,-2,StatusPermission.SPECIAL));
		assertTrue(assignmentDao.updateStatusById(-2,-2,StatusPermission.REQUEST));
		assertTrue(assignmentDao.updateStatusById(-2,-2,StatusPermission.APPROVED));
		assertTrue(assignmentDao.updateStatusById(-2,-2,StatusPermission.CANCELED));
		assignmentDao.deleteById(-2,-2);
	}

	@Test
	void incrementKaakjes() {
		assertFalse(assignmentDao.incrementKaakjes(-2,-2));
		assertTrue(assignmentDao.save(assignment));
		assertTrue(assignmentDao.incrementKaakjes(-2,-2));
		assertTrue(assignmentDao.incrementKaakjes(-2,-2));
		assertTrue(assignmentDao.incrementKaakjes(-2,-2));
		assertTrue(assignmentDao.incrementKaakjes(-2,-2));
		assertEquals(4,assignmentDao.getById(-2,-2).get(0).getCounterKaakjes());
		assignmentDao.deleteById(-2,-2);
	}

	@Test
	void deleteById() {
		assertThrows(BadRequestException.class,()->assignmentDao.deleteById(-2,-2));
		assertTrue(assignmentDao.save(assignment));
		assignmentDao.deleteById(-2,-2);

	}
}