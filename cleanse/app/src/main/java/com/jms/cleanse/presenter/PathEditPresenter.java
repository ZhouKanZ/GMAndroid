package com.jms.cleanse.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.jms.cleanse.JMApplication;
import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.contract.PathEditContract;
import com.jms.cleanse.entity.db.PoiPoint;
import com.jms.cleanse.entity.db.PoiTask;
import com.jms.cleanse.entity.db.PositionBean;
import com.jms.cleanse.entity.file.POIJson;
import com.jms.cleanse.util.FileUtil;
import com.jms.cleanse.widget.mapview.CustomPOI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import robot.boocax.com.sdkmodule.APPSend;
import robot.boocax.com.sdkmodule.entity.entity_file.poi.sdk.Position;
import robot.boocax.com.sdkmodule.entity.entity_sdk.from_server.AllMapInfo;


/**
 * Created by WangJun on 2018/4/9.
 */

public class PathEditPresenter extends BasePresenter<PathEditContract.View> implements PathEditContract.Presenter {

    private static final String TAG = "PathEditPresenter";
    private Box<PoiTask> poiTaskBox;


    @Override
    public void onCreate() {
        registerEventBus();
    }

    @Override
    public void onDestory() {
        unRegisterEventBus();
    }

    private void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unRegisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void saveTaskToDB(String name, List<PoiPoint> pointList) {

        // 为每一个点添加名字  名字的规则 name+index

        PoiTask newTask = new PoiTask();
        newTask.name = name;
        for (int i = 0; i < pointList.size(); i++) {
            PoiPoint poiPoint = pointList.get(i);
            poiPoint.setName(name + i);
            newTask.poiPoints.add(poiPoint);
        }
        long id = poiTaskBox.put(newTask);
        Log.d(TAG, "saveTaskToDB:  id:" + id);

        // 插入成功
        if (id > 0) {
            getView().notifyAdapter(newTask);
            updatePoiJson(newTask, FileUtil.ADD);
        }

    }

    /**
     * 更新poi.json
     *
     * @param poiTask
     * @param tag
     */
    private void updatePoiJson(PoiTask poiTask, int tag) {

        POIJson poiJson = FileUtil.readFileJM(FileUtil.POI_JSON);

        List<CustomPOI> poiList = new ArrayList<>();
        List<String> group = new ArrayList<>();

        // 构建新的string bytes流
        for (PoiPoint poiPoint : poiTask.poiPoints) {

            CustomPOI customPOI = new CustomPOI();
            customPOI.setState(poiPoint.state);
            customPOI.name = poiPoint.name;

            customPOI.position = new Position();
            customPOI.position.x = poiPoint.position.getTarget().x;
            customPOI.position.y = poiPoint.position.getTarget().y;
            customPOI.position.yaw = poiPoint.position.getTarget().yaw;
            poiList.add(customPOI);
            group.add(poiPoint.name);
        }

        switch (tag) {
            case FileUtil.ADD:
                poiJson.getPoi_info().addAll(poiList);
                poiJson.getGroups().put(poiTask.name, group);
                break;
            case FileUtil.DELETE:
                for (CustomPOI customPOI : poiList) {
                    poiJson.getPoi_info().remove(customPOI);
                }
                poiJson.getGroups().remove(poiTask.name);
                break;
            case FileUtil.UPDATE:
                break;
            default:
                break;
        }


        Gson gson = new Gson();
//        APPSend.sendFile(gson.toJson(poiJson).getBytes(), FileUtil.POI_JSON);
        Observable.just(gson.toJson(poiJson))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(content -> APPSend.sendFile(content.getBytes(), FileUtil.POI_JSON));

        Log.i(TAG, "updatePoiJson: json = " + gson.toJson(poiJson));
    }

    /**
     * 从数据库读取数据
     *
     * @return 任务数据集合
     */
    public List<PoiTask> loadData() {

        poiTaskBox = JMApplication.getBoxStore().boxFor(PoiTask.class);
        if (poiTaskBox == null) {
            Log.i(TAG, "loadData: null");
            return null;
        }
        List<PoiTask> poiTasks = poiTaskBox.query().build().find();
        return poiTasks;
    }


    /**
     * 测试数据
     *
     * @param poiTasks poi任务数据
     */
    private void setPoiJson(List<PoiTask> poiTasks) {
        final POIJson poiJson = new POIJson();
        poiJson.setVersion("1.0.0");
        poiJson.setEncoding("utf-8");
        poiJson.setPoi_info(getPOIData());
        poiJson.setGroups(new HashMap<>());
        poiJson.getGroups().put("test", getGroups());


        Gson gson = new Gson();
        String poiJsonStr = gson.toJson(poiJson);
        Log.i(TAG, "btnClick: " + poiJson);

        new Thread(new Runnable() {
            @Override
            public void run() {
                APPSend.sendFile(poiJsonStr.getBytes(), FileUtil.POI_JSON);
            }
        }).start();
    }

    private List<CustomPOI> getPOIData() {
        List<CustomPOI> poiList = new ArrayList<>();
        CustomPOI p0 = new CustomPOI();
        p0.name = "p0";
        Position pos = new Position();
        pos.x = 0.3;
        pos.y = -2.65;
        pos.yaw = 0;
        p0.position = pos;
        p0.setState(true);
        poiList.add(p0);

        CustomPOI p1 = new CustomPOI();
        p1.name = "p1";
        Position pos1 = new Position();
        pos1.x = -6.7;
        pos1.y = 0.45;
        pos1.yaw = 0;
        p1.position = pos1;
        p1.setState(true);
        poiList.add(p1);

        CustomPOI p2 = new CustomPOI();
        p2.name = "p2";
        Position pos2 = new Position();
        pos2.x = 3;
        pos2.y = 0.2;
        pos2.yaw = 0;
        p2.position = pos2;
        p2.setState(false);
        poiList.add(p2);

        CustomPOI p3 = new CustomPOI();
        p3.name = "p3";
        Position pos3 = new Position();
        pos3.x = -3.15;
        pos3.y = 3.7;
        pos3.yaw = 0;
        p3.position = pos3;
        p3.setState(false);
        poiList.add(p3);

        return poiList;
    }

    private List<String> getGroups() {

        List<String> poiNames = new ArrayList<>();
        poiNames.add("p1");
        poiNames.add("p0");
        poiNames.add("p2");
        poiNames.add("p3");
        return poiNames;
    }

    /**
     * 从数据库中一处数据,同时删除远程仓库的数据
     *
     * @param task
     */
    @Override
    public void removeData(PoiTask task) {

        poiTaskBox.remove(task);
        // 操作 重新读取poiJson
        updatePoiJson(task, FileUtil.DELETE);

    }

    @Override
    public List<PoiTask> synchronousData() {

        List<PoiTask> poiTasks = new ArrayList<>();
        // 异步操作
        POIJson poiJson = FileUtil.readFileJM(FileUtil.POI_JSON);
        // 内容可能为空
        List<CustomPOI> customPois = poiJson.getPoi_info();
        Map<String, List<String>> groups = poiJson.getGroups();

        BoxStore boxStore = JMApplication.getBoxStore();
        Box<PoiTask> box = boxStore.boxFor(PoiTask.class);

        // 数据存在且有意义的时候
        if (customPois.size() > 0 && groups.size() > 0) {

            for (String groupName : groups.keySet()) {

                List<PoiPoint> poiPoints = new ArrayList<>();
                PoiTask poiTask = new PoiTask();


                // 将所有符合条件的点加入PoiPoint中
                for (CustomPOI poi : customPois) {

                    Log.d(TAG, "synchronousData: " + poi.name + ",groupName:"+groupName);
                    // 包含group的名字表示为是同一个任务的点
                    if (poi.name.contains(groupName)) {
                        PoiPoint poiPoint = new PoiPoint();
                        poiPoint.name = poi.name;
                        PositionBean pb = new PositionBean();
                        pb.x = poi.position.x;
                        pb.y = poi.position.y;
                        pb.yaw = poi.position.yaw;
                        poiPoint.position.setTarget(pb);
                        poiPoints.add(poiPoint);
                    }

                }

                poiTask.name = groupName;
                poiTask.poiPoints.addAll(poiPoints);
                // 将poiTask存入数据库中
                box.put(poiTask);
                poiTasks.add(poiTask);
            }
        }

        List<PoiPoint> poiPoints = poiTasks.get(0).poiPoints;
        Log.d(TAG, "synchronousData: " + poiTasks.size() + "single0 : (x,y) + (" + poiPoints.get(0).position.getTarget().x+","+poiPoints.get(0).position.getTarget().y+")");
        return poiTasks;

    }

    /**
     * 短按屏幕获取屏幕坐标.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllMapInfo(AllMapInfo allMapInfo) {
        if (allMapInfo != null) {
            Log.d(TAG, "getAllMapInfo: " + allMapInfo.getCurrent_map_info().getUuid());
        }
    }


}

