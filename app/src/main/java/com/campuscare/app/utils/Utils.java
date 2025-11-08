package com.campuscare.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern ROLL_NUMBER_PATTERN = Pattern.compile("^[A-Z]{3}\\d{7}$");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public static boolean isValidRollNumber(String rollNumber) {
        return rollNumber != null && ROLL_NUMBER_PATTERN.matcher(rollNumber).matches();
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public static int getStatusColor(String status) {
        switch (status) {
            case "pending":
                return android.graphics.Color.parseColor("#FFA500"); // Orange
            case "in_progress":
                return android.graphics.Color.parseColor("#2196F3"); // Blue
            case "resolved":
                return android.graphics.Color.parseColor("#4CAF50"); // Green
            default:
                return android.graphics.Color.parseColor("#9E9E9E"); // Gray
        }
    }

    public static int getTypeColor(String type) {
        switch (type) {
            case "lost":
            case "found":
                return android.graphics.Color.parseColor("#2196F3"); // Blue
            case "repair":
            case "replace":
                return android.graphics.Color.parseColor("#FF5722"); // Deep Orange
            default:
                return android.graphics.Color.parseColor("#9E9E9E"); // Gray
        }
    }
}
