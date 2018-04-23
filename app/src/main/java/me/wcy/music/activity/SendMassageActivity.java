package me.wcy.music.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.IView.IUserView;
import me.wcy.music.R;
import me.wcy.music.adapter.IInterface.OnMoreClickListener;
import me.wcy.music.adapter.SendMessageAdapter;
import me.wcy.music.model.Message;
import me.wcy.music.model.Params;
import me.wcy.music.model.User;
import me.wcy.music.presenter.MessageP;
import me.wcy.music.storage.db.UserManger;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class SendMassageActivity extends BaseActivity implements IUserView, View.OnClickListener {

    private User mFriendUser;

    @Bind(R.id.send_message_recycle)
    RecyclerView rcSendMessage;
    @Bind(R.id.send_message_edit)
    EditText etSendMessage;
    @Bind(R.id.send_message_bnt)
    TextView tvSendMessage;

    private long mUserID;
    private long toUserId;

    private List<Message> mData;
    private SendMessageAdapter adapter;

    private MessageP messageP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_massage);
    }

    @Override
    public void onClick(View v) {
        String msg = etSendMessage.getText().toString();
        if (!TextUtils.isEmpty(msg)) {
            Message message = getMessage(msg);
            Params params = new Params("message", new Gson().toJson(message));
            messageP.insert(params);

            message.setType(1);
            mData.add(message);
            adapter.notifyDataSetChanged();
            rcSendMessage.smoothScrollToPosition(mData.size() - 1);

            etSendMessage.setText("");
        }
    }

    public Message getMessage(String msg) {
        Message message = new Message();
        message.setTime(System.currentTimeMillis() / 1000);
        message.setMessage(msg);
        message.setUserID(mUserID);
        message.setToUserID(toUserId);

        return message;
    }

    @Override
    protected void onServiceBound() {

        mFriendUser = (User) getIntent().getSerializableExtra("user");
        mUserID = UserManger.getInstance().getmUserDao().queryBuilder().list().get(0).getUserID();
        toUserId = mFriendUser.getUserID();
        mData = new ArrayList<>();

        setTitle(mFriendUser.getUserName());

        tvSendMessage.setOnClickListener(this);

        messageP = new MessageP(this);
        Params userID = new Params("userID", mUserID + "");
        Params toUserID = new Params("toUserID", toUserId + "");

        messageP.select(new Params[]{userID, toUserID});
    }

    @Override
    public void result(String result, Exception e) {
        if (null == result || e != null) {
            ToastUtils.show("获取数据失败");
        } else {
            try {
                JSONArray jaa = new JSONArray(result);
                Gson gson = new Gson();
                Message message;

                for (int i = 0; i < jaa.length(); i++) {
                    message = gson.fromJson(jaa.getJSONObject(i).toString(), Message.class);
                    if (message.getUserID() == mUserID) {
                        message.setType(1);
                    } else {
                        message.setType(2);
                    }
                    mData.add(message);
                }

                fillData();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void fillData() {
        adapter = new SendMessageAdapter(mData, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcSendMessage.setLayoutManager(layoutManager);
        rcSendMessage.setAdapter(adapter);
        rcSendMessage.scrollToPosition(mData.size() - 1);
    }
}
