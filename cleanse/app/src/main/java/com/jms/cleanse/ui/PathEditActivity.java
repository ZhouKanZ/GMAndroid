package com.jms.cleanse.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.contract.PathEditContract;
import com.jms.cleanse.entity.file.POIPoint;
import com.jms.cleanse.entity.file.POITask;
import com.jms.cleanse.presenter.PathEditPresenter;
import com.jms.cleanse.util.FileUtil;
import com.jms.cleanse.widget.AngleWheelView;
import com.jms.cleanse.widget.JMMapView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
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


    public static String KEY_TASK_NAME = "key_task_name";

    CommonAdapter<POITask> adapter;
    List<POITask> taskEntities;
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

    /* dialog中的view */
    Dialog namedDialog;
    Dialog editDialog;
    EditText etNamedTask;
    Button btnSure;
    Button btnCancel;
    AngleWheelView angleWheelView;
//    Switch isCleanse;
    EditText editText;
    Button btn;
    ImageView imageView;
    ImageView imageView2;

    private static int MODE_EDIT = 0X01;    // 编辑模式
    private static int MODE_LIST = 0X02;    // 列表模式
    @BindView(R.id.layout)
    RelativeLayout layout;

    private int current_mode = MODE_LIST;
    // 当前点是否是消毒点
    private boolean cleanseable = false;


    private String taskName;
    private POITask seletedTask = null;

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
        taskEntities = mPresenter.loadData();
        adapter = new CommonAdapter<POITask>(this, R.layout.item_task_info, taskEntities) {
            @Override
            protected void convert(ViewHolder holder, POITask poiTask, int position) {
                holder.setText(R.id.tv_task_name, poiTask.getName());
            }
        };

        // todo dialog 的大小异常
        namedDialog = new Dialog(this);
        namedDialog.setContentView(R.layout.dialog_task_name);

        editDialog = new Dialog(this);
        editDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.dialog_edit_point);

        hideRightLayout();
        ivTaskDelete.setEnabled(false);
    }

    @Override
    protected void initListeners() {
        taskRv.setHasFixedSize(false);
        taskRv.setLayoutManager(new LinearLayoutManager(this));
        taskRv.setAdapter(adapter);

        etNamedTask = namedDialog.findViewById(R.id.et_task_named);
        btnSure = namedDialog.findViewById(R.id.btn_sure);
        btnCancel = namedDialog.findViewById(R.id.btn_cancel);

        angleWheelView = editDialog.findViewById(R.id.angleWheelView);
//        isCleanse = editDialog.findViewById(R.id.switch1);
        editText = editDialog.findViewById(R.id.editText);
        btn = editDialog.findViewById(R.id.button);
        imageView = editDialog.findViewById(R.id.imageView);
        imageView2 = editDialog.findViewById(R.id.imageView2);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angleWheelView.addAngle();
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angleWheelView.subAngle();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取角度、停留时间、是否消毒
                String strTime = editText.getEditableText().toString();
                if (!TextUtils.isEmpty(strTime)) {
                    long time = Long.valueOf(strTime);
                    mapView.addPoint(time != 0, time, angleWheelView.getCurrentAngle());
                    editDialog.dismiss();
                } else {
                    Toast.makeText(PathEditActivity.this, "停留时间不可为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSure.setOnClickListener(v -> namedTask());
        btnCancel.setOnClickListener(v -> dismissNamedDialog());
        setAdapterItemListener();
    }


    /**
     * 为任务命名
     */
    private void namedTask() {

        String name = etNamedTask.getEditableText().toString().trim();

        // 任务没有添加点
        if (mapView.getTestPOIS().size() == 0) {
            Toast.makeText(this, "不要忘记给任务添加消毒路径!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 任务没有添加点
        if (mapView.getTestPOIS().size() < 2) {
            Toast.makeText(this, "路径任务最低包含两个点!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "不要忘记给它命名了", Toast.LENGTH_SHORT).show();
        } else {
            taskName = name;
            current_mode = MODE_LIST;
            dismissNamedDialog();
            // 创建任务
            createTask(name);
            taskComplete();
            showLeftLayout();
            hideRightLayout();
            showBtntask();
        }
    }

    private void setAdapterItemListener() {

        /**
         * 任务列表点击监听
         */
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                POITask poiTask = taskEntities.get(position);
                taskName = poiTask.getName();
                seletedTask = taskEntities.get(position);
                showBtntask();
                showTaskPath(seletedTask);
                enableDelete();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    /**
     * 使能删除按钮
     */
    private void enableDelete() {
        ivTaskDelete.setEnabled(true);
    }

    /**
     * 显示任务的路径
     */
    private void showTaskPath(POITask poiTask) {

        List<POIPoint> poiPoints = poiTask.getPoiPoints();
        mapView.setTestPOIS(poiPoints);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMap();
    }

    /**
     * 创建并保存任务
     */
    private void createTask(String name) {
        // 需要得到所有任务点的数据
        List<POIPoint> newPoints = new ArrayList<>();
        newPoints.clear();
        newPoints.addAll(mapView.getTestPOIS());
        mPresenter.saveTaskToDB(name, newPoints);
    }

    @Override
    public void showBtntask() {
        btnStartTask.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.iv_exit, R.id.btn_start_task, R.id.iv_task_delete, R.id.iv_task_add, R.id.btn_add, R.id.btn_end})
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
                // 执行任务
                backUpToMaster(taskName);
                break;
            case R.id.iv_task_delete: // 删除任务
                // 确定选择的任务
                if (seletedTask != null) {
                    taskEntities.remove(seletedTask);
                    mPresenter.removeData(seletedTask);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.iv_task_add:    // 添加任务
                current_mode = MODE_EDIT;
                hideLeftLayout();
                showRightLayout();
                hideBtnTask();
                addTask();
                break;
            case R.id.btn_add:
                addPoint();
                break;
            case R.id.btn_end:
                showNamedDialog();
                break;
        }
    }

    /**
     * 返回操作页面
     *
     * @param taskName
     */
    private void backUpToMaster(String taskName) {
        Intent intent = new Intent();
        intent.putExtra(PathEditActivity.KEY_TASK_NAME, taskName);
        setResult(RESULT_OK, intent);
        this.finish();
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
        editDialog.show();
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
    public void notifyAdapter(POITask newTask) {
        // taskEntities是从Poijson里面读取出来的，所以所有的引用都是指向同一个对象，故而出现了添加一次出现两次的bug
//        taskEntities.add(newTask);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void hideBtnTask() {
        btnStartTask.setVisibility(View.GONE);
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
