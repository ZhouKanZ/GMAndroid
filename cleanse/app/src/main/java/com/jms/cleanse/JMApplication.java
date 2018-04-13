package com.jms.cleanse;

import android.app.Application;
import android.content.Context;

import com.jms.cleanse.entity.db.MyObjectBox;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import robot.boocax.com.sdkmodule.setlog.SetLog;

/**
 * Created by zhoukan on 2018/3/26.
 *
 * @desc:
 */

public class JMApplication extends Application {


    public static Context context;
    private static BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        boxStore = MyObjectBox.builder().androidContext(this).build();
        SetLog.APPSend_Debug = true;
//        SetLog.recvJson_Debug = true;
        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(boxStore).start(this);
        }

        // 日志捕获
//        MyCrashHandler myCrashHandler = MyCrashHandler.getInstance();
//        myCrashHandler.init(getApplicationContext());
    }

    public static BoxStore getBoxStore() {
        return boxStore;
    }
}
