package com.jms.cleanse.contract;

import android.support.v7.widget.RecyclerView;

import com.jms.cleanse.base.IView;
import com.jms.cleanse.entity.robot.ServerEntity;

import java.util.List;

import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.UDPList;


/**
 * Created by WangJun on 2018/3/21.
 */

public interface ServerListContract {

    interface ServerListView extends IView {

        void notifyAdapter(UDPList udpList);

        void jumpToRobotMaster(ServerEntity serverEntity);

    }

    interface Presenter {

        void initData();
        RecyclerView.Adapter initAdapter();
    }
}
