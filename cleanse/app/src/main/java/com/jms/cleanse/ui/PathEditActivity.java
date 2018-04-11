package com.jms.cleanse.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.contract.PathEditContract;
import com.jms.cleanse.entity.db.PoiTask;
import com.jms.cleanse.entity.uiTest.PointSpec;
import com.jms.cleanse.presenter.PathEditPresenter;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PathEditActivity extends BaseActivity<PathEditPresenter> implements PathEditContract.PathEditView{

    CommonAdapter<PoiTask> adapter;
    CommonAdapter<PointSpec> pointSpecCommonAdapter;

    List<PointSpec> specs;
    List<PoiTask> taskEntities;
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
    protected PathEditPresenter loadPresenter() {
        return new PathEditPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_path_edit;
    }

    @Override
    protected void initData() {

        taskEntities = new ArrayList<>();
//        mPresenter.objectBoxTest();
        taskEntities = mPresenter.loadData();

        adapter = new CommonAdapter<PoiTask>(this, R.layout.item_task_info, taskEntities) {
            @Override
            protected void convert(ViewHolder holder,PoiTask poiTask, int position) {
                holder.setText(R.id.tv_task_name, poiTask.name);
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

        pointSpecCommonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                switch (position) {
                    case 0:

                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

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
                mPresenter.executeTask("test");
                break;
            case R.id.iv_task_delete:
                break;
            case R.id.iv_task_add:
                mPresenter.objectBoxTest();
                break;
        }
    }
}
