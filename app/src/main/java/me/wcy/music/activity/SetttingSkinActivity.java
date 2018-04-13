package me.wcy.music.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import me.wcy.music.R;
import me.wcy.music.adapter.IInterface.IOnItemClick;
import me.wcy.music.adapter.SettingSkinAdapter;
import me.wcy.music.storage.preference.Preferences;
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
                if (position != Preferences.getThemeId()) {
                    Preferences.setThemeId(position);
                    recreate();
                    SetttingSkinActivity.this.setResult(0);
                } else {
                    SetttingSkinActivity.this.setResult(1);
                }
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
