package com.jms.cleanse.services;

import android.app.ActivityManager;
import android.content.Context;

import com.jms.cleanse.JMApplication;

/**
 * Created by zhoukan on 2018/6/29.
 *
 * @desc:
 */

public class ServiceUtils {

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) JMApplication.context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
