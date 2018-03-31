package com.jms.cleanse;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhoukan on 2018/3/26.
 *
 * @desc:
 */

public class JMApplication extends Application {


    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // 日志捕获
//        MyCrashHandler myCrashHandler = MyCrashHandler.getInstance();
//        myCrashHandler.init(getApplicationContext());
    }
}
