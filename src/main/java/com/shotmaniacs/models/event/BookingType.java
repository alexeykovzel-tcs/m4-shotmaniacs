package com.shotmaniacs.models.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shotmaniacs.utils.EnumUtils;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum BookingType {

    @JsonProperty("Photography")
    PHOTOGRAPHY,

    @JsonProperty("Film")
    FILM,

    @JsonProperty("Marketing")
    MARKETING,

    @JsonProperty("Other")
    OTHER;

    public static BookingType valueOfOrNull(String value) {
        return EnumUtils.valueOfOrNull(value, values());
    }

    public static String getValue(BookingType booking) {
        return (booking != null) ? booking.name() : null;
    }
}