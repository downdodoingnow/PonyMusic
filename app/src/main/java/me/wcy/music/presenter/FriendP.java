package me.wcy.music.presenter;

import me.wcy.music.IView.IUserView;
import me.wcy.music.internetModel.FriendModel;
import me.wcy.music.internetModel.ICallBack;
import me.wcy.music.model.Params;
import me.wcy.music.presenter.interfaceP.IFriendP;

public class FriendP implements IFriendP {

    private IUserView mIUserView;
    private FriendModel mFriendModel;

    public FriendP(IUserView mIUserView) {
        this.mIUserView = mIUserView;
        mFriendModel = new FriendModel();
    }

    @Override
    public void insert(Params params) {
        mFriendModel.insert("/friend/insert", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void delete(Params[] params) {
        mFriendModel.delete("/friend/delete", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }
}
