package me.wcy.music.internetModel;

import me.wcy.music.model.Params;
import me.wcy.music.utils.GetDatabaseUtil;

public class CommonModel {
    public void select(String action, Params[] params, ICallBack iCallBack) {
        GetDatabaseUtil.okhttpUtil(action, iCallBack, params);
    }

    public void insert(String action, Params params, ICallBack iCallBack) {
        GetDatabaseUtil.okhttpUtil(action, iCallBack, params);
    }
}
