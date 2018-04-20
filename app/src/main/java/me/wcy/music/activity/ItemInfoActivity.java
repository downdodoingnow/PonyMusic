package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import me.wcy.music.R;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class ItemInfoActivity extends BaseActivity {

    @Bind(R.id.change_info)
    EditText etChangeInfo;

    private String mUserName;
    private String mArea;
    private String mSignature;
    public static final int maxTextNum = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
    }

    @Override
    protected void onServiceBound() {
        Intent intent = getIntent();
        mUserName = intent.getStringExtra("username");
        mArea = intent.getStringExtra("area");
        mSignature = intent.getStringExtra("signature");

        whichInfo();
    }

    private void whichInfo() {
        if (null != mUserName) {
            setTitle("修改昵称");
            etChangeInfo.setText(mUserName);
            setEditTextMaxSize();
        } else if (null != mArea) {
            setTitle("修改地区");
            etChangeInfo.setText(mArea);
        } else if (null != mSignature) {
            setTitle("修改签名");
            etChangeInfo.setText(mSignature);
        }
    }

    /**
     * 设置最多输入20个字符
     */
    private void setEditTextMaxSize() {
        etChangeInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxTextNum)});
        etChangeInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > maxTextNum) {
                    ToastUtils.show("你已经达到字数上限");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            returnForward();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void returnForward() {
        Intent intent = new Intent(this, SettingInfoActivity.class);
        intent.putExtra("data", etChangeInfo.getText().toString());
        setResult(0, intent);
    }
}
