package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;

import me.wcy.music.IView.IPraiseView;
import me.wcy.music.IView.IUserView;
import me.wcy.music.R;
import me.wcy.music.adapter.CommonAdapter;
import me.wcy.music.adapter.IInterface.IOnItemClick;
import me.wcy.music.adapter.IInterface.OnPraiseClick;
import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.ArtistInfo;
import me.wcy.music.model.Common;
import me.wcy.music.model.Params;
import me.wcy.music.model.Praise;
import me.wcy.music.model.User;
import me.wcy.music.presenter.CommonP;
import me.wcy.music.presenter.PraiseP;
import me.wcy.music.storage.db.UserManger;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class CommonActivity extends BaseActivity implements IUserView, IPraiseView, View.OnClickListener {

    private static final String SELECT = "select";
    private static final String UPDATE = "insert";
    private String praiseWay;

    @Bind(R.id.common_list)
    RecyclerView mCommonList;
    @Bind(R.id.ll_loading)
    LinearLayout mLlLoading;
    @Bind(R.id.ll_load_fail)
    LinearLayout mLLLoadingFail;
    @Bind(R.id.tv_load_fail_text)
    TextView tvFail;
    @Bind(R.id.publish_common)
    TextView tvPulishCommon;

    private List<Common> mData = new ArrayList<>();
    private CommonAdapter adapter;

    private String mMusicName;
    private String mMusicArtist;
    private String common;

    private long userID;
    private boolean isCommon;
    private Common common1;
    private ArrayList<Boolean> isPraised = new ArrayList<>();
    private ArrayList<Praise> praises = new ArrayList<>();
    private int praisePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
    }

    @Override
    protected void onServiceBound() {
        initRecycleView();
        userID = UserManger.getInstance().getmUserDao().queryBuilder().list().get(0).getUserID();

        mMusicName = getIntent().getStringExtra("musicName");
        mMusicArtist = getIntent().getStringExtra("musicArtist");
        setTitle(mMusicName);
        getData();
        tvPulishCommon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PublishCommonActivity.class);
        startActivityForResult(intent, 0);
    }

    private void getData() {
        mLlLoading.setVisibility(View.VISIBLE);

        CommonP commonP = new CommonP(this);
        Params musicName = new Params("musicName", mMusicName);
        Params musicArtist = new Params("musicArtist", mMusicArtist);

        commonP.select(new Params[]{musicName, musicArtist});
    }

    private void getPraiseData(long musicID) {
        PraiseP praiseP = new PraiseP(this);
        praiseP.select(new Params("musicID", musicID + ""));
        praiseWay = "select";
    }

    private void insertPraise(int position) {
        PraiseP praiseP = new PraiseP(this);

        Common common = mData.get(position);
        Praise praise = new Praise();
        praise.setMusicID(common.getMusicID());
        praise.setCommonID(common.getCommonID());
        praise.setUserID(userID);

        praiseP.insert(new Params("praise", new Gson().toJson(praise)));

        praiseWay = "insert";
    }

    @Override
    public void praiseResult(String result, Exception e) {
        if (null != result && !"操作失败,请重试！".equals(result) && null == e) {
            if (praiseWay.equals(SELECT)) {
                convertPraiseData(result);
            } else {
                if (Integer.parseInt(result) < 0) {
                    ToastUtils.show("点赞失败");
                } else {
                    Common common = mData.get(praisePosition);
                    common.setPraiseNum(common.getPraiseNum() + 1);
                    common.setPraiseType(1);
                    adapter.notifyItemChanged(praisePosition);

                    isPraised.set(praisePosition, true);
                }
            }
        } else {
            if (praiseWay.equals(SELECT)) {
                ToastUtils.show("获取数据失败");
            } else {
                ToastUtils.show("点赞失败");
            }
        }
    }

    private void convertPraiseData(String result) {
        try {
            JSONArray jaa = new JSONArray(result);

            for (int i = 0; i < jaa.length(); i++) {
                Gson gson = new Gson();
                Praise praise = gson.fromJson(jaa.get(i).toString(), Praise.class);
                praises.add(praise);
            }
            setPraiseType();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    private void setPraiseType() {
        for (int i = 0; i < mData.size(); i++) {
            Common common = mData.get(i);
            for (int j = 0; j < praises.size(); j++) {
                Praise praise = praises.get(j);
                if (userID == praise.getUserID() && common.getCommonID().equals(praise.getCommonID())) {
                    common.setPraiseType(1);
                    isPraised.set(i, true);
                }
            }
        }
    }

    @Override
    public void result(String result, Exception e) {
        mLlLoading.setVisibility(View.GONE);
        if (!isCommon) {
            if (null == result || "操作失败,请重试！".equals(result) || null != e) {
                tvFail.setText("加载失败");
                mLLLoadingFail.setVisibility(View.VISIBLE);
            } else if (result.length() == 2) {
                setLoadingFaile(true);
            } else {
                conversionData(result);
            }
        } else {
            if (null != result && !"操作失败,请重试！".equals(result)) {
                updateCommon(result);
            } else {
                ToastUtils.show("评论失败");
            }
        }
    }

    private void setLoadingFaile(boolean isShow) {
        if (isShow) {
            tvFail.setText("暂无评论");
            mLLLoadingFail.setVisibility(View.VISIBLE);
        } else {
            mLLLoadingFail.setVisibility(View.GONE);
        }
    }

    private void conversionData(String result) {
        try {
            JSONArray jaa = new JSONArray(result);

            for (int i = 0; i < jaa.length(); i++) {
                Gson gson = new Gson();
                Common common = gson.fromJson(jaa.get(i).toString(), Common.class);
                mData.add(common);
                isPraised.add(false);
            }
            getPraiseData(mData.get(0).getMusicID());

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void updateCommon(String result) {
        setLoadingFaile(false);

        mData.add(0, new Gson().fromJson(result, Common.class));
        isPraised.add(0, false);

        adapter.notifyItemChanged(0);
    }

    private void initRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mCommonList.setLayoutManager(linearLayoutManager);
        adapter = new CommonAdapter(this, mData);
        mCommonList.setAdapter(adapter);
        adapter.setOnPraiseClick(new OnPraiseClick() {
            @Override
            public void onPraiseClick(ImageView imageView, TextView textView, int position) {
                if (!isPraised.get(position)) {
                    praisePosition = position;
                    insertPraise(position);
                } else {
                    ToastUtils.show("您已经点过赞了");
                }

            }
        });

    }

    public void insertCommon() {
        common1 = new Common();
        User user = UserManger.getInstance().getmUserDao().queryBuilder().list().get(0);

        common1.setContent(common);
        common1.setCommonID(System.currentTimeMillis() + "");
        common1.setUserID(user.getUserID());
        common1.setUsername(user.getUserName());
        common1.setMusicArtist(mMusicArtist);
        common1.setMusicName(mMusicName);
        common1.setTime(getTime());

        insertCommonToDataBase(common1);
    }

    public void insertCommonToDataBase(Common common1) {
        JsonElement jsonElement = new Gson().toJsonTree(common1, Common.class);
        JsonObject joo = jsonElement.getAsJsonObject();

        new CommonP(this).insert(new Params("common", joo.toString()));
    }

    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + isSingleDigit(month) + "-" + isSingleDigit(dayOfMonth);
    }

    private String isSingleDigit(int number) {
        if (number <= 9) {
            return "0" + number;
        } else {
            return number + "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (null != data) {
                    common = data.getStringExtra("common");
                    insertCommon();
                    isCommon = true;
                }
                break;
            default:
        }

    }
}
