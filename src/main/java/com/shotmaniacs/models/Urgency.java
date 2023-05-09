package com.shotmaniacs.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Urgency {

    @JsonProperty("LEVEL_1")
    LEVEL_1,

    @JsonProperty("LEVEL_2")
    LEVEL_2,

    @JsonProperty("LEVEL_3")
    LEVEL_3;

    public static Urgency valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(Urgency urgency) {
        return (urgency != null) ? urgency.name() : null;
    }
}
