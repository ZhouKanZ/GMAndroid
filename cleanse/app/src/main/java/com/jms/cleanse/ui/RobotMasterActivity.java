package com.jms.cleanse.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.bean.MSG_TYPE;
import com.jms.cleanse.bean.TaskExecution;
import com.jms.cleanse.config.RobotConfig;
import com.jms.cleanse.contract.RobotMasterContract;
import com.jms.cleanse.entity.file.POIJson;
import com.jms.cleanse.entity.file.POIPoint;
import com.jms.cleanse.entity.file.POITask;
import com.jms.cleanse.entity.map.MapTabSpec;
import com.jms.cleanse.presenter.RobotMasterPresenter;
import com.jms.cleanse.util.FileUtil;
import com.jms.cleanse.widget.BatteryBar;
import com.jms.cleanse.widget.JMMapView;
import com.jms.cleanse.widget.MapSelectPopupWindow;
import com.jms.cleanse.widget.RockerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import robot.boocax.com.sdkmodule.APPSend;
import robot.boocax.com.sdkmodule.TCP_CONN;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.All_map_info;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ExistMap;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.Map_param;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.OtherJson;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ReconnReason;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.TempMapBytes;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ThumbnailCache;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.All_file_info;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Charge_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Loc_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Move_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.OBD;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Pos_vel_status;
import robot.boocax.com.sdkmodule.utils.sdk_utils.SendUtil;

import static com.jms.cleanse.bean.CustomCommandKt.appendCustomCommand;


public class RobotMasterActivity extends BaseActivity<RobotMasterPresenter>
        implements RockerView.OnAngleChangeListener,
        RobotMasterContract.View {

    private static final String TAG = "RobotMasterActivity";
    private static final int REQUEST_CODE = 0X01;


    @BindView(R.id.tv_loc)
    TextView tvLoc;
    @BindView(R.id.tv_task)
    TextView tvTask;
    @BindView(R.id.tv_conn)
    TextView tvConn;
    @BindView(R.id.tv_pos)
    TextView tvPos;
    @BindView(R.id.tvElectricity)
    TextView tvElectricity;
    @BindView(R.id.ib_task_list)
    ImageView ibTaskList;
    @BindView(R.id.ib_map_list)
    ImageView ibMapList;
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
    @BindView(R.id.tv_master_model)
    TextView tvMasterModel;
    @BindView(R.id.switch_upper_computer)
    Switch switchUpperComputer;
    @BindView(R.id.iv_urgent)
    ImageView ivUrgent;
    @BindView(R.id.reset)
    ImageView reset;
    @BindView(R.id.ib_plan_list)
    ImageView ibPlanList;
    @BindView(R.id.ib_recorder_list)
    ImageView ibRecorderList;
    @BindView(R.id.cancel)
    ImageView cancel;

    private All_map_info allMapInfo;
    private List<MapTabSpec> mapTabSpecs;
    private boolean isChecked = false;
    private double[] speed = new double[3];
    private int size = -1;     // 任务点的长度
    private int goneTimes = 0; //到达的次数
    private int state = 1; // 0 1 present the state of control 0:auto 1:manu
    private int range = 8;// 表示角度的浮动范围

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
        switchUpperComputer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RobotMasterActivity.this.isChecked = isChecked;
                mPresenter.motor_onoff();
//                progressBar.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.ib_task_list, R.id.ib_map_list, R.id.iv_urgent, R.id.reset, R.id.ib_plan_list, R.id.ib_recorder_list,R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_task_list:
                // START ACTIVITY FOR RESULT ....
                if (stateJudge() == 1) {
                    startActivityForResult(new Intent(RobotMasterActivity.this, PathEditActivity.class), REQUEST_CODE);
                }
                break;
            case R.id.ib_map_list:
                // 唤起popwindow
                if (stateJudge() == 1) {
                    mPresenter.showMapList();
                    popupWindow.showAtLocation(layoutRobotMaster, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    // 隐藏sliderLayout
                    layoutRightSider.setVisibility(View.GONE);
                    popupWindow.notifyAdapter(mapTabSpecs);
                }

                break;
            case R.id.iv_urgent:
                    taskFinished();
                break;
            case R.id.reset:

                if (stateJudge() == 1) {
                    // 触发旋转一周的动作
                    startAnim();
                    mPresenter.reset();
                }

                break;
            case R.id.ib_plan_list:

                break;
            case R.id.ib_recorder_list:

                if (stateJudge() == 1) {
                    // 跳转至日志页面
                    startActivity(new Intent(RobotMasterActivity.this, TaskRecorderActivity.class));
                }

            case R.id.cancel:

//              if (stateJudge() == 1) {
//                    // 跳转至日志页面
                mPresenter.cancelGoal();
                state = 1;
//               }
                break;
        }
    }

    private int stateJudge() {
        if (state == 0) {
            Toast.makeText(RobotMasterActivity.this, "operation is not allowed on current mode !", Toast.LENGTH_SHORT).show();
        }
        return state;
    }

    private void taskFinished() {

        state = 1;
        goneTimes = 0;
        size = -1;
        mPresenter.cancelGoal();
        // 将rockerView
        showRockerView();
        switchMode(0);
        hideUrgentImage();
        clearPath();
        enableSilder(true);
    }

    /**
     * 开启动画
     */
    private void startAnim() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(reset, "rotation", 0, 360);
        animation
                .setDuration(500)
                .start();
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
     * 获取网络连接状态
     *
     * @param reconnReason
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getReconn(ReconnReason reconnReason) {
        if (reconnReason != null) {
            String reason = reconnReason.getReason();
            Log.d("buildMapTest", "偏振" + "getReconn: " + reason);
            switch (reason) {
                case "overtime":
                    tvConn.setTextColor(getResources().getColor(R.color.warning));
                    tvConn.setText("连接超时");
                    break;
                case "normal":
                    tvConn.setTextColor(getResources().getColor(R.color.info));
                    tvConn.setText("连接正常");
                    break;
                case "exception":
                    tvConn.setTextColor(getResources().getColor(R.color.error));
                    tvConn.setText("网络异常");
                    // 开启计时线程 超过等待时间即认为 连接已经断开并提示重连，或者检查机器人是否通电 15s
                    break;
            }
        }
    }


    /**
     * 机器人定位状态
     *
     * @param loc_status
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getLocStatus(Loc_status loc_status) {
        if (null != loc_status) {

            switch (loc_status.getLoc_status()) {
                case 0://定位正常
                    tvPos.setTextColor(getResources().getColor(R.color.info));
                    tvPos.setText("定位正常");
                    break;
                case 1:// 正在尝试定位
                    tvPos.setTextColor(getResources().getColor(R.color.warning));
                    tvPos.setText("尝试定位");
                    break;
                case 2:// 还未构建地图无法定位
                    break;
                case 3:// 正在构建地图中
                    break;
                case 4:// UWB错误
                    break;
                case 5:// 正在回环检测，优化地图
                    break;
            }
        }
    }

    //    Move_status 导航状态
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getMoveStatus(Move_status move_status) {
        if (null != move_status) {
            Log.d(TAG, "getMoveStatus: " + move_status.getMove_status());
            switch (move_status.getMove_status()) {
                case 0:// 静止待命
                    tvLoc.setTextColor(getResources().getColor(R.color.info));
                    tvLoc.setText("等待命令");
                    break;
                case 1:// 上次目标失败，等待新的导航任务 -- 导航失败
                    tvLoc.setTextColor(getResources().getColor(R.color.error));
                    tvLoc.setText("导航失败");

                    break;
                case 2:// 上次目标完成，等待新的导航任务 -- 导航完成
                    Log.d(TAG, "getMoveStatus: times: " + goneTimes + ",size:" + size);
                    // 所有的点都已经到达
                    if (goneTimes == size) {
                        showRockerView();
                        switchMode(0);
                        hideUrgentImage();
                        clearPath();
                        enableSilder(true);
                        goneTimes = 0;
                        size = -1;
                        state = 1;
                    }
                    tvLoc.setTextColor(getResources().getColor(R.color.info));
                    tvLoc.setText("导航完成");
                    break;
                case 3:// 移动中
                    tvLoc.setTextColor(getResources().getColor(R.color.info));
                    tvLoc.setText("移动中");
                    break;
                case 4:// 前方障碍物
                    tvLoc.setTextColor(getResources().getColor(R.color.warning));
                    tvLoc.setText("前方障碍物");
                    break;
                case 5:// 目的地被遮挡
                    tvLoc.setTextColor(getResources().getColor(R.color.warning));
                    tvLoc.setText("被遮挡");
                    break;
                case 6:// 导航取消
                    tvLoc.setTextColor(getResources().getColor(R.color.warning));
                    tvLoc.setText("导航取消");
                    break;
                case 7:// 新目标点
                    // 执行任务期间才执行++
                    if (size > 0) {
                        goneTimes++;
                    }
                    tvLoc.setTextColor(getResources().getColor(R.color.info));
                    tvLoc.setText("新目标点");
                    break;
                case 8:// 导航路径阻塞
                    tvLoc.setTextColor(getResources().getColor(R.color.warning));
                    tvLoc.setText("路径阻塞");
                    break;
            }
        }
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

        Gson gson = new Gson();
        Log.i(TAG, "getAllMapInfo: " + allMapInfo.getAll_map_info().toString());

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

    // 所有地图的信息
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getMapinfo(Map_param map_param) {
        if (null != map_param) {
            All_file_info.MapInfoBean bean = map_param.getMapParam();
            RobotConfig.current_uuid = bean.getUuid();
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
        } else {

        }

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getOBD(OBD obd) {

        if (obd == null || obd.getObd() == null) {
            return;
        }

        Gson gson = new Gson();
        String s = gson.toJson(obd);
        String[] obds = obd.getObd().split(" ");
        Observable.interval(0, 1, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        float ele = Float.valueOf(obds[5]);
                        ele = 75;
                        batteryBar.updateCharge(ele);
                        tvElectricity.setText((int) Math.ceil((double) ele) + "%");
//                        Log.i(TAG, "getOBD: " + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "update charge error " + throwable.getMessage());
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getOtherJson(OtherJson otherJson) {
        if (null != otherJson) {
            String str = otherJson.getOtherJson();
            Log.d(TAG, "getOtherJson: " + str);
        }
    }

    @Override
    public double[] getSpeed() {
        return speed;
    }

    @Override
    public void showLoadMap(String mapName) {
    }

    @Override
    public void setLocalMapName(String mapName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLoc.setText(mapName);
            }
        });
    }

    @Override
    public boolean getchecked() {
        return this.isChecked;
    }

    private static double convertAngleToRadians(double angle) {
        return angle * (2 * (Math.PI / 360));
    }


    /**
     * 接收返回的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String taskName = data.getStringExtra(PathEditActivity.KEY_TASK_NAME);
            Log.d(TAG, "onActivityResult: " + taskName);
            queryTask(taskName);
        }

    }

    /**
     * 查询任务
     *
     * @param taskName
     */
    private void queryTask(String taskName) {
        Log.d(TAG, "queryTask: " + taskName);
        setCurrentTaskName(taskName);
        POIJson poiJson = FileUtil.readFileJM(FileUtil.POI_JSON);
        for (POITask task : poiJson.getTasks()) {
            if (taskName.equals(task.getName())) {
                showTaskPath(task.getPoiPoints(), taskName);
                return;
            }
        }
    }

    /**
     * 设置当前任务的名称
     *
     * @param taskName
     */
    private void setCurrentTaskName(String taskName) {

        tvTask.setText(taskName);
    }

    /**
     * 执行任务
     */
    private void executeTask(String taskName) {

        // convert state to auto mode
        state = 0;

        Observable.just(taskName)
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String s) throws Exception {
                        TaskExecution te = new TaskExecution(s);
                        String teCommandStr = appendCustomCommand(te, LoginEntity.robotMac, MSG_TYPE.disinfection_task_exec);
                        boolean flag = SendUtil.send(teCommandStr, TCP_CONN.channel);
                        return flag;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->
                                Toast.makeText(RobotMasterActivity.this, s ? "数据成功发送" : "网络故障请稍后重试", Toast.LENGTH_SHORT).show()
                        , e -> e.fillInStackTrace());

    }

    /**
     * @param mode 0 1 分别表示 手动模式和自动模式
     */
    private void switchMode(int mode) {
        if (mode == 0) {
            tvMasterModel.setText(R.string.manualMode);
        } else {
            tvMasterModel.setText(R.string.automaticMode);
        }
    }


    /**
     * @param enable
     */
    private void enableSilder(boolean enable) {
        layoutRightSider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return enable;
            }
        });
    }

    // 显示急停按钮
    private void showUrgentImage() {
        ivUrgent.setVisibility(View.VISIBLE);
    }

    private void hideRockerView() {
        rockerview.setVisibility(View.GONE);
    }

    /**
     * 显示任务路径
     */
    private void showTaskPath(List<POIPoint> points, String taskName) {
        // 保存任务点的个数
        goneTimes = 0;
        size = points.size();
        mapView.setTestPOIS(points);
        switchMode(1);
        executeTask(taskName);
        // 隐藏操作轮盘
        hideRockerView();
        showUrgentImage();
        enableSilder(false);
    }

    /**
     * 清空路径
     */
    private void clearPath() {
        mapView.setTestPOIS(null);
    }

    private void hideUrgentImage() {
        ivUrgent.setVisibility(View.GONE);
    }

    private void showRockerView() {
        rockerview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStarting() {
        mPresenter.doLoopSendMove();
    }

    /**
     * @param angle  角度[0,360)  角度和android坐标系相同 ps：在正方向的左右5度之间 当作正方向来处理
     * @param length [0,R-r]
     */
    @Override
    public void change(double angle, float length) {

        Log.d(TAG, "change: "+angle);
        // 设置精确度
        DecimalFormat df = new DecimalFormat("#0.000");

        // 当作0
        if (angle < range || angle > 360 - range){
            angle = 0;
        }

        // 当作0
        if (angle  <90+ range && angle > 90-range){
            angle = 90;
        }

        // 当作0
        if (angle  <180+ range && angle > 180-range){
            angle = 180;
        }

        // 当作0
        if (angle  <270+ range && angle > 270-range){
            angle = 270;
        }

        // 机器人的线速度 vx
        speed[0] = Double.valueOf(df.format(-Math.sin(convertAngleToRadians(angle)) * length * RobotConfig.MAX_VEL));
        // vy 没有意义 设置为0
        speed[1] = 0;
        // 机器人的角速度
        speed[2] = Double.valueOf(df.format(-Math.cos(convertAngleToRadians(angle)) * RobotConfig.MAX_ANGULAR_SPEED));






    }

    @Override
    public void onFinish() {

        for (int i = 0; i <= 3; i++) {
            Observable.just(speed)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(s ->
                            APPSend.sendMove(LoginEntity.robotMac, 0, 0, 0), e -> {
                    });
        }

        mPresenter.cancelLoop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        loadMap();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    private void loadMap() {
        mapView.setMap(mPresenter.loadMapPng());
    }

}
