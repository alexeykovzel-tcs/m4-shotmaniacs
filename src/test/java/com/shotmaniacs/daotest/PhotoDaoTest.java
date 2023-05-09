package com.shotmaniacs.daotest;

import com.shotmaniacs.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoDaoTest {
    private final List<Photo> photos = new ArrayList<>();

    private PhotoDaoTest() {
    }

    public List<Photo> getAll() {
        return photos;
    }

    public static PhotoDaoTest getInstance() {
        return PhotoDaoTestHolder.instance;
    }

    private final static class PhotoDaoTestHolder {
        private final static PhotoDaoTest instance = new PhotoDaoTest();
    }
}
