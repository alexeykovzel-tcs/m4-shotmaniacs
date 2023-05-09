package com.shotmaniacs.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Availability {

    @JsonProperty("Vacation")
    VACATION,

    @JsonProperty("Available")
    AVAILABLE,

    @JsonProperty("Retired")
    RETIRED;

    public static Availability valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(Availability availability) {
        return (availability != null) ? availability.name() : null;
    }
}