package com.jms.cleanse.util;

import android.os.Build;

/**
 * Created by zhoukan on 2018/3/27.
 *
 * @desc:
 */

public class SystemUtils {

    public static int getAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static boolean getTargetVersion() {
        return getAndroidVersion() <= 25;
    }

}
