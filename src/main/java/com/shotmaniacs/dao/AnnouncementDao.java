package com.shotmaniacs.dao;

import com.shotmaniacs.models.Announcement;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.Urgency;
import com.shotmaniacs.models.dto.AnnouncementDTO;
import com.shotmaniacs.models.user.CrewMember;
import org.jvnet.hk2.annotations.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnnouncementDao extends Dao {
    private static final String CLASS_NAME = "ANNOUNCEMENT";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String TIMESTAMP = "timestamp";
    private static final String URGENCY = "urgency";
    private static final String DEPARTMENT = "department";
    private static final String RECEIVE = "receive";
    private static final String SENDER = "sender";

    /**
     * This methode makes a call for all announcements in the database.
     *
     * @return result to be objects of announcement. It will return an empty list otherwise.
     */
    public List<Announcement> getAll() {
        return convertFromResultSetToAnnouncement(getAll(CLASS_NAME));
    }

    /**
     * This methode makes a call for all announcements in the database.
     *
     * @param announcement this methode takes announcement, in order to find out the id number.
     * @return an id number from the announcement.
     */
    private long getId(Announcement announcement) {
        Announcement announcement1 = getAnnouncement(TIMESTAMP, announcement.getTimestamp(), SENDER, announcement.getSenderId()).get(0);
        return announcement1.getId();
    }

    /*
     * This methode saves the announcement in the database.
     * @param announcement this is the announcement that will be saved.
     * @return the id number of the announcement.
     * */
    public long save(Announcement announcement) {
        if (announcement != null) {
            if (announcement.getId() == -1) {
                if (insertQuery(CLASS_NAME,
                        TITLE, announcement.getTitle(),
                        BODY, announcement.getBody(),
                        TIMESTAMP, announcement.getTimestamp(),
                        URGENCY, Urgency.getValue(announcement.getUrgency()),
                        DEPARTMENT, Department.getValue(announcement.getDepartment()),
                        RECEIVE, announcement.getReceiverId(),
                        SENDER, announcement.getSenderId()
                )) {

                    return getId(announcement);
                }
                return -1;
            }
            if (insertQuery(CLASS_NAME,
                    ID, announcement.getId(),
                    TITLE, announcement.getTitle(),
                    BODY, announcement.getBody(),
                    TIMESTAMP, announcement.getTimestamp(),
                    URGENCY, Urgency.getValue(announcement.getUrgency()),
                    DEPARTMENT, Department.getValue(announcement.getDepartment()),
                    RECEIVE, announcement.getReceiverId(),
                    SENDER, announcement.getSenderId()
            )) {
                return getId(announcement);
            }
            return -1;
        }
        return -1;
    }

    /**
     * This methode delete the announcement according to the id of announcement.
     *
     * @param id is id from announcement
     * @return boolean if it is deleted from the database our not.
     */
    public boolean deleteById(int id) {
        return deleteQuery(CLASS_NAME, ID, id);
    }

    /*
     * This methode get All announcements for the crewMembers, that they are allowed to see.
     * @param member that is the member were we need to find the announcement from.
     * @result is a list of announcement that the crewMember is allowed to see.
     * */
    public List<Announcement> getForCrewMember(CrewMember member) {
        if (member != null) {
            List<Announcement> result = new ArrayList<>();
            List<Announcement> personal = getAnnouncement(RECEIVE, member.getId());
            List<Announcement> forDepartment = getAnnouncement(DEPARTMENT, Department.getValue(member.getDepartment()));
            List<Announcement> forEveryone = getAnnouncement(DEPARTMENT, null, RECEIVE, 0);
            if (personal != null) {
                result.addAll(personal);
            }
            if (forDepartment != null) {
                result.addAll(forDepartment);
            }
            if (forEveryone != null) {
                result.addAll(forEveryone);
            }
            return result;
        }
        return new ArrayList<>();
    }

    /*
     * The methode should take a simplified announcement and update the announcement accordingly.
     * If it exists in the database or do nothing.
     * @param dto
     * */
    public void edit(AnnouncementDTO dto) {
        if (dto != null) {
            if (existById(dto.getId())) {
                if (dto.getBody() != null) {
                    updateQuery(CLASS_NAME, BODY, dto.getBody(), ID, dto.getId());
                }
                if (dto.getTitle() != null) {
                    updateQuery(CLASS_NAME, TITLE, dto.getTitle(), ID, dto.getId());
                }
            }
        }
    }

    //Following methods are strictly private to protected them from malicious forms out the class. This methodes are called only when the input in sanitized.
    /*
     * The methode will get announcement according two variables.
     * @param attribute is a variable of the constant strings above.
     * @param value is a value according the attribute were the database needs to compair with.
     * @return a list of announcement according to the given input.
     * */
    private List<Announcement> getAnnouncement(String attribute, Object value) {
        return convertFromResultSetToAnnouncement(getObject(CLASS_NAME, attribute, value));
    }

    /*
     * The methode will get announcement according four variables.
     * @param attribute1 is a variable of the constant strings above.
     * @param value1 is a value according the attribute were the database needs to compare with.
     * @param attribute2 is a variable of the constant strings above.
     * @param value2 is a value according the attribute were the database needs to compare with.
     * @return a list of announcement according to the given input.
     * */
    private List<Announcement> getAnnouncement(String attribute1, Object value1, String attribute2, Object value2) {
        return convertFromResultSetToAnnouncement(getObject(CLASS_NAME, attribute1, value1, attribute2, value2));
    }

    /*
     * The methode will get announcement according four variables.
     * @param attribute1 is a variable of the constant strings above.
     * @param value1 is a value according the attribute were the database needs to compare with.
     * @param attribute2 is a variable of the constant strings above.
     * @param value2 is a value according the attribute were the database needs to compare with.
     * @return a list of announcement according to the given input.
     * */
    private boolean existById(Long id) {
        List<Announcement> announcementList = convertFromResultSetToAnnouncement(getObject(CLASS_NAME, ID, id));
        if (announcementList != null) {
            return announcementList.size() > 0;
        }
        return false;
    }

    /*
     * This methode convert a resultSet produced from the announcement table to a list with announcements.
     * @param resultSet this is a result set with the column as given the constant above the class.
     *
     * */
    private List<Announcement> convertFromResultSetToAnnouncement(ResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        try {
            List<Announcement> announcementList = new ArrayList<>();
            while (resultSet.next()) {
                announcementList.add(new Announcement(
                        resultSet.getInt(ID),
                        resultSet.getString(TITLE),
                        resultSet.getString(BODY),
                        resultSet.getTimestamp(TIMESTAMP),
                        Urgency.valueOfOrNull(resultSet.getString(URGENCY)),
                        Department.valueOfOrNull(resultSet.getString(DEPARTMENT)),
                        resultSet.getLong(RECEIVE),
                        resultSet.getLong(SENDER)));
            }
            return announcementList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
