package me.wcy.music.internetModel;

import me.wcy.music.model.Params;
import me.wcy.music.utils.GetDatabaseUtil;

public class UserModel {

    public void login(String action, Params[] params, ICallBack callBack) {
        GetDatabaseUtil.okhttpUtil(action, callBack, params);
    }

    public void register(String action, Params params, ICallBack iCallBack) {
        GetDatabaseUtil.okhttpUtil(action, iCallBack, params);
    }

    public void updata(String action, Params[] params, ICallBack iCallBack) {
        GetDatabaseUtil.okhttpUtil(action, iCallBack, params);
    }

    public void updataUser(String action, Params params, ICallBack iCallBack) {
        GetDatabaseUtil.okhttpUtil(action, iCallBack, params);
    }

    public void getUser(String action, Params params, ICallBack iCallBack) {
        GetDatabaseUtil.okhttpUtil(action, iCallBack, params);
    }

    public void getFriend(String action, Params params, ICallBack iCallBack) {
        GetDatabaseUtil.okhttpUtil(action, iCallBack, params);
    }
}
