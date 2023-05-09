package com.shotmaniacs.daotest;

import com.shotmaniacs.models.Session;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SessionDaoTest {
    private final Set<Session> sessions = new HashSet<>();

    private SessionDaoTest() {
    }

    public Set<Session> getAll() {
        return sessions;
    }

    public Optional<Session> getByUserId(long userid) {
        return sessions.stream().filter(s -> s.getUserId() == userid).findFirst();
    }

    public Session establish(long userid) {
        Optional<Session> session = getByUserId(userid);
        if (session.isEmpty()) {
            Session newSession = new Session(userid);
            sessions.add(newSession);
            return newSession;
        }
        return session.get();
    }

    public static SessionDaoTest getInstance() {
        return SessionDaoHolder.instance;
    }

    private static final class SessionDaoHolder {
        public static final SessionDaoTest instance = new SessionDaoTest();
    }
}