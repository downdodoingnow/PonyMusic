package me.wcy.music.presenter;

import me.wcy.music.IView.IUserView;
import me.wcy.music.model.Params;
import me.wcy.music.internetModel.ICallBack;
import me.wcy.music.internetModel.UserModel;
import me.wcy.music.model.User;
import me.wcy.music.presenter.interfaceP.IUserP;

public class UserP implements IUserP {

    private IUserView mIUserView;
    private UserModel mUserModel;

    public UserP(IUserView mIUserView) {
        this.mIUserView = mIUserView;
        mUserModel = new UserModel();
    }

    @Override
    public void login(Params[] params) {
        mUserModel.login("/user/login", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void register(Params params) {
        mUserModel.register("/user/register", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void update(Params[] params) {
        mUserModel.updata("/user/update", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void updataUser(Params params) {
        mUserModel.updataUser("/user/updataUser", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void getUser(Params params) {
        mUserModel.updataUser("/user/getUser", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void getUserByID(Params params) {
        mUserModel.getUserById("/user/getUserByID", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void getFriend(Params params) {
        mUserModel.updataUser("/user/getFriend", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }
}
