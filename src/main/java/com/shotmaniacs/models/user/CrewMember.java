package com.shotmaniacs.models.user;

import com.shotmaniacs.models.Department;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class CrewMember extends Person {

    private List<ProjectRole> roles;
    private Availability availability;
    private Department department;

    public CrewMember(long id, String fullname, String phone, String email, String password,
                      List<ProjectRole> roles, Availability availability, Department department) {
        super(id, fullname, phone, email, password, ServerRole.CREW);
        this.roles = roles;
        this.availability = availability;
        this.department = department;
    }

    public CrewMember(Person person, List<ProjectRole> roles, Availability availability, Department department) {
        this(person.getId(), person.getFullname(), person.getPhone(), person.getEmail(), person.getPassword(),
                roles, availability, department);
    }

    public CrewMember() {
        setRole(ServerRole.CREW);
    }

    public List<ProjectRole> getRoles() {
        return roles;
    }

    public void setRoles(List<ProjectRole> roles) {
        this.roles = roles;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "CrewMember{" +
                "roles=" + roles +
                ", availability=" + availability +
                ", department=" + department +
                "} " + super.toString();
    }
}