package com.shotmaniacs.models.dto;

import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.user.ProjectRole;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class CrewMemberDTO {

    private long id;
    private Department department;
    private List<ProjectRole> roles;

    public CrewMemberDTO(long id, Department department, List<ProjectRole> roles) {
        this.id = id;
        this.department = department;
        this.roles = roles;
    }

    public CrewMemberDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<ProjectRole> getRoles() {
        return roles;
    }

    public void setRoles(List<ProjectRole> roles) {
        this.roles = roles;
    }
}
