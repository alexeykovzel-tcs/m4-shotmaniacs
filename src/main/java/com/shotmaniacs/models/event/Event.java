package com.shotmaniacs.models.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.user.Client;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.utils.DateUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
public class Event {

    private long id = 0;
    private String name;
    private BookingType bookingType;
    private EventType eventType;
    private String location;
    private Date startDate;
    private Date endDate;
    private int duration;
    private String notes;
    private Client client;
    private EventStatus status;
    private CrewMember productManager;
    private Department departmentType;

    public Event(long id, String name, BookingType bookingType, EventType eventType, String location, Date startDate,
                 Date endDate, int duration, String notes, Client client) {
        this(id, name, bookingType, eventType, location, startDate, endDate, duration, notes, client,
                EventStatus.TO_APPROVE, null, null);
    }

    public Event(long id, String name, BookingType bookingType, EventType eventType, String location,
                 Date startDate, Date endDate, int duration, String notes, Client client, EventStatus status,
                 CrewMember productManager, Department departmentType) {
        this.id = id;
        this.name = name;
        this.bookingType = bookingType;
        this.eventType = eventType;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.notes = notes;
        this.client = client;
        this.status = status;
        this.productManager = productManager;
        this.departmentType = departmentType;
    }

    public Event() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public CrewMember getProductManager() {
        return productManager;
    }

    public void setProductManager(CrewMember productManager) {
        this.productManager = productManager;
    }

    public Department getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(Department departmentType) {
        this.departmentType = departmentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id
                && duration == event.duration
                && Objects.equals(name, event.name)
                && bookingType == event.bookingType
                && eventType == event.eventType
                && Objects.equals(location, event.location)
                && Objects.equals(startDate, event.startDate)
                && Objects.equals(endDate, event.endDate)
                && Objects.equals(notes, event.notes)
                && Objects.equals(client, event.client)
                && status == event.status
                && Objects.equals(productManager, event.productManager)
                && departmentType == event.departmentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, bookingType, eventType, location, startDate, endDate, duration, notes, client, status, productManager, departmentType);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bookingType=" + bookingType +
                ", eventType=" + eventType +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", notes='" + notes + '\'' +
                ", duration=" + duration +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", client=" + client +
                ", productManager=" + productManager +
                ", departmentType=" + departmentType +
                '}';
    }

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    public static class EventBuilder {

        private final Event event = new Event();

        @JsonFormat(pattern = DateUtils.FORMAT)
        private Date startDate;

        @JsonFormat(pattern = DateUtils.FORMAT)
        private Date endDate;

        private int duration;
        private String notes;
        private Client client;
        private EventStatus status;
        private CrewMember productManager;
        private Department departmentType;

        public EventBuilder id(long id) {
            event.setId(id);
            return this;
        }

        public EventBuilder name(String name) {
            event.setName(name);
            return this;
        }

        public EventBuilder bookingType(BookingType bookingType) {
            event.setBookingType(bookingType);
            return this;
        }

        public EventBuilder eventType(EventType eventType) {
            event.setEventType(eventType);
            return this;
        }

        public EventBuilder startDate(String startDate) {
            event.setLocation(startDate);
            return this;
        }

        public EventBuilder endDate(String endDate) {
            event.setLocation(endDate);
            return this;
        }

        public EventBuilder duration(String duration) {
            event.setLocation(duration);
            return this;
        }

        public EventBuilder notes(String notes) {
            event.setLocation(notes);
            return this;
        }

        public EventBuilder client(Client client) {
            event.setClient(client);
            return this;
        }

        public EventBuilder status(EventStatus status) {
            event.setStatus(status);
            return this;
        }

        public EventBuilder productManager(CrewMember productManager) {
            event.setProductManager(productManager);
            return this;
        }

        public EventBuilder department(Department departmentType) {
            event.setDepartmentType(departmentType);
            return this;
        }

        public Event build() {
            return event;
        }
    }
}