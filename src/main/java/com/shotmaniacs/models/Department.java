package com.shotmaniacs.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Department {

    @JsonProperty("Event Photography")
    EVENT_PHOTOGRAPHY,

    @JsonProperty("Event Filmmaking")
    EVENT_FILMMAKING,

    @JsonProperty("Club Photography")
    CLUB_PHOTOGRAPHY,

    @JsonProperty("Marketing")
    MARKETING;

    public static Department valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(Department department) {
        return (department != null) ? department.name() : null;
    }
}