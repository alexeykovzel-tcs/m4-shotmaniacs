package com.shotmaniacs.dao;

import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.dto.CrewMemberDTO;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.models.user.Person;
import com.shotmaniacs.models.user.ProjectRole;
import com.shotmaniacs.models.user.ServerRole;
import org.jvnet.hk2.annotations.Service;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

@Service
public class CrewDao extends PersonDao {
    private static final String CLASS_NAME = "CREW_MEMBER";
    private static final String ID = "id";
    private static final String AVAILABILITY = "AVAILABILITY";
    private static final String DEPARTMENT = "department_type";
    private static final String CLASS_PROJECT_ROLE = "ROLE_CREW";
    private static final String ID_CREW = "id_crew";
    private static final String PROJECT_ROLE = "project_role";

    /**
     * This methode gets all object from the CrewMember table.
     *
     * @return a list of all crewMember that are in the database.
     */
    public List<CrewMember> getAll() {
        return convertFromResultSetToCrew(getCrew(SERVER_ROLE, ServerRole.getValue(ServerRole.CREW)));
    }

    /**
     * This methode update the crewMember with the simplified crewDTO.
     */
    public boolean update(CrewMemberDTO dto) {
        long id = dto.getId();
        Object toSet;
        if (getById(id) != null) {
            if ((toSet = dto.getDepartment()) != null) {
                updateQuery(CLASS_NAME, DEPARTMENT, Department.getValue((Department) toSet), ID, id);
            }
            Person person = getById(id);
            for (ProjectRole r1 : ((CrewMember) person).getRoles()) {
                deleteQuery(CLASS_PROJECT_ROLE, ID_CREW, id, PROJECT_ROLE, ProjectRole.getValue(r1));
            }
            for (ProjectRole r2 : dto.getRoles()) {
                insertQuery(CLASS_PROJECT_ROLE, PROJECT_ROLE, ProjectRole.getValue(r2), ID_CREW, id);
            }
        }
        return false;
    }

    /**
     * This methode gets a crewMember by id.
     * It will always return one crewMember since id is unique number by the database.
     *
     * @param id is the id where we want the crewMember from.
     * @return a list of all crewMember that are in the database.
     */
    public CrewMember getCrewById(long id) {
        List<CrewMember> members = convertFromResultSetToCrew(getCrew(ID, id));
        if (members.size() == 0) return null;
        return members.get(0);
    }

    /**
     * This methode will check if a certain crewMember exist in the database or not.
     *
     * @param id of the crewMember, that needs to be checked.
     * @return boolean if the crewMember exist or not.
     */
    public boolean existsById(Long id) {
        List<CrewMember> crew = convertFromResultSetToCrew(getCrew(ID, id));
        return crew.size() != 0;
    }

    /**
     * This methode will help other methods in the class by not giving the need to type
     * classNamePerson
     *
     * @param attribute is the attribute name where you want to check on the sql statement.
     * @param value     is the value to compare with.
     * @return boolean if the crewMember exist or not.
     */
    private ResultSet getCrew(String attribute, Object value) {
        return getObject(CLASS_NAME_PERSON, attribute, value);
    }

    /**
     * This methode takes care of converting the resultSet after querying.
     *
     * @param resultSet the resultSet from the CrewMember class that needs to be converted to crewMember objects.
     * @return List<CrewMember> this methode returns a list of CrewMembers.
     */
    private List<CrewMember> convertFromResultSetToCrew(ResultSet resultSet) {
        if (resultSet == null) {
            return new ArrayList<>();
        }
        List<Person> list = convertFromResultSetToPerson(resultSet);
        List<CrewMember> result = new ArrayList<>();
        for (Person person : list) {
            if (person instanceof CrewMember) {
                result.add((CrewMember) person);
            }
            //otherwise do nothing, since it is not part of what need to know.
        }
        return result;
    }
}