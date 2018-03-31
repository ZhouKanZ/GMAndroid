package com.jms.cleanse.contract;

import com.jms.cleanse.base.IView;

/**
 * Created by zhoukan on 2018/3/28.
 *
 * @desc:
 */

public class RobotMasterContract {

    public interface View extends IView{

        /**
         *  设置速度
         */
         double[] getSpeed();

    }

    public interface Presenter {

        /**
         *  循环发送move指令
         */
        void doLoopSendMove();

        /**
         *  取消循环
         */
        void cancelLoop();

        /**
         *  请求所有地图
         */
        void requestAllMapInfo();
    }

}
