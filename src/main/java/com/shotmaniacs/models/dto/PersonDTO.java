package com.shotmaniacs.models.dto;

import com.shotmaniacs.models.user.ServerRole;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class PersonDTO {

    private long id;
    private String email;
    private String fullname;
    private String phone;
    private ServerRole role;

    public PersonDTO(long id, String email, String fullname, String phone, ServerRole role) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
        this.phone = phone;
        this.role = role;
    }

    public PersonDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ServerRole getRole() {
        return role;
    }

    public void setRole(ServerRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return id == personDTO.id
                && Objects.equals(email, personDTO.email)
                && Objects.equals(fullname, personDTO.fullname)
                && Objects.equals(phone, personDTO.phone)
                && role == personDTO.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, fullname, phone, role);
    }
}