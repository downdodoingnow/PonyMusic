package me.wcy.music.model;

public class MusicUserSong {
    private String songName;
    private int index;

    public MusicUserSong(String songName, int index) {
        this.songName = songName;
        this.index = index;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
