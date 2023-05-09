package com.shotmaniacs.dao;

import com.shotmaniacs.models.Session;
import com.shotmaniacs.models.user.Client;
import com.shotmaniacs.models.user.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionDaoTest {
	SessionDao sessionDao;
	Session session1;
	PersonDao personDao;
	Person person2;
	String email2;
	@BeforeEach
	void beforeAll() {
		this.sessionDao = new SessionDao();
		this.personDao = new PersonDao();
		person2 = new Client(-2,"client1","phone",(email2 = "email"),"password","companu");
		this.personDao.save(person2);
		this.personDao = new PersonDao();
		this.session1 = this.sessionDao.establish(-2);
	}
	@AfterEach
	void tearDown() {
		this.personDao.deleteByPerson(person2);
	}

	@Test
	void getAll() {
		int start = this.sessionDao.getAll().size();
		this.sessionDao.delete(session1);
		assertEquals(start-1,this.sessionDao.getAll().size());
	}

	@Test
	void getByUserId() {
		int start = this.sessionDao.getAll().size();
		assertDoesNotThrow(()->this.sessionDao.getByUserId(-2));
		this.sessionDao.delete(session1);
		assertEquals(start-1,this.sessionDao.getAll().size());
	}

	@Test
	void delete() {
		assertDoesNotThrow(()->this.sessionDao.delete(session1));
	}

	@Test
	void establish() {
		assertDoesNotThrow(()->this.sessionDao.establish(-2));
		this.sessionDao.delete(session1);
	}


}