package me.wcy.music.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity
public class MusicUser implements Serializable {
    private static final long serialVersionUID = 536871010;
    @Property(nameInDb = "userID")
    private String userID;
    @Property(nameInDb = "songName")
    private String songName;
    @Property(nameInDb = "singer")
    private String singer;
    @Property(nameInDb = "count")
    private int count;

    @Generated(hash = 1718708696)
    public MusicUser(String userID, String songName, String singer, int count) {
        this.userID = userID;
        this.songName = songName;
        this.singer = singer;
        this.count = count;
    }

    @Generated(hash = 1450845591)
    public MusicUser() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "MusicUser{" +
                "userID='" + userID + '\'' +
                ", songName='" + songName + '\'' +
                ", singer='" + singer + '\'' +
                ", count=" + count +
                '}';
    }
}
