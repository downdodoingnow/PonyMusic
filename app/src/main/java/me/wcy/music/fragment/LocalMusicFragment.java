
package me.wcy.music.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.activity.LocalMusicActivity;
import me.wcy.music.activity.PlaylistActivity;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.RxBusTags;
import me.wcy.music.model.Music;
import me.wcy.music.model.MusicUser;
import me.wcy.music.model.MusicUserSong;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.storage.db.MusicUserDBManager;
import me.wcy.music.storage.db.greendao.MusicUserDao;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.PermissionReq;
import me.wcy.music.utils.ReptilianUtils;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;
import us.codecraft.webmagic.Spider;

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
    @Bind(R.id.recommend_music)
    RelativeLayout mRecommonedMusic;

    private ArrayList<Music> mMusicList = new ArrayList<>();
    private ArrayList<Music> mDownloadMusicList = new ArrayList<>();
    private ArrayList<MusicUser> mRecommondMusicList = new ArrayList<>();

    private ArrayList<MusicUser> mMusicUsers = new ArrayList<>();

    private MusicUserDao mMusicUserDao;
    private MyHandler mHandler;

    //用户歌曲歌曲评价余弦值
    private double[] mCosList;
    private ArrayList<String> mUserNames = new ArrayList<>();
    private ArrayList<MusicUserSong> mSongNames = new ArrayList<>();
    private double[] mScoreList;
    private int[][] mUserSongMatrix;
    //推荐歌曲数量
    private static final int NUM = 10;

    private final static String TAG = "LocalMusicFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMusicUserDao = MusicUserDBManager.getInstance().getmMusicUserDao();
        mHandler = new MyHandler();

        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocalMusic.setOnClickListener(this);
        mDownloadMusic.setOnClickListener(this);
        mPlayListMusic.setOnClickListener(this);
        mRecommonedMusic.setOnClickListener(this);

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
            case R.id.recommend_music:
                mRecommondMusicList = (ArrayList<MusicUser>) mMusicUserDao.queryBuilder().list();
                getRcommondMusicList();
                break;
            default:
                break;
        }
    }

    private void getRcommondMusicList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                if (null == mRecommondMusicList || mRecommondMusicList.size() == 0) {
                    Log.i(TAG, "getRcommondMusicList: 开始爬取数据");
                    //Spider.create(new ReptilianUtils()).addUrl("https://www.music.baidu.com/search?type=people").thread(5).run();
                    mMusicUsers = ReptilianUtils.mMusicUsers;

                    if (null != mMusicUsers && mMusicUsers.size() != 0) {
                        calculateCos();
                        insertMusicusersData();
                    }
                    Log.i(TAG, "getRcommondMusicList: " + (System.currentTimeMillis() - startTime));
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    /**
     * 计算余弦相似度，越小说明越相似
     */
    public void calculateCos() {
        getCommondMatrixFirst();
        getCommondMatrix();
        calculate();
        calculateSore();
        quickSort(mScoreList);
        getRecommondSong();
    }

    /**
     * 获取推荐列表
     */
    public void getRecommondSong() {
        for (int i = 0; i < NUM; i++) {
            mRecommondMusicList.add(mMusicUsers.get(mSongNames.get(i).getIndex()));
        }
    }

    /**
     * 计算每首哥的推荐评分
     */
    public void calculateSore() {
        mScoreList = new double[mCosList.length];
        //分子
        float moleculeValue = 0;
        //分母
        float denominator = 0;
        for (int i = 0; i < mCosList.length; i++) {
            denominator += Math.abs(mCosList[i]);
        }
        //计算出每一首歌的评分值
        for (int i = 0; i < mUserSongMatrix.length; i++) {//代表了列
            for (int j = 1; j < mUserSongMatrix[i].length; j++) {//代表了行
                moleculeValue += mCosList[i] * mUserSongMatrix[j][i];
            }
            //获取到每首歌曲的评分
            mScoreList[i] = moleculeValue / Math.sqrt(denominator);
        }
    }

    /**
     * 对获取到的余弦列表进行从小到大排序
     */
    public void quickSort(double[] list) {
        if (list.length > 0) {
            quickSort(list, 0, list.length - 1);
        }
    }

    private void quickSort(double[] list, int low, int high) {
        if (low > high) {
            return;
        }
        int i = low;
        int j = high;
        double key = list[low];
        while (i < j) {
            while (i < j && list[j] > key) {
                j--;
            }
            while (i < j && list[i] <= key) {
                i++;
            }
            if (i < j) {
                double p = list[i];
                list[i] = list[j];
                list[j] = p;

                MusicUserSong songNameP = mSongNames.get(i);
                mSongNames.set(i, mSongNames.get(j));
                mSongNames.set(j, songNameP);
            }
        }
        double p = list[i];
        list[i] = list[low];
        list[low] = p;

        MusicUserSong songNameP = mSongNames.get(i);
        mSongNames.set(i, mSongNames.get(j));
        mSongNames.set(j, songNameP);

        quickSort(list, low, i - 1);
        quickSort(list, i + 1, high);
    }

    /**
     * 计算相似度
     */
    public void calculate() {
        mCosList = new double[mUserSongMatrix.length - 1];
        //分子
        float moleculeValue = 0;
        //分母
        float denominator1 = 0;
        float denominator2 = 0;

        for (int i = 1; i < mUserSongMatrix.length; i++) {
            for (int j = 0; j < mUserSongMatrix[i].length; j++) {
                moleculeValue += mUserSongMatrix[0][j] * mUserSongMatrix[i][j];
                denominator1 += Math.pow(mUserSongMatrix[0][j], 2);
                denominator2 += Math.pow(mUserSongMatrix[i][j], 2);
            }
            mCosList[i - 1] = (moleculeValue / (Math.sqrt(denominator1) * Math.sqrt(denominator2)));
        }
    }

    /**
     * 形成评价矩阵
     */
    public void getCommondMatrix() {
        MusicUser user;
        for (int i = 0; i < mMusicUsers.size(); i++) {
            user = mMusicUsers.get(i);
            int userIndex = mUserNames.indexOf(user.getUserID());
            int songIndex = mSongNames.indexOf(user.getSongName());

            mUserSongMatrix[userIndex][songIndex] = user.getCount();
        }
    }

    /**
     * 获取评价矩阵第一行第一列
     */
    public void getCommondMatrixFirst() {
        MusicUser user;
        boolean isHasUser = false;
        boolean isHasSong = false;
        //将user和song分别存储不同的数据结构，方便在构建矩阵的时候进行对应
        for (int i = 0; i < mMusicUsers.size(); i++) {
            user = mMusicUsers.get(i);
            for (int j = 0; j < mUserNames.size(); j++) {
                if (user.getUserID().equals(mUserNames.get(i))) {
                    isHasUser = true;
                    break;
                }
            }
            for (int k = 0; k < mUserNames.size(); k++) {
                if (user.getUserID().equals(mSongNames.get(k))) {
                    isHasSong = true;
                    break;
                }
            }
            if (!isHasUser) {
                mUserNames.add(user.getUserID());
            }
            if (!isHasSong) {
                mSongNames.add(new MusicUserSong(user.getSongName(), i));
            }
        }

        mUserSongMatrix = new int[mUserNames.size()][mSongNames.size()];
    }

    public void insertMusicusersData() {
        for (int i = 0; i < mRecommondMusicList.size(); i++) {
            mMusicUserDao.insert(mRecommondMusicList.get(i));
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

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intentRecommoned = new Intent(getContext(), LocalMusicActivity.class);
            intentRecommoned.putExtra("recommoned_list", mRecommondMusicList);
            intentRecommoned.putExtra("type", "recommoned");
            LocalMusicFragment.this.startActivity(intentRecommoned);
        }
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
