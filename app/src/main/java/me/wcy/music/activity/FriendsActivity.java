package me.wcy.music.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.IView.IUserView;
import me.wcy.music.R;
import me.wcy.music.adapter.FriendAdapter;
import me.wcy.music.adapter.IInterface.IOnItemClick;
import me.wcy.music.adapter.IInterface.OnMoreClickListener;
import me.wcy.music.model.Friend;
import me.wcy.music.model.Params;
import me.wcy.music.model.User;
import me.wcy.music.presenter.FriendP;
import me.wcy.music.presenter.UserP;
import me.wcy.music.storage.db.UserManger;
import me.wcy.music.utils.AlertDialogUtils;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class FriendsActivity extends BaseActivity implements IUserView, OnMoreClickListener, IOnItemClick {
    @Bind(R.id.friend_recycle)
    RecyclerView reFriendRecyle;
    @Bind(R.id.get_data_fail)
    TextView tvGetDataFail;

    private FriendAdapter adapter;
    private List<User> mData;
    private boolean isUnFollow;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
    }

    @Override
    protected void onServiceBound() {
        showProgress();

        mData = new ArrayList<>();

        UserP userP = new UserP(this);
        Params params = new Params("userID", UserManger.getInstance().getmUserDao().queryBuilder().list().get(0).getUserID() + "");
        userP.getFriend(params);
    }

    @Override
    public void result(String result, Exception e) {
        cancelProgress();
        if (!isUnFollow) {
            if (null != e) {
                tvGetDataFail.setVisibility(View.VISIBLE);
                ToastUtils.show("加载数据失败");
            } else {
                tvGetDataFail.setVisibility(View.GONE);
                initData(result);
            }
        } else if (Integer.parseInt(result) > 0) {
            mData.remove(mPosition);
            adapter.notifyDataSetChanged();
            ToastUtils.show("取消关注成功");
        }
    }

    /**
     * 初始化数据
     */
    public void initData(String result) {
        try {
            JSONArray jaa = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < jaa.length(); i++) {
                mData.add(gson.fromJson(jaa.getJSONObject(i).toString(), User.class));
            }
            fillData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillData() {
        adapter = new FriendAdapter(mData, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        reFriendRecyle.setLayoutManager(layoutManager);
        reFriendRecyle.setAdapter(adapter);

        adapter.setmOnItemClick(this);
        adapter.setOnMoreClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(FriendsActivity.this, SettingInfoActivity.class);
        intent.putExtra("frienduser", mData.get(position));
        startActivity(intent);
    }

    @Override
    public void onMoreClick(int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(mData.get(position).getUserName());
        int itemsId = R.array.friend_more_dialog;
        dialog.setItems(itemsId, (dialog1, which) -> {
            switch (which) {
                case 0:// 取消关注
                    new AlertDialogUtils(FriendsActivity.this, new AlertDialogUtils.IConfirmCallBack() {
                        @Override
                        public void operate(DialogInterface dialog) {
                            unfollow(position);
                        }
                    }).build(R.string.is_cancle_follow);
                    break;
                case 1:// 发送私信
                    sendMessage(position);
                    break;
            }
        });
        dialog.show();
    }

    private void unfollow(int position) {
        FriendP friendP = new FriendP(this);
        Params userID = new Params("userID", UserManger.getInstance().getmUserDao().queryBuilder().list().get(0).getUserID() + "");
        Params friendUserID = new Params("friendUserID", mData.get(position).getUserID() + "");
        Params[] params = new Params[]{userID, friendUserID};

        isUnFollow = true;
        mPosition = position;
        showProgress();
        friendP.delete(params);
    }

    private void sendMessage(int position) {
        Intent intent = new Intent(this, SendMassageActivity.class);
        intent.putExtra("user", mData.get(position));
        startActivity(intent);
    }
}
