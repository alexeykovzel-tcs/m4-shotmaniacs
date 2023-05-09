package com.shotmaniacs.models.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum StatusPermission {

    @JsonProperty("Special")
    SPECIAL,

    @JsonProperty("Request")
    REQUEST,

    @JsonProperty("Approved")
    APPROVED,

    @JsonProperty("Canceled")
    CANCELED;

    public static StatusPermission valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(StatusPermission statusPermission) {
        return (statusPermission != null) ? statusPermission.name() : null;
    }
}