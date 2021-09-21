package cargotracker.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateConverter {
    public static final String DATE_FORMAT = "M/d/yyyy";
    public static final String DATE_TIME_FORMAT = "M/d/yyyy HH:mm";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH).withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_FORMAT, Locale.ENGLISH).withZone(ZoneId.systemDefault());

    private DateConverter() {
    }

    public static LocalDateTime toDateTime(String datetime) {
        return LocalDateTime.parse(datetime, DATE_TIME_FORMATTER);
    }

    public static String toString(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String toString(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}
