package com.jms.cleanse.presenter;

import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.bean.TaskRecoder;
import com.jms.cleanse.contract.TaskRecorderContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoukan on 2018/6/1.
 *
 * @desc:
 */

public class TaskRecoderPresenter extends BasePresenter<TaskRecorderContract.View> implements TaskRecorderContract.Presenter{

    public List<TaskRecoder> taskRecoders = new ArrayList<>();

    public TaskRecoderPresenter() {
    }

    @Override
    public void onCreate() {
        //
    }

    @Override
    public void onDestory() {
        //
    }


    @Override
    public List<TaskRecoder> loadData() {
        // 加载数据
        taskRecoders.add(new TaskRecoder("task1","2018-08-28","2018-08-28",null));
        taskRecoders.add(new TaskRecoder("task1","2018-08-28","2018-08-28",null));
        taskRecoders.add(new TaskRecoder("task1","2018-08-28","2018-08-28",null));
        taskRecoders.add(new TaskRecoder("task1","2018-08-28","2018-08-28",null));
        taskRecoders.add(new TaskRecoder("task1","2018-08-28","2018-08-28",null));

        return taskRecoders;
    }
}
