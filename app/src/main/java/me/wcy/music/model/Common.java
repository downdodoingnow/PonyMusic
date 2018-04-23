package me.wcy.music.model;

public class Common {
    private long CommonID;
    private long userID;
    private long musicID;
    private String content;
    private int praiseNum;
    private String time;

    private String username;

    private String musicName;
    private String musicArtist;

    public long getCommonID() {
        return CommonID;
    }

    public void setCommonID(long commonID) {
        CommonID = commonID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getMusicID() {
        return musicID;
    }

    public void setMusicID(long musicID) {
        this.musicID = musicID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public void setMusicArtist(String musicArtist) {
        this.musicArtist = musicArtist;
    }
}
