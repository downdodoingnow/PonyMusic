package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import me.wcy.music.IView.IUserView;
import me.wcy.music.R;
import me.wcy.music.model.Params;
import me.wcy.music.model.User;
import me.wcy.music.presenter.UserP;
import me.wcy.music.storage.db.UserManger;
import me.wcy.music.storage.db.greendao.UserDao;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class LoginActivity extends BaseActivity implements View.OnClickListener, IUserView {

    @Bind(R.id.phonenum_input)
    EditText etPhonenum;
    @Bind(R.id.password_input)
    EditText etPassword;
    @Bind(R.id.forget_password)
    TextView tvForget;
    @Bind(R.id.login_bnt)
    Button btLogin;
    @Bind(R.id.register_bnt)
    Button btRegister;

    private String mTelphonenum;
    private String mPassword;

    private UserP mUserP;
    public static final String WHICHACTIVITY = "forgetpassword";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onServiceBound() {

        mUserP = new UserP(this);

        tvForget.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_password:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("whichActivity", WHICHACTIVITY);
                startActivity(intent);
                break;
            case R.id.login_bnt:
                login();
                hideSoftInput(etPhonenum);
                break;
            case R.id.register_bnt:
                startActivity(RegisterActivity.class);
                break;
            default:
                break;
        }
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    //用于判断用户输入的信息是否符合要求
    private boolean isLogin() {
        mTelphonenum = etPhonenum.getText().toString();
        mPassword = etPassword.getText().toString();

        if (TextUtils.isEmpty(mTelphonenum)) {
            ToastUtils.show(getResources().getString(R.string.login_tel_hint));
            return false;
        }
        if (TextUtils.isEmpty(mPassword) || mPassword.length() <= 6) {
            ToastUtils.show(getResources().getString(R.string.login_pass_hint));
            return false;
        }
        return true;
    }

    public void login() {
        if (isLogin()) {
            showProgress();
            Params telphone = new Params("telphone", mTelphonenum);
            Params password = new Params("password", mPassword);

            Params[] params = new Params[]{telphone, password};

            mUserP.login(params);

            Log.i("login", "login: " + mTelphonenum + " " + mPassword);
        }
    }

    @Override
    public void result(String result, Exception e) {
        cancelProgress();
        //登录失败
        if (null != e) {
            ToastUtils.show(result);
        } else {
            Gson gson = new Gson();
            User user = gson.fromJson(result, User.class);
            if (1 == user.getCode()) {
                ToastUtils.show("密码错误，请重新登录");
            } else if (2 == user.getCode()) {
                ToastUtils.show("暂无该用户，请注册！");
            } else {
                Preferences.saveLoginMode(true);
                ToastUtils.show("登录成功");
                insertToLocalDatabase(user);
                finish();
            }
        }
    }

    /**
     * 将该用户插入到本地数据库用于显示个人资料
     */
    public void insertToLocalDatabase(User user) {
        UserDao userDao = UserManger.getInstance().getmUserDao();
        //只保证本地数据库有一个用户
        userDao.deleteAll();
        userDao.insert(user);
    }
}

