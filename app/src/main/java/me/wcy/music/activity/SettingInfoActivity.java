package me.wcy.music.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import me.wcy.music.IView.IUserView;
import me.wcy.music.R;
import me.wcy.music.model.Friend;
import me.wcy.music.model.Params;
import me.wcy.music.model.User;
import me.wcy.music.presenter.FriendP;
import me.wcy.music.presenter.UserP;
import me.wcy.music.storage.db.UserManger;
import me.wcy.music.storage.db.greendao.UserDao;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class SettingInfoActivity extends BaseActivity implements View.OnClickListener, IUserView {

    @Bind(R.id.username_ll)
    LinearLayout llUsername;
    @Bind(R.id.username)
    TextView tvUsername;
    @Bind(R.id.sex_re)
    RelativeLayout reSex;
    @Bind(R.id.sex)
    TextView tvSex;
    @Bind(R.id.age)
    TextView tvAge;
    @Bind(R.id.birthday_re)
    RelativeLayout reBirthday;
    @Bind(R.id.birthday)
    TextView tvBirthday;
    @Bind(R.id.area_re)
    RelativeLayout reArea;
    @Bind(R.id.area)
    TextView tvArea;
    @Bind(R.id.phonenum)
    TextView tvPhoneNum;
    @Bind(R.id.signature_ll)
    LinearLayout llSignature;
    @Bind(R.id.signature)
    TextView tvSignature;
    @Bind(R.id.two_code_re)
    RelativeLayout reTwoCode;
    @Bind(R.id.head_photo_re)
    RelativeLayout reHeadPhoto;
    @Bind(R.id.head_photo)
    ImageView tvHeadPhoto;

    @Bind(R.id.save)
    Button btSave;

    private String[] sexArray = new String[]{"男", "女"};

    private UserDao userDao;

    private User mRegisterUser;
    private User mInfoUser;
    private User mScanUser;
    private User mFrienduser;

    private User user;
    //private boolean isChangeInfo;
    //用于表示是否是修改信息
    private int year = 1990, month = 1, dayOgMonth = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_info);
    }

    public void gtForwardData() {
        //从扫一扫页面传递过来
        mScanUser = (User) getIntent().getSerializableExtra("scanUser");
        if (null != mScanUser) {
            btSave.setText("关注");
            btSave.setVisibility(View.VISIBLE);
            initTextView(mScanUser);
            setTitle("资料信息");
        }
        //当从注册页面传递过来的时候
        mRegisterUser = (User) getIntent().getSerializableExtra("user");
        if (null != mRegisterUser) {
            btSave.setVisibility(View.VISIBLE);
            tvPhoneNum.setText(mRegisterUser.getPhoneNum());
            user = mRegisterUser;
        }
        //从好友页面进行跳转
        mFrienduser = (User) getIntent().getSerializableExtra("frienduser");
        if (null != mFrienduser) {
            setTitle("好友资料");
            initTextView(mFrienduser);
        }
    }

    @Override
    protected void onServiceBound() {
        gtForwardData();
        if (null == mScanUser && null == mFrienduser) {
            llUsername.setOnClickListener(this);
            reSex.setOnClickListener(this);
            reArea.setOnClickListener(this);
            reBirthday.setOnClickListener(this);
            llSignature.setOnClickListener(this);
            reHeadPhoto.setOnClickListener(this);
        }

        reTwoCode.setOnClickListener(this);
        btSave.setOnClickListener(this);

        //从注册页面进行跳转那么肯定没有进行登录操作或者已经退出登录
        if (Preferences.isLogin()) {
            if (null == mScanUser && null == mFrienduser && null == mRegisterUser) {
                initInfo();
            }
        } else {
            reTwoCode.setVisibility(View.GONE);
        }
    }

    //初始化资料
    public void initInfo() {
        userDao = UserManger.getInstance().getmUserDao();
        mInfoUser = userDao.queryBuilder().list().get(0);

        if (null != mInfoUser) {
            initTextView(mInfoUser);
            user = mInfoUser;
        }
    }

    private void initTextView(User user) {
        tvUsername.setText(user.getUserName());
        tvSex.setText(user.getSex());
        tvAge.setText(user.getAge() + "");
        tvBirthday.setText(user.getBirthday());
        tvPhoneNum.setText(user.getPhoneNum());
        tvArea.setText(user.getArea());
        tvSignature.setText(user.getSignature());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_photo_re:
                changeHeadPhoto();
                break;
            case R.id.username_ll:
                startActivity("username", tvUsername.getText().toString(), 0);
                break;
            case R.id.sex_re:
                changeSex();
                break;
            case R.id.birthday_re:
                getBirthdayDate();
                break;
            case R.id.area_re:
                startActivity("area", tvArea.getText().toString(), 3);
                break;
            case R.id.signature_ll:
                startActivity("signature", tvSignature.getText().toString(), 4);
                break;
            case R.id.two_code_re:
                Intent intent = new Intent(this, TwoCodeActivity.class);
                intent.putExtra("username", tvUsername.getText().toString());
                intent.putExtra("phoneNum", tvPhoneNum.getText().toString());
                intent.putExtra("sex", tvSex.getText().toString());
                startActivity(intent);
                break;
            case R.id.save:
                setInfo();
                break;
            default:
        }
    }

    private void changeHeadPhoto() {
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("请选择");
        int itemsId = R.array.head_photo_dialog;
        dialog.setItems(itemsId, (dialog1, which) -> {
            switch (which) {
                case 0:// 拍照
                    break;
                case 1:// 相册
                    break;
            }
        });
        dialog.show();
    }

    private void getBirthdayDate() {

        getDefaultDate();

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String month1 = (month + 1) + "";
                String dayOfMonth1 = dayOfMonth + "";
                if (month < 10) {
                    month1 = "0" + (month + 1);
                }
                if (dayOfMonth < 10) {
                    dayOfMonth1 = "0" + dayOfMonth;
                }
                String birthday = year + "-" + month1 + "-" + dayOfMonth1;
                if (!birthday.equals(tvBirthday.getText().toString())) {
                    tvBirthday.setText(birthday);
                    mInfoUser.setBirthday(birthday);
                    //isChangeInfo = true;
                    btSave.setVisibility(View.VISIBLE);

                    updateAgeByBirthday(year, month, dayOfMonth);
                }
            }
        }, year, month - 1, dayOgMonth).show();
    }

    //更新年龄
    private void updateAgeByBirthday(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        int age = calendar.get(Calendar.YEAR) - year;
        if (calendar.get(Calendar.MONTH) < month) {
            age -= 1;
        } else if (currentMonth == month && currentDayOfMonth < dayOfMonth) {
            age -= 1;
        }
        tvAge.setText(age + "");
        mInfoUser.setAge(age);
    }

    private void getDefaultDate() {
        String dayOfBirthday = tvBirthday.getText().toString();
        String[] birthday = dayOfBirthday.split("-");

        year = Integer.parseInt(birthday[0]);
        month = Integer.parseInt(birthday[1]);
        dayOgMonth = Integer.parseInt(birthday[2]);
    }

    private void changeSex() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int which;
        if (tvSex.getText().toString().equals("男")) {
            which = 0;
        } else {
            which = 1;
        }
        builder.setSingleChoiceItems(sexArray, which, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sex = sexArray[which];
                if (!sex.equals(mInfoUser.getSex())) {
                    mInfoUser.setSex(sex);
                    tvSex.setText(sex);
                    //isChangeInfo = true;
                    btSave.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }
        }).show();
    }

    public void startActivity(String key, String value, int requestCode) {
        Intent intent = new Intent(this, ItemInfoActivity.class);
        intent.putExtra(key, value);
        startActivityForResult(intent, requestCode);
    }

    //注册、修改信息
    private void setInfo() {
        showProgress();

        Gson gson = new Gson();
        JsonElement jsonElement = null;
        JsonObject joo = null;
        Params params = null;
        UserP userP = new UserP(this);

        if (null != mRegisterUser) {
            initRegisterUser();
            jsonElement = gson.toJsonTree(mRegisterUser, User.class);
            joo = jsonElement.getAsJsonObject();

            params = new Params("user", joo.toString());
            userP.register(params);
        } else if (null != mScanUser) {
            follow();
        } else {
            jsonElement = gson.toJsonTree(mInfoUser, User.class);
            joo = jsonElement.getAsJsonObject();

            params = new Params("user", joo.toString());
            userP.updataUser(params);
        }
    }

    /**
     * 关注
     */
    public void follow() {
        Friend friend = new Friend();
        friend.setFriendUserID(mScanUser.getUserID());
        friend.setUserID(UserManger.getInstance().getmUserDao().queryBuilder().list().get(0).getUserID());

        JsonElement jsonElement = new Gson().toJsonTree(friend, Friend.class);
        JsonObject joo = jsonElement.getAsJsonObject();
        new FriendP(this).insert(new Params("friend", joo.toString()));
    }

    private void initRegisterUser() {
        mRegisterUser.setUserName(tvUsername.getText().toString());
        mRegisterUser.setSex(tvSex.getText().toString());
        mRegisterUser.setAge(Integer.parseInt(tvAge.getText().toString()));
        mRegisterUser.setBirthday(tvBirthday.getText().toString());
        mRegisterUser.setArea(tvArea.getText().toString());
        mRegisterUser.setSignature(tvSignature.getText().toString());
    }

    @Override
    public void result(String result, Exception e) {
        cancelProgress();
        if (null != mRegisterUser) {
            registerResult(result);
        } else if (null != mScanUser) {
            if (result.equals("3")) {
                ToastUtils.show("你已经关注了该用户");
            } else if (result.equals("1")) {
                ToastUtils.show("关注成功");
                btSave.setVisibility(View.GONE);
            } else {
                ToastUtils.show("关注失败");
            }
        } else {
            changeInfoResult(result);
        }
    }

    public void changeInfoResult(String result) {
        if ("1".equals(result)) {
            ToastUtils.show("信息更新成功");
            //更新本地数据库
            userDao.deleteAll();
            userDao.insert(mInfoUser);

            btSave.setVisibility(View.GONE);
        } else {
            ToastUtils.show("信息更新失败");
        }
    }

    public void registerResult(String result) {
        if ("1".equals(result)) {
            ToastUtils.show("注册成功");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("phoneNum", mRegisterUser.getPhoneNum());
            startActivity(intent);
        } else if ("2".equals(result)) {
            ToastUtils.show("用户已经存在");
        } else {
            ToastUtils.show("注册失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserManger.getInstance().init(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            String dataInfo = data.getStringExtra("data");
            switch (requestCode) {
                case 0:
                    tvUsername.setText(dataInfo);
                    if (!dataInfo.equals(user.getUserName())) {
                        btSave.setVisibility(View.VISIBLE);
                        user.setUserName(dataInfo);
                    }
                    break;
                case 3:
                    tvArea.setText(dataInfo);
                    if (!dataInfo.equals(user.getArea())) {
                        btSave.setVisibility(View.VISIBLE);
                        user.setArea(dataInfo);
                    }
                    break;
                case 4:
                    tvSignature.setText(dataInfo);
                    if (!dataInfo.equals(user.getSignature())) {
                        btSave.setVisibility(View.VISIBLE);
                        user.setSignature(dataInfo);
                    }
                    break;
                default:
            }
        }
    }
}
