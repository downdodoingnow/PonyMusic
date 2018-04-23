
package me.wcy.music.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import java.util.ArrayList;

import me.wcy.music.R;
import me.wcy.music.activity.LocalMusicActivity;
import me.wcy.music.activity.PlaylistActivity;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.RxBusTags;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.PermissionReq;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

/**
 * 本地音乐列表
 */
public class LocalMusicFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.local_music_num)
    TextView mLocalMusicNum;
    @Bind(R.id.download_music_num)
    TextView mDownloadMusicNum;
    @Bind(R.id.play_list_music_num)
    TextView mPlayListMusicNum;

    @Bind(R.id.local_music)
    RelativeLayout mLocalMusic;
    @Bind(R.id.download_music)
    RelativeLayout mDownloadMusic;
    @Bind(R.id.play_list_music)
    RelativeLayout mPlayListMusic;

    private ArrayList<Music> mMusicList;
    private ArrayList<Music> mDownloadMusicList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocalMusic.setOnClickListener(this);
        mDownloadMusic.setOnClickListener(this);
        mPlayListMusic.setOnClickListener(this);

        if (AppCache.get().getLocalMusicList().isEmpty()) {
            scanMusic(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayListMusicNum.setText("(" + AudioPlayer.get().getMusicList().size() + ")");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_music:
                Intent intent = new Intent(getContext(), LocalMusicActivity.class);
                intent.putExtra("music_list", mMusicList);
                intent.putExtra("type", "local");
                startActivity(intent);
                break;
            case R.id.download_music:
                Intent intentDownload = new Intent(getContext(), LocalMusicActivity.class);
                intentDownload.putExtra("download_list", mDownloadMusicList);
                intentDownload.putExtra("type", "download");
                startActivity(intentDownload);
                break;
            case R.id.play_list_music:
                Intent intentPlayList = new Intent(getContext(), PlaylistActivity.class);
                startActivity(intentPlayList);
                break;
            default:
                break;
        }
    }

    @Subscribe(tags = {@Tag(RxBusTags.SCAN_MUSIC)})
    public void scanMusic(Object object) {
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onGranted() {
                        new AsyncTask<Void, Void, ArrayList<Music>>() {
                            @Override
                            protected ArrayList<Music> doInBackground(Void... params) {
                                Log.i("RxBusTags", "开始扫描");
                                return MusicUtils.scanMusic(getContext());
                            }

                            @Override
                            protected void onPostExecute(ArrayList<Music> musicList) {
                                mMusicList = musicList;
                                mDownloadMusicList = MusicUtils.getDownloadMusicList();

                                mDownloadMusicNum.setText("(" + mDownloadMusicList.size() + ")");
                                mLocalMusicNum.setText("(" + mMusicList.size() + ")");
                            }
                        }.execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_storage);
                    }
                }).request();
    }
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        int position = lvLocalMusic.getFirstVisiblePosition();
//        int offset = (lvLocalMusic.getChildAt(0) == null) ? 0 : lvLocalMusic.getChildAt(0).getTop();
//        outState.putInt(Keys.LOCAL_MUSIC_POSITION, position);
//        outState.putInt(Keys.LOCAL_MUSIC_OFFSET, offset);
//    }
//
//    public void onRestoreInstanceState(final Bundle savedInstanceState) {
//        lvLocalMusic.post(() -> {
//            int position = savedInstanceState.getInt(Keys.LOCAL_MUSIC_POSITION);
//            int offset = savedInstanceState.getInt(Keys.LOCAL_MUSIC_OFFSET);
//            lvLocalMusic.setSelectionFromTop(position, offset);
//        });
//
//
//    }
}
