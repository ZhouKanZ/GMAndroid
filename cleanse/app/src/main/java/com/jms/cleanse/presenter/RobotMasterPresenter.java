package com.jms.cleanse.presenter;

import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.contract.RobotMasterContract;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import robot.boocax.com.sdkmodule.APPSend;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;

/**
 * Created by zhoukan on 2018/3/28.
 *
 * @desc:
 */

public class RobotMasterPresenter extends BasePresenter<RobotMasterContract.View> implements RobotMasterContract.Presenter {

    Disposable loopDispose;

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestory() {

    }

    @Override
    public void doLoopSendMove() {
        // 间隔100ms发一次指令
        loopDispose = Observable
                .interval(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(along -> APPSend.sendMove(LoginEntity.robotMac, getView().getSpeed()[0], getView().getSpeed()[1], getView().getSpeed()[2]));

    }

    @Override
    public void cancelLoop() {
        if (null != loopDispose && !loopDispose.isDisposed()) {
            loopDispose.dispose();
        }
    }

    @Override
    public void requestAllMapInfo() {
        new Thread(APPSend::sendGetAllMap).start();
    }

    @Override
    public void cancelGoal() {
        // 发送取消导航的命令
        Observable.just(LoginEntity.robotMac)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(APPSend::sendCancel_goal);
    }

}
