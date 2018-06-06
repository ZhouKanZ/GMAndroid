package com.jms.cleanse.contract;

import com.jms.cleanse.base.IView;
import com.jms.cleanse.bean.TaskRecoder;

import java.util.List;

/**
 * Created by zhoukan on 2018/5/31.
 *
 * @desc:
 */

public class TaskRecorderContract {

    public interface View extends IView{

        void notifyAdapter(List<TaskRecoder> recoders);

    }

    public interface Presenter{

        List<TaskRecoder> loadData();

    }

    public interface Model{

    }


}
