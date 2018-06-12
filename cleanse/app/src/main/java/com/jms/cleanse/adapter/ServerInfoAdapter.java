package com.jms.cleanse.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jms.cleanse.R;
import com.jms.cleanse.entity.robot.ServerEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhoukan on 2018/6/7.
 *
 * @desc: 服务列表的adapter
 */

public class ServerInfoAdapter extends BaseAdapter<ServerEntity> {

    public static int NORMOL_TYPE = 0X01;  // 正常的
    public static int WATTING_TYPE = 0X02; // 无数据
    private List<ServerEntity> serverEntities;
    private Context ctz;
    private LayoutInflater inflater;

    public ServerInfoAdapter(List<ServerEntity> serverEntities, Context ctz) {
        this.serverEntities = serverEntities;
        this.ctz = ctz;
        this.inflater = LayoutInflater.from(this.ctz);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder holder = null;
        if (viewType == WATTING_TYPE) {
            view = inflater.inflate(R.layout.item_server_nodata, null);
            holder = new NoDataViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_server_info, null);
            holder = new ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == WATTING_TYPE) {
            OnbindNodataViewHolder((NoDataViewHolder) holder, position);
        } else {
            OnbindViewHolder((ViewHolder) holder, position);
        }
    }

    private void OnbindViewHolder(ViewHolder holder, int position) {
        ServerEntity se = serverEntities.get(position);
        holder.tvServerIp.setText(se.getServerIp());
        holder.tvServerName.setText(se.getServerName());
        holder.layoutServerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.OnItemClick(se,position);
                }
            }
        });
    }

    private void OnbindNodataViewHolder(NoDataViewHolder holder, int position) {
        Glide.with(ctz)
                .load(R.drawable.waitudp)
                .into(holder.ivGif);
    }

    @Override
    public int getItemCount() {
        return serverEntities.size() == 0 ?1:serverEntities.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && serverEntities.size() == 0) {
            return WATTING_TYPE;
        }
        return NORMOL_TYPE;
    }

    public static class NoDataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_gif)
        ImageView ivGif;

        public NoDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_server_name)
        TextView tvServerName;
        @BindView(R.id.tv_server_ip)
        TextView tvServerIp;
        @BindView(R.id.layout_server_info)
        CardView layoutServerInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
