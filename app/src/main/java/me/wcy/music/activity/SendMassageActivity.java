package me.wcy.music.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import me.wcy.music.utils.ChatClientSocketUtil;
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

    private ChatClientSocketUtil chatClientSocketUtil;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            String content = (String) msg.obj;
            Message message = new Message();
            message.setType(0);
            message.setMessage(content);

            addMData(message);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_massage);

        //开启长连接用于数据通信
        chatClientSocketUtil = new ChatClientSocketUtil(mHandler);
        chatClientSocketUtil.init();
    }

    @Override
    public void onClick(View v) {
        String msg = etSendMessage.getText().toString();
        if (!TextUtils.isEmpty(msg)) {
            Message message = getMessage(msg);
            Params params = new Params("message", new Gson().toJson(message));
            messageP.insert(params);

            message.setType(1);
            addMData(message);
            sendMsg(msg);
        }
    }

    private void addMData(Message message) {
        mData.add(message);
        adapter.notifyDataSetChanged();
        rcSendMessage.smoothScrollToPosition(mData.size() - 1);

        etSendMessage.setText("");
    }

    private void sendMsg(String msg) {
        JSONObject joo = new JSONObject();
        try {
            joo.put("userID", mUserID);
            joo.put("toUserID", toUserId);
            joo.put("content", msg);
            chatClientSocketUtil.sendMsg(joo);
            chatClientSocketUtil.receiveMsg();
        } catch (JSONException e) {
            e.printStackTrace();
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
                //e1.printStackTrace();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatClientSocketUtil.close();
    }
}
