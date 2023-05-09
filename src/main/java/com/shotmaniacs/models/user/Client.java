package com.shotmaniacs.models.user;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Client extends Person {

    private String company;

    public Client(long id, String fullname, String phone, String email, String password, String company) {
        super(id, fullname, phone, email, password, ServerRole.CLIENT);
        this.company = company;
    }

    public Client(Person person, String company) {
        super(person);
        this.company = company;
    }

    public Client() {
        setRole(ServerRole.CLIENT);
    }

    @Override
    public String toString() {
        return "Client{} " + super.toString();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}