package me.wcy.music.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.IInterface.OnMoreClickListener;
import me.wcy.music.adapter.PlaylistAdapter;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.Keys;
import me.wcy.music.constants.RequestCode;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.PermissionReq;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class LocalMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.lv_local_music)
    private ListView lvLocalMusic;
    @Bind(R.id.v_searching)
    private TextView vSearching;

    private PlaylistAdapter adapter;

    private LocalMusicActivity mContext;

    private ArrayList<Music> mMusicList;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
    }

    //初始化数据
    public void initListView() {
        AppCache.get().getLocalMusicList().clear();
        AppCache.get().getLocalMusicList().addAll(mMusicList);
        lvLocalMusic.setVisibility(View.VISIBLE);
        vSearching.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onServiceBound() {
        super.onServiceBound();

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (type.equals("local")) {
            mMusicList = (ArrayList<Music>) getIntent().getSerializableExtra("music_list");
        } else {
            mMusicList = (ArrayList<Music>) getIntent().getSerializableExtra("download_list");
        }

        adapter = new PlaylistAdapter(AppCache.get().getLocalMusicList());
        adapter.setOnMoreClickListener(this);
        lvLocalMusic.setAdapter(adapter);
        lvLocalMusic.setOnItemClickListener(this);
        mContext = this;

        if (null == mMusicList && AppCache.get().getLocalMusicList().isEmpty()) {
            scanMusic();
        } else {
            initListView();
        }
    }

    /**
     * 当在首页获取音乐数据失败后再次进行获取
     */
    public void scanMusic() {
        lvLocalMusic.setVisibility(View.GONE);
        vSearching.setVisibility(View.VISIBLE);
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onGranted() {
                        new AsyncTask<Void, Void, List<Music>>() {
                            @Override
                            protected List<Music> doInBackground(Void... params) {
                                return MusicUtils.scanMusic(mContext);
                            }

                            @Override
                            protected void onPostExecute(List<Music> musicList) {
                                mMusicList = MusicUtils.getDownloadMusicList();
                                AppCache.get().getLocalMusicList().clear();
                                AppCache.get().getLocalMusicList().addAll(mMusicList);
                                lvLocalMusic.setVisibility(View.VISIBLE);
                                vSearching.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }.execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_storage);
                        lvLocalMusic.setVisibility(View.VISIBLE);
                        vSearching.setVisibility(View.GONE);
                    }
                })
                .request();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Music music = AppCache.get().getLocalMusicList().get(position);
        AudioPlayer.get().addAndPlay(music);
        ToastUtils.show("已添加到播放列表");
    }

    @Override
    public void onMoreClick(final int position) {
        Music music = AppCache.get().getLocalMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(music.getTitle());
        dialog.setItems(R.array.local_music_dialog, (dialog1, which) -> {
            switch (which) {
                case 0:// 分享
                    shareMusic(music);
                    break;
                case 1:// 设为铃声
                    requestSetRingtone(music);
                    break;
                case 2:// 查看歌曲信息
                    MusicInfoActivity.start(mContext, music);
                    break;
                case 3:// 删除
                    deleteMusic(music);
                    break;
            }
        });
        dialog.show();
    }

    /**
     * 分享音乐
     */
    private void shareMusic(Music music) {
        File file = new File(music.getPath());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    private void requestSetRingtone(final Music music) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(mContext)) {
            ToastUtils.show(R.string.no_permission_setting);
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            startActivityForResult(intent, RequestCode.REQUEST_WRITE_SETTINGS);
        } else {
            setRingtone(music);
        }
    }

    /**
     * 设置铃声
     */
    private void setRingtone(Music music) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getPath());
        // 查询音乐文件在媒体库是否存在
        Cursor cursor = mContext.getContentResolver().query(uri, null,
                MediaStore.MediaColumns.DATA + "=?", new String[]{music.getPath()}, null);
        if (cursor == null) {
            return;
        }
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            values.put(MediaStore.Audio.Media.IS_PODCAST, false);

            mContext.getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?",
                    new String[]{music.getPath()});
            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE, newUri);
            ToastUtils.show(R.string.setting_ringtone_success);
        }
        cursor.close();
    }

    private void deleteMusic(final Music music) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, (dialog1, which) -> {
            File file = new File(music.getPath());
            if (file.delete()) {
                AppCache.get().getLocalMusicList().remove(music);
                adapter.notifyDataSetChanged();
                // 刷新媒体库
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
                mContext.sendBroadcast(intent);
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(mContext)) {
                ToastUtils.show(R.string.grant_permission_setting);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int position = lvLocalMusic.getFirstVisiblePosition();
        int offset = (lvLocalMusic.getChildAt(0) == null) ? 0 : lvLocalMusic.getChildAt(0).getTop();
        outState.putInt(Keys.LOCAL_MUSIC_POSITION, position);
        outState.putInt(Keys.LOCAL_MUSIC_OFFSET, offset);
    }

    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        lvLocalMusic.post(() -> {
            int position = savedInstanceState.getInt(Keys.LOCAL_MUSIC_POSITION);
            int offset = savedInstanceState.getInt(Keys.LOCAL_MUSIC_OFFSET);
            lvLocalMusic.setSelectionFromTop(position, offset);
        });
    }
}
