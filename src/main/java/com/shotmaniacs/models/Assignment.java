package com.shotmaniacs.models;

import com.shotmaniacs.models.event.StatusPermission;
import com.shotmaniacs.models.user.ProjectRole;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Assignment {

    private int duration;
    private int counterKaakjes;
    private ProjectRole role;
    private StatusPermission status;
    private List<Photo> photos;
    private long memberId;
    private long eventId;

    public Assignment(int duration, int counterKaakjes, ProjectRole role, StatusPermission status,
                      long crewMember, long event) {
        this.duration = duration;
        this.counterKaakjes = counterKaakjes;
        this.role = role;
        this.status = status;
        this.memberId = crewMember;
        this.eventId = event;
    }

    public Assignment() {
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCounterKaakjes() {
        return counterKaakjes;
    }

    public void setCounterKaakjes(int counterKaakjes) {
        this.counterKaakjes = counterKaakjes;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }

    public StatusPermission getStatus() {
        return status;
    }

    public void setStatus(StatusPermission status) {
        this.status = status;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public void incrementKaakjes() {
        counterKaakjes++;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "duration=" + duration +
                ", counterKaakjes=" + counterKaakjes +
                ", role=" + role +
                ", status=" + status +
                ", photos=" + photos +
                ", memberId=" + memberId +
                ", eventId=" + eventId +
                '}';
    }
}
