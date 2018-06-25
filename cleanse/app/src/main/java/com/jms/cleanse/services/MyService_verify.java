package com.jms.cleanse.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import robot.boocax.com.sdkmodule.APPSend;
import robot.boocax.com.sdkmodule.TCP_CONN;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.setlog.SetLog;

/**
 * Created by zhoukan on 2018/3/27.
 *
 * @desc:
 */

public class MyService_verify extends Service {
    public MyService_verify() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new KEEPCONN()).start();
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 建立与服务器的TCP长连接线程;
     */
    class KEEPCONN implements Runnable {

        @Override
        public void run() {

            APPSend.initFiles(getApplicationContext());         // 初始化文件目录,存放服务器发来的文件;
            if (null != LoginEntity.serverIP) {
//                SetLog.recvJson_Debug = true;                   // 开启conn bug
                TCP_CONN.doTCPLoop(LoginEntity.serverIP,
                        LoginEntity.user_name, LoginEntity.password, LoginEntity.salt, true);
                //建立TCP长连接,参数依次为:服务器IP,用户名(可为null),密码(可为null),盐(可为null),boolean(是否自动请求文件);
            } else {
                // 不可能为空
            }

        }
    }
}
