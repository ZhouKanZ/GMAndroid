package com.jms.cleanse.contract;

import com.jms.cleanse.base.IView;
import com.jms.cleanse.entity.db.PoiTask;
import com.jms.cleanse.entity.file.POIPoint;
import com.jms.cleanse.entity.file.POITask;

import java.util.List;


/**
 * Created by zhoukan on 2018/4/10.
 *
 * @desc:
 */

public class PathEditContract {

    public interface View extends IView {
        // 隐藏左侧布局
        void hideLeftLayout();
        // 显示左侧布局
        void showLeftLayout();

        // 得到点是否消毒
        boolean isCleansePoint();
        // 弹出 name dialog
        void showNamedDialog();
        // 隐藏 name dialog
        void dismissNamedDialog();

        // 添加任务
        void addTask();
        // 任务编辑完成
        void taskComplete();
        // 添加点
        void addPoint();
        // 显示地图
        void loadMap();
        // 显示右侧的布局
        void showRightLayout();
        // 隐藏右侧的布局
        void hideRightLayout();

        void notifyAdapter(POITask newTask);

        // 显示开始任务的按钮
        void showBtntask();
        // 隐藏开始任务的按钮
        void hideBtnTask();

    }

    public interface Presenter{

        // 添加到数据库中
        void saveTaskToDB(String name,List<POIPoint> pointList);

        // 删除任务
        void removeData(PoiTask task);

    }

}
