package com.jms.cleanse.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.config.RobotConfig;
import com.jms.cleanse.contract.RobotMasterContract;
import com.jms.cleanse.entity.map.MapTabSpec;
import com.jms.cleanse.entity.robot.LaserEntity;
import com.jms.cleanse.presenter.RobotMasterPresenter;
import com.jms.cleanse.util.DisplayUtil;
import com.jms.cleanse.widget.BatteryBar;
import com.jms.cleanse.widget.JMMapView;
import com.jms.cleanse.widget.MapSelectPopupWindow;
import com.jms.cleanse.widget.RockerView;
import com.jms.cleanse.widget.mapview.TestPOI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.All_map_info;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ExistMap;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.LongPressPositionEntity;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ReconnReason;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ShortPressScreenPosition;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.TempMapBytes;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ThumbnailCache;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.UpliftScreenPosition;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Charge_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.OBD;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Pos_vel_status;

public class RobotMasterActivity extends BaseActivity<RobotMasterPresenter>
        implements RockerView.OnAngleChangeListener,
        RobotMasterContract.View {


    private static final String TAG = "RobotMasterActivity";

    @BindView(R.id.ib_task_list)
    ImageView ibTaskList;

    @BindView(R.id.ib_map_list)
    ImageView ibMapList;

    @BindView(R.id.ib_server_list)
    ImageView ibServerList;
    MapSelectPopupWindow popupWindow;

    @BindView(R.id.layout_robot_master)
    RelativeLayout layoutRobotMaster;

    @BindView(R.id.layout_right_sider)
    LinearLayout layoutRightSider;

    @BindView(R.id.map_view)
    JMMapView mapView;

    @BindView(R.id.rockerview)
    RockerView rockerview;

    @BindView(R.id.battery_bar)
    BatteryBar batteryBar;

    @BindView(R.id.test_iv)
    ImageView testIv;

    List<TestPOI> testPOIS;

    double[] speed = new double[3];
    private All_map_info allMapInfo;
    private List<MapTabSpec> mapTabSpecs;

    @Override
    protected RobotMasterPresenter loadPresenter() {
        return new RobotMasterPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_robot_master;
    }

    @Override
    protected void initData() {

        mPresenter.initData();
        mapTabSpecs = new ArrayList<>();
        popupWindow = new MapSelectPopupWindow(this);
        EventBus.getDefault().register(this);
        layoutRobotMaster.post(new Runnable() {
            @Override
            public void run() {
                int layoutWidth = layoutRobotMaster.getMeasuredWidth();
                int layoutHeight = layoutRobotMaster.getMeasuredHeight();
                float toolBarHeight = DisplayUtil.dip2px(RobotMasterActivity.this, 33);
            }
        });
    }

    @Override
    protected void initListeners() {

        if (popupWindow != null) {
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    layoutRightSider.setVisibility(View.VISIBLE);
                }
            });
            popupWindow.setOnClickListener(new MapSelectPopupWindow.OnClickListener() {
                @Override
                public void onClick(int position) {
                    mPresenter.popupWindowOnClick(position);
                    popupWindow.dismiss();
                }
            });
        }
        rockerview.setOnAngleChangeListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.ib_task_list, R.id.ib_map_list, R.id.ib_server_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_task_list:
                startActivity(new Intent(RobotMasterActivity.this, PathEditActivity.class));
                break;
            case R.id.ib_map_list:
                // 唤起popwindow
                mPresenter.showMapList();
                popupWindow.showAtLocation(layoutRobotMaster, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                // 隐藏sliderLayout
                layoutRightSider.setVisibility(View.GONE);
                popupWindow.notifyAdapter(mapTabSpecs);
                break;
            case R.id.ib_server_list:
                // 绘制point
                break;
        }
    }

    /**
     * 长按传来的位置坐标(实际坐标)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLongPress(LongPressPositionEntity longPressPositionEntity) {
        if (longPressPositionEntity != null) {
        }
    }

    /**
     * SDK传来是否有地图可供显示.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getExistMap(ExistMap existMap) {
        if (existMap != null) {
//            Log.i("getExistMap", "=" + existMap.getMapStatus());
            //=1 代表含有地图  =2 代表没有地图
        }
    }

    /**
     * 短按屏幕获取屏幕坐标.
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getShortPress(ShortPressScreenPosition shortPressScreenPosition) {
        if (shortPressScreenPosition != null) {
            float eventGetRawX = shortPressScreenPosition.getX();
            float eventGetRawY = shortPressScreenPosition.getY();
//            Log.i("短按", "X=" + eventGetRawX);
//            Log.i("短按", "y=" + eventGetRawY);
        }
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

    @Override
    public void onStarting() {
        mPresenter.doLoopSendMove();
    }


    /**
     * 速度与位置信息
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getVelPose(Pos_vel_status pos_vel_status) {
        if (pos_vel_status != null) {
            double poseX = pos_vel_status.getPose().getX();
            double poseY = pos_vel_status.getPose().getY();
            double poseYaw = pos_vel_status.getPose().getYaw();

            double vx = pos_vel_status.getVel().getVx();
            double vy = pos_vel_status.getVel().getVy();
            double vtheta = pos_vel_status.getVel().getVtheta();

            mapView.setPos(pos_vel_status.getPose());
            // 更新机器人位置
            Log.d(TAG, "getVelPose: x" + poseX + "y" + poseY);
        }
    }


    /**
     * @param angle  角度[0,360)
     * @param length [0,R-r]
     */
    @Override
    public void change(double angle, float length) {
        // 机器人的线速度 vx
        speed[0] = -Math.sin(convertAngleToRadians(angle)) * length * RobotConfig.MAX_VEL;
        // vy 没有意义 设置为0
        speed[1] = 0;
        // 机器人的角速度
        speed[2] = -Math.cos(convertAngleToRadians(angle)) * RobotConfig.MAX_ANGULAR_SPEED;
        Log.d(TAG, "change: vx:" + "angle:" + angle + "length:" + length + speed[0] + "vy:" + speed[1] + "vr:" + speed[2]);
    }

    @Override
    public void onFinish() {
        mPresenter.cancelLoop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMap();
    }

    private void loadMap() {
        mapView.setMap(mPresenter.loadMapPng());
    }

    // 充电状态
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getCharge_Status(Charge_status charge_status) {
        if (charge_status != null && !charge_status.getMac_address().equals("")) {
            Log.d(TAG, "getCharge_Status: " + charge_status.getCharge_status());
        }
    }


    // 所有地图的信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllMapInfo(All_map_info allMapInfo) {


        Gson gson = new Gson();
        Log.i(TAG, "getAllMapInfo: " + allMapInfo.getAll_map_info().size());
        if (mapTabSpecs.size() == allMapInfo.getAll_map_info().size()) {
            return;
        }

        if (allMapInfo != null) {
            this.allMapInfo = allMapInfo;
            // 需要判断是否相同并只加入不同的地图到其中
//            compareOriginMapSpec(this.allMapInfo.getAll_map_info());
            for (String mapName : allMapInfo.getAll_map_info()) {
                mPresenter.notifyAskNewBitmap(mapName);
            }
        }
    }

    /**
     * 判断是否有相同的内容，如果存在不同的就添加到集合中
     *
     * @param all_map_info
     */
    private void compareOriginMapSpec(List<String> all_map_info) {
        // 相同
        boolean isSame = false;

        int originSize = mapTabSpecs.size();

        for (int i = 0; i < all_map_info.size(); i++) {
            isSame = false;
            String s = all_map_info.get(i);
            for (int j = 0; j < mapTabSpecs.size(); j++) {
                String name = mapTabSpecs.get(j).getMapName();
                // 相等
                if (name.equals(s)) {
                    isSame = true;
                    break;
                }
            }

            // 如果不相同添加到list中
            if (!isSame || mapTabSpecs.isEmpty()) {
                MapTabSpec mapTabSpec = new MapTabSpec();
                mapTabSpec.setMapName(s);
                mapTabSpecs.add(mapTabSpec);
                // 并且请求对应地图的缩略图
                Log.i(TAG, "compareOriginMapSpec: ");
                mPresenter.notifyAskNewBitmap(s);
            }
        }

        int nowSize = mapTabSpecs.size();
        //
        if (nowSize > originSize) {
            popupWindow.notifyAdapter(mapTabSpecs);
        }

    }

    // 得到缩略图
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllThumbnailMap(ThumbnailCache thumbnailCache) {

        mapTabSpecs = mPresenter.getAllThumbnailMaps(thumbnailCache);
        if (mapTabSpecs.size() == allMapInfo.getAll_map_info().size()) {
            popupWindow.notifyAdapter(mapTabSpecs);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getOBD(OBD obd) {

        Gson gson = new Gson();
        String s = gson.toJson(obd);
        String[] obds = obd.getObd().split(" ");
        Observable.interval(0, 1, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        batteryBar.updateCharge(Float.valueOf(obds[5]));
//                        Log.i(TAG, "getOBD: " + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "update charge error " + throwable.getMessage());
                    }
                });
    }

    @Override
    public double[] getSpeed() {
        return speed;
    }

    private static double convertAngleToRadians(double angle) {
        return angle * (2 * (Math.PI / 360));
    }

    /**
     * map图片byte[]
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void getMapBytes(final TempMapBytes tempMapBytes) {
        Log.i(TAG, "getMapBytes: ");
        if (tempMapBytes != null) {
            byte[] bytes = tempMapBytes.getBytes();
            Observable.just(BitmapFactory.decodeByteArray(bytes, 0, bytes.length))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            mapView.setMap(bitmap);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.i(TAG, "apply map error " + throwable.getMessage());
                        }
                    });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getConnectState(ReconnReason reason) {

        Log.i(TAG, "getConnectState: " + reason.getReason());
    }
}
