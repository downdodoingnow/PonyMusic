package me.wcy.music.model;

import com.google.gson.annotations.SerializedName;

public class RecommonedMusicResult {

    @SerializedName("result")
    private RecommonedMusicList result;

    public RecommonedMusicList getResult() {
        return result;
    }

    public void setResult(RecommonedMusicList result) {
        this.result = result;
    }
}
