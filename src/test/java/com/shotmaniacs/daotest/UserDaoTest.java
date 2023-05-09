package com.shotmaniacs.daotest;

import com.shotmaniacs.models.dto.PersonDTO;
import com.shotmaniacs.models.user.Person;
import com.shotmaniacs.models.user.ServerRole;
import com.shotmaniacs.utils.PasswordHasher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoTest {
    private final List<Person> users = new ArrayList<>();

    private long uniqueId = 666;

    private UserDaoTest() {
        users.add(new Person(1, "Test Client 1", "+111", "client1@gmail.com",
                "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", ServerRole.CLIENT));
        users.add(new Person(2, "Test Client 2", "+222", "client2@gmail.com",
                "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", ServerRole.CLIENT));
        users.add(new Person(3, "Test Client 3", "+333", "client3@gmail.com",
                "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", ServerRole.CLIENT));
        users.add(new Person(4, "Test Admin", "+444", "admin@gmail.com",
                "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", ServerRole.ADMIN));
        users.add(new Person(5, "Test Crew", "+555", "crew@gmail.com",
                "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", ServerRole.CREW));
    }

    public static UserDaoTest getInstance() {
        return UserDaoTestHolder.instance;
    }

    public Optional<Person> getById(long id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    public Optional<Person> getByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    public ServerRole getRoleById(long id) {
        return getById(id).map(Person::getRole).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equals(email));
    }

    public boolean save(Person person) {
        uniqueId++;
        person.setId(uniqueId);
        return users.add(person);
    }

    public long getIdByEmail(String email) {
        return getByEmail(email).map(Person::getId).orElse(-1L);
    }

    public void update(Person person) {
        for (Person user : users) {
            if (person.getId() == user.getId()) {
                if (person.getEmail() != null) user.setEmail(person.getEmail());
                if (person.getFullname() != null) user.setFullname(person.getFullname());
                if (person.getPhone() != null) user.setPhone(person.getPhone());
                if (person.getPassword() != null) user.setPassword(new PasswordHasher().hash(person.getPassword()));
                break;
            }
        }
    }

    public boolean deleteById(long id) {
        return users.removeIf(u -> u.getId() == id);
    }

    public Optional<PersonDTO> getDtoById(long id) {
        Optional<Person> person = getById(id);
        if (person.isEmpty()) return Optional.empty();
        Person data = person.get();
        return Optional.of(new PersonDTO(
                data.getId(),
                data.getEmail(),
                data.getFullname(),
                data.getPhone(),
                data.getRole()
        ));
    }

    private final static class UserDaoTestHolder {
        private final static UserDaoTest instance = new UserDaoTest();
    }
}