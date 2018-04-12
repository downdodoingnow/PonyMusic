
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
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import java.util.ArrayList;

import me.wcy.music.R;
import me.wcy.music.activity.LocalMusicActivity;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.RxBusTags;
import me.wcy.music.model.Music;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.PermissionReq;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

/**
 * 本地音乐列表
 */
public class LocalMusicFragment extends BaseFragment implements View.OnClickListener {
    //    @Bind(R.id.lv_local_music)
//    private ListView lvLocalMusic;
//    @Bind(R.id.v_searching)
//    private TextView vSearching;
//
//    private PlaylistAdapter adapter;
    @Bind(R.id.local_music_num)
    TextView mLocalMusicNum;
    @Bind(R.id.download_music_num)
    TextView mDownloadMusicNum;
    @Bind(R.id.local_music)
    LinearLayout mLocalMusic;
    @Bind(R.id.download_music)
    LinearLayout mDownloadMusic;

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
//        adapter = new PlaylistAdapter(AppCache.get().getLocalMusicList());
//        adapter.setOnMoreClickListener(this);
//        lvLocalMusic.setAdapter(adapter);
        if (AppCache.get().getLocalMusicList().isEmpty()) {
            scanMusic(null);
        }
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
//    protected void setListener() {
//        lvLocalMusic.setOnItemClickListener(this);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Music music = AppCache.get().getLocalMusicList().get(position);
//        AudioPlayer.get().addAndPlay(music);
//        ToastUtils.show("已添加到播放列表");
//    }
//
//    @Override
//    public void onMoreClick(final int position) {
//        Music music = AppCache.get().getLocalMusicList().get(position);
//        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//        dialog.setTitle(music.getTitle());
//        dialog.setItems(R.array.local_music_dialog, (dialog1, which) -> {
//            switch (which) {
//                case 0:// 分享
//                    shareMusic(music);
//                    break;
//                case 1:// 设为铃声
//                    requestSetRingtone(music);
//                    break;
//                case 2:// 查看歌曲信息
//                    MusicInfoActivity.start(getContext(), music);
//                    break;
//                case 3:// 删除
//                    deleteMusic(music);
//                    break;
//            }
//        });
//        dialog.show();
//    }
//
//    /**
//     * 分享音乐
//     */
//    private void shareMusic(Music music) {
//        File file = new File(music.getPath());
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("audio/*");
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//        startActivity(Intent.createChooser(intent, getString(R.string.share)));
//    }
//
//    private void requestSetRingtone(final Music music) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getContext())) {
//            ToastUtils.show(R.string.no_permission_setting);
//            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
//            startActivityForResult(intent, RequestCode.REQUEST_WRITE_SETTINGS);
//        } else {
//            setRingtone(music);
//        }
//    }
//
//    /**
//     * 设置铃声
//     */
//    private void setRingtone(Music music) {
//        Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getPath());
//        // 查询音乐文件在媒体库是否存在
//        Cursor cursor = getContext().getContentResolver().query(uri, null,
//                MediaStore.MediaColumns.DATA + "=?", new String[]{music.getPath()}, null);
//        if (cursor == null) {
//            return;
//        }
//        if (cursor.moveToFirst() && cursor.getCount() > 0) {
//            String _id = cursor.getString(0);
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
//            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//            values.put(MediaStore.Audio.Media.IS_ALARM, false);
//            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
//            values.put(MediaStore.Audio.Media.IS_PODCAST, false);
//
//            getContext().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?",
//                    new String[]{music.getPath()});
//            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
//            RingtoneManager.setActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE, newUri);
//            ToastUtils.show(R.string.setting_ringtone_success);
//        }
//        cursor.close();
//    }
//
//    private void deleteMusic(final Music music) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//        String title = music.getTitle();
//        String msg = getString(R.string.delete_music, title);
//        dialog.setMessage(msg);
//        dialog.setPositiveButton(R.string.delete, (dialog1, which) -> {
//            File file = new File(music.getPath());
//            if (file.delete()) {
//                AppCache.get().getLocalMusicList().remove(music);
//                adapter.notifyDataSetChanged();
//                // 刷新媒体库
//                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
//                getContext().sendBroadcast(intent);
//            }
//        });
//        dialog.setNegativeButton(R.string.cancel, null);
//        dialog.show();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RequestCode.REQUEST_WRITE_SETTINGS) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(getContext())) {
//                ToastUtils.show(R.string.grant_permission_setting);
//            }
//        }
//    }
//
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
