package com.shotmaniacs.models.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum EventType {

    @JsonProperty("Wedding")
    WEDDING,

    @JsonProperty("Commercial")
    COMMERCIAL,

    @JsonProperty("Business")
    BUSINESS,

    @JsonProperty("Festival")
    FESTIVAL,

    @JsonProperty("Club")
    CLUB;

    public static EventType valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(EventType eventType) {
        return (eventType != null) ? eventType.name() : null;
    }
}