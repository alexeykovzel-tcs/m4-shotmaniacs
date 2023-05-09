package com.shotmaniacs.utils;

/**
 * Manages enum converting, etc.
 */
public class EnumUtils {

    public static <T extends Enum<T>> T valueOfOrNull(String value, T[] values) {
        return valueOfOrDefault(value, values, null);
    }

    public static <T extends Enum<T>> T valueOfOrDefault(String value, T[] values, T defaultValue) {
        if (value == null) return defaultValue;
        String formattedValue = value.replace(" ", "_").toUpperCase();
        for (T status : values) {
            if (status.name().equals(formattedValue)) {
                return status;
            }
        }
        return defaultValue;
    }
}