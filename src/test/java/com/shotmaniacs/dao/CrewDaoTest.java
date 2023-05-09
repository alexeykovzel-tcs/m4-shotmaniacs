package com.shotmaniacs.dao;

import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.user.Availability;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.models.user.ProjectRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CrewDaoTest {
	CrewDao crewDao;
	CrewMember crewMember1;
	CrewMember crewMember2;
	CrewMember crewMember3;
	CrewMember crewMember4;
	@BeforeEach
	void setUp() {
		crewDao = new CrewDao();
		crewMember1 = new CrewMember(-2, "Test Crew 1", "+111", "crew1@gmail.com",
				"pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", null, Availability.AVAILABLE,
				Department.CLUB_PHOTOGRAPHY);
		crewMember2 = new CrewMember(-2, "Test Crew 2", "+222", "crew2@gmail.com",
				"pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", null, Availability.AVAILABLE,
				Department.MARKETING);
		crewMember3 = new CrewMember(-1, "Test Crew 2", "+222", "crew3@gmail.com",
				"pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", null, Availability.AVAILABLE,
				Department.MARKETING);
		List<ProjectRole> projectRoleList = new ArrayList<>();
		projectRoleList.add(ProjectRole.PRODUCTION);
		crewMember4 = new CrewMember(-3,"test","+12435","email@email.com","password",projectRoleList,Availability.AVAILABLE,Department.MARKETING);

	}

	@AfterEach
	void tearDown() {
		//crewDao.deleteByPerson(crewMember1);
		crewDao.deleteByPerson(crewMember2);
		crewDao.deleteByPerson(crewMember3);
		crewDao.deleteByPerson(crewMember4);
	}

	@Test
	void getAll() {
		int start = this.crewDao.getAll().size();
		this.crewDao.save(crewMember1);
		assertEquals(start+1,this.crewDao.getAll().size());
		this.crewDao.save(crewMember3);
		assertEquals(start + 2, this.crewDao.getAll().size());
		this.crewDao.save(crewMember4);
		assertEquals(start + 3,this.crewDao.getAll().size());
	}

	@Test
	void getById() {
		this.crewDao.save(crewMember1);
		CrewMember crew = this.crewDao.getCrewById(-2);
		assertEquals(crew.getDepartment(),crewMember1.getDepartment());
		assertEquals(crew.getAvailability(),crewMember1.getAvailability());
		assertEquals(crew.getId(),crewMember1.getId());
		this.crewDao.save(crewMember4);
		crew = this.crewDao.getCrewById(-3);
		assertEquals(crewMember4.getRoles().size(),crew.getRoles().size());
	}

	@Test
	void existsById() {
		assertFalse(this.crewDao.existsById((long) -2));
		this.crewDao.save(crewMember1);
		assertTrue(this.crewDao.existsById((long)-2));
		assertFalse(this.crewDao.existsById((long) -1));
		this.crewDao.save(crewMember3);
		assertFalse(this.crewDao.existsById((long) -1));
		assertTrue(this.crewDao.existsById(this.crewDao.getIdByEmail("crew3@gmail.com")));
	}
}