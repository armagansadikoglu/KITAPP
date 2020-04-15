package com.armagansadikoglu.kitapp;

public class Notification {
    private String senderUID;
    private String senderName;
    private String message;

    public Notification(String senderUID, String senderName, String message) {
        this.senderUID = senderUID;
        this.senderName = senderName;
        this.message = message;
    }

    public Notification(){}

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
