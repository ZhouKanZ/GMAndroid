package com.jms.cleanse.presenter;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.bean.MSG_TYPE;
import com.jms.cleanse.bean.MotorOnOff;
import com.jms.cleanse.contract.RobotMasterContract;
import com.jms.cleanse.entity.map.MapTabSpec;
import com.jms.cleanse.presenter.status.RunContext;
import com.jms.cleanse.util.FileUtil;
import com.jms.cleanse.presenter.status.RunTimeState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import robot.boocax.com.sdkmodule.APPSend;
import robot.boocax.com.sdkmodule.TCP_CONN;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.Map_param;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ThumbnailCache;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.All_file_info;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Register_status;
import robot.boocax.com.sdkmodule.utils.sdk_utils.SendUtil;

import static com.jms.cleanse.bean.CustomCommandKt.appendCustomCommand;

/**
 * Created by zhoukan on 2018/3/28.
 *
 * @desc:
 */
public class RobotMasterPresenter extends BasePresenter<RobotMasterContract.View> implements RobotMasterContract.Presenter {

    private static final String TAG = "RobotMasterPresenter";
    private Disposable loopDispose;
    private Disposable commadnDispose;
    private MotorOnOff motorOnOff = new MotorOnOff("off");
    private RunContext runContext = new RunContext();


    private List<MapTabSpec> mapTabSpecs;

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestory() {
        EventBus.getDefault().unregister(this);
    }

    public void initData() {

        mapTabSpecs = new ArrayList<>();
        // 默认为手动模式
        runContext.setState(RunContext.MANUALLY_STATE);
    }

    @Override
    public void doLoopSendMove() {

        // 间隔100ms发一次指令
        loopDispose = Observable
                .interval(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(along ->
                        APPSend.sendMove(LoginEntity.robotMac, getView().getSpeed()[0], getView().getSpeed()[1], getView().getSpeed()[2]), e -> Log.d(TAG, "doLoopSendMove: " + e.toString())
                );

    }

    public void motor_onoff() {

        motorOnOff.setState(getView().getchecked() ? "on" : "off");
        String content = appendCustomCommand(motorOnOff, LoginEntity.robotMac, MSG_TYPE.motor_onoff);
        Observable.just(content)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(s -> SendUtil.send(s, TCP_CONN.channel));
    }

    @Override
    public void cancelLoop() {
        if (null != loopDispose && !loopDispose.isDisposed()) {
            loopDispose.dispose();
        }
    }

    public void popupWindowOnClick(int position) {

        Log.i(TAG, "onClick: " + mapTabSpecs.get(position).getMapName());
        Observable.just(mapTabSpecs.get(position).getMapName())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String mapName) throws Exception {
                        // 设置当前地图的名称
                        getView().setLocalMapName(mapName);
                        APPSend.sendApply_map(mapName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public void showMapList() {
        requestAllMapInfo();
    }

    public Bitmap loadMapPng() {
        byte[] mapBytes = FileUtil.readPng("map.png");
        if (mapBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mapBytes, 0, mapBytes.length);
            return bitmap;
        }
        return null;
    }

    @Override
    public void requestAllMapInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                APPSend.sendGetAllMap();
                APPSend.sendGetAllFile();
            }
        }).start();

    }

    /**
     * 请求新的缩略图
     *
     * @param s
     */
    public void notifyAskNewBitmap(String s) {
        Observable.just(s)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(mapStr -> APPSend.sendGetThumbnailMap(mapStr));
    }

    // 得到缩略图
    @TargetApi(Build.VERSION_CODES.FROYO)
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public List<MapTabSpec> getAllThumbnailMaps(ThumbnailCache thumbnailCache) {
        MapTabSpec mapTabSpec = new MapTabSpec();
        String map_name = thumbnailCache.getThumbnail().getMap_name();
        mapTabSpec.setMapName(map_name);
        byte[] mapBytes = Base64.decode(thumbnailCache.getThumbnail().getContent(), Base64.DEFAULT);
        mapTabSpec.setData(mapBytes);
        mapTabSpecs.add(mapTabSpec);
        Log.i(TAG, "getAllThumbnailMap: " + mapTabSpec.getMapName() + ", size = " + mapTabSpecs.size());
        return mapTabSpecs;
    }

    /**
     * 获取当前地图的信息
     *
     * @param map_param
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getMap_info(Map_param map_param) {
        if (map_param != null && map_param.getMapParam() != null) {
            All_file_info.MapInfoBean param = map_param.getMapParam();
            String mapName = param.getName();
            if (!TextUtils.isEmpty(mapName)) {
                getView().showLoadMap(mapName);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getRegister_status(Register_status register_status) {
        if (register_status == null) {
            return;
        }

        Log.d(TAG, "getRegister_status: " + register_status.getAuth_result());

        if (register_status.getAuth_result().equals("true")) {

        } else if (register_status.getAuth_result().equals("false")) {

        }

    }


    @Override
    public void cancelGoal() {
        // 发送取消导航的命令
        Observable.just(LoginEntity.robotMac)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(APPSend::sendCancel_goal);
    }

    @Override
    public void reset() {
        Observable.just(LoginEntity.robotMac)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(APPSend::sendReset);
    }

    @Override
    public void switchWithIndex(int i) {
        runContext.switchWithIndex(i);
    }

    @Override
    public void switchMotorState(boolean flag) {
        runContext.switchMotorState(flag);
    }

}
