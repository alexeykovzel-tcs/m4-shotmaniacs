package com.shotmaniacs.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.models.Department;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ServerRole {

    @JsonProperty("Client")
    CLIENT,

    @JsonProperty("Admin")
    ADMIN,

    @JsonProperty("Crew")
    CREW;

    public static ServerRole valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(ServerRole serverRole) {
        return (serverRole != null) ? serverRole.name() : null;
    }
}