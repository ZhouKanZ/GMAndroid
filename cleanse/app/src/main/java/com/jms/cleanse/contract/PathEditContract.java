package com.jms.cleanse.contract;

import com.jms.cleanse.base.IView;

/**
 * Created by zhoukan on 2018/4/10.
 *
 * @desc:
 */

public class PathEditContract {

    public interface View extends IView{
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
    }

    public interface Presenter{

    }

}
