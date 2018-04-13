package com.jms.cleanse.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jms.cleanse.R;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.contract.PathEditContract;
import com.jms.cleanse.entity.db.PoiPoint;
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


    public static String KEY_TASK_NAME = "key_task_name";

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
    private PoiTask currentTask = null;
    private long taskId = -1;

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

        isCleanse.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        cleanseable = isChecked;
                    }
                }
        );

        // todo dialog 的大小异常
        namedDialog = new Dialog(this);
        view = LayoutInflater.from(this).inflate(R.layout.dialog_task_name, null);
        namedDialog.setContentView(view);

        hideRightLayout();
        ivTaskDelete.setEnabled(false);

    }

    @Override
    protected void initListeners() {
        taskRv.setHasFixedSize(false);
        taskRv.setLayoutManager(new LinearLayoutManager(this));
        taskRv.setAdapter(adapter);

        etNamedTask = view.findViewById(R.id.et_task_named);
        btnSure = view.findViewById(R.id.btn_sure);
        btnCancel = view.findViewById(R.id.btn_cancel);

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
        if (mapView.getTestPOIS().size() == 0){
            Toast.makeText(this, "不要忘记给任务添加消毒路径!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "朋友！您忘记给它命名了", Toast.LENGTH_SHORT).show();
        } else {
            current_mode = MODE_LIST;

            dismissNamedDialog();
            createTask(name);
            taskComplete();
            showLeftLayout();
            hideRightLayout();
            showBtntask();

        }
    }

    /**
     * 创建并保存任务
     */
    private void createTask(String name) {
        // 需要得到所有任务点的数据
        List<PoiPoint> newPoints = new ArrayList<>();
        newPoints.clear();
        newPoints.addAll(mapView.getTestPOIS());
        mPresenter.saveTaskToDB(name,newPoints);
    }

    private void setAdapterItemListener() {

        /**
         * 任务列表点击监听
         */
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                PoiTask poiTask = taskEntities.get(position);
                taskName = poiTask.name;
                showBtntask();
                showTaskPath(poiTask);
                enableDelete();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

    }

    /**
     *  使能删除按钮
     */
    private void enableDelete() {
        ivTaskDelete.setEnabled(true);
    }

    /**
     *  显示任务的路径
     */
    private void showTaskPath(PoiTask poiTask) {

        List<PoiPoint> poiPoints = poiTask.poiPoints;
        mapView.setTestPOIS(poiPoints);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMap();
    }


    @OnClick({R.id.iv_exit, R.id.btn_start_task, R.id.iv_task_delete, R.id.iv_task_add,R.id.btn_add,R.id.btn_end})
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
                mPresenter.executeTask(taskName);
                backUpToMaster(taskName);
                break;
            case R.id.iv_task_delete: // 删除任务
                // 确定选择的任务
                mPresenter.removeData(null);
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
     *  返回操作页面
     * @param taskName
     */
    private void backUpToMaster(String taskName) {
        Intent intent = new Intent();
        intent.putExtra(PathEditActivity.KEY_TASK_NAME,taskName);
        setResult(RESULT_OK,intent);
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
    public void showBtntask() {
        btnStartTask.setVisibility(View.VISIBLE);
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
