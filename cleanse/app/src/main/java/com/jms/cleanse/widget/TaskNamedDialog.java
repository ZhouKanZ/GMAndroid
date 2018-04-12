package com.jms.cleanse.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by zhoukan on 2018/4/11.
 *
 * @desc:
 */

public class TaskNamedDialog extends Dialog {

    public TaskNamedDialog(@NonNull Context context) {
        this(context,0);
    }

    public TaskNamedDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected TaskNamedDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
