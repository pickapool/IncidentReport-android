package com.example.incidentreport.Models;

public class mNews {
    public String uid;
    public String title;
    public String body;


    public  mNews(){}
    public mNews(String uid, String title, String body) {
        this.uid = uid;
        this.title = title;
        this.body = body;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
