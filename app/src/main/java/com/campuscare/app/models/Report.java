package com.campuscare.app.models;

import java.util.Date;

public class Report {
    private String id;
    private String type; // "lost", "found", "repair", "replace"
    private String title;
    private String description;
    private String location;
    private String status; // "pending", "in_progress", "resolved"
    private Date date;
    private String rollNumber;
    private String imageUrl;
    private String category; // "lost_found" or "repair_replace"

    public Report() {
        this.date = new Date();
    }

    public Report(String type, String title, String description, String location, String rollNumber) {
        this();
        this.type = type;
        this.title = title;
        this.description = description;
        this.location = location;
        this.rollNumber = rollNumber;
        this.status = "pending";
        this.id = generateId();

        if ("lost".equals(type) || "found".equals(type)) {
            this.category = "lost_found";
        } else {
            this.category = "repair_replace";
        }
    }

    private String generateId() {
        return "RPT" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatusDisplayText() {
        switch (status) {
            case "pending": return "Pending";
            case "in_progress": return "In Progress";
            case "resolved": return "Resolved";
            default: return status;
        }
    }

    public String getTypeDisplayText() {
        switch (type) {
            case "lost": return "Lost";
            case "found": return "Found";
            case "repair": return "Repair";
            case "replace": return "Replace";
            default: return type;
        }
    }
}
