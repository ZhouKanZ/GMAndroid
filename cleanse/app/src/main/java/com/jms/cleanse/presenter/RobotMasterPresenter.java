package com.jms.cleanse.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.contract.RobotMasterContract;
import com.jms.cleanse.entity.map.MapTabSpec;
import com.jms.cleanse.entity.robot.LaserEntity;
import com.jms.cleanse.util.FileUtil;

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
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ReconnReason;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ThumbnailCache;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.UpliftScreenPosition;

/**
 * Created by zhoukan on 2018/3/28.
 *
 * @desc:
 */

public class RobotMasterPresenter extends BasePresenter<RobotMasterContract.View> implements RobotMasterContract.Presenter {

    private static final String TAG = "RobotMasterPresenter";
    Disposable loopDispose;

    private List<MapTabSpec> mapTabSpecs;
    private List<Bitmap> thumbnailMaps;
    @Override
    public void onCreate() {

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestory() {
        EventBus.getDefault().unregister(this);
    }

    public void initData(){
        mapTabSpecs = new ArrayList<>();
        thumbnailMaps = new ArrayList<>();
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

    public void popupWindowOnClick(int position){

        Log.i(TAG, "onClick: " + mapTabSpecs.get(position).getMapName());
        Observable.just(mapTabSpecs.get(position).getMapName())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String mapName) throws Exception {

                        APPSend.sendApply_map(mapName);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public void showMapList(){
        requestAllMapInfo();
    }

    public Bitmap loadMapPng(){
        byte[] mapBytes = FileUtil.readPng("map.png");
        if (mapBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mapBytes, 0, mapBytes.length);
            return bitmap;
        }
        return null;
    }

    @Override
    public void requestAllMapInfo() {
        new Thread(APPSend::sendGetAllMap).start();
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
    public List<MapTabSpec> getAllThumbnailMaps(ThumbnailCache thumbnailCache){
        MapTabSpec mapTabSpec = new MapTabSpec();
        String map_name = thumbnailCache.getThumbnail().getMap_name();
        mapTabSpec.setMapName(map_name);

        byte[] decode = Base64.decode(thumbnailCache.getThumbnail().getContent(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        mapTabSpec.setMap(bitmap);
        mapTabSpecs.add(mapTabSpec);
//            bitmap.recycle();
        Log.i(TAG, "getAllThumbnailMap: " + mapTabSpec.getMapName() + ", size = " + mapTabSpecs.size());
        return mapTabSpecs;
    }
    /**
     * 抬起手指获取屏幕坐标.
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getRelift(UpliftScreenPosition upliftScreenPosition) {
        float eventGetRawX = upliftScreenPosition.getX();
        float eventGetRawY = upliftScreenPosition.getY();
//        Log.i("抬起手指", "X= " + eventGetRawX);
//        Log.i("抬起手指", "Y= " + eventGetRawY);
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getReconn(ReconnReason reconnReason) {
        LaserEntity laserEntity = new LaserEntity();
        laserEntity.getDistanceList();
        if (reconnReason != null) {
            String reason = reconnReason.getReason();
//            Log.i("buildMapTest", "偏振" + "getReconn: " + reason);
        }

    }
}
