package me.wcy.music.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;
import com.yzq.zxinglibrary.encode.CodeCreator;

import me.wcy.music.R;
import me.wcy.music.utils.binding.Bind;

public class TwoCodeActivity extends BaseActivity {
    @Bind(R.id.two_code)
    ImageView imTwoCode;
    @Bind(R.id.username)
    TextView tvUsername;
    @Bind(R.id.sex)
    ImageView imSex;

    private String mUsername;
    private String mPhoneNum;
    private String mSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_code);
    }

    @Override
    protected void onServiceBound() {

        Intent intent = getIntent();
        mUsername = intent.getStringExtra("username");
        mPhoneNum = intent.getStringExtra("phoneNum");
        mSex = intent.getStringExtra("sex");

        initView();
        getTwoCode();

    }

    private void initView() {
        tvUsername.setText(mUsername);
        if ("男".equals(mSex)) {
            imSex.setImageResource(R.drawable.ic_boy);
        } else {
            imSex.setImageResource(R.drawable.ic_girl);
        }
    }

    private void getTwoCode() {

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        try {
            Bitmap bitmap = CodeCreator.createQRCode(mPhoneNum, 400, 400, logo);
            imTwoCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
