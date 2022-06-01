package com.example.incidentreport.Models;

public class mUserAccount {
    public String uid;
    public String fullName;
    public String address;
    public String contactNumber;
    public String email;
    public String password;
    public String idFilePath;
    public boolean isApprove;

    public mUserAccount(){}


    public mUserAccount(String uid, String fullName, String address, String contactNumber, String email, String password, String idFilePath, boolean isApprove) {
        this.uid = uid;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.password = password;
        this.idFilePath = idFilePath;
        this.isApprove = isApprove;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdFilePath() {
        return idFilePath;
    }

    public void setIdFilePath(String idFilePath) {
        this.idFilePath = idFilePath;
    }

    public boolean isApprove() {
        return isApprove;
    }

    public void setApprove(boolean approve) {
        isApprove = approve;
    }
}
