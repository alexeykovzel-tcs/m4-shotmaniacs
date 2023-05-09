package com.shotmaniacs.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
public class Announcement {

    private long id;
    private String title;
    private String body;
    private Date timestamp;
    private Urgency urgency;
    private Department department;
    private Long receiverId;
    private long senderId;

    public Announcement(long id, String title, String body, Date timestamp, Urgency urgency,
                        Department department, Long receiverId, long senderId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.urgency = urgency;
        this.department = department;
        this.receiverId = receiverId;
        this.senderId = senderId;
    }

    public Announcement() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", timestamp=" + timestamp +
                ", urgency=" + urgency +
                ", department=" + department +
                ", receiverId=" + receiverId +
                ", senderId=" + senderId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Announcement that = (Announcement) o;
        return id == that.id
                && receiverId == that.receiverId
                && senderId == that.senderId
                && Objects.equals(title, that.title)
                && Objects.equals(body, that.body)
                && Objects.equals(timestamp, that.timestamp)
                && urgency == that.urgency
                && department == that.department;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, timestamp, urgency, department, receiverId, senderId);
    }

    public static class builder {

        private final Announcement announcement = new Announcement();

        public builder id(long id) {
            announcement.setId(id);
            return this;
        }

        public builder title(String title) {
            announcement.setTitle(title);
            return this;
        }

        public builder body(String body) {
            announcement.setBody(body);
            return this;
        }

        public builder timestamp(Date timestamp) {
            announcement.setTimestamp(timestamp);
            return this;
        }

        public builder urgency(Urgency urgency) {
            announcement.setUrgency(urgency);
            return this;
        }

        public builder department(Department department) {
            announcement.setDepartment(department);
            return this;
        }

        public builder receiverId(long receiverId) {
            announcement.setReceiverId(receiverId);
            return this;
        }

        public builder senderId(long senderId) {
            announcement.setSenderId(senderId);
            return this;
        }

        public Announcement build() {
            return announcement;
        }
    }
}
