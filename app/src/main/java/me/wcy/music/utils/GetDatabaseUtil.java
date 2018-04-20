package me.wcy.music.utils;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import me.wcy.music.constants.Keys;
import me.wcy.music.internetModel.ICallBack;
import me.wcy.music.model.Params;
import okhttp3.Call;

public class GetDatabaseUtil {
    public static void okhttpUtil(String action, ICallBack callBack, Params... params) {
        Log.i("login", "login: " + (Keys.ADRRESS + action));

        GetBuilder getBuilder = OkHttpUtils.get();
        addParams(getBuilder, params);
        getBuilder.url(Keys.ADRRESS + action)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int id) {
                        callBack.loginResult(response, null);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callBack.loginResult("操作失败,请重试！", e);
                    }
                });
    }

    private static void addParams(GetBuilder getBuilder, Params... params) {
        for (int i = 0; i < params.length; i++) {
            getBuilder.addParams(params[i].key, params[i].value);
        }
    }
}
