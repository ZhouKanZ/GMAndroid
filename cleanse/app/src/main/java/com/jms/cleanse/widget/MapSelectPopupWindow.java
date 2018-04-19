package com.jms.cleanse.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bumptech.glide.request.RequestOptions;
import com.jms.cleanse.R;
import com.jms.cleanse.entity.map.MapTabSpec;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoukan on 2018/3/22.
 *
 * @desc:
 */

public class MapSelectPopupWindow extends PopupWindow {

    private static final String TAG = "MapSelectPopupWindow";
    LayoutInflater inflater;
    //    @BindView(R.id.map_rv)
    RecyclerView mapRv;
    CommonAdapter<MapTabSpec> adapter;
    Context context;
    List<MapTabSpec> mapInfos;
    OnClickListener onClickListener;
    private MultiItemTypeAdapter.OnItemClickListener itemClickListener;

    public MapSelectPopupWindow(Context context) {
        super(context);
        initData();
        inflater = LayoutInflater.from(context);
        this.context = context;
        setContentView();

    }

    public interface OnClickListener {
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnItemClickListener(MultiItemTypeAdapter.OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    private void initData() {

        mapInfos = new ArrayList<>();
    }

    private void setContentView() {

        View view = inflater.inflate(R.layout.map_select_popup, null);
        adapter = new CommonAdapter<MapTabSpec>(context, R.layout.item_map_info, mapInfos) {
            @Override
            protected void convert(ViewHolder holder, MapTabSpec s, int position) {
                holder.setText(R.id.tv_map_name, s.getMapName());
                holder.setImageBitmap(R.id.iv_map_icon,s.getMap());
                holder.setOnClickListener(R.id.item_map_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickListener != null) {
                            onClickListener.onClick(position);
                        }
                    }
                });

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.mipmap.ic_launcher);
                requestOptions.error(R.drawable.map_unselected);

               /* Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(s.getMapBytes())
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                holder.setImageDrawable(R.id.iv_map_icon, resource);
                            }
                        });*/
//                }
            }
        };
//        adapter.setOnItemClickListener(itemClickListener);
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
     * 更新adapter
     */
    public void notifyAdapter(List<MapTabSpec> mapInfos) {
        if (mapInfos == null) {
            return;
        }
        this.mapInfos.clear();
        this.mapInfos.addAll(mapInfos);
        adapter.notifyDataSetChanged();
    }

    public void notifyItemChange(int position) {
        adapter.notifyItemChanged(position);
    }

}
