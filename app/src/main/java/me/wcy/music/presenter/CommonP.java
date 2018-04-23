package me.wcy.music.presenter;

import me.wcy.music.IView.IUserView;
import me.wcy.music.internetModel.CommonModel;
import me.wcy.music.internetModel.ICallBack;
import me.wcy.music.model.Params;
import me.wcy.music.presenter.interfaceP.ICommonP;

public class CommonP implements ICommonP {
    private IUserView mIUserView;
    private CommonModel mCommonModel;

    public CommonP(IUserView mIUserView) {
        this.mIUserView = mIUserView;
        mCommonModel = new CommonModel();
    }

    @Override
    public void select(Params[] params) {
        mCommonModel.select("/common/select", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }

    @Override
    public void insert(Params params) {
        mCommonModel.insert("/common/insert", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIUserView.result(result, e);
            }
        });
    }
}
