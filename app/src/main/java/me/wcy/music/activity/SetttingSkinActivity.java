package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.R;
import me.wcy.music.adapter.IInterface.IOnItemClick;
import me.wcy.music.adapter.SettingSkinAdapter;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class SetttingSkinActivity extends BaseActivity {
    @Bind(R.id.recycle_view_color)
    RecyclerView recyclerView;

    private SettingSkinAdapter mAdapter;
    private Integer[] mColorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settting_skin);
    }

    @Override
    protected void onServiceBound() {
        initColorData();

        mAdapter = new SettingSkinAdapter(this, mColorList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClick(new IOnItemClick() {
            @Override
            public void onItemClick(int position) {
                Preferences.setThemeId(position);
                recreate();
            }
        });
    }

    private void initColorData() {
        mColorList = new Integer[]{
                R.color.black, R.color.blue, R.color.purple, R.color.grey, R.color.pink, R.color.royal_blue,
                R.color.deep_sky_blue, R.color.light_green, R.color.orange, R.color.dark_slate_blue
        };
    }
}
