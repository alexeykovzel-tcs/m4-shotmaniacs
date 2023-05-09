package com.shotmaniacs.dao;

import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.event.*;
import com.shotmaniacs.models.user.Client;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.models.user.Person;
import org.jvnet.hk2.annotations.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventDao extends Dao {
    private static final String CLASS_NAME = "EVENT";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String LOCATION = "location";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";
    private static final String DURATION = "duration";
    private static final String NOTES = "notes";
    private static final String PRODUCT_MANAGER = "product_manager";
    private static final String CLIENT = "client";
    private static final String BOOKING = "booking";
    private static final String STATUS = "status";
    private static final String EVENT_TYPE = "event_type";
    private static final String DEPARTMENT = "department_type";
    private final PersonDao personDao = new PersonDao();

    /**
     * This methode gives all Events in a list.
     *
     * @return a list of all events existing in the database.
     */
    public List<Event> getAll() {
        return convertFromResultSetToEvent(getAll(CLASS_NAME));
    }

    /**
     * This methode give all Events with the give status
     *
     * @param status is the status that all events have as result.
     * @return a list with all event with status given.
     */
    public List<Event> getByStatus(EventStatus status) {
        return convertFromResultSetToEvent(getEvents(STATUS, EventStatus.getValue(status)));
    }

    /**
     * This methode gives an event object according to the id given
     *
     * @param id the id of the event that need to be returned.
     * @return Event according to the id.
     */
    public Event getById(long id) {
        List<Event> eventList = convertFromResultSetToEvent(getEvents(ID, id));
        if (eventList != null && eventList.size() > 0) {
            return eventList.get(0);
        }
        return null;
    }

    /**
     * This methode query the database to get Event objects according the given values.
     *
     * @param attribute the name of the attribute to compare with.
     * @param value     the value to compair the attribute with.
     * @return ResultSet containing events.
     */
    private ResultSet getEvents(String attribute, Object value) {
        return getObject(CLASS_NAME, attribute, value);
    }
//All exists statements.

    /**
     * This methode checks if the id exist in the event class.
     *
     * @param id is the unique identifier to check.
     * @return boolean if the query did succeed.
     */
    public boolean existById(long id) {
        return getById(id) != null;
    }

    /**
     * This methode get the id from the client from the id of the event.
     *
     * @param idEvent is id from event.
     * @return -1 if something goes wrong other wise it returns the id number from the client.
     */
    public long getClientIdById(long idEvent) {
        ResultSet resultSet = getAttribute(CLASS_NAME, CLIENT, ID, idEvent);
        try {
            if (resultSet.next()) {
                return resultSet.getLong(CLIENT);
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
     *All save statements
     * */

    /**
     * This methode takes care to save event.
     *
     * @param event that doesn't exist already in the database, the
     * @return boolean if the query went succesfully.
     */
    public boolean save(Event event) {
        if (event == null) {
            return false;
        }
        CrewMember crew = event.getProductManager();
        Object idManager;
        if (crew != null) {
            idManager = crew.getId();
            return createEvent(TITLE, event.getName(), LOCATION, event.getLocation(), START_DATE, event.getStartDate(),
                    END_DATE, event.getEndDate(), DURATION, event.getDuration(), NOTES, event.getNotes(), PRODUCT_MANAGER,
                    idManager, CLIENT, event.getClient().getId(), BOOKING,
                    BookingType.getValue(event.getBookingType()), STATUS, EventStatus.getValue(event.getStatus()),
                    EVENT_TYPE, EventType.getValue(event.getEventType()), DEPARTMENT,
                    Department.getValue(event.getDepartmentType()));
        } else {
            if (event.getId() == -1) {
                return createEvent(TITLE, event.getName(), LOCATION, event.getLocation(), START_DATE, event.getStartDate(),
                        END_DATE, event.getEndDate(), DURATION, event.getDuration(), NOTES, event.getNotes(), CLIENT, event.getClient().getId(), BOOKING,
                        BookingType.getValue(event.getBookingType()), STATUS, EventStatus.getValue(event.getStatus()),
                        EVENT_TYPE, EventType.getValue(event.getEventType()), DEPARTMENT,
                        Department.getValue(event.getDepartmentType()));
            } else {
                return createEvent(ID, event.getId(), TITLE, event.getName(), LOCATION, event.getLocation(), START_DATE, event.getStartDate(),
                        END_DATE, event.getEndDate(), DURATION, event.getDuration(), NOTES, event.getNotes(), CLIENT, event.getClient().getId(), BOOKING,
                        BookingType.getValue(event.getBookingType()), STATUS, EventStatus.getValue(event.getStatus()),
                        EVENT_TYPE, EventType.getValue(event.getEventType()), DEPARTMENT,
                        Department.getValue(event.getDepartmentType()));
            }
        }

    }

    /**
     * This methode takes care to save all event and checks if the client already exist in the database. Otherwise it will be added.
     *
     * @param events a list of all events to be added.
     * @return boolean if the query went succesfully.
     */
    public boolean saveAll(List<Event> events) {
        boolean dirty = true;
        if (events != null) {
            for (Event even : events) {
                long idClient = checkForClient(even);
                if (idClient != -1) {
                    even.getClient().setId(idClient);
                    dirty = dirty | save(even);
                } else {
                    dirty = false;
                }
            }
            return dirty;
        }
        return false;
    }

    /**
     * This is helper classes for saveAll events.
     * This methode checks if the client already have an account and which id belong to them.
     *
     * @param event with id from the client to check.
     * @return the id for the client.
     */
    private long checkForClient(Event event) {
        long idClient = -1;
        if (event.getClient() != null) {
            String email = event.getClient().getEmail();
            Client client = event.getClient();
            if (!personDao.existsByEmail(email) && personDao.getById(client.getId()) == null) {
                client.setPhone("");
                personDao.save(client);
                idClient = (new PersonDao()).getIdByEmail(client.getEmail());
            } else if (personDao.getById(client.getId()) == null) {
                idClient = (new PersonDao()).getIdByEmail(email);
            } else {
                idClient = client.getId();
            }
        }
        return idClient;
    }

    /**
     * This class helps to insert events.
     *
     * @param values the numbers 0,2,4,6,... contain attributes and the 1,3,5,7 accordly contain the values.
     * @return boolean if the query went well.
     */
    private boolean createEvent(Object... values) {
        return insertQuery(CLASS_NAME, values);
    }

    /*
     *  All update Statements
     */

    /**
     * This methode update an event
     *
     * @param event that need to be updated.
     * @return boolean if the query went well.
     */
    public boolean update(Event event) {
        long id = event.getId();
        Object toSet;
        if (id != -1) {
            if ((toSet = event.getName()) != null) {
                updateSingleEvent(id, this.TITLE, toSet);
            }
            if ((toSet = event.getLocation()) != null) {
                updateSingleEvent(id, this.LOCATION, toSet);
            }
            if ((toSet = event.getStartDate()) != null) {
                updateSingleEvent(id, this.START_DATE, toSet);
            }
            if ((toSet = event.getEndDate()) != null) {
                updateSingleEvent(id, this.END_DATE, toSet);
            }
            if ((toSet = event.getDuration()) != null) {
                updateSingleEvent(id, this.DURATION, toSet);
            }
            if ((toSet = event.getNotes()) != null) {
                updateSingleEvent(id, this.NOTES, toSet);
            }
            if (event.getProductManager() != null) {
                updateSingleEvent(id, this.PRODUCT_MANAGER, event.getProductManager().getId());
            }
            if (event.getClient() != null) {
                updateSingleEvent(id, this.CLIENT, event.getClient().getId());
            }
            if (event.getBookingType() != null) {
                updateSingleEvent(id, this.BOOKING, BookingType.getValue(event.getBookingType()));
            }
            if (event.getStatus() != null) {
                updateSingleEvent(id, this.STATUS, EventStatus.getValue(event.getStatus()));
            }
            if (event.getEventType() != null) {
                updateSingleEvent(id, this.EVENT_TYPE, EventType.getValue(event.getEventType()));
            }
            if (event.getDepartmentType() != null) {
                updateSingleEvent(id, this.DEPARTMENT, Department.getValue(event.getDepartmentType()));
            }
            return true;
        }
        return false;
    }

    /**
     * This methode is a helper class to updateEvents.
     *
     * @param id     the id of the event
     * @param status towards the event needs to be changed to..
     * @return boolean if the query went well.
     */
    public boolean updateStatus(long id, EventStatus status) {
        if (status == null) return false;
        Event event = getById(id);
        if (event != null) {
            updateSingleEvent(id, this.STATUS, EventStatus.getValue(status));
            return true;
        }
        return false;
    }

    /**
     * This methode is a helper class to updateEvents.
     *
     * @param attributeSet is the attribute that needs to be changed.
     * @param valueSet     is the value towards it needs to be changed.
     * @param idEvent      is the uniquely identifier for the event.
     * @return boolean if the query went well.
     */
    private boolean updateSingleEvent(long idEvent, String attributeSet, Object valueSet) {
        return updateEvent(attributeSet, valueSet, ID, idEvent);
    }

    /**
     * This methode is a helper class to updateEvents.
     *
     * @param attributeSet   is the attribute that needs to be changed.
     * @param valueSet       is the value towards it needs to be changed.
     * @param attributeWhere is the attribute that uniquely identifies the event.
     * @param valueWhere     is the value that it uniquely identifies the event.
     * @return boolean if the query went well.
     */
    private boolean updateEvent(String attributeSet, Object valueSet, String attributeWhere, Object valueWhere) {
        return updateQuery(CLASS_NAME, attributeSet, valueSet, attributeWhere, valueWhere);
    }
//AllowedEventsForCrew

    /**
     * This methode gets all events that are allowed to see for the CrewMember.
     *
     * @param member is a member existing in the CrewMember of the Database
     * @return a list that is only allowed to be seen be that specified crewMember.
     */
    public List<Event> getAllowedEventsFor(CrewMember member) {
        List<Event> allowedEvents = new ArrayList<>();
        Department department = member.getDepartment();
        for (Event event : getAll()) {
            switch (event.getBookingType()) {
                case FILM:
                    if (department != Department.EVENT_FILMMAKING) continue;
                    break;
                case PHOTOGRAPHY:
                    if (department != Department.EVENT_PHOTOGRAPHY
                            && (department != Department.CLUB_PHOTOGRAPHY
                            && event.getEventType() != EventType.CLUB)) continue;
                    break;
                case MARKETING:
                    if (department != Department.MARKETING) continue;
                    break;
            }
            allowedEvents.add(event);
        }
        return allowedEvents;
    }
    // All delete Statements

    /**
     * This methode deleted the events accordingly the id number.
     *
     * @param id the unique identifier for the event.
     * @return boolean if the event is well deleted.
     */
    public boolean deleteById(long id) {
        return deleteEvent(ID, id);
    }

    /**
     * This methode helps with deleting the Events.
     *
     * @param attributeWhere is attribute name to compare with
     * @param valueWhere     is the value the attribute should have to compare.
     * @return boolean if the event is well deleted.
     */
    private boolean deleteEvent(String attributeWhere, Object valueWhere) {
        return deleteQuery(CLASS_NAME, attributeWhere, valueWhere);
    }

    /**
     * Convert all ResultSet in events
     *
     * @param events the resultSet of the events.
     * @return the list with events that did was in the resultSet.
     */
    private List<Event> convertFromResultSetToEvent(ResultSet events) {
        List<Event> eventsList = new ArrayList<>();
        try {
            while (events.next()) {
                // Define a client who booked the event
                long clientId = events.getLong(CLIENT);
                Person person = personDao.getById(clientId);
                Client client =  new Client();
                client.setId(clientId);
                if (person != null) {
                    client = new Client(person, null);
                }

                // Define a product manager that is responsible for the event
                CrewMember productManager = new CrewMember();
                productManager.setId(events.getInt(PRODUCT_MANAGER));
                eventsList.add(new Event(
                        events.getLong(ID),
                        events.getString(TITLE),
                        BookingType.valueOfOrNull(events.getString(BOOKING)),
                        EventType.valueOfOrNull(events.getString(EVENT_TYPE)),
                        events.getString(LOCATION),
                        events.getDate(START_DATE),
                        events.getDate(END_DATE),
                        events.getInt(DURATION),
                        events.getString(NOTES),
                        client,
                        EventStatus.valueOfOrNull(events.getString(STATUS)),
                        productManager,
                        Department.valueOfOrNull(events.getString(DEPARTMENT))));
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong");
        }
        return eventsList;
    }

    /**
     * This methode get all available events for a crew member.
     *
     * @param crewMember is the crewMember for which the events are available.
     * @return a list with all available events for the crewMember.
     */
    public List<Event> getAvailableEventsFor(CrewMember crewMember) {
        List<Event> events = new ArrayList<>();
        for (Event event : getAllowedEventsFor(crewMember)) {
            if (event.getStatus().equals(EventStatus.TODO)) {
                events.add(event);
            }
        }
        return events;
    }
}