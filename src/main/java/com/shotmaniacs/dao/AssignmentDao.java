package com.shotmaniacs.dao;

import com.shotmaniacs.models.Assignment;
import com.shotmaniacs.models.event.StatusPermission;
import com.shotmaniacs.models.user.ProjectRole;
import org.jvnet.hk2.annotations.Service;

import javax.ws.rs.BadRequestException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentDao extends Dao {
    private static final String CLASS_NAME = "ENROLMENT";
    private static final String DURATION = "duration";
    private static final String COUNTER = "counter_kaakjes";
    private static final String ROLE = "project_role";
    private static final String STATUS = "status_permission";
    private static final String CREW_MEMBER = "crewMember";
    private static final String EVENT = "event";
    private final EventDao eventDao;
    private final CrewDao crewDao;

    /**
     * This constructor make sure there is a crewDoa() and a eventDao()
     */
    public AssignmentDao() {
        eventDao = new EventDao();
        crewDao = new CrewDao();
    }

    /**
     * This methode returns all Assigment that are stored in the database.
     *
     * @return a list of all assignments.
     */
    public List<Assignment> getAll() {
        return convertFromResultSetToAssignment(getAll(CLASS_NAME));
    }

    /**
     * This is the methode return a list with the assignment for a certain event and a certain crewMember.
     *
     * @param eventId  this the id of the event.
     * @param memberId this the id of the crewMember.
     * @return list of assigment where there is both member and event are part of. Usually one assignment.
     * (Since memberId, evenId are primary key.)
     */
    public List<Assignment> getById(long memberId, long eventId) {
        return convertFromResultSetToAssignment(getObject(CLASS_NAME, EVENT, eventId, CREW_MEMBER, memberId));
    }

    /**
     * This methode gets all the assigment where a certain crewMember is part of.
     *
     * @param memberId this is an id of the crew.
     * @return a list of assignments, where memberId is part of.
     */
    public List<Assignment> getByMemberId(long memberId) {
        return convertFromResultSetToAssignment(getAssignment(CREW_MEMBER, memberId));
    }

    /**
     * This methode gets all the assigment that are assigned to certain crewMembers.
     *
     * @param eventId this is an id of the event.
     * @return a list of assignments, where eventId is part of.
     */
    public List<Assignment> getByEventId(long eventId) {
        return convertFromResultSetToAssignment(getAssignment(EVENT, eventId));
    }

    /**
     * This methode checks if a member in combination with the eventId exist.
     *
     * @param eventId  this is an id of the client.
     * @param memberId this is an id
     * @return boolean if the id of memberId and eventId exist in the enrolment class.
     */
    public boolean existsById(long memberId, long eventId) {
        List<Assignment> assignmentList = getById(memberId, eventId);
        if (assignmentList != null) {
            return assignmentList.size() > 0;
        }
        return false;
    }

    /**
     * This methode takes care of saving the assigment to the database.
     *
     * @param assignment the assigment to be added
     * @return boolean if the assignment have been added at the database or not.
     * @throws BadRequestException if the event from the assigment doesn't exist.
     * @throws BadRequestException if the member from the assignment doesn't exist.
     * @throws BadRequestException if the assigment already exist.
     */
    public boolean save(Assignment assignment) throws BadRequestException {
        if (assignment == null) {
            throw new BadRequestException("assigment is null");
        }
        if (!eventDao.existById(assignment.getEventId())) {
            throw new BadRequestException("Such event does not exist");
        }
        if (!crewDao.existsById(assignment.getMemberId())) {
            throw new BadRequestException("Such crew member does not exist");
        }
        if (existsById(assignment.getMemberId(), assignment.getEventId())) {
            throw new BadRequestException("Such assignment already exists");
        }
        return insertQuery(CLASS_NAME, DURATION, assignment.getDuration(),
                COUNTER, assignment.getCounterKaakjes(),
                ROLE, assignment.getRole().name(),
                STATUS, assignment.getStatus().name(),
                CREW_MEMBER, assignment.getMemberId(),
                EVENT, assignment.getEventId());

    }

    /**
     * This is a helper class to query the database in order to don't write the className everyTime.
     *
     * @param attribute is the name of the attribute that will be in the WHERE clause.
     * @param value     is an object in order to take any value from String to date, to be conditioned with the attribute.
     * @return een resultSet from the part of
     */
    private ResultSet getAssignment(String attribute, Object value) {
        return getObject(CLASS_NAME, attribute, value);
    }

    /**
     * This methode update the Status by the id of crewMember and from event.
     *
     * @param status   != null. the status is the one where it is needed to be changed too.
     * @param memberId is an id of a crewMember
     * @param eventId  is an id of a event
     * @return boolean if the update did succeed or not.
     */
    public boolean updateStatusById(long memberId, long eventId, StatusPermission status) {
        return updateQuery(CLASS_NAME,
                STATUS, StatusPermission.getValue(status),
                CREW_MEMBER, memberId,
                EVENT, eventId);
    }

    /**
     * This methode update the kaakjes counter by 1the id of crewMember and from event.
     *
     * @param memberId is an id of a crewMember
     * @param eventId  is an id of a event
     * @return boolean if the update did succeed or not.
     */
    public boolean incrementKaakjes(long memberId, long eventId) {
        List<Assignment> assignment = getById(memberId, eventId);
        if (assignment != null && assignment.size() > 0) {
            return updateQuery(CLASS_NAME,
                    COUNTER, assignment.get(0).getCounterKaakjes() + 1,
                    CREW_MEMBER, memberId,
                    EVENT, eventId);
        }
        return false;
    }

    /**
     * Delete the assigment by id from memberId and eventId
     *
     * @param memberId is the id of the crewMember.
     * @param eventId  is the id of the event
     * @return boolean if the assigment was successfully deleted.
     * @throws BadRequestException if the assigment doesn't exist.
     */
    public boolean deleteById(long memberId, long eventId) {
        if (!existsById(memberId, eventId)) {
            throw new BadRequestException("Such assignment doesn't exists");
        }
        return deleteQuery(CLASS_NAME, CREW_MEMBER, memberId, EVENT, eventId);
    }

    /**
     * This methode takes care of converting the resultSet after querying.
     *
     * @param resultSet the resultSet from the enrolment class that needs to be converted to assignments objects.
     * @return List<Assigment> this methode returns a list of assigment.
     */
    private List<Assignment> convertFromResultSetToAssignment(ResultSet resultSet) {
        try {
            List<Assignment> assignments = new ArrayList<>();
            while (resultSet.next()) {
                assignments.add(new Assignment(resultSet.getInt(DURATION),
                        resultSet.getInt(COUNTER),
                        ProjectRole.valueOfOrNull(resultSet.getString(ROLE)),
                        StatusPermission.valueOfOrNull(resultSet.getString(STATUS)),
                        resultSet.getInt(CREW_MEMBER),
                        resultSet.getInt(EVENT)));
            }
            return assignments;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
