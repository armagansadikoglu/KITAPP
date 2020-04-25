package com.armagansadikoglu.kitapp;

public class Topic {
    private String topicName;
    private String topicDate;
    private String topicCreatorUID;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    //  users does not define no argument constructor [duplicate] hatası için boş constructor gerek
    public Topic() {
    }

    public Topic(String topicName, String topicDate, String topicCreatorUID) {
        this.topicName = topicName;
        this.topicDate = topicDate;
        this.topicCreatorUID = topicCreatorUID;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicDate() {
        return topicDate;
    }

    public void setTopicDate(String topicDate) {
        this.topicDate = topicDate;
    }

    public String getTopicCreatorUID() {
        return topicCreatorUID;
    }

    public void setTopicCreatorUID(String topicCreatorUID) {
        this.topicCreatorUID = topicCreatorUID;
    }
}
