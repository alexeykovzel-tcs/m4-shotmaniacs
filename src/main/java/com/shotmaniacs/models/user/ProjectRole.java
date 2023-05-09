package com.shotmaniacs.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ProjectRole {

    @JsonProperty("Filmmaker")
    FILMMAKER,

    @JsonProperty("Photographer")
    PHOTOGRAPHER,

    @JsonProperty("Production")
    PRODUCTION;

    public static ProjectRole valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(ProjectRole projectRole) {
        return (projectRole != null) ? projectRole.name() : null;
    }
}