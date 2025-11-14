package com.campuscare.app.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Registration Number Validator for CampusCare App
 * Validates registration numbers in the format: 20231CSE0503
 * Where:
 * - 2023 = Year (4 digits)
 * - 1 = Semester (1 digit, 1-8)
 * - CSE = Department code (2-4 letters)
 * - 0503 = Roll number (4 digits)
 */
public class RegistrationNumberValidator {
    
    // Regex pattern for registration number validation
    private static final String REGISTRATION_PATTERN = "^(\\d{4})(\\d{1})([A-Z]{2,4})(\\d{4})$";
    private static final Pattern pattern = Pattern.compile(REGISTRATION_PATTERN);
    
    // Valid department codes
    private static final String[] VALID_DEPARTMENTS = {
        "CSE", "ECE", "EEE", "MECH", "CIVIL", "IT", "AI", "DS", "CYBER", "IOT"
    };
    
    // Valid semesters (1-8)
    private static final int MIN_SEMESTER = 1;
    private static final int MAX_SEMESTER = 8;
    
    // Valid year range (adjust as needed)
    private static final int MIN_YEAR = 2020;
    private static final int MAX_YEAR = 2030;
    
    /**
     * Validates the complete registration number format and components
     * @param registrationNumber The registration number to validate
     * @return ValidationResult containing validation status and error message
     */
    public static ValidationResult validate(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            return new ValidationResult(false, "Registration number cannot be empty");
        }
        
        // Remove any whitespace and convert to uppercase
        registrationNumber = registrationNumber.trim().toUpperCase();
        
        // Check basic format
        Matcher matcher = pattern.matcher(registrationNumber);
        if (!matcher.matches()) {
            return new ValidationResult(false, "Invalid registration number format. Expected format: YYYYSDDDDNNNN (e.g., 20231CSE0503)");
        }
        
        // Extract components
        String yearStr = matcher.group(1);
        String semesterStr = matcher.group(2);
        String departmentCode = matcher.group(3);
        String rollNumberStr = matcher.group(4);
        
        // Validate year
        int year = Integer.parseInt(yearStr);
        if (year < MIN_YEAR || year > MAX_YEAR) {
            return new ValidationResult(false, 
                String.format("Invalid year %d. Year must be between %d and %d", year, MIN_YEAR, MAX_YEAR));
        }
        
        // Validate semester
        int semester = Integer.parseInt(semesterStr);
        if (semester < MIN_SEMESTER || semester > MAX_SEMESTER) {
            return new ValidationResult(false, 
                String.format("Invalid semester %d. Semester must be between %d and %d", semester, MIN_SEMESTER, MAX_SEMESTER));
        }
        
        // Validate department code
        boolean validDepartment = false;
        for (String dept : VALID_DEPARTMENTS) {
            if (dept.equals(departmentCode)) {
                validDepartment = true;
                break;
            }
        }
        
        if (!validDepartment) {
            return new ValidationResult(false, 
                String.format("Invalid department code '%s'. Valid departments: %s", 
                    departmentCode, String.join(", ", VALID_DEPARTMENTS)));
        }
        
        // Validate roll number (should not be 0000)
        int rollNumber = Integer.parseInt(rollNumberStr);
        if (rollNumber == 0) {
            return new ValidationResult(false, "Roll number cannot be 0000");
        }
        
        return new ValidationResult(true, "Valid registration number");
    }
    
    /**
     * Quick validation - checks only format without detailed component validation
     * @param registrationNumber The registration number to validate
     * @return true if format is correct, false otherwise
     */
    public static boolean isValidFormat(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(registrationNumber.trim().toUpperCase()).matches();
    }
    
    /**
     * Extracts year from registration number
     * @param registrationNumber Valid registration number
     * @return Year as integer, or -1 if invalid
     */
    public static int extractYear(String registrationNumber) {
        if (registrationNumber == null) return -1;
        Matcher matcher = pattern.matcher(registrationNumber.trim().toUpperCase());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
    
    /**
     * Extracts semester from registration number
     * @param registrationNumber Valid registration number
     * @return Semester as integer, or -1 if invalid
     */
    public static int extractSemester(String registrationNumber) {
        if (registrationNumber == null) return -1;
        Matcher matcher = pattern.matcher(registrationNumber.trim().toUpperCase());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(2));
        }
        return -1;
    }
    
    /**
     * Extracts department code from registration number
     * @param registrationNumber Valid registration number
     * @return Department code as string, or null if invalid
     */
    public static String extractDepartment(String registrationNumber) {
        if (registrationNumber == null) return null;
        Matcher matcher = pattern.matcher(registrationNumber.trim().toUpperCase());
        if (matcher.matches()) {
            return matcher.group(3);
        }
        return null;
    }
    
    /**
     * Extracts roll number from registration number
     * @param registrationNumber Valid registration number
     * @return Roll number as integer, or -1 if invalid
     */
    public static int extractRollNumber(String registrationNumber) {
        if (registrationNumber == null) return -1;
        Matcher matcher = pattern.matcher(registrationNumber.trim().toUpperCase());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(4));
        }
        return -1;
    }
    
    /**
     * Gets a formatted display version of the registration number
     * @param registrationNumber Valid registration number
     * @return Formatted string or original if invalid
     */
    public static String formatForDisplay(String registrationNumber) {
        if (registrationNumber == null) return "";
        Matcher matcher = pattern.matcher(registrationNumber.trim().toUpperCase());
        if (matcher.matches()) {
            return String.format("%s-%s-%s-%s", 
                matcher.group(1), 
                matcher.group(2), 
                matcher.group(3), 
                matcher.group(4));
        }
        return registrationNumber;
    }
    
    /**
     * Result class for validation with status and message
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String message;
        
        public ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("ValidationResult{isValid=%s, message='%s'}", isValid, message);
        }
    }
}
