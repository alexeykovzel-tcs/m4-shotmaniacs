package com.shotmaniacs.models.user;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Admin extends Person {

    public Admin(long id, String fullname, String phone, String email, String password) {
        super(id, fullname, phone, email, password, ServerRole.ADMIN);
    }

    public Admin() {
        setRole(ServerRole.ADMIN);
    }

    @Override
    public String toString() {
        return "Admin{} " + super.toString();
    }
}