package com.jms.cleanse.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.jms.cleanse.R;

/**
 * Created by zhoukan on 2018/4/18.
 *
 * @desc:
 */

public class PointEditPopupWindow extends PopupWindow {

    private static final String TAG = "PointEditPopupWindow";

    private View view;
    private LayoutInflater inflater;
    private Context context;

    public PointEditPopupWindow(Context context) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        setContentView();
    }

    /**
     * 设置view
     */
    private void setContentView() {

        view = inflater.inflate(R.layout.dialog_edit_point, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setAnimationStyle(R.style.popup_anim_mapselector);
        setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明

    }
}
