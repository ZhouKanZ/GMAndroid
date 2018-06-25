package com.jms.cleanse.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.jms.cleanse.R;
import com.jms.cleanse.adapter.OnItemClickListener;
import com.jms.cleanse.adapter.ServerInfoAdapter;
import com.jms.cleanse.base.BaseActivity;
import com.jms.cleanse.contract.ServerListContract;
import com.jms.cleanse.entity.robot.ServerEntity;
import com.jms.cleanse.net.NettyClient;
import com.jms.cleanse.presenter.ServerListPresenter;
import com.jms.cleanse.services.MyService_verify;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import robot.boocax.com.sdkmodule.TCP_CONN;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.entity.entity_sdk.for_app.UDPList;
import robot.boocax.com.sdkmodule.setlog.SetLog;
import robot.boocax.com.sdkmodule.utils.logutil.LogUtils;


/**
 * Created by zhoukan on 2018/3/26.
 *
 * @desc: 服务器列表 做初始化初始化工作 ， 并监听udp广播
 */

public class ServerListActivity extends BaseActivity<ServerListPresenter> implements ServerListContract.ServerListView, OnItemClickListener<ServerEntity> {


    private static final String TAG = "ServerListActivity";

    @BindView(R.id.server_info_rv)
    RecyclerView serverInfoRv;

    List<ServerEntity> serverEntities;
    ServerInfoAdapter adapter;

    @Override
    protected ServerListPresenter loadPresenter() {
        return new ServerListPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_server_list;
    }

    @Override
    protected void initData() {
        serverEntities = new ArrayList<>();
        adapter = new ServerInfoAdapter(serverEntities, this);
        adapter.setOnItemClickListener(this);
        mPresenter.initData();
    }

    @Override
    protected void initListeners() {

        serverInfoRv.setHasFixedSize(true);
        serverInfoRv.setLayoutManager(new LinearLayoutManager(this));
        serverInfoRv.setAdapter(adapter);

    }


    @Override
    public void notifyAdapter(UDPList udpList) {
        if (udpList != null) {
            String serverName = udpList.getList().get(0);
            String serverIP = udpList.getList().get(1);
            String displayStr = serverName + "" + serverIP;

            ServerEntity serverEntity = new ServerEntity();
            serverEntity.setServerName(serverName);
            serverEntity.setServerIp(serverIP);
            serverEntity.setLock(udpList.getList().get(2));

            int a = compare(serverEntities, displayStr);
            if (a == 0) {                           //UDP传来的server信息与原有的不相同,添加进listView
                serverEntities.add(serverEntity);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void jumpToRobotMaster(ServerEntity serverEntity) {

        LoginEntity.serverIP = serverEntity.getServerIp();                                                 //获取serverIP
        LoginEntity.serverName = serverEntity.getServerName();                                               //获取serverName
        String isLock;
        isLock = serverEntity.getLock();//获取是否加锁

        if ("false".equals(isLock)) {                                                       //无密码登录(跳转至LaunchActivity页面)
            TCP_CONN.isSendReconn = true;               //是否开启重连
            TCP_CONN.loopMark = true;                   //开启TCP主循环;
            TCP_CONN.reconnTime = 2000L;
            // 用另外的service来替换之前的tcp
//            NettyClient.getInstance().connect();
            startService(new Intent(this, MyService_verify.class));
            startActivity(new Intent(ServerListActivity.this, RobotMasterActivity.class));
            this.finish();
        } else if ("true".equals(isLock)) {                                                 //有密码登录(跳转至SignActivity页面)
//            startActivity(intent);                      //跳转至SignActivity页面
            Toast.makeText(this, "服务器被加锁", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * ###返回为0表示有相同元素   如果不为0表示没有
     * 此方法用于比对UDP传来的信息是由是否有相同, 如果相同 就舍去, 如果不同, 就加入list中 显示在UDP弹框中
     */
    private int compare(List<ServerEntity> list, String str) {
        int a = 0;
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i).getServerName() + "" + list.get(i).getServerIp();
            if (s.equals(str)) {
                a++;
            }
        }
        return a;
    }

    @Override
    public void OnItemClick(ServerEntity serverEntity, int position) {
        jumpToRobotMaster(serverEntity);
    }
}
