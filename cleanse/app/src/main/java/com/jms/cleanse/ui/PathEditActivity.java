package com.jms.cleanse.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.base.IPresenter;
import com.jms.cleanse.entity.uiTest.PointSpec;
import com.jms.cleanse.entity.uiTest.TaskEntity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PathEditActivity extends BaseActivity {


    CommonAdapter<TaskEntity> adapter;
    CommonAdapter<PointSpec> pointSpecCommonAdapter;

    List<PointSpec> specs;
    List<TaskEntity> taskEntities;
    @BindView(R.id.iv_exit)
    ImageView ivExit;
    @BindView(R.id.point_control_rv)
    RecyclerView pointControlRv;
    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.task_rv)
    RecyclerView taskRv;
    @BindView(R.id.iv_task_delete)
    ImageView ivTaskDelete;
    @BindView(R.id.iv_task_add)
    ImageView ivTaskAdd;

    @Override
    protected IPresenter loadPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_path_edit;
    }

    @Override
    protected void initData() {
        taskEntities = new ArrayList<>();
        taskEntities.add(new TaskEntity("大厅清扫任务", "三楼大厅", "2018-3-22(周二) 14:00", false));
        taskEntities.add(new TaskEntity("厕所清扫任务", "三楼大厅", "2018-3-22(周二) 14:00", false));
        taskEntities.add(new TaskEntity("卫生间清扫任务", "三楼大厅", "2018-3-22(周二) 14:00", false));
        taskEntities.add(new TaskEntity("茅坑清扫任务", "三楼大厅", "2018-3-22(周二) 14:00", false));
        taskEntities.add(new TaskEntity("洗手间清扫任务", "三楼大厅", "2018-3-22(周二) 14:00", false));
        taskEntities.add(new TaskEntity("浴室清扫任务", "三楼大厅", "2018-3-22(周二) 14:00", false));

        adapter = new CommonAdapter<TaskEntity>(this, R.layout.item_task_info, taskEntities) {
            @Override
            protected void convert(ViewHolder holder, TaskEntity taskEntity, int position) {
                holder.setText(R.id.tv_task_name, taskEntity.getTaskName());
                holder.setText(R.id.tv_task_location, taskEntity.getLocation());
                holder.setText(R.id.tv_create_date, taskEntity.getCreateTime());
            }
        };

        specs = new ArrayList<>();
        specs.add(new PointSpec("开始消毒"));
        specs.add(new PointSpec("停止消毒"));
        specs.add(new PointSpec("删除"));
        specs.add(new PointSpec("设为终点"));

        pointSpecCommonAdapter = new CommonAdapter<PointSpec>(this, R.layout.item_point_spec, specs) {
            @Override
            protected void convert(ViewHolder holder, PointSpec pointSpec, int position) {
                holder.setText(R.id.btn_control_spec, pointSpec.getName());
            }
        };

    }

    @Override
    protected void initListeners() {
        taskRv.setHasFixedSize(false);
        taskRv.setLayoutManager(new LinearLayoutManager(this));
        taskRv.setAdapter(adapter);

        pointControlRv.setHasFixedSize(false);
        pointControlRv.setLayoutManager(new LinearLayoutManager(this));
        pointControlRv.setAdapter(pointSpecCommonAdapter);
    }

    @OnClick({R.id.iv_exit, R.id.btn_start_task, R.id.iv_task_delete, R.id.iv_task_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_exit:
                this.finish();
                break;
            case R.id.btn_start_task:
                break;
            case R.id.iv_task_delete:
                break;
            case R.id.iv_task_add:
                break;
        }
    }
}
