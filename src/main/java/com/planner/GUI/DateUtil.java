package com.planner.GUI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateUtil {

    private static final DateTimeFormatter UI_FORMAT =

            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter DB_FORMAT =
            DateTimeFormatter.ofPattern("dd_MM_yyyy");

    public static String formatForUI(LocalDate date) {
        if (date == null) return "";  // ← add karo
        return date.format(UI_FORMAT);
    }

    public static String formatForDB(LocalDate date) {
        if (date == null) return "";  // ← add karo
        return date.format(DB_FORMAT);
    }

    // Parse flexible input
    public static LocalDate parse(String dateStr) {

        List<DateTimeFormatter> formats = List.of(
                DateTimeFormatter.ofPattern("dd_MM_yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy_MM_dd")
        );

        for (DateTimeFormatter f : formats) {
            try {
                return LocalDate.parse(dateStr, f);
            } catch (Exception ignored) {}
        }

        throw new RuntimeException("Invalid date: " + dateStr);
    }
}