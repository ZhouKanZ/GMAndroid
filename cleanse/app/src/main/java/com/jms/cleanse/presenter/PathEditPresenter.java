package com.jms.cleanse.presenter;

import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.contract.PathEditContract;

/**
 * Created by zhoukan on 2018/4/10.
 *
 * @desc:
 */

public class PathEditPresenter extends BasePresenter<PathEditContract.View> implements PathEditContract.Presenter {

    @Override
    public void onCreate() {
        registerEventBus();
    }

    @Override
    public void onDestory() {
        unRegisterEventBus();
    }

    private void registerEventBus() {

    }

    private void unRegisterEventBus() {

    }
}
