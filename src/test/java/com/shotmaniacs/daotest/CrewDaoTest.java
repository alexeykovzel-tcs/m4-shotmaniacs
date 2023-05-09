package com.shotmaniacs.daotest;

import com.shotmaniacs.models.Department;
import com.shotmaniacs.models.dto.CrewMemberDTO;
import com.shotmaniacs.models.user.Availability;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.models.user.ProjectRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CrewDaoTest {
    private List<CrewMember> members = new ArrayList<>();

    private CrewDaoTest() {
        members.add(new CrewMember(5, "Test Crew 1", "+111", "crew1@gmail.com",
                "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", List.of(ProjectRole.PHOTOGRAPHER),
                Availability.AVAILABLE, Department.CLUB_PHOTOGRAPHY));

        members.add(new CrewMember(6, "Test Crew 2", "+222", "crew2@gmail.com",
                "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", List.of(ProjectRole.PRODUCTION, ProjectRole.PHOTOGRAPHER),
                Availability.AVAILABLE, Department.MARKETING));

        members.add(new CrewMember(7, "Test Crew 3", "+333", "crew3@gmail.com",
                "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=", List.of(ProjectRole.FILMMAKER),
                Availability.AVAILABLE, Department.EVENT_PHOTOGRAPHY));
    }

    public static CrewDaoTest getInstance() {
        return CrewDaoTestHolder.instance;
    }

    public boolean save(CrewMember member) {
        if (members.stream().anyMatch(m -> m.getId() == member.getId()
                || Objects.equals(m.getEmail(), member.getEmail()))) return false;
        members.add(member);
        return true;
    }

    public List<CrewMember> getAll() {
        return members;
    }

    public Optional<CrewMember> getById(long id) {
        return members.stream().filter(m -> m.getId() == id).findFirst();
    }

    public boolean existsById(long id) {
        return members.stream().anyMatch(m -> m.getId() == id);
    }

    public boolean deleteById(long id) {
        return members.removeIf(m -> m.getId() == id);
    }

    public void update(CrewMemberDTO dto) {
        for (CrewMember member : members) {
            if (member.getId() == dto.getId()) {
                member.setDepartment(dto.getDepartment());
                member.setRoles(dto.getRoles());
                return;
            }
        }
    }

    private final static class CrewDaoTestHolder {
        private final static CrewDaoTest instance = new CrewDaoTest();
    }
}