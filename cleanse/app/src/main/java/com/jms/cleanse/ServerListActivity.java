package com.jms.cleanse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jms.cleanse.entity.uiTest.RobotEntity;
import com.jms.cleanse.entity.uiTest.ServerEntity;
import com.jms.cleanse.ui.RobotMasterActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ServerListActivity extends AppCompatActivity {

    CommonAdapter<ServerEntity> adapter;
    List<ServerEntity> serverEntities;


    RecyclerView server_info_rv;
    int lastExpandPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list);

        server_info_rv = findViewById(R.id.server_info_rv);

        initData();

        adapter = new CommonAdapter<ServerEntity>(this, R.layout.item_server_info, serverEntities) {
            @Override
            protected void convert(final ViewHolder holder, final ServerEntity serverEntity, final int position) {
                holder.setText(R.id.tv_server_name, serverEntity.getServer_address());
                holder.setText(R.id.tv_server_ip, serverEntity.getIp());

                holder.setOnClickListener(R.id.layout_server_info, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 点击item的时候，如果有已经展开的，需要将其先闭合

                        if (lastExpandPosition != -1 && lastExpandPosition != position) {
                            serverEntities.get(lastExpandPosition).setItemIsExpand(false);
                            adapter.notifyItemChanged(lastExpandPosition);
                        }

                        if (serverEntity.getRobotEntityList() != null && serverEntity.getRobotEntityList().size()>0){
                            serverEntity.setItemIsExpand(!serverEntity.isItemIsExpand());
                            adapter.notifyItemChanged(position);
                        }

                    }
                });

                if (serverEntity.isItemIsExpand()) {
                    lastExpandPosition = position;
                    holder.getView(R.id.robot_rv).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.robot_rv).setVisibility(View.GONE);
                }

                if (holder.getView(R.id.robot_rv).getVisibility() == View.VISIBLE && serverEntity.getRobotEntityList() != null &&serverEntity.getRobotEntityList().size() > 0) {
                    final RecyclerView robot__rv = ((RecyclerView) holder.getView(R.id.robot_rv));
                    robot__rv.setHasFixedSize(false);
                    robot__rv.setLayoutManager(new LinearLayoutManager(ServerListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    robot__rv.setAdapter(new CommonAdapter<RobotEntity>(ServerListActivity.this, R.layout.item_robot_info, serverEntity.getRobotEntityList()) {

                        @Override
                        protected void convert(final ViewHolder holder, final RobotEntity robotEntity, int position) {

                            // 仅有一个被选中，被选中后更改样式
                            holder.setText(R.id.tv_robot_name, robotEntity.getRobotName());
                            holder.setOnClickListener(R.id.iv_robot_logo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    // 跳转到下一个页面中
                                startActivity(new Intent(ServerListActivity.this, RobotMasterActivity.class));
                                }
                            });
                        }
                    });
                }
            }
        };

        server_info_rv.setHasFixedSize(false);
        server_info_rv.setLayoutManager(new LinearLayoutManager(this));
        server_info_rv.setAdapter(adapter);

    }

    private void initData() {

        serverEntities = new ArrayList<>();
        ServerEntity s1 = new ServerEntity("JM_SERVER_DINNER", "192.168.2.188");
        List<RobotEntity> robotEntities = new ArrayList<>();
        robotEntities.add(new RobotEntity("科比"));
        robotEntities.add(new RobotEntity("奥利尔"));
        robotEntities.add(new RobotEntity("奥多姆"));
        robotEntities.add(new RobotEntity("本泽马"));
        s1.setRobotEntityList(robotEntities);
        serverEntities.add(s1);
        serverEntities.add(new ServerEntity("JM_SERVER_KITCHEN", "192.168.0.123"));
        serverEntities.add(new ServerEntity("JM_SERVER_DINNER", "192.168.2.188"));
        serverEntities.add(new ServerEntity("JM_SERVER_KITCHEN", "192.168.0.123"));
        serverEntities.add(new ServerEntity("JM_SERVER_DINNER", "192.168.2.188"));
        serverEntities.add(new ServerEntity("JM_SERVER_KITCHEN", "192.168.0.123"));
        serverEntities.add(new ServerEntity("JM_SERVER_DINNER", "192.168.2.188"));
        serverEntities.add(new ServerEntity("JM_SERVER_KITCHEN", "192.168.0.123"));
        serverEntities.add(new ServerEntity("JM_SERVER_DINNER", "192.168.2.188"));

        ServerEntity s2 = new ServerEntity("JM_SERVER_DINNER", "192.168.2.188");
        List<RobotEntity> robotEntities2 = new ArrayList<>();
        robotEntities2.add(new RobotEntity("科比"));
        robotEntities2.add(new RobotEntity("奥利尔"));
        robotEntities2.add(new RobotEntity("奥多姆"));
        robotEntities2.add(new RobotEntity("本泽马"));
        s2.setRobotEntityList(robotEntities2);


        serverEntities.add(s2);
        serverEntities.add(new ServerEntity("JM_SERVER_DINNER", "192.168.2.188"));
        serverEntities.add(new ServerEntity("JM_SERVER_KITCHEN", "192.168.0.123"));
        serverEntities.add(new ServerEntity("JM_SERVER_HOUSE", "192.168.4.55"));

    }


}
