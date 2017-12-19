package com.gps.gmdialog.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @Author : zhoukan
 * @CreateDate : 2017/12/19 0019
 * @Descriptiong : GMUIDialog 对话框一般由 {@link GMUIDialogBuilder} 及其子类创建, 不同的 Builder 可以创建不同类型的对话框,
 * 例如消息类型的对话框、菜单项对话框等等。
 */

public class GMUIDialog extends Dialog{

    public GMUIDialog(@NonNull Context context) {
        super(context);
    }

    public GMUIDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


}
