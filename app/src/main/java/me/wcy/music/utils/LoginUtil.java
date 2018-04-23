package me.wcy.music.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import me.wcy.music.R;
import me.wcy.music.activity.LoginActivity;
import me.wcy.music.storage.preference.Preferences;

public class LoginUtil {
    public static boolean isLogin(Context context) {
        if (!Preferences.isLogin()) {
            new AlertDialogUtils(context, new AlertDialogUtils.IConfirmCallBack() {
                @Override
                public void operate(DialogInterface dialog) {
                    dialog.dismiss();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }).build(R.string.not_login);
            return false;
        }
        return true;
    }
}
