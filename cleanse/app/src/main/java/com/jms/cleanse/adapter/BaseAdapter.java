package com.jms.cleanse.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by zhoukan on 2018/6/8.
 *
 * @desc:
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public OnItemClickListener<T> onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
