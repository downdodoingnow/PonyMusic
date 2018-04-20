package me.wcy.music.presenter;

import me.wcy.music.IView.IUserView;
import me.wcy.music.internetModel.ICallBack;
import me.wcy.music.internetModel.MessageModel;
import me.wcy.music.model.Params;
import me.wcy.music.presenter.interfaceP.IMessageP;

public class MessageP implements IMessageP {

    private IUserView mIUserView;
    private MessageModel mMessageModel;

    public MessageP(IUserView mIUserView) {
        this.mIUserView = mIUserView;
        mMessageModel = new MessageModel();
    }

    @Override
    public void select(Params[] params) {
        mMessageModel.select("/message/select", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void insert(Params params) {
        mMessageModel.insert("/message/insert", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }
}
