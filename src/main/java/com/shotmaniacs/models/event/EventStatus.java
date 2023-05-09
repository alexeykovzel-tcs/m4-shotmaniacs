package com.shotmaniacs.models.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum EventStatus {

    @JsonProperty("To Approve")
    TO_APPROVE,

    @JsonProperty("To Do")
    TODO,

    @JsonProperty("In Progress")
    IN_PROGRESS,

    @JsonProperty("Review")
    REVIEW,

    @JsonProperty("Done")
    DONE;

    public static EventStatus valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(EventStatus eventStatus) {
        return (eventStatus != null) ? eventStatus.name() : null;
    }
}