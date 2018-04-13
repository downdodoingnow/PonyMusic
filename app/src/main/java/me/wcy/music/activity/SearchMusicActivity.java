package me.wcy.music.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.HistorySearchAdapter;
import me.wcy.music.adapter.IInterface.IOnItemClick;
import me.wcy.music.adapter.IInterface.OnMoreClickListener;
import me.wcy.music.adapter.SearchMusicAdapter;
import me.wcy.music.enums.LoadStateEnum;
import me.wcy.music.executor.DownloadSearchedMusic;
import me.wcy.music.executor.PlaySearchedMusic;
import me.wcy.music.executor.ShareOnlineMusic;
import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.HistorySearch;
import me.wcy.music.model.Music;
import me.wcy.music.model.SearchMusic;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.storage.db.HistorySearchDBManager;
import me.wcy.music.storage.db.greendao.HistorySearchDao;
import me.wcy.music.utils.AlertDialogUtils;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.ViewUtils;
import me.wcy.music.utils.binding.Bind;

public class SearchMusicActivity extends BaseActivity implements SearchView.OnQueryTextListener
        , AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.lv_search_music_list)
    private ListView lvSearchMusic;
    @Bind(R.id.history_list)
    private RecyclerView historyList;
    @Bind(R.id.search_history_ll)
    private LinearLayout searchHistoryll;
    @Bind(R.id.ll_loading)
    private LinearLayout llLoading;
    @Bind(R.id.ll_load_fail)
    private LinearLayout llLoadFail;
    private List<SearchMusic.Song> searchMusicList = new ArrayList<>();
    private SearchMusicAdapter mAdapter = new SearchMusicAdapter(searchMusicList);

    private List<HistorySearch> mData;
    private HistorySearchAdapter mHistorySearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
    }

    @Override
    protected void onServiceBound() {
        lvSearchMusic.setAdapter(mAdapter);
        TextView tvLoadFail = llLoadFail.findViewById(R.id.tv_load_fail_text);
        tvLoadFail.setText(R.string.search_empty);

        lvSearchMusic.setOnItemClickListener(this);
        mAdapter.setOnMoreClickListener(this);

        initHistory();
    }

    private void initHistory() {
        mData = HistorySearchDBManager.getInstance().getmHistorySearchDao().queryBuilder().list();
        if (0 != mData.size()) {
            searchHistoryll.setVisibility(View.VISIBLE);

            //让数据倒序排列
            Collections.reverse(mData);
            mHistorySearchAdapter = new HistorySearchAdapter(this, mData, searchHistoryll);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(OrientationHelper.VERTICAL);
            historyList.setLayoutManager(manager);
            historyList.setAdapter(mHistorySearchAdapter);
            //增加条目或者删除条目的动画效果
            historyList.setItemAnimator(new DefaultItemAnimator());

            mHistorySearchAdapter.setOnItemClick(new IOnItemClick() {
                @Override
                public void onItemClick(int position) {
                    searchMusic(mData.get(position).getName());
                }
            });
        }
    }

    public void deleteHistoryClick(View view) {

        new AlertDialogUtils(this, new AlertDialogUtils.IConfirmCallBack() {
            @Override
            public void operate(DialogInterface dialog) {
                HistorySearchDBManager.getInstance().getmHistorySearchDao().deleteAll();
                dialog.dismiss();
                mData.clear();
                mHistorySearchAdapter.notifyDataSetChanged();
                searchHistoryll.setVisibility(View.GONE);
            }
        }).build(R.string.is_delete_all);
    }

    @Override
    protected int getDarkTheme() {
        return R.style.AppThemeDark_Search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_music, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.onActionViewExpanded();
        searchView.setQueryHint(getString(R.string.search_tips));
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        try {
            Field field = searchView.getClass().getDeclaredField("mGoButton");
            field.setAccessible(true);
            ImageView mGoButton = (ImageView) field.get(searchView);
            mGoButton.setImageResource(R.drawable.ic_menu_search);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);
        searchMusic(query);
        addHistoryToDatabase(query);
        return false;
    }

    private void addHistoryToDatabase(String name) {
        HistorySearch historySearch = new HistorySearch();
        historySearch.setName(name);
        HistorySearchDao historySearchDao = HistorySearchDBManager.getInstance().getmHistorySearchDao();
        List<HistorySearch> data = historySearchDao.queryBuilder().where(HistorySearchDao.Properties.Name.eq(name)).list();
        //防止用户搜索已经搜索的内容而重复显示
        if (data.size() == 0) {
            HistorySearchDBManager.getInstance().getmHistorySearchDao().insert(historySearch);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void searchMusic(String keyword) {
        //隐藏搜索历史
        searchHistoryll.setVisibility(View.GONE);

        HttpClient.searchMusic(keyword, new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(SearchMusic response) {
                if (response == null || response.getSong() == null) {
                    ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    return;
                }
                ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);

                searchMusicList.clear();
                searchMusicList.addAll(response.getSong());
                mAdapter.notifyDataSetChanged();
                lvSearchMusic.requestFocus();
                handler.post(() -> lvSearchMusic.setSelection(0));
            }

            @Override
            public void onFail(Exception e) {
                ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new PlaySearchedMusic(this, searchMusicList.get(position)) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Music music) {
                cancelProgress();
                AudioPlayer.get().addAndPlay(music);
                ToastUtils.show("已添加到播放列表");
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
                ToastUtils.show(R.string.unable_to_play);
            }
        }.execute();
    }

    @Override
    public void onMoreClick(int position) {
        final SearchMusic.Song song = searchMusicList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(song.getSongname());
        String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(song.getArtistname(), song.getSongname());
        File file = new File(path);
        int itemsId = file.exists() ? R.array.search_music_dialog_no_download : R.array.search_music_dialog;
        dialog.setItems(itemsId, (dialog1, which) -> {
            switch (which) {
                case 0:// 分享
                    share(song);
                    break;
                case 1:// 下载
                    download(song);
                    break;
            }
        });
        dialog.show();
    }

    private void share(SearchMusic.Song song) {
        new ShareOnlineMusic(this, song.getSongname(), song.getSongid()) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
            }
        }.execute();
    }

    private void download(final SearchMusic.Song song) {
        new DownloadSearchedMusic(this, song) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
                ToastUtils.show(getString(R.string.now_download, song.getSongname()));
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
                ToastUtils.show(R.string.unable_to_download);
            }
        }.execute();
    }
}
