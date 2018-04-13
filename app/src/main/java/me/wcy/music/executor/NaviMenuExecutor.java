package me.wcy.music.executor;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import me.wcy.music.R;
import me.wcy.music.activity.AboutActivity;
import me.wcy.music.activity.FriendsActivity;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.activity.ScanActivity;
import me.wcy.music.activity.SettingActivity;
import me.wcy.music.activity.SettingInfoActivity;
import me.wcy.music.activity.SetttingSkinActivity;
import me.wcy.music.adapter.SettingSkinAdapter;
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
            case R.id.action_info:
                startActivity(SettingInfoActivity.class);
                return true;
            case R.id.action_friends:
                startActivity(FriendsActivity.class);
                return true;
            case R.id.action_scan:
                startActivity(ScanActivity.class);
                return true;
            case R.id.action_skin:
                activity.startActivityForResult(new Intent(activity, SetttingSkinActivity.class), 0);
                return true;
        }
        return false;
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
