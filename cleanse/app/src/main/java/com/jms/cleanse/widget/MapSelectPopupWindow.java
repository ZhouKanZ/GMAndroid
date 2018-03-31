package com.jms.cleanse.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.jms.cleanse.R;
import com.jms.cleanse.entity.uiTest.MapInfo;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoukan on 2018/3/22.
 *
 * @desc:
 */

public class MapSelectPopupWindow extends PopupWindow {

    LayoutInflater inflater;
//    @BindView(R.id.map_rv)
    RecyclerView mapRv;
    CommonAdapter<String> adapter;
    Context context;
    List<String> mapInfos;
    View.OnClickListener onClickListener;

    public MapSelectPopupWindow(Context context) {
        super(context);
        initData();
        inflater = LayoutInflater.from(context);
        this.context = context;
        setContentView();

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void initData() {

        mapInfos = new ArrayList<>();
//        mapInfos.add(new MapInfo(R.drawable.map_selected, "大厅"));
//        mapInfos.add(new MapInfo(R.drawable.map_unselected, "厨房"));
//        mapInfos.add(new MapInfo(R.drawable.map_unselected, "更衣室"));
//        mapInfos.add(new MapInfo(R.drawable.map_unselected, "卫生间"));

    }

    private void setContentView() {

        View view = inflater.inflate(R.layout.map_select_popup, null);
        adapter = new CommonAdapter<String>(context, R.layout.item_map_info, mapInfos) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.tv_map_name, s);
                if (onClickListener != null) {
                    holder.setOnClickListener(R.id.item_map_layout, onClickListener);
                }
            }
        };

        mapRv = view.findViewById(R.id.map_rv);
        mapRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mapRv.setAdapter(adapter);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setAnimationStyle(R.style.popup_anim_mapselector);
        setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明

    }

    /**
     *  更新adapter
     */
    public void notifyAdapter(List<String> mapInfos){
        this.mapInfos.clear();
        this.mapInfos.addAll(mapInfos);
        adapter.notifyDataSetChanged();
    }


}
