package me.wcy.music.model;

public class Praise {
    private long praiseID;
    private long userID;
    private String commonID;
    private long musicID;

    public long getPraiseID() {
        return praiseID;
    }

    public void setPraiseID(long praiseID) {
        this.praiseID = praiseID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getCommonID() {
        return commonID;
    }

    public void setCommonID(String commonID) {
        this.commonID = commonID;
    }

    public long getMusicID() {
        return musicID;
    }

    public void setMusicID(long musicID) {
        this.musicID = musicID;
    }
}
