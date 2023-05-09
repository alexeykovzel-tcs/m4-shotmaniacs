package com.shotmaniacs.dao;

import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.dto.PersonDTO;
import com.shotmaniacs.models.user.*;
import com.shotmaniacs.utils.PasswordHasher;
import org.jvnet.hk2.annotations.Service;

import javax.ws.rs.BadRequestException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PersonDao extends Dao {
    protected static final String CLASS_NAME_PERSON = "PERSON";
    protected static final String SERVER_ROLE = "server_role";

    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String NAME = "fullname";
    private static final String PHONE = "phone_no";
    private static final String PASSWORD = "password_hash";
    private static final String CLASS_NAME_CLIENT = "CLIENT";
    private static final String COMPANY = "company";
    private static final String CLASS_NAME_CREW = "CREW_MEMBER";
    private static final String AVAILABILITY = "AVAILABILITY";
    private static final String DEPARTMENT = "department_type";
    private static final String CLASS_NAME_PROJECT_ROLE = "ROLE_CREW";
    private static final String ID_CREW = "id_crew";
    private static final String PROJECT_ROLE = "project_role";

    /**
     * This methode returns a person if the id exist in the database otherwise null
     *
     * @param id is an id of a person.
     * @return the person if the person is found by id.
     */
    public Person getById(long id) {
        List<Person> result = convertFromResultSetToPerson(getPerson(ID, id));
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    /**
     * This methode returns a person according to the email.
     *
     * @param email is the email to compare with.
     * @return Person object with the same email address.
     */
    public Person getByEmail(String email) {
        List<Person> result = convertFromResultSetToPerson(getPerson(EMAIL, email));
        return (result != null && result.size() > 0) ? result.get(0) : null;
    }

    /**
     * This methode returns a list of persons with the same phone number.
     *
     * @param phone is the phone to compare with.
     * @return List<Person> with all persons with the same phone number.
     */
    public List<Person> getByPhone(String phone) {
        List<Person> result = convertFromResultSetToPerson(getPerson(PHONE, phone));
        if (result != null && result.size() > 0) {
            return result;
        }
        return null;
    }

    public ServerRole getRoleById(long id) {
        Person person = getById(id);
        if (person != null) {
            return person.getRole();
        }
        return null;
    }

    /**
     * This methode helps to trace back the id number of a client after inserting it in the database.
     * Since the database automatically assign unique numbers.
     *
     * @param previous the list that is smaller.
     * @param later    the list that is bigger and later made.
     * @return the id of the attribute that is not in previous but in later.
     */
    private long compareID(List<Person> previous, List<Person> later) {
        boolean dirty;
        long id;
        if (later.size() == 1) {
            return later.get(0).getId();
        }
        for (Person person : later) {
            id = person.getId();
            dirty = true;
            for (Person perso : previous) {
                dirty = dirty & perso.getId() != id;
            }
            if (dirty) {
                return id;
            }
        }
        return -1;
    }

    /**
     * This methode checks if the person exist by email.
     *
     * @param email to be checked in the database.
     * @return boolean if the email exist in the database.
     */
    public boolean existsByEmail(String email) {
        return (email != null) && ifExists(CLASS_NAME_PERSON, EMAIL, email);
    }

    /**
     * This methode saves the person in the database. It aswell makes sure it exist in the client or
     * crew accordingly.
     *
     * @param person to be saved.
     * @return boolean if the person is proper saved or not.
     * @throws BadRequestException if person == null or person.getRole() == null or
     *                             if the person already exist in the database in a certain way.
     */
    public boolean save(Person person) {
        boolean dirty = true;
        if (person == null) {
            throw new BadRequestException("Person can not be null");
        }
        if (person.getRole() == null) {
            throw new BadRequestException("Server role is unknown");
        }
        if (person.getRole().equals(ServerRole.CLIENT)) {
//            List<Person> personListPhone = getByPhone(person.getPhone());
            dirty = add(person);
//            if (dirty) {
//                long id = compareID(personListPhone, getByPhone(person.getPhone()));
//                if (person instanceof Client) {
//                    dirty = insertQuery(CLASS_NAME_CLIENT, ID, id, COMPANY, ((Client) person).getCompany());
//                } else {
//                    dirty = insertQuery(CLASS_NAME_CLIENT, ID, id);
//                }
//            }
        } else if (person.getRole().equals(ServerRole.CREW)) {
            add(person);
            long id = getIdByEmail(person.getEmail());
            if (person instanceof CrewMember) {
                CrewMember member = (CrewMember) person;
                dirty = insertQuery(CLASS_NAME_CREW,
                        ID, id,
                        AVAILABILITY, Availability.getValue(member.getAvailability()),
                        DEPARTMENT, Department.getValue(member.getDepartment()));

                if (dirty && member.getRoles() != null && member.getRoles().size() != 0) {
                    for (ProjectRole role : member.getRoles()) {
                        dirty = dirty & saveProjectRole(id, role);

                    }
                }
            } else {
                dirty = insertQuery(CLASS_NAME_CREW,
                        ID, id);

            }
        } else if (person.getRole().equals(ServerRole.ADMIN)) {
            add(person);
        } else {
            return false;
        }
        return dirty;
    }

    /**
     * This methode makes sure that the projectRoles from a CrewMember will be saved.
     * This methode helps the save() methode in this class.
     *
     * @param id          from the crewMember.
     * @param projectRole the roles from the crewMember.
     * @return boolean of it did succeed to pass the test.
     */
    private boolean saveProjectRole(long id, ProjectRole projectRole) {
        return insertQuery(CLASS_NAME_PROJECT_ROLE,
                ID_CREW, id,
                PROJECT_ROLE, ProjectRole.getValue(projectRole));
    }

    private boolean add(Person person) {
        if (person != null) {
            return insertQuery(CLASS_NAME_PERSON,
                    EMAIL, person.getEmail(),
                    NAME, person.getFullname(),
                    PHONE, person.getPhone(),
                    PASSWORD, person.getPassword(),
                    SERVER_ROLE, ServerRole.getValue(person.getRole()));
        }
        return false;
    }

    /**
     * Get the id from the emailAddress.
     *
     * @param email to compare with
     * @return id= -1 if the person is not found, id of the person if the person is found by email.
     */
    public long getIdByEmail(String email) {
        List<Person> personList = convertFromResultSetToPerson(getPerson(EMAIL, email));
        if (personList != null && personList.size() > 0) {
            return personList.get(0).getId();
        }
        return -1;
    }

    /**
     * Get the id by phone
     *
     * @param phone to compare with
     * @return id= -1 if the person is not found, id of the person if the person is found by phone.
     */
    private long getIdByPhone(String phone) {
        List<Person> personList = convertFromResultSetToPerson(getPerson(PHONE, phone));
        if (personList != null && personList.size() > 0) {
            return personList.get(0).getId();
        }
        return -1;
    }

    /**
     * This methode update the person by a simplified class.
     *
     * @param personDTO is the object to by updated.
     * @return boolean if the transaction did succeed or not.
     */
    public boolean update(PersonDTO personDTO) {
        long id = personDTO.getId();
        Object toSet;
        if (getById(id) != null) {
            if ((toSet = personDTO.getEmail()) != null) {
                updatePerson(EMAIL, toSet, id);
            }
            if ((toSet = personDTO.getFullname()) != null) {
                updatePerson(NAME, toSet, id);
            }
            if ((toSet = personDTO.getPhone()) != null) {
                updatePerson(PHONE, toSet, id);
            }
            if ((toSet = personDTO.getRole()) != null) {
                updatePerson(SERVER_ROLE, ServerRole.getValue((ServerRole) toSet), id);
            }
            return true;
        }
        return false;
    }

    /**
     * This methode update a person according the new Data.
     *
     * @param newUser is the user that will be updated the values.
     * @return boolean if the transaction did succeed or not.
     */
    public boolean update(Person newUser) {
        if (newUser == null) return false;
        long id = newUser.getId();
        Object toSet;
        for (Person user : getAll()) {
            if (id == user.getId()) {
                if ((toSet = newUser.getEmail()) != null) {
                    updatePerson(EMAIL, toSet, id);
                }
                if ((toSet = newUser.getFullname()) != null) {
                    updatePerson(NAME, toSet, id);
                }
                if ((toSet = newUser.getPhone()) != null) {
                    updatePerson(PHONE, toSet, id);
                }
                if (newUser.getPassword() != null) {
                    String hashedPassword = new PasswordHasher().hash(newUser.getPassword());
                    updatePerson(PASSWORD, hashedPassword, id);
                }
                if ((toSet = newUser.getRole()) != null) {
                    updatePerson(SERVER_ROLE, ServerRole.getValue((ServerRole) toSet), id);
                }
                if (newUser instanceof Client && Objects.equals(newUser.getRole(), ServerRole.CLIENT)) {
                    if ((toSet = ((Client) newUser).getCompany()) != null) {
                        updateClient(COMPANY, toSet, id);
                    }
                } else if (newUser instanceof CrewMember && Objects.equals(newUser.getRole(), ServerRole.CREW)) {
                    if ((toSet = ((CrewMember) newUser).getAvailability()) != null) {
                        updateCrew(AVAILABILITY, Availability.getValue((Availability) toSet), id);
                    }
                    if ((toSet = ((CrewMember) newUser).getDepartment()) != null) {
                        updateCrew(DEPARTMENT, Department.getValue((Department) toSet), id);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * This methode update a crewMember
     *
     * @param attributeSet the attribute name that needs to be changed
     * @param valueSet     the value towards the attribute need to be set
     * @param id           is the unique identifier for the crewMember.
     * @return boolean if the transaction did succeed.
     */
    private boolean updateCrew(String attributeSet, Object valueSet, long id) {
        return updateQuery(CLASS_NAME_CREW, attributeSet, valueSet, ID, id);
    }

    /**
     * This methode update a client
     *
     * @param attributeSet the attribute name that needs to be changed
     * @param valueSet     the value towards the attribute need to be set
     * @param id           is the unique identifier for the client.
     * @return boolean if the transaction did succeed.
     */
    protected boolean updateClient(String attributeSet, Object valueSet, long id) {
        return updateQuery(CLASS_NAME_CLIENT, attributeSet, valueSet, ID, id);
    }

    /**
     * This methode update a person
     *
     * @param attributeSet the attribute name that needs to be changed
     * @param valueSet     the value towards the attribute need to be set
     * @param id           is the unique identifier for the person.
     * @return boolean if the transaction did succeed.
     */
    protected boolean updatePerson(String attributeSet, Object valueSet, Long id) {
        return updateQuery(CLASS_NAME_PERSON, attributeSet, valueSet, ID, id);
    }

    /**
     * This methode delete an person by id from 1 class.
     *
     * @param className from which class the person needs to be deleted.
     * @param id        is the unique identifier for the person.
     * @return boolean if the transaction did succeed.
     */
    private boolean deleteById(long id, String className) {
        if (className == null) {
            return false;
        }
        if (className.equals(CLASS_NAME_PROJECT_ROLE)) {
            return deleteQuery(className, ID_CREW, id);
        }
        return deleteQuery(className, ID, id);
    }

    /**
     * This methode delete an person by id from all class, by using the methode deleteByPerson.
     *
     * @param id from the person to be delted.
     * @return boolean if the transaction did succeed.
     */
    public boolean deleteById(long id) {
        return (getById(id) != null) && deleteByPerson(getById(id));
    }

    /**
     * This methode delete an person by id from all classes where it exist.
     *
     * @param person is the person to be deleted.
     * @return boolean if the transaction did succeed.
     */
    public boolean deleteByPerson(Person person) {
        if (person != null) {
            long id = person.getId();
            if (id == -1) {
                if (person.getRole() != ServerRole.CLIENT) {
                    id = getIdByEmail(person.getEmail());
                } else {
                    id = getIdByPhone(person.getPhone());
                }
            }
            if (person.getRole().equals(ServerRole.CLIENT)) {
                return deleteById(id, CLASS_NAME_CLIENT) && deleteById(id, CLASS_NAME_PERSON);
            } else if (person.getRole().equals(ServerRole.ADMIN)) {
                return deleteById(id, CLASS_NAME_PERSON);
            } else if (person.getRole().equals(ServerRole.CREW)) {
                return deleteById(id, CLASS_NAME_PROJECT_ROLE)
                        && deleteById(id, CLASS_NAME_CREW)
                        && deleteById(id, CLASS_NAME_PERSON);
            }
        }
        return false;
    }

    /**
     * This methode gets all object that exist in the Person class.
     *
     * @return a list of all persons in the database
     */
    private List<Person> getAll() {
        return convertFromResultSetToPerson(getAll(CLASS_NAME_PERSON));
    }


    /**
     * This methode helps the other methods in this class.
     * This methode takes care of to get Persons according a demand (attribute).
     *
     * @param attribute is a name of a attribute in persons.
     * @param value     is a value that the attribute needs to have in this search statement.
     * @return a resultSet with all values that met the properties of attribute and value.
     */
    private ResultSet getPerson(String attribute, Object value) {
        return getObject(CLASS_NAME_PERSON, attribute, value);
    }

    /**
     * This methode helps to convert from a normal person attribute to a client object.
     *
     * @param id is the id of the client.
     * @return a String with the company name.
     */
    private String getCompanyByID(long id) {
        ResultSet resultSet = getAttribute(CLASS_NAME_CLIENT, COMPANY, ID, id);
        try {
            if (resultSet.next()) {
                return resultSet.getString(COMPANY);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * This methode helps to convert from a normal person attribute to a crewMember object.
     *
     * @param id is the id of the crewMember.
     * @return Availability of the crewMember.
     */
    private Availability getAvailabilityByID(long id) {
        ResultSet resultSet = getAttribute(CLASS_NAME_CREW, AVAILABILITY, ID, id);
        try {
            if (resultSet.next()) {
                return Availability.valueOfOrNull(resultSet.getString(AVAILABILITY));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This methode helps to convert from a normal person attribute to a crewMember object.
     *
     * @param id is the id of the crewMember.
     * @return Department of the crewMember.
     */
    private Department getDepartmentByID(long id) {
        ResultSet resultSet = getAttribute(CLASS_NAME_CREW, DEPARTMENT, ID, id);
        try {
            if (resultSet.next()) {
                return Department.valueOfOrNull(resultSet.getString(DEPARTMENT));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This methode helps to convert from a normal person attribute to a crewMember object.
     *
     * @param id is the id of the crewMember.
     * @return List<ProjectRole> with all roles the crewMember has.
     */
    private List<ProjectRole> getListProjectRole(long id) {
        return convertFromResultSetToProjectRole(getObject(CLASS_NAME_PROJECT_ROLE, ID_CREW, id));
    }

    /**
     * This methode helps to convert from a resultSet from ProjectRole to a List<ProjectRole>
     *
     * @param resultSet
     * @return List<ProjectRole> with all roles the crewMember has.
     */
    private List<ProjectRole> convertFromResultSetToProjectRole(ResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        try {
            List<ProjectRole> projectRoleList = new ArrayList<>();
            while (resultSet.next()) {
                projectRoleList.add(ProjectRole.valueOfOrNull(resultSet.getString(PROJECT_ROLE)));
            }
            return projectRoleList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This methode takes care of converting the resultSet after querying.
     *
     * @param resultSet the resultSet from the Person class that needs to be converted to Person Objects.
     * @return List<Person> this methode returns a list of Persons.
     */
    protected List<Person> convertFromResultSetToPerson(ResultSet resultSet) {
        List<Person> result = new ArrayList<>();
        if (resultSet == null) return result;
        try {
            List<Person> personArrayList = new ArrayList<>();
            while (resultSet.next()) {
                if (ServerRole.valueOfOrNull(resultSet.getString(SERVER_ROLE)).equals(ServerRole.CLIENT)) {
                    personArrayList.add(new Client(resultSet.getLong(ID),
                            resultSet.getString(NAME),
                            resultSet.getString(PHONE),
                            resultSet.getString(EMAIL),
                            resultSet.getString(PASSWORD),
                            getCompanyByID(resultSet.getLong(ID))));
                } else if (ServerRole.valueOfOrNull(resultSet.getString(SERVER_ROLE)).equals(ServerRole.CREW)) {
                    // Define project roles of a crew member
                    int id = resultSet.getInt(ID);
                    List<ProjectRole> roles = getListProjectRole(id);
                    // Define regular information about the person
                    Person person = new Person(resultSet.getLong(ID),
                            resultSet.getString(NAME),
                            resultSet.getString(PHONE),
                            resultSet.getString(EMAIL),
                            resultSet.getString(PASSWORD),
                            ServerRole.CREW);
                    personArrayList.add(new CrewMember(person, roles,
                            getAvailabilityByID(id),
                            getDepartmentByID(id))
                    );
                } else if (ServerRole.valueOfOrNull(resultSet.getString(SERVER_ROLE)).equals(ServerRole.ADMIN)) {
                    personArrayList.add(new Admin(
                            resultSet.getLong(ID),
                            resultSet.getString(NAME),
                            resultSet.getString(PHONE),
                            resultSet.getString(EMAIL),
                            resultSet.getString(PASSWORD)));
                }

            }
            return personArrayList;
        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
            return result;
        }
    }

    /**
     * This methode transport the person date in a simplified way for security reasons.
     *
     * @param userId is the id of the user.
     * @return an object personDTO
     */
    public Optional<PersonDTO> getDtoById(long userId) {
        Person person = getById(userId);
        PersonDTO personDTO = new PersonDTO(person.getId(), person.getEmail(),
                person.getFullname(), person.getPhone(), person.getRole());
        return Optional.of(personDTO);
    }
}