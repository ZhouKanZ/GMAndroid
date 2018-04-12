package com.jms.cleanse.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.jms.cleanse.JMApplication;
import com.jms.cleanse.base.BasePresenter;
import com.jms.cleanse.contract.PathEditContract;
import com.jms.cleanse.entity.db.PoiPoint;
import com.jms.cleanse.entity.db.PoiTask;
import com.jms.cleanse.entity.file.POIJson;
import com.jms.cleanse.util.FileUtil;
import com.jms.cleanse.widget.mapview.CustomPOI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import robot.boocax.com.sdkmodule.APPSend;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;
import robot.boocax.com.sdkmodule.entity.entity_file.poi.sdk.Position;


/**
 * Created by WangJun on 2018/4/9.
 */

public class PathEditPresenter extends BasePresenter<PathEditContract.View> {

    private static final String TAG = "PathEditPresenter";
    private Box<PoiTask> poiTaskBox;

    @Override
    public void onCreate() {

//        setPoiJson();
    }

    @Override
    public void onDestory() {

    }

    private void unRegisterEventBus() {

    }

    public void saveTaskToDB(String name,List<PoiPoint> pointList){

        PoiTask newTask = new PoiTask();
        newTask.name = name;
        for (PoiPoint poi : pointList) {
            newTask.poiPoints.add(poi);
        }
        long id = poiTaskBox.put(newTask);

        // 插入成功
        if (id > 0){
            getView().notifyAdapter(newTask);
        }

        updatePoiJson(newTask, FileUtil.ADD);
    }

    /**
     * 更新poi.json
     * @param poiTask
     * @param tag
     */
    private void updatePoiJson(PoiTask poiTask, int tag){

        POIJson poiJson = FileUtil.readFileJM(FileUtil.POI_JSON);

        List<CustomPOI> poiList = new ArrayList<>();
        List<String> group = new ArrayList<>();

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
        Observable.just(gson.toJson(poiJson))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String content) throws Exception {
                        APPSend.sendFile(content.getBytes(), FileUtil.POI_JSON);
                    }
                });

        Log.i(TAG, "updatePoiJson: json = " + gson.toJson(poiJson));
    }

    /**
     * 把数据写入数据库中
     * 注意：要记得同步更新poi.json文件
     */
    public void createTask() {

    }

    /**
     * 执行巡航任务
     *
     * @param taskName group名字
     */
    public void executeTask(String taskName) {
        Observable.just(taskName)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        APPSend.sendOrder_roaming(LoginEntity.robotMac, taskName, 1, "false");
                    }
                });
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
}

