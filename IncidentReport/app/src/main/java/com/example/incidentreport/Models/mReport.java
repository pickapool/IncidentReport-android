package com.example.incidentreport.Models;

public class mReport {
    String date;
    String uid;
    String userUid;
    String incident;
    String latitude;
    String longitude;
    String address;
    String contactPerson;
    String contactNumber;
    String reportPath;
    String status;
    String respondedBy;

    public mReport(){}

    public mReport(String date, String incident, String address, String contactPerson, String contactNumber, String status,String respondedBy) {
        this.date = date;
        this.incident = incident;
        this.address = address;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.status = status;
        this.respondedBy = respondedBy;
    }

    public mReport(String date, String uid, String userUid, String incident, String latitude, String longitude, String address, String contactPerson, String contactNumber, String reportPath, String status, String respondedBy) {
        this.date = date;
        this.uid = uid;
        this.userUid = userUid;
        this.incident = incident;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.reportPath = reportPath;
        this.status = status;
        this.respondedBy = respondedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getIncident() {
        return incident;
    }

    public void setIncident(String incident) {
        this.incident = incident;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRespondedBy() {
        return respondedBy;
    }

    public void setRespondedBy(String respondedBy) {
        this.respondedBy = respondedBy;
    }
}
