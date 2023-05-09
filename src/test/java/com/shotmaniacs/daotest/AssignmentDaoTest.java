package com.shotmaniacs.daotest;

import com.shotmaniacs.models.Assignment;
import com.shotmaniacs.models.event.StatusPermission;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AssignmentDaoTest {
    private final EventDaoTest eventDao = EventDaoTest.getInstance();
    private final CrewDaoTest crewDao = CrewDaoTest.getInstance();
    private final List<Assignment> assignments = new ArrayList<>();

    private AssignmentDaoTest() {
    }

    public static AssignmentDaoTest getInstance() {
        return AssignmentDaoTestHolder.instance;
    }

    public List<Assignment> getAll() {
        return assignments;
    }

    public Optional<Assignment> getById(long memberId, long eventId) {
        return assignments.stream().filter(a -> a.getMemberId() == memberId && a.getEventId() == eventId).findFirst();
    }

    public List<Assignment> getByMemberId(long memberId) {
        return assignments.stream().filter(a -> a.getMemberId() == memberId).collect(Collectors.toList());
    }

    public List<Assignment> getByEventId(long eventId) {
        return assignments.stream().filter(a -> a.getEventId() == eventId).collect(Collectors.toList());
    }

    public void save(Assignment assignment) throws BadRequestException {
        if (!eventDao.existsById(assignment.getEventId())) {
            throw new BadRequestException("Such event does not exist");
        }
        if (!crewDao.existsById(assignment.getMemberId())) {
            throw new BadRequestException("Such crew member does not exist");
        }
        if (existsById(assignment.getMemberId(), assignment.getEventId())) {
            throw new BadRequestException("Such assignment already exists");
        }
        assignments.add(assignment);
    }

    public boolean existsById(long memberId, long eventId) {
        return assignments.stream().anyMatch(a -> a.getMemberId() == memberId && a.getEventId() == eventId);
    }

    public void updateStatusById(long memberId, long eventId, StatusPermission status) {
        for (Assignment assignment : assignments) {
            if ((memberId == 0 || memberId == assignment.getMemberId())
                    && (eventId == 0 || eventId == assignment.getEventId())) {
                assignment.setStatus(status);
            }
        }
    }

    public void deleteById(long memberId, long eventId) {
        assignments.removeIf(a -> (memberId == 0 || memberId == a.getMemberId())
                && (eventId == 0 || eventId == a.getEventId()));
    }

    public void incrementKaakjes(long memberId, long eventId) {
        getById(memberId, eventId).ifPresentOrElse(Assignment::incrementKaakjes, () -> {
            throw new BadRequestException("Such assignment does not exist");
        });
    }

    private final static class AssignmentDaoTestHolder {
        private final static AssignmentDaoTest instance = new AssignmentDaoTest();
    }
}