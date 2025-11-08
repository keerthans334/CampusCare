package com.campuscare.app.data;

import com.campuscare.app.models.Report;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Report> reports;

    private DataManager() {
        reports = new ArrayList<>();
        initializeMockData();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void initializeMockData() {
        // Create mock data similar to the React app
        Report report1 = new Report("lost", "Lost iPhone 14 Pro",
                "Black iPhone 14 Pro with a blue case. Lost near the library.",
                "Main Library - 2nd Floor", "CSE2021045");
        report1.setStatus("pending");

        Report report2 = new Report("found", "Found Wallet",
                "Brown leather wallet found in the cafeteria. Contains student ID.",
                "Student Cafeteria", "ECE2020112");
        report2.setStatus("pending");

        Report report3 = new Report("repair", "Broken Projector in CSE Lab",
                "The projector in CSE Lab 301 is not turning on. Students unable to attend presentations.",
                "CSE Block - Lab 301", "CSE2019088");
        report3.setStatus("in_progress");

        Report report4 = new Report("replace", "Damaged Chair",
                "Chair leg is broken and unsafe to use.",
                "Library Block - Study Area 5", "MECH2020034");
        report4.setStatus("resolved");

        Report report5 = new Report("lost", "Lost Laptop Charger",
                "MacBook Pro charger, white color with some stickers.",
                "Engineering Block - Room 205", "EEE2021099");
        report5.setStatus("resolved");

        reports.add(report1);
        reports.add(report2);
        reports.add(report3);
        reports.add(report4);
        reports.add(report5);
    }

    public List<Report> getAllReports() {
        return new ArrayList<>(reports);
    }

    public List<Report> getReportsByCategory(String category) {
        List<Report> filteredReports = new ArrayList<>();
        for (Report report : reports) {
            if (category.equals("all") || category.equals(report.getCategory())) {
                filteredReports.add(report);
            }
        }
        return filteredReports;
    }

    public List<Report> getReportsByStatus(String status) {
        List<Report> filteredReports = new ArrayList<>();
        for (Report report : reports) {
            if (status.equals("all") || status.equals(report.getStatus())) {
                filteredReports.add(report);
            }
        }
        return filteredReports;
    }

    public void addReport(Report report) {
        reports.add(0, report); // Add to beginning
    }

    public Report getReportById(String id) {
        for (Report report : reports) {
            if (report.getId().equals(id)) {
                return report;
            }
        }
        return null;
    }

    public void updateReportStatus(String id, String newStatus) {
        Report report = getReportById(id);
        if (report != null) {
            report.setStatus(newStatus);
        }
    }

    public int getStatsCount(String type) {
        switch (type) {
            case "reunited":
                return 250;
            case "repairs":
                return 180;
            case "resolution":
                return 98;
            default:
                return 0;
        }
    }
}
