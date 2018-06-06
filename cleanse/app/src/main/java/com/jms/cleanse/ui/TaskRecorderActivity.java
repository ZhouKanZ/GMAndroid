package com.jms.cleanse.ui;

import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.jms.cleanse.R;
import com.jms.cleanse.adapter.TaskRecoderAdapter;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.bean.TaskRecoder;
import com.jms.cleanse.contract.TaskRecorderContract;
import com.jms.cleanse.presenter.TaskRecoderPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhoukan on 2018/6/1.
 *
 * @desc:
 */

public class TaskRecorderActivity extends BaseActivity<TaskRecoderPresenter> implements TaskRecorderContract.View, TaskRecoderAdapter.OnItemClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rv_task_recorder)
    RecyclerView rvTaskRecorder;
    List<TaskRecoder> recoders;
    TaskRecoderAdapter adapter;

    @Override
    protected TaskRecoderPresenter loadPresenter() {
        return new TaskRecoderPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_task_recorder;
    }

    @Override
    protected void initData() {

        recoders = mPresenter.loadData();
        adapter = new TaskRecoderAdapter(this, recoders);
        adapter.setOnItemClickListener(this);
        rvTaskRecorder.setLayoutManager(new LinearLayoutManager(this));
        rvTaskRecorder.setAdapter(adapter);

    }

    @Override
    protected void initListeners() {

    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        this.finish();
    }

    @Override
    public void notifyAdapter(List<TaskRecoder> recoders) {
        if (recoders != null && recoders.size() > 0) {
            this.recoders.clear();
            this.recoders.addAll(recoders);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(TaskRecoder taskRecoder) {


    }
}
