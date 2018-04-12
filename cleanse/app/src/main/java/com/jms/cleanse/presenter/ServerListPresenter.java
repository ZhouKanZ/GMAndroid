package com.jms.cleanse.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.jms.cleanse.JMApplication;
import com.jms.cleanse.R;
import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.contract.ServerListContract;
import com.jms.cleanse.entity.robot.ServerEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import robot.boocax.com.sdkmodule.APPSend;
import robot.boocax.com.sdkmodule.TCP_CONN;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.AllFileInfoFilesList;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.AllRobotMacList;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.AllRobotNameList;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.NeedDeleteFilesList;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.ReconnReason;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.TempMapBytes;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.UDPList;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.All_robot_infos;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Charge_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Loc_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Move_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.OBD;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Pos_vel_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Register_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Report_fault_code;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Report_poi_status;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.Report_stat;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.System_message;
import robot.boocax.com.sdkmodule.utils.init_files.NaviContext;
import robot.boocax.com.sdkmodule.view.BoocaxMapView;

/**
 * Created by WangJun on 2018/3/21.
 */

public class ServerListPresenter extends BasePresenter<ServerListContract.ServerListView> implements ServerListContract.Presenter {

    private static final String TAG = "ServerListPresenter";
    public static SharedPreferences sp_curDoc;
    public static SharedPreferences.Editor editor_curDoc;//用于记录文件(服务器传来)

    public static List<ServerEntity> udpServerInfoList = new ArrayList<>();

    private Context context;

    public ServerListPresenter(Context context) {
        this.context = context;
    }


    /**
     * 实例化数据
     */
    @Override
    public void initData() {
        configuration();//配置页面
//        configuredView();//配置地图控件

        TCP_CONN.reconnTime = 5000;               //重连时间(不设置默认5000ms,上层可更改,建议3000ms以上).
    }

    private void configuredView() {
        // first question : why init here?
        BoocaxMapView.boocaxMapViewLeft = 0;
        BoocaxMapView.boocaxMapViewTop = 50;
        BoocaxMapView.boocaxMapViewRight = 1920;
        BoocaxMapView.boocaxMapViewBottom = 1080;                                   //实际BoocaxMapView的上下左右的屏幕坐标,根据页面动态获取
        BoocaxMapView.setCenterArea(0.2f);                                          //地图居中区域的占比（占BoocaxMapView）

        BoocaxMapView.setFirstShowMapMode(0);                                       //设置居中显示模式 0代表地图机器人居中显示(地图自动缩放) 1代表地图平铺展示到屏幕中
        BoocaxMapView.setChoseRobotPic(context, R.drawable.chooserobot);                 //设置选定机器人图标
        BoocaxMapView.setOtherRobotPic(context, R.drawable.otherrobot);                  //设置非选定机器人图标
        BoocaxMapView.setPOIPic(context, R.drawable.ic_poi);     //设置POI点图标资源
        BoocaxMapView.setLaserColor(80, 255, 255, 0);                               //设置激光扫过路径的颜色
        BoocaxMapView.setLaserPointColor(Color.RED);                                //设置激光探测位置的颜色
        BoocaxMapView.setRealPathColor(Color.GREEN);                                //设置real_path的颜色
        BoocaxMapView.setRealPahtSize(2.5f);                                        //设置real_path上点的半径
    }

    //SearchServerActivity页面配置
    private void configuration() {
//        SysApplication.getInstance().addActivity(this);     //记录Activity(Demo中用于退出程序kill所有activity)
        initContext();                                      //同步app和SDK环境;
        instanceData();                                     //实例化数据
//        initState();                                        //沉浸式状态栏
        EventBus.getDefault().register(this);               //注册EventBus

        LoginEntity.oneServerAllRobotName = new ArrayList<>();
        startUDP();                                         //开启UDP
        displayUDPListview();                               //展示Listview的列表
    }

    private void initContext() {
        if (NaviContext.context == null) {
            NaviContext.context = JMApplication.context;
        }
        //创建SharedPreferences,用于记录文件清单
        sp_curDoc = context.getSharedPreferences("recordDoc", context.MODE_PRIVATE);
        editor_curDoc = sp_curDoc.edit();
        //获取SharedPreferences地址
        if (TCP_CONN.sp_curDoc == null) {
            TCP_CONN.sp_curDoc = sp_curDoc;
        }
        if (TCP_CONN.editor_curDoc == null) {
            TCP_CONN.editor_curDoc = editor_curDoc;
        }
    }

    private void instanceData() {
        /**
         * (只有数据改变的时候才会传来)
         */
        LoginEntity.recvFileTypes = new ArrayList<>();
        LoginEntity.recvFileTypes.add("map.png");
        LoginEntity.recvFileTypes.add("restrict.dat");
        LoginEntity.recvFileTypes.add("anchor.dat");
        LoginEntity.recvFileTypes.add("poi.json");
        LoginEntity.recvFileTypes.add("agv_graph.json");//定义Android客户端接收的文件类型,SDK使用者根据自身客户端功能选择需要接收的文件

    }

    private void initCustomFile(){
        File file = new File(JMApplication.context.getFilesDir() + "/Boocax/curDoc", "poitask.json");

        if (file.exists() && file.isFile()) {
//            file.delete();
        }else {
            try {
                if (file.createNewFile()) {
                    Log.i(TAG, "initData: file is created" + file.getAbsolutePath());
                }else {
                    Log.i(TAG, "initData: file create failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    private void initState() {
//    }

    //开启UDP
    public void startUDP() {
        new Thread(() -> {
            TCP_CONN.isUDP = false;
            TCP_CONN.getUDPs();
        }).start();
    }

    private void displayUDPListview() {

    }


    @Override
    public RecyclerView.Adapter initAdapter() {
        return null;
    }


    /**
     * 接收poi状态反馈
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getPOIStatus(Report_poi_status report_poi_status) {
        if (report_poi_status != null) {
        }
    }


    /**
     * 收到新传来的Robot的Name
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getName(AllRobotNameList allRobotNameList) {
        if (allRobotNameList != null) {
            LoginEntity.oneServerAllRobotName = allRobotNameList.getList();

        }
    }

    /**
     * 收到新传来的Robot的Mac
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getMac(AllRobotMacList allRobotMacList) {
        if (allRobotMacList != null) {
            Log.d("mac", "getMac: " + allRobotMacList.getList().get(0));
            LoginEntity.robotMac = allRobotMacList.getList().get(0);
        }
    }


    /**
     * 收到新传来的allRobotInfo
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getAllRobotInfos(All_robot_infos.AllRobotInfoBean all_robot_info) {
        if (all_robot_info != null && all_robot_info.getMac_address() != null) {
            String mac_address = all_robot_info.getMac_address();
        }
    }

    /**
     * Move_status状态
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getMove_Status(Move_status move_status) {
        if (move_status != null && move_status.getMac_address() != null) {
//            AllRobotEntity.singleMacMoveMap.put(move_status.getMac_address(), move_status.getMove_status());
        }
    }

    /**
     * Loc_status状态
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void getLoc_Status(Loc_status loc_status) {
        if (loc_status != null && loc_status.getMac_address() != null) {
            if ((loc_status.getMac_address().equals(LoginEntity.robotMac))) {
                Log.i("loc反馈", "loc_status=" + loc_status.getLoc_status());

            }
        }
    }

    /**
     * Charge_status状态
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getCharge_Status(Charge_status charge_status) {
        if (charge_status != null && charge_status.getMac_address() != null) {
//            AllRobotEntity.singleMacChargeMap.put(charge_status.getMac_address(), charge_status.getCharge_status());
        }
    }

    /**
     * 需要删除的文件
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getNeedDeleteFiles(NeedDeleteFilesList needDeleteFilesList) {
        if (needDeleteFilesList != null) {
            for (int i = 0; i < needDeleteFilesList.getList().size(); i++) {
                String fileName = needDeleteFilesList.getList().get(0);
                editor_curDoc.remove(fileName);
                File file = new File(JMApplication.context + File.separator + "Boocax" + File.separator + fileName);
                if (file.isFile() && file.exists()) {                                           //删除旧文件列表相对于新文件列表多出的文件
                    file.delete();
                }
                editor_curDoc.commit();
                if (fileName.equals("poi.json")) {
                    //上层删除文件 + 清空变量.
                }
                if (fileName.equals("agv_graph.json")) {

                }
                if (fileName.equals("anchor.dat")) {

                }
                if (fileName.equals("restrict.dat")) {

                }
            }
        }
    }

    /**
     * all_file_info Json串当中包含的文件
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getAllFileInfoInclude(AllFileInfoFilesList allFileInfoFilesList) {
        if (allFileInfoFilesList != null) {
            //此方法接收的信息暂时并没有作用;
        }
    }

    /**
     * 接收注册反馈信息
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getRegister_status(Register_status register_status) {
        if (register_status != null) {
            if (register_status.getAuth_result().equals("succeeded")) {

            }
            if (register_status.getAuth_result().equals("failed")) {

            }
        }
    }

    /**
     * 接收重连的信息
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getReconn(ReconnReason reconnReason) {
        if (reconnReason != null) {
            if (reconnReason.getReason().equals("重连")) {
                instanceData();//重新初始化信息
            }
        }
    }

    /**
     * 充电错误码
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getCharge_fault_code(Report_fault_code report_fault_code) {
        if (report_fault_code != null) {
            int code = report_fault_code.getCode();
        }
    }

    /**
     * 系统反馈无权限操作
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getSystem_message(System_message system_message) {
        if (system_message != null) {

        }
    }


    /**
     * 里程
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getStat(Report_stat report_stat) {
        if (report_stat != null) {

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
        }
    }

    /**
     * OBD信息
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getOBD(OBD obd) {
        if (obd != null) {

        }
    }


    /**
     * map图片byte[]
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void getMapBytes(final TempMapBytes tempMapBytes) {
        if (tempMapBytes != null) {
            byte[] bytes = tempMapBytes.getBytes();
            //对bytes进行处理
            new Thread(new Runnable() {
                @Override
                public void run() {
                    APPSend.exeRepairPic(tempMapBytes.getBytes());
                }
            }).start();
        }
    }

    /**
     * UDP广播
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUDPInfo(UDPList udpList) {
        getView().notifyAdapter(udpList);
    }


    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestory() {
        EventBus.getDefault().unregister(this);
    }
}
