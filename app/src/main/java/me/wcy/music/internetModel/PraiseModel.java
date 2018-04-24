package me.wcy.music.internetModel;

import me.wcy.music.model.Params;
import me.wcy.music.utils.GetDatabaseUtil;

public class PraiseModel {
    public void select(String action, Params params, ICallBack callBack) {
        GetDatabaseUtil.okhttpUtil(action, callBack, params);
    }

    public void insert(String action, Params params, ICallBack callBack) {
        GetDatabaseUtil.okhttpUtil(action, callBack, params);
    }
}
