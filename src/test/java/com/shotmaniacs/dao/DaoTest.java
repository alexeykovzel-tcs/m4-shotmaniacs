package com.shotmaniacs.dao;

import com.shotmaniacs.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DaoTest {
	Dao dao;
	private static final String TITLE = "title";
	private static final String CLIENT = "client";
	private static final String className ="EVENT";
	@BeforeEach
	void setUp() {
		this.dao = new EventDao();
	}

	@Test
	void getConnection() {
		assertNotNull(this.dao.getConnection());
		assertDoesNotThrow( ()-> this.dao.getConnection());
	}
	@Test
	void ifExists() {
		assertTrue(this.dao.insertQuery("PERSON","id",-1,"fullname","hallo"));
		assertTrue(this.dao.ifExists("PERSON","id",-1));
		assertTrue(this.dao.deleteQuery("PERSON","id",-1));
		assertFalse(this.dao.ifExists("PERSON","id",-1));
	}

	@Test
	void insertQuery() {
		//Making an event
		assertTrue(this.dao.insertQuery("PERSON","id",-1,"fullname","hallo"));
		assertTrue(this.dao.insertQuery("CLIENT","id",-1,"company","halloCompany"));
		assertTrue(this.dao.insertQuery("EVENT","title","title","client",-1));
		assertTrue(this.dao.insertQuery("EVENT","id",-1,"title","title1","client",-1,"start_date", DateUtils.format("02-02-2022 08:00")));
		//Making crew
		assertTrue(this.dao.insertQuery("PERSON","id",-2,"fullname","crew"));
		assertTrue(this.dao.insertQuery("CREW_MEMBER","id",-2));
		assertTrue(this.dao.insertQuery("ENROLMENT","crewMember",-2,"event",-1));

		//Delete all the attributes after making them.
		assertTrue(this.dao.deleteQuery("ENROLMENT","event",-1));
		assertTrue(this.dao.deleteQuery("EVENT","client",-1));
		assertTrue(this.dao.deleteQuery("CLIENT","id",-1));
		assertTrue(this.dao.deleteQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.deleteQuery("PERSON","id",-1));

		assertTrue(this.dao.deleteQuery("CREW_MEMBER","id",-2));
		assertTrue(this.dao.deleteQuery("PERSON","id",-2));
		assertFalse(this.dao.insertQuery("PERSON","the is an odd number of attribute without value"));


	}

	@Test
	void updateQuery() {
		assertTrue(this.dao.insertQuery("PERSON","id",-1,"fullname","hallo"));
		assertTrue(this.dao.insertQuery("CLIENT","id",-1,"company","halloCompany"));
		assertTrue(this.dao.updateQuery("PERSON", "email","hoi@hoi.nl","id",-1));
		assertTrue(this.dao.updateQuery("CLIENT","company","fullNameCompany","id",-1));
		assertTrue(this.dao.deleteQuery("CLIENT","id",-1));
		assertTrue(this.dao.deleteQuery("PERSON","id",-1));
	}

	@Test
	void deleteQuery() {
		assertTrue(this.dao.insertQuery("PERSON","id",-1,"fullname","hallo"));
		assertTrue(this.dao.deleteQuery("PERSON","id",-1));
	}

	@Test
	void getObject() {
		assertTrue(this.dao.insertQuery("PERSON","id",-1,"fullname","hallo"));
		try {
			ResultSet resultSet =this.dao.getObject("PERSON","fullname","hallo");
			resultSet.next();
			ResultSet resultSet1 = this.dao.getObject("PERSON","id",-1);
			resultSet1.next();
			assertEquals(resultSet.getInt("id"),resultSet1.getInt("id")
			);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertTrue(this.dao.deleteQuery("PERSON","id",-1));

	}

	@Test
	void getAll() {
		ResultSet resultSet =this.dao.getAll("PERSON");
		int count = 0;
		try {
			while(resultSet.next()){
				count = resultSet.getRow();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		resultSet =this.dao.getAll("PERSON");
		int before = count;
		try {
			while(resultSet.next()){
				count = resultSet.getRow();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(5 + before,count);

		assertTrue(this.dao.deleteQuery("PERSON","fullname","hallo"));

	}

	@Test
	void getAttribute() {
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		assertTrue(this.dao.insertQuery("PERSON","fullname","hallo"));
		ResultSet resultSet =this.dao.getAttribute("PERSON","*","fullname","hallo");
		int count = 0;
		try {
			while(resultSet.next()){
				assertNotEquals(count,resultSet.getInt("id"));
				count =resultSet.getInt("id") ;
				assertEquals("hallo",resultSet.getString("fullname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertTrue(this.dao.deleteQuery("PERSON","fullname","hallo"));

	}
}