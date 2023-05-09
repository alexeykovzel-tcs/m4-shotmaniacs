package com.shotmaniacs.models.user;

import com.shotmaniacs.models.Credentials;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class Person {

    private long id;
    private String fullname;
    private String phone;
    private String email;
    private String password;
    private ServerRole role;

    public Person(long id, String fullname, String phone, String email, String password, ServerRole role) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Person(String fullname, String phone, String email, String password, ServerRole role) {
        this(-1, fullname, phone, email, password, role);
    }

    public Person(Person person) {
        this(person.getId(), person.getFullname(), person.getPhone(), person.getEmail(), person.getPassword(), person.getRole());
    }

    public Person() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        Person person = (Person) o;
        return id == person.id
                && Objects.equals(fullname, person.fullname)
                && Objects.equals(phone, person.phone)
                && Objects.equals(email, person.email)
                && Objects.equals(password, person.password)
                && role == person.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullname, phone, email, password, role);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    public static class builder {
        private final Person person = new Person();

        public builder id(long id) {
            person.setId(id);
            return this;
        }

        public builder fullname(String fullname) {
            person.setFullname(fullname);
            return this;
        }

        public builder phone(String phone) {
            person.setPhone(phone);
            return this;
        }

        public builder email(String email) {
            person.setEmail(email);
            return this;
        }

        public builder password(String password) {
            person.setPassword(password);
            return this;
        }

        public builder role(ServerRole role) {
            person.setRole(role);
            return this;
        }

        public builder credentials(Credentials credentials) {
            person.setEmail(credentials.getEmail());
            person.setPassword(credentials.getPassword());
            return this;
        }

        public Person build() {
            return person;
        }
    }
}