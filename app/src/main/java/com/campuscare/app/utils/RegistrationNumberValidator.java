package com.campuscare.app.utils;

import java.util.regex.Pattern;

public class RegistrationNumberValidator {

    // Pattern for registration number: YYYYSCCCNNNN
    // YYYY = Year (4 digits)
    // S = Semester (1 digit: 1 or 2)
    // CCC = Department code (3 letters)
    // NNNN = Roll number (4 digits)
    private static final Pattern REGISTRATION_PATTERN =
            Pattern.compile("^\\d{4}[12][A-Z]{3}\\d{4}$");

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    public static ValidationResult validate(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            return new ValidationResult(false, "Registration number is required");
        }

        String regNum = registrationNumber.trim().toUpperCase();

        if (regNum.length() != 12) {
            return new ValidationResult(false, "Registration number must be 12 characters long");
        }

        if (!REGISTRATION_PATTERN.matcher(regNum).matches()) {
            return new ValidationResult(false, "Invalid format. Use: YYYYSCCCNNNN (e.g., 20231CSE0503)");
        }

        // Additional validation
        int year = Integer.parseInt(regNum.substring(0, 4));
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        if (year < 2020 || year > currentYear + 1) {
            return new ValidationResult(false, "Invalid year in registration number");
        }

        String deptCode = regNum.substring(5, 8);
        if (!isValidDepartmentCode(deptCode)) {
            return new ValidationResult(false, "Invalid department code: " + deptCode);
        }

        return new ValidationResult(true, "Valid registration number");
    }

    private static boolean isValidDepartmentCode(String deptCode) {
        String[] validDepts = {
                "CSE", "ECE", "EEE", "MEE", "CEE", "CHE", "AER", "BIO", "PHY", "MAT", "ENG", "MBA", "MCA"
        };

        for (String dept : validDepts) {
            if (dept.equals(deptCode)) {
                return true;
            }
        }
        return false;
    }

    public static String extractYear(String registrationNumber) {
        if (registrationNumber != null && registrationNumber.length() >= 4) {
            return registrationNumber.substring(0, 4);
        }
        return "";
    }

    public static String extractSemester(String registrationNumber) {
        if (registrationNumber != null && registrationNumber.length() >= 5) {
            return registrationNumber.substring(4, 5);
        }
        return "";
    }

    public static String extractDepartment(String registrationNumber) {
        if (registrationNumber != null && registrationNumber.length() >= 8) {
            return registrationNumber.substring(5, 8);
        }
        return "";
    }

    public static String extractRollNumber(String registrationNumber) {
        if (registrationNumber != null && registrationNumber.length() >= 12) {
            return registrationNumber.substring(8, 12);
        }
        return "";
    }

    public static String getDepartmentFullName(String deptCode) {
        switch (deptCode.toUpperCase()) {
            case "CSE": return "Computer Science & Engineering";
            case "ECE": return "Electronics & Communication Engineering";
            case "EEE": return "Electrical & Electronics Engineering";
            case "MEE": return "Mechanical Engineering";
            case "CEE": return "Civil Engineering";
            case "CHE": return "Chemical Engineering";
            case "AER": return "Aeronautical Engineering";
            case "BIO": return "Biotechnology";
            case "PHY": return "Physics";
            case "MAT": return "Mathematics";
            case "ENG": return "English";
            case "MBA": return "Master of Business Administration";
            case "MCA": return "Master of Computer Applications";
            default: return deptCode;
        }
    }
}
