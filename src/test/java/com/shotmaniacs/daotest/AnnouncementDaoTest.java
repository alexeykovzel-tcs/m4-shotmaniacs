package com.shotmaniacs.daotest;

import com.shotmaniacs.models.Announcement;
import com.shotmaniacs.models.Urgency;
import com.shotmaniacs.models.dto.AnnouncementDTO;
import com.shotmaniacs.models.user.CrewMember;
import com.shotmaniacs.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementDaoTest {
    private final List<Announcement> announcements = new ArrayList<>();
    private long uniqueId = 666;

    private AnnouncementDaoTest() {
        announcements.add(new Announcement(1, "Some title",
                "Donec arcu dui, lobortis non lacinia sit amet, scelerisque nec quam. Pellentesque vitae.",
                DateUtils.format("2022-01-01 16:00"), Urgency.LEVEL_1, null, 0L, 4));

        announcements.add(new Announcement(2, "Some title", "Donec arcu dui, lobortis non lacinia " +
                "sit amet, scelerisque nec quam. Pellentesque vitae elit nec nibh efficitur scelerisque. Pellentesque",
                DateUtils.format("2022-01-01 17:00"), Urgency.LEVEL_2, null, 0L, 4));
    }

    public static AnnouncementDaoTest getInstance() {
        return AnnouncementDaoTest.AnnouncementDaoTestHolder.instance;
    }

    public List<Announcement> getAll() {
        return announcements;
    }

    public long save(Announcement announcement) {
        long nextId = uniqueId;
        announcement.setId(nextId);
        announcements.add(announcement);
        uniqueId++;
        return nextId;
    }

    public boolean deleteById(int id) {
        return announcements.removeIf(a -> a.getId() == id);
    }


    public List<Announcement> getForCrewMember(CrewMember member) {
        List<Announcement> result = new ArrayList<>();
        if (member == null) return result;
        for (Announcement announcement : announcements) {
            boolean personal = announcement.getReceiverId() != 0;
            boolean forDepartment = announcement.getDepartment() != null;
            boolean forEveryone = !personal && !forDepartment;

            if (forEveryone || (forDepartment && announcement.getDepartment() == member.getDepartment())
                    || (personal && announcement.getReceiverId() == member.getId())) {
                result.add(announcement);
            }
        }
        return result;
    }

    public void edit(AnnouncementDTO dto) {
        for (Announcement announcement : announcements) {
            if (announcement.getId() == dto.getId()) {
                announcement.setTitle(dto.getTitle());
                announcement.setBody(dto.getBody());
                return;
            }
        }
    }

    private static final class AnnouncementDaoTestHolder {
        public static final AnnouncementDaoTest instance = new AnnouncementDaoTest();
    }
}