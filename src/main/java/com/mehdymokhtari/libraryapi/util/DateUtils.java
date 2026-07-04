package com.mehdymokhtari.libraryapi.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations.
 * Provides common date formatting and manipulation methods
 * used across the application.
 */
public final class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    // Formats a LocalDate to ISO format (yyyy-MM-dd)
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }


    // Formats a LocalDateTime to ISO format (yyyy-MM-dd'T'HH:mm:ss)
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    // Parses a date string in ISO format to LocalDate
    public static LocalDate parseDate(String date) {
        return date != null ? LocalDate.parse(date, DATE_FORMATTER) : null;
    }


    // Parses a datetime string in ISO format to LocalDateTime
    public static LocalDateTime parseDateTime(String dateTime) {
        return dateTime != null ? LocalDateTime.parse(dateTime, DATETIME_FORMATTER) : null;
    }


    // Returns the current date
    public static LocalDate today() {
        return LocalDate.now();
    }


    //  Returns the current datetime
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }


    // Checks if a given date is in the past
    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }


    // Checks if a given date is in the future
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }


    //  Checks if a given date is today
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }
}