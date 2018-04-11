package com.jms.cleanse.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.contract.PathEditContract;
import com.jms.cleanse.entity.db.PoiTask;
import com.jms.cleanse.entity.uiTest.PointSpec;
import com.jms.cleanse.presenter.PathEditPresenter;
import com.jms.cleanse.util.FileUtil;
import com.jms.cleanse.widget.JMMapView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @desc : 任务编辑
 */
public class PathEditActivity extends BaseActivity<PathEditPresenter> implements PathEditContract.View {


    CommonAdapter<PoiTask> adapter;
    //    CommonAdapter<PointSpec> pointSpecCommonAdapter;
    RecyclerView.Adapter pointSpecCommonAdapter;

    List<PointSpec> specs;
    List<PoiTask> taskEntities;
    @BindView(R.id.iv_exit)
    ImageView ivExit;
    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.task_rv)
    RecyclerView taskRv;
    @BindView(R.id.iv_task_delete)
    ImageView ivTaskDelete;
    @BindView(R.id.iv_task_add)
    ImageView ivTaskAdd;
    @BindView(R.id.map_view)
    JMMapView mapView;
    @BindView(R.id.task_list_layout)
    View taskListLayout;
    LinearLayout taskListController;
    @BindView(R.id.layout_task_controller)
    LinearLayout layoutTaskController;
    @BindView(R.id.tv_cleanse_notice)
    TextView tvCleanseNotice;
    @BindView(R.id.isCleanse)
    Switch isCleanse;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_end)
    Button btnEnd;
    @BindView(R.id.point_control_rv)
    LinearLayout pointControlRv;


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

        taskListController = ButterKnife.findById(taskListLayout, R.id.layout_task_controller);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMap();
    }

    @Override
    protected void initListeners() {
        taskRv.setHasFixedSize(false);
        taskRv.setLayoutManager(new LinearLayoutManager(this));
        taskRv.setAdapter(adapter);
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
            case R.id.iv_task_delete: // 删除任务
                break;
            case R.id.iv_task_add:    // 添加任务
                hideLeftLayout();
                addTask();
                break;
        }
    }

    @Override
    public void addTask() {
        mapView.setTaskEditing(true);
    }

    @Override
    public void taskComplete() {
        mapView.editComplete();
    }

    @Override
    public void addPoint() {

    }

    @Override
    public void loadMap() {
        byte[] mapBytes = FileUtil.readPng("map.png");
        if (mapBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mapBytes, 0, mapBytes.length);
            mapView.setMap(bitmap);
        }
    }

    @Override
    public void hideLeftLayout() {
        taskListLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLeftLayout() {
        taskListLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isCleansePoint() {
        return false;
    }

    @Override
    public void showNamedDialog() {

    }

    @Override
    public void dismissNamedDialog() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
