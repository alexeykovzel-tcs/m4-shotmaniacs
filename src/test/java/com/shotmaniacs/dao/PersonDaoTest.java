package com.shotmaniacs.dao;

import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonDaoTest {
	PersonDao personDao;
	Person person1;
	Person person2;
	Person person3;
	Person person4;
	Person person5;
	String email1;
	String email2;
	String email3;
	String email4;
	String email5;
	@BeforeEach
	void setUp() {
		personDao = new PersonDao();
		person1 = new Person(-1,"name1","06-234567812",(email1 = "email@email.com"),"sssst", ServerRole.CLIENT);
		person2 = new Client(-2,"client1","phone",(email2 = "email2"),"password","companu");
		List<ProjectRole> projectRoleList = new ArrayList<>();
		projectRoleList.add(ProjectRole.PRODUCTION);
		person3 = new CrewMember(-3,"test","+12435",(email3="emal@email.com"),"password",projectRoleList, Availability.AVAILABLE, Department.MARKETING);
		person4 = new Admin(-4,"name1","06-234567812",(email4 = "email@email3.com"),"sssst");
		person5 = new Admin(-1,"name1","06-234567812",(email5 = "email@email5.com"),"sssst");

	}

	@Test
	void getById() {
		assertDoesNotThrow(()->this.personDao.save(person2));
		assertDoesNotThrow(()->this.personDao.save(person3));
		assertNull(this.personDao.getById(-4));
		Person list = this.personDao.getById(-3);
		assertEquals(person3.getId(), list.getId());
		assertEquals(person3.getEmail(), list.getEmail());
		assertNull(this.personDao.getById(-1));
		assertNull(this.personDao.getById(-4));
		this.personDao.deleteByPerson(person2);
		this.personDao.deleteByPerson(person3);
	}

	@Test
	void getByEmail() {
		assertDoesNotThrow(()->this.personDao.save(person4));
		assertDoesNotThrow(()->this.personDao.save(person2));
		Person list = this.personDao.getByEmail(email2);
		assertEquals(person2.getId(), list.getId());
		assertEquals(person2.getEmail(), list.getEmail());
		list = this.personDao.getByEmail(email4);
		assertEquals(person4.getId(), list.getId());
		assertEquals(person4.getEmail(), list.getEmail());
		assertNull(this.personDao.getByEmail(email3));
		assertNull(this.personDao.getByEmail(email1));
		this.personDao.deleteByPerson(person4);
		this.personDao.deleteByPerson(person2);
	}

	@Test
	void getRoleById() {
		assertDoesNotThrow(()->this.personDao.save(person2));
		assertDoesNotThrow(()->this.personDao.save(person3));
		assertDoesNotThrow(()->this.personDao.save(person4));
		assertEquals(ServerRole.CLIENT,this.personDao.getRoleById(-2));
		assertEquals(ServerRole.CREW,this.personDao.getRoleById(-3));
		assertEquals(ServerRole.ADMIN,this.personDao.getRoleById(-4));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person2));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person3));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person4));
	}

	@Test
	void existsByEmail() {
		assertDoesNotThrow(()->this.personDao.save(person1));
		assertDoesNotThrow(()->this.personDao.save(person2));
		assertTrue(this.personDao.existsByEmail(email1));
		assertTrue(this.personDao.existsByEmail(email2));
		assertFalse(this.personDao.existsByEmail(email3));
		assertFalse(this.personDao.existsByEmail(email4));
		this.personDao.deleteByPerson(person1);
		this.personDao.deleteByPerson(person2);
		assertFalse(this.personDao.existsByEmail(email1));
		assertFalse(this.personDao.existsByEmail(email2));
	}

	@Test
	void save() {
		assertTrue(()->this.personDao.save(person1));
		this.personDao.deleteByPerson(person1);
		assertDoesNotThrow(()->this.personDao.save(person2));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person2));

		assertTrue(this.personDao.save(person3));
		assertTrue(this.personDao.deleteByPerson(person3));
		assertTrue(this.personDao.save(person4));
		assertTrue(this.personDao.deleteByPerson(person4));
		assertTrue(this.personDao.save(person5));
		assertTrue(this.personDao.deleteByPerson(person5));
	}

	@Test
	void getIdByEmail() {
		assertDoesNotThrow(()->this.personDao.save(person1));
		assertDoesNotThrow(()->this.personDao.save(person2));
		assertDoesNotThrow(()->this.personDao.save(person3));
		assertDoesNotThrow(()->this.personDao.save(person4));
		assertNotEquals(-1,this.personDao.getIdByEmail(email1));
		assertEquals(-2,this.personDao.getIdByEmail(email2));
		assertEquals(-3,this.personDao.getIdByEmail(email3));
		assertEquals(-4,this.personDao.getIdByEmail(email4));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person1));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person2));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person3));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person4));
	}

	@Test
	void update() {
		assertDoesNotThrow(()->this.personDao.save(person2));
		this.person2.setFullname("updateName2");
		this.personDao.update(person2);
		assertEquals("updateName2",this.personDao.getById(-2).getFullname());
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person2));
	}

	@Test
	void deleteByPerson() {
		assertDoesNotThrow(()->this.personDao.save(person1));
		assertDoesNotThrow(()->this.personDao.save(person2));
		assertDoesNotThrow(()->this.personDao.save(person3));
		assertDoesNotThrow(()->this.personDao.save(person4));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person1));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person2));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person3));
		assertDoesNotThrow(()->this.personDao.deleteByPerson(person4));

	}
}