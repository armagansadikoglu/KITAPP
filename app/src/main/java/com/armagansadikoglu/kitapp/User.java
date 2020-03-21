package com.armagansadikoglu.kitapp;

public class User {
    private String userID;
    private String userDisplayName;
    public User(){}
    public User(String userID, String userDisplayName) {
        this.userID = userID;
        this.userDisplayName = userDisplayName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }
}
