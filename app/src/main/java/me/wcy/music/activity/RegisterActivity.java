package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import me.wcy.music.IView.IUserView;
import me.wcy.music.R;
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
    private Handler mHandlSMS = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (null != whichActivity) {

                    } else {

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
            showProgress();
            SMSSDK.submitVerificationCode("86", mPhoneNum, mCode);
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.registerEventHandler(mHandler);
    }
}
