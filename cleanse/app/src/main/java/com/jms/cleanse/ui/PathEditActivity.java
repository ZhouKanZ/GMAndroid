package com.jms.cleanse.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.contract.PathEditContract;
import com.jms.cleanse.entity.db.PoiTask;
import com.jms.cleanse.presenter.PathEditPresenter;
import com.jms.cleanse.util.FileUtil;
import com.jms.cleanse.widget.JMMapView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @desc : 任务编辑
 */
public class PathEditActivity extends BaseActivity<PathEditPresenter> implements PathEditContract.View {


    CommonAdapter<PoiTask> adapter;
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
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar_server_list)
    Toolbar toolbarServerList;

    Dialog namedDialog;
    View view;
    EditText etNamedTask;
    Button btnSure;
    Button btnCancel;

    private static int MODE_EDIT = 0X01;    // 编辑模式
    private static int MODE_LIST = 0X02;    // 列表模式

    private int current_mode = MODE_LIST;
    // 当前点是否是消毒点
    private boolean cleanseable = false;


    private String taskName;

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
            protected void convert(ViewHolder holder, PoiTask poiTask, int position) {
                holder.setText(R.id.tv_task_name, poiTask.name);
            }
        };

        isCleanse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cleanseable = isChecked;
            }
        }
        );
    }

    @Override
    protected void initListeners() {
        taskRv.setHasFixedSize(false);
        taskRv.setLayoutManager(new LinearLayoutManager(this));
        taskRv.setAdapter(adapter);

    }

    private void setAdapterItemListener(){

        /**
         * 任务列表点击监听
         */
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                PoiTask poiTask = taskEntities.get(position);
                taskName = poiTask.name;
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMap();
    }


    @OnClick({R.id.iv_exit, R.id.btn_start_task, R.id.iv_task_delete, R.id.iv_task_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_exit:
                if (current_mode == MODE_EDIT) {
                    current_mode = MODE_LIST;
                    showLeftLayout();
                    taskComplete();
                    hideRightLayout();
                } else {
                    PathEditActivity.this.finish();
                }
                break;
            case R.id.btn_start_task:
                mPresenter.executeTask(taskName);
                break;
            case R.id.iv_task_delete: // 删除任务
                break;
            case R.id.iv_task_add:    // 添加任务
                current_mode = MODE_EDIT;
                hideLeftLayout();
                showRightLayout();
                addTask();
                break;
            case R.id.btn_add:
                addPoint();
                break;
            case R.id.btn_end:
//                taskComplete();
                showNamedDialog();
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
        mapView.addPoint(isCleansePoint());
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
    public void showRightLayout() {
        pointControlRv.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRightLayout() {
        pointControlRv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void notifyAdapter(PoiTask newTask) {
        taskEntities.add(newTask);
        adapter.notifyDataSetChanged();
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
        return cleanseable;
    }

    @Override
    public void showNamedDialog() {
        namedDialog.show();
    }

    @Override
    public void dismissNamedDialog() {
        namedDialog.dismiss();
    }


}
