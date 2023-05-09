package com.shotmaniacs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages date formatting, converting, etc.
 */
public class DateUtils {
    public static final String FORMAT = "dd-MM-yyyy HH:mm";

    /**
     * Formats a string value to a date using the Dutch format.
     *
     * @param date given date as a string
     * @return formatted date instance
     */
    public static Date format(String date) {
        try {
            var formatter = new SimpleDateFormat(FORMAT);
            return formatter.parse(date);
        } catch (ParseException e) {
            System.out.println("[ERROR] Invalid date format: " + date);
            return null;
        }
    }

    /**
     * Calculates the number of days that passed after the given date.
     *
     * @param date given date instance
     * @return the number of days after this date
     */
    public static int getPassedDays(Date date) {
        long timePassed = System.currentTimeMillis() - date.getTime();
        return (int) (timePassed / 1000 / 60 / 60 / 24);
    }
}