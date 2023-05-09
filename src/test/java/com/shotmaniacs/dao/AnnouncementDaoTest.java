package com.shotmaniacs.dao;

import com.shotmaniacs.models.Announcement;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.Urgency;
import com.shotmaniacs.models.dto.AnnouncementDTO;
import com.shotmaniacs.models.user.Admin;
import com.shotmaniacs.models.user.Availability;
import com.shotmaniacs.models.user.CrewMember;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnnouncementDaoTest {
	CrewMember crew1;
	CrewMember crew2;
	CrewMember crew3;
	CrewMember crew4;

	Admin admin1;
	PersonDao personDao;
	AnnouncementDao announcementDao;
	Announcement announcement1;
	Announcement announcement2;
	Announcement announcement3;
	AnnouncementDTO announcementDTO;
	@BeforeEach
	void setUp() {
		crew1 = new CrewMember(-2,"test2","+1245","emil@email.com","password",null, Availability.AVAILABLE, Department.MARKETING);
		crew2 = new CrewMember(-3,"test3","+1235","emal@email.com","password",null, Availability.RETIRED, Department.CLUB_PHOTOGRAPHY);
		crew3 = new CrewMember(-4,"test4","+135","email@email.com","password",null, Availability.VACATION, Department.EVENT_FILMMAKING);
		crew4 = new CrewMember(-5,"test5","+2435","eail@email.com","password",null, Availability.AVAILABLE, Department.EVENT_PHOTOGRAPHY);
		admin1 = new Admin(-6,"name1","06-234567812", "email@email3.com","sssst");
		personDao = new PersonDao();
		announcementDao = new AnnouncementDao();
		announcement1 = new Announcement(-2,"title","body", new Date(System.currentTimeMillis()), Urgency.LEVEL_1,Department.MARKETING,0L,-6);
		announcement2 = new Announcement(-3,"title","body", new Date(System.currentTimeMillis()), Urgency.LEVEL_2,null,-3L,-6);
		announcement3 = new Announcement(-4,"title","body", new Date(System.currentTimeMillis()), Urgency.LEVEL_1,null,0L,-6);

		announcementDTO = new AnnouncementDTO(-2,"newtitle2","newBody2");
		personDao.save(crew1);
		personDao.save(crew2);
		personDao.save(crew3);
		personDao.save(crew4);
		personDao.save(admin1);

	}

	@AfterEach
	void tearDown() {
		personDao.deleteByPerson(crew1);
		personDao.deleteByPerson(crew2);
		personDao.deleteByPerson(crew3);
		personDao.deleteByPerson(crew4);
		personDao.deleteByPerson(admin1);
	}

	@Test
	void getAll() {
		int start = announcementDao.getAll().size();
		announcementDao.save(announcement1);
		assertEquals(start+1, announcementDao.getAll().size());
		announcementDao.deleteById(-2);
		assertEquals(start, announcementDao.getAll().size());
	}

	@Test
	void save() {
		assertDoesNotThrow(()->announcementDao.save(announcement1));
		assertDoesNotThrow(()->announcementDao.save(announcement2));
		announcementDao.deleteById(-2);
		announcementDao.deleteById(-3);
	}

	@Test
	void deleteById() {
		announcementDao.save(announcement1);
		announcementDao.save(announcement2);
		assertDoesNotThrow(()->announcementDao.deleteById(-2));
		assertDoesNotThrow(()->announcementDao.deleteById(-3));
	}

	@Test
	void getForCrewMember() {
		announcementDao.save(announcement1);
		announcementDao.save(announcement3);
		announcementDao.save(announcement2);
		List<Announcement> announcementList = announcementDao.getForCrewMember(crew1);
		for (Announcement annoucement:announcementList) {
			assertTrue((annoucement.getDepartment() == null && annoucement.getReceiverId() == 0) ||
					annoucement.getDepartment().equals(crew1.getDepartment()) ||
					annoucement.getReceiverId() == crew1.getId());
		}
		announcementDao.deleteById(-2);
		announcementDao.deleteById(-3);
		announcementDao.deleteById(-4);

	}

	@Test
	void edit() {
		announcementDao.save(announcement1);
		assertDoesNotThrow(()->announcementDao.edit(announcementDTO));
		List<Announcement> announcementList = announcementDao.getForCrewMember(crew1);
		for (Announcement annoucement:announcementList) {
			if (annoucement.getId() == announcement1.getId()){
				assertEquals(annoucement.getTitle(),announcementDTO.getTitle());
				assertEquals(annoucement.getBody(),announcementDTO.getBody());
			}
			break;
		}
		announcementDao.deleteById(-2);
	}
}