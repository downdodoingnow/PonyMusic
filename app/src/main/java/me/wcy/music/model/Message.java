package me.wcy.music.model;

public class Message {
    private String message;
    private int messageID;
    private long time;
    private long toUserID;
    private long userID;

    private int type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getToUserID() {
        return toUserID;
    }

    public void setToUserID(long toUserID) {
        this.toUserID = toUserID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
