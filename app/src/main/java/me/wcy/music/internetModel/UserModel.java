package me.wcy.music.internetModel;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import me.wcy.music.constants.Keys;
import me.wcy.music.model.Params;
import okhttp3.Call;

public class UserModel {

    public void login(String action, Params[] params, ICallBack callBack) {
        Log.i("login", "login: " + (Keys.ADRRESS + action));
        OkHttpUtils.get().url("http://172.17.115.0:8080/SSM/user/login")
                .addParams(params[0].key, params[0].value)
                .addParams(params[1].key, params[1].value)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int id) {
                        callBack.loginResult(response, null);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callBack.loginResult("登录失败,请重试！", e);
                    }
                });
    }
}
