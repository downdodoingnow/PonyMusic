package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import me.wcy.music.IView.IUserView;
import me.wcy.music.R;
import me.wcy.music.model.Params;
import me.wcy.music.model.User;
import me.wcy.music.presenter.UserP;
import me.wcy.music.storage.db.UserManger;
import me.wcy.music.storage.db.greendao.UserDao;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, IUserView {

    @Bind(R.id.phonenum_input)
    EditText etPhonenum;
    @Bind(R.id.code_input)
    EditText etCode;
    @Bind(R.id.get_code)
    TextView tvGetCode;
    @Bind(R.id.password_input)
    EditText etPassword;
    @Bind(R.id.password_input_again)
    EditText etPasswordAgain;
    @Bind(R.id.next_bnt)
    Button btNext;

    private String mCode;
    private String mPhoneNum;
    private String mPassword;
    private String mPasswordAgain;
    private CountDownTimer mCountDownTimer;

    private String whichActivity;
    private User mUser;
    private Handler mHandlSMS = new SMSHandler();
    private UserP userP;

    private EventHandler mHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            super.afterEvent(event, result, data);
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            mHandlSMS.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void onServiceBound() {
        Intent intent = getIntent();
        whichActivity = intent.getStringExtra("whichActivity");

        if (null != whichActivity) {
            setTitle("忘记密码");
        }
        userP = new UserP(this);

        mUser = new User();
        btNext.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);

        SMSSDK.registerEventHandler(mHandler);

        //获取验证码倒计时
        mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvGetCode.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvGetCode.setText(getResources().getString(R.string.register_get_code_again));
                tvGetCode.setClickable(true);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_code:
                getCode();
                break;
            case R.id.next_bnt:
                next();
                break;
            default:
        }
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * 用于判断用户输入是否合法
     *
     * @return
     */
    public boolean isInputOk() {
        mPassword = etPassword.getText().toString().trim();
        mPasswordAgain = etPasswordAgain.getText().toString().trim();
        mCode = etCode.getText().toString().trim();
        mPhoneNum = etPhonenum.getText().toString().trim();

        if (TextUtils.isEmpty(mPhoneNum)) {
            ToastUtils.show(getResources().getString(R.string.login_tel_hint));
            return false;
        }

        if (TextUtils.isEmpty(mCode)) {
            ToastUtils.show(getResources().getString(R.string.register_code_hint));
            return false;
        }
        if (TextUtils.isEmpty(mPassword) || mPassword.length() <= 6) {
            ToastUtils.show(getResources().getString(R.string.login_pass_hint));
            return false;
        }

        if (TextUtils.isEmpty(mPasswordAgain)) {
            ToastUtils.show("请再次输入密码");
            return false;
        }

        if (mPassword.equals(mPasswordAgain)) {
            return true;
        } else {
            ToastUtils.show("两次输入的密码不相同，请重新输入！");
        }
        return false;
    }

    public void next() {
        if (isInputOk()) {
            mUser.setPhoneNum(mPhoneNum);
            mUser.setPassword(mPassword);

            hideSoftInput(etPhonenum);

            showProgress();
            SMSSDK.submitVerificationCode("86", mPhoneNum, mCode);
//            register();
        }
    }

    //获取验证码
    public void getCode() {
        mPhoneNum = etPhonenum.getText().toString().trim();
        if (!TextUtils.isEmpty(mPhoneNum)) {
            tvGetCode.setClickable(false);
            mCountDownTimer.start();
            SMSSDK.getVerificationCode("86", mPhoneNum);
        } else {
            ToastUtils.show(getResources().getString(R.string.login_tel_hint));
        }
    }

    @Override
    public void result(String result, Exception e) {
        cancelProgress();
        if (result.equals("0")) {
            ToastUtils.show("密码修改失敗");
        } else if (result.equals("1")) {
            ToastUtils.show("密码修改成功");
            finish();
        } else {
            ToastUtils.show("该用户不存在，请注册！");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.registerEventHandler(mHandler);
    }

    public void forgetPassword() {
        Params password = new Params("password", mPassword);
        Params phoneNum = new Params("phoneNum", mPhoneNum);
        Params[] params = new Params[]{password, phoneNum};

        userP.update(params);
    }

    public void register() {
        Intent intent = new Intent(RegisterActivity.this, SettingInfoActivity.class);
        intent.putExtra("user", mUser);
        startActivity(intent);
    }

    class SMSHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //忘记密码
                    if (null != whichActivity) {
                        forgetPassword();
                    } else {//注册
                        cancelProgress();
                        register();
                    }
                }
            }
            if (result == SMSSDK.RESULT_ERROR) {
                cancelProgress();
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    ToastUtils.show("验证码输入错误，请重新获取！");
                } else {
                    ToastUtils.show("获取验证码失败！");
                }
            }
        }
    }
}
