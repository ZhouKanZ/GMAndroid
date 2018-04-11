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

import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.config.RobotConfig;
import com.jms.cleanse.contract.RobotMasterContract;
import com.jms.cleanse.entity.robot.LaserEntity;
import com.jms.cleanse.presenter.RobotMasterPresenter;
import com.jms.cleanse.util.DisplayUtil;
import com.jms.cleanse.util.FileUtil;
import com.jms.cleanse.widget.JMMapView;
import com.jms.cleanse.widget.MapSelectPopupWindow;
import com.jms.cleanse.widget.RockerView;
import com.jms.cleanse.widget.mapview.TestPOI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.All_map_info;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ExistMap;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.LongPressPositionEntity;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ReconnReason;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ShortPressScreenPosition;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.UpliftScreenPosition;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Charge_status;
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
    List<TestPOI> testPOIS;

    double[] speed = new double[3];

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
            popupWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                mPresenter.requestAllMapInfo();
                popupWindow.showAtLocation(layoutRobotMaster, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                // 隐藏sliderLayout
                layoutRightSider.setVisibility(View.GONE);
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
        byte[] mapBytes = FileUtil.readPng("map.png");
        if (mapBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mapBytes, 0, mapBytes.length);
            mapView.setMap(bitmap);
        }
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
        if (allMapInfo != null) {
            // 得到所有的地图信息
            popupWindow.notifyAdapter(allMapInfo.getAll_map_info());
        }
    }

    @Override
    public double[] getSpeed() {
        return speed;
    }

    private static double convertAngleToRadians(double angle) {
        return angle * (2 * (Math.PI / 360));
    }
}
