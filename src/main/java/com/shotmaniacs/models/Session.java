package com.shotmaniacs.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

@XmlRootElement
public class Session implements Principal {

    public static final int EXPIRES_IN_SECONDS = 3600;

    private long userId;
    private String token;
    private Date expiryDate;

    public Session(long userId) {
        this.userId = userId;
        // Generate a unique random token
        this.token = UUID.randomUUID().toString();
        // Set the expiration date for 1 hour from now
        this.expiryDate = new Date(System.currentTimeMillis() + EXPIRES_IN_SECONDS * 1000);
    }
    public Session(String token, Date expiryDate, int userId){
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public Session() {
    }

    public boolean isActive() {
        return new Date().before(expiryDate);
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "Session{" +
                "userId=" + userId +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}