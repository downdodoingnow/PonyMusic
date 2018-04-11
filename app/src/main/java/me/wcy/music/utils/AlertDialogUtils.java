package me.wcy.music.utils;

import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogUtils {
    private Context mContext;
    private IConfirmCallBack mIConfirmCallBack;

    public AlertDialogUtils(Context mContext, IConfirmCallBack mIConfirmCallBack) {
        this.mContext = mContext;
        this.mIConfirmCallBack = mIConfirmCallBack;
    }

    public void build(int title) {
        new android.support.v7.app.AlertDialog.Builder(mContext)
                .setTitle(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIConfirmCallBack.operate(dialog);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public interface IConfirmCallBack {
        void operate(DialogInterface dialog);
    }
}
