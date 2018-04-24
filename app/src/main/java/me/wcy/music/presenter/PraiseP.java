package me.wcy.music.presenter;

import me.wcy.music.IView.IPraiseView;
import me.wcy.music.internetModel.ICallBack;
import me.wcy.music.internetModel.PraiseModel;
import me.wcy.music.model.Params;
import me.wcy.music.presenter.interfaceP.IPraiseP;

public class PraiseP implements IPraiseP {

    private IPraiseView mIPraiseView;
    private PraiseModel mPraiseModel;

    public PraiseP(IPraiseView mIPraiseView) {
        this.mIPraiseView = mIPraiseView;
        mPraiseModel = new PraiseModel();
    }

    @Override
    public void select(Params params) {
        mPraiseModel.select("/praise/select", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIPraiseView.praiseResult(result, e);
            }
        });
    }

    @Override
    public void insert(Params params) {
        mPraiseModel.insert("/praise/insert", params, new ICallBack() {
            @Override
            public void loginResult(String result, Exception e) {
                mIPraiseView.praiseResult(result, e);
            }
        });
    }
}
