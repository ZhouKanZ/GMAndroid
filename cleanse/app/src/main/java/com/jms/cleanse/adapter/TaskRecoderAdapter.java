package com.jms.cleanse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jms.cleanse.R;
import com.jms.cleanse.bean.TaskRecoder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhoukan on 2018/6/1.
 *
 * @desc:
 */

public class TaskRecoderAdapter extends RecyclerView.Adapter<TaskRecoderAdapter.ViewHolder> {



    private Context ctz;
    private LayoutInflater inflater;
    private List<TaskRecoder> taskRecoders = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(TaskRecoder taskRecoder);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TaskRecoderAdapter(Context ctz, List<TaskRecoder> taskRecoders) {
        this.ctz = ctz;
        this.taskRecoders = taskRecoders;
        inflater = LayoutInflater.from(this.ctz);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_task_recoder, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskRecoder taskRecoder = taskRecoders.get(position);
        holder.tvTaskName.setText(taskRecoder.getTaskName());
        holder.tvExeDate.setText(taskRecoder.getStartTime());
        holder.tvKeepTime.setText(taskRecoder.getEndTime());
        holder.itemTaskRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(taskRecoder);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return taskRecoders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_map)
        ImageView ivMap;
        @BindView(R.id.tv_task_name)
        TextView tvTaskName;
        @BindView(R.id.tv_exe_date)
        TextView tvExeDate;
        @BindView(R.id.tv_keep_time)
        TextView tvKeepTime;
        @BindView(R.id.item_task_recorder)
        RelativeLayout itemTaskRecorder;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
