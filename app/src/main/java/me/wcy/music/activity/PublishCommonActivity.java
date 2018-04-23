package me.wcy.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import me.wcy.music.R;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;

public class PublishCommonActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.publish_et)
    EditText etPublish;
    @Bind(R.id.publish_bnt)
    Button btPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_common);
    }

    @Override
    protected void onServiceBound() {
        btPublish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String common = etPublish.getText().toString();
        if (!TextUtils.isEmpty(common)) {
            Intent intent = new Intent(this, CommonActivity.class);
            intent.putExtra("common", common);
            setResult(1, intent);
            finish();
        } else {
            ToastUtils.show("请输入评论的内容");
        }
    }
}
