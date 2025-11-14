package com.campuscare.app.models;

import java.io.Serializable;

/**
 * Model class for Lost Item data
 * Represents a lost item report in the CampusCare system
 */
public class LostItem implements Serializable {
    
    private String id;
    private String registrationNumber;
    private String itemName;
    private String description;
    private String location;
    private String imageUrl;
    private String status;
    private String reportedDate;
    private long timestamp;
    
    // Registration details (extracted from registration number)
    private int year;
    private int semester;
    private String department;
    private int rollNumber;
    
    // Default constructor required for Firestore
    public LostItem() {
    }
    
    // Full constructor
    public LostItem(String id, String registrationNumber, String itemName, String description,
                   String location, String imageUrl, String status, String reportedDate,
                   long timestamp, int year, int semester, String department, int rollNumber) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.itemName = itemName;
        this.description = description;
        this.location = location;
        this.imageUrl = imageUrl;
        this.status = status;
        this.reportedDate = reportedDate;
        this.timestamp = timestamp;
        this.year = year;
        this.semester = semester;
        this.department = department;
        this.rollNumber = rollNumber;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReportedDate() {
        return reportedDate;
    }
    
    public void setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public int getSemester() {
        return semester;
    }
    
    public void setSemester(int semester) {
        this.semester = semester;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public int getRollNumber() {
        return rollNumber;
    }
    
    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }
    
    // Utility methods
    
    /**
     * Get formatted registration number for display
     */
    public String getFormattedRegistrationNumber() {
        if (registrationNumber != null && registrationNumber.length() >= 10) {
            return String.format("%s-%s-%s-%s",
                registrationNumber.substring(0, 4),    // Year
                registrationNumber.substring(4, 5),    // Semester
                registrationNumber.substring(5, registrationNumber.length() - 4), // Department
                registrationNumber.substring(registrationNumber.length() - 4)     // Roll number
            );
        }
        return registrationNumber;
    }
    
    /**
     * Get status color resource name
     */
    public String getStatusColorName() {
        switch (status.toLowerCase()) {
            case "lost":
                return "status_lost";
            case "found":
                return "status_found";
            case "claimed":
                return "status_claimed";
            default:
                return "text_secondary";
        }
    }
    
    /**
     * Check if item has an image
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }
    
    /**
     * Get display status (capitalized)
     */
    public String getDisplayStatus() {
        if (status == null) return "Unknown";
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }
    
    /**
     * Get short description (truncated if too long)
     */
    public String getShortDescription(int maxLength) {
        if (description == null) return "";
        if (description.length() <= maxLength) return description;
        return description.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Get academic year display (e.g., "2023-24")
     */
    public String getAcademicYearDisplay() {
        return year + "-" + String.valueOf(year + 1).substring(2);
    }
    
    @Override
    public String toString() {
        return "LostItem{" +
                "id='" + id + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", reportedDate='" + reportedDate + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        LostItem lostItem = (LostItem) o;
        
        return id != null ? id.equals(lostItem.id) : lostItem.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
