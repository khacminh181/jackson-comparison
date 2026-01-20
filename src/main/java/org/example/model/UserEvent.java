package org.example.model;

// UserEvent.java
public class UserEvent {
    public String userId;
    public String action;
    public long timestamp;
    public int score;

    public UserEvent() {}

    public UserEvent(String userId, String action, long timestamp, int score) {
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
        this.score = score;
    }
}
