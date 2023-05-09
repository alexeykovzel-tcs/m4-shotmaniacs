package com.shotmaniacs.dao;


import com.shotmaniacs.models.Session;
import org.jvnet.hk2.annotations.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class SessionDao extends Dao {
    private static final String CLASS_NAME = "WEB_SESSION";
    private static final String SESSION = "id";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String ID = "person_id";

    /**
     * This methode gets all sessions.
     *
     * @return a list with all session that exist in the database.
     **/
    public List<Session> getAll() {
        return convertFromResultSetToSession(getAll(CLASS_NAME));
    }

    /**
     * This methode gets all session from the user with userid
     *
     * @return the session where result.get(0).getId()== userid;
     */
    public List<Session> getByUserId(long userid) {
        return convertFromResultSetToSession(getObject(CLASS_NAME, ID, userid));
    }

    /**
     * This methode save a session. This methode is private, since only to establish methode
     * in this class should make new sessions.
     *
     * @param session is the session to be saved.
     * @return boolean is returned if the save was successfully or not.
     */
    private boolean save(Session session) {
        if (session != null) {
            return insertQuery(CLASS_NAME, SESSION, session.getToken(),
                    EXPIRY_DATE, session.getExpiryDate(),
                    ID, session.getUserId()
            );
        }
        return false;
    }

    /**
     * This methode delete a certain session.
     *
     * @param session is a session to be deleted.
     * @return boolean if the to delete went proper.
     */
    public boolean delete(Session session) {
        if (session == null) {
            return false;
        }
        return deleteQuery(CLASS_NAME, ID, session.getUserId());
    }

    /**
     * This methode establish a connection and save the connection in the database.
     *
     * @param userid is the id from the user.
     * @return boolean if the establishment went proper.
     */
    public Session establish(long userid) {
        List<Session> session = getByUserId(userid);
        if (session.isEmpty()) {
            Session newSession = new Session(userid);
            if (save(newSession)) {
                return newSession;
            }
            return null;
        } else if (!session.get(0).isActive()) {
            System.out.println(session.get(0).isActive());
            delete(session.get(0));
            Session newSession = new Session(userid);
            if (save(newSession)) {
                return newSession;
            }
            return session.get(0);
        }
        return session.get(0);
    }

    /**
     * This methode takes care of converting the resultSet after querying.
     *
     * @param resultSet the resultSet from the web_session class that needs to be converted to Session Objects.
     * @return List<Session> this methode returns a list of Session.
     */
    private List<Session> convertFromResultSetToSession(ResultSet resultSet) {
        List<Session> sessions = new ArrayList<>();
        if (resultSet == null) return sessions;
        try {
            while (resultSet.next()) {
                sessions.add(new Session(resultSet.getString(SESSION),
                        resultSet.getTimestamp(EXPIRY_DATE),
                        resultSet.getInt(ID)
                ));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        return sessions;
    }
}
