package me.wcy.music.model;

import java.io.Serializable;

public class Friend implements Serializable {
    private static final long serialVersionUID = 1;

    private int friendID;

    private long userID;

    private long friendUserID;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getFriendID() {
        return friendID;
    }

    public void setFriendID(int friendID) {
        this.friendID = friendID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getFriendUserID() {
        return friendUserID;
    }

    public void setFriendUserID(long friendUserID) {
        this.friendUserID = friendUserID;
    }
}
