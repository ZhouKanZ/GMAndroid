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
 * Created by zhoukan on 2018/4/10.
 *
 * @desc:
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

    }

    private void unRegisterEventBus() {

    }

    @Override
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
     * 把数据写入自定义的poitask.json文件中
     * 注意：要记得同步更新poi.json文件
     */
    public void createTask() {

/*        PoiTask poiTask = new PoiTask();
        poiTask.setName("test");
        poiTask.setPois(getPOIData());
        poiTaskList.add(poiTask);
        Gson gson = new Gson();
        Observable.just(gson.toJson(poiTaskList))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        FileUtil.writeFile(" ");
                        FileUtil.writeFile(s);
                    }
                });*/
//        FileUtil.writeFile(gson.toJson(poiTaskList));//要同步更新poi.json
//        setPoiJson();//更新poi.json文件
    }

    /**
     * 执行巡航任务
     * @param taskName group名字
     */
    public void executeTask(String taskName) {
        Observable.just(taskName)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        APPSend.sendOrder_roaming(LoginEntity.robotMac, taskName,1,"false");
                    }
                });
    }

    public List<PoiTask> loadData() {

        poiTaskBox = JMApplication.getBoxStore().boxFor(PoiTask.class);
        if (poiTaskBox == null){
            Log.i(TAG, "loadData: null");
            return null;
        }
        List<PoiTask> poiTasks = poiTaskBox.query().build().find();
        Gson gson = new Gson();
        Log.i(TAG, "loadData: data = " + poiTasks.size());
        return poiTasks;
    }

}
