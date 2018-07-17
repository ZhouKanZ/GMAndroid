package com.jms.cleanse.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jms.cleanse.JMApplication;

import java.io.IOException;

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

    private static final String TAG = "MyService_verify";
    
    private Thread t;

    public MyService_verify() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if ( t!=null && t.isAlive()){
            t.interrupt();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        t =new Thread(new KEEPCONN());
        t.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public class KEEPCONN implements Runnable {

        @Override
        public void run() {
            APPSend.initFiles(JMApplication.context);         // 初始化文件目录,存放服务器发来的文件;
            if (null != LoginEntity.serverIP) {
                // avoid start multiple thread keep TCP
                if (TCP_CONN.channel != null && TCP_CONN.channel.isConnected()) {
                    try {
                        TCP_CONN.channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                SetLog.recvJson_Debug = true;
                TCP_CONN.doTCPLoop(LoginEntity.serverIP,
                        LoginEntity.user_name, LoginEntity.password, LoginEntity.salt, true);
                //建立TCP长连接,参数依次为:服务器IP,用户名(可为null),密码(可为null),盐(可为null),boolean(是否自动请求文件);
            } else {
                // 不可能为空
            }
        }
    }
}



