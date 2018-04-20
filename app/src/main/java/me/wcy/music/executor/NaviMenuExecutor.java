package me.wcy.music.executor;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import me.wcy.music.R;
import me.wcy.music.activity.FriendsActivity;
import me.wcy.music.activity.LoginActivity;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.activity.SettingActivity;
import me.wcy.music.activity.SettingInfoActivity;
import me.wcy.music.activity.SetttingSkinActivity;
import me.wcy.music.constants.Actions;
import me.wcy.music.service.PlayService;
import me.wcy.music.service.QuitTimer;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.AlertDialogUtils;
import me.wcy.music.utils.ToastUtils;

/**
 * 导航菜单执行器
 */
public class NaviMenuExecutor {
    private MusicActivity activity;

    public NaviMenuExecutor(MusicActivity activity) {
        this.activity = activity;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                startActivity(SettingActivity.class);
                return true;
            case R.id.action_night:
                nightMode();
                break;
            case R.id.action_timer:
                timerDialog();
                return true;
            case R.id.action_exit:
                exit();
                return true;
            case R.id.action_info:
                if (isLogin()) {
                    startActivity(SettingInfoActivity.class);
                }
                return true;
            case R.id.action_friends:
                if (isLogin()) {
                    startActivity(FriendsActivity.class);
                }
                return true;
            case R.id.action_scan:
                if (isLogin()) {
                    startActivityToScan();
                }
                return true;
            case R.id.action_skin:
                activity.startActivityForResult(new Intent(activity, SetttingSkinActivity.class), 0);
                return true;
        }
        return false;
    }

    /**
     * 用于提示用户进行登录
     *
     * @return
     */
    private boolean isLogin() {
        if (!Preferences.isLogin()) {
            new AlertDialogUtils(activity, new AlertDialogUtils.IConfirmCallBack() {
                @Override
                public void operate(DialogInterface dialog) {
                    dialog.dismiss();
                    startActivity(LoginActivity.class);
                }
            }).build(R.string.not_login);
            return false;
        }
        return true;
    }

    private void exit() {
        new AlertDialogUtils(activity, new AlertDialogUtils.IConfirmCallBack() {
            @Override
            public void operate(DialogInterface dialog) {
                dialog.dismiss();
                activity.finish();
                PlayService.startCommand(activity, Actions.ACTION_STOP);
            }
        }).build(R.string.menu_is_exit);
    }

    private void startActivityToScan() {
        ZxingConfig config = new ZxingConfig();
        config.setShowAlbum(true);
        config.setShowbottomLayout(true);

        Intent intent = new Intent(activity, CaptureActivity.class);
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        activity.startActivityForResult(intent, 100);
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    private void nightMode() {
        Preferences.saveNightMode(!Preferences.isNightMode());
        activity.recreate();
    }

    private void timerDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.menu_timer)
                .setItems(activity.getResources().getStringArray(R.array.timer_text), (dialog, which) -> {
                    int[] times = activity.getResources().getIntArray(R.array.timer_int);
                    startTimer(times[which]);
                })
                .show();
    }

    private void startTimer(int minute) {
        QuitTimer.get().start(minute * 60 * 1000);
        if (minute > 0) {
            ToastUtils.show(activity.getString(R.string.timer_set, String.valueOf(minute)));
        } else {
            ToastUtils.show(R.string.timer_cancel);
        }
    }
}
