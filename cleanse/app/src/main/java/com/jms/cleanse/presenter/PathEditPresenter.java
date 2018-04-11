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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.objectbox.Box;
import io.reactivex.Observable;
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


    public void objectBoxTest(){

        PoiTask poiTask = new PoiTask();
        PoiPoint poiPoint2 = new PoiPoint();
        PoiPoint poiPoint1 = new PoiPoint();

        PositionBean pb1 = new PositionBean();
        pb1.x = 0.3;
        pb1.y = -2.65;
        pb1.yaw = 0;

        PositionBean pb2 = new PositionBean();
        pb2.x = -6.7;
        pb2.y = 0.45;
        pb2.yaw = 0;

        poiPoint1.position.setTarget(pb1);
        poiPoint2.position.setTarget(pb2);
        poiPoint1.name = "p1";
        poiPoint1.state = true;
        poiPoint2.name = "p2";
        poiPoint2.state = false;

        poiTask.poiPoints.add(poiPoint1);
        poiTask.poiPoints.add(poiPoint2);
        poiTask.name = "db_test";

        poiTaskBox.put(poiTask);

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
//        poiTaskBox.remove(1,2,3,4,5,6,7,8,9);
        List<PoiTask> poiTasks = poiTaskBox.query().build().find();

        Gson gson = new Gson();
        Log.i(TAG, "loadData: data = " + poiTasks.size());

/*        Gson gson = new Gson();
        Observable.just(gson.toJson(poiTasks))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        APPSend.sendFile(s.getBytes(), FileUtil.POI_JSON);
                    }
                });*/
        return poiTasks;

/*        Observable.just(FileUtil.readFileJM(FileUtil.POI_TASK_JSON))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        List<PoiTask> pois = new ArrayList<>();
                        Gson gson = new Gson();
                        pois = gson.fromJson(s, new TypeToken<List<PoiTask>>() {
                        }.getType());
                        if (pois.size() == 0) {
                            return;
                        }
                        poiTaskList.clear();
                        poiTaskList.addAll(pois);
                    }*/
/*                        POIJsonEntity poiJsonEntity = gson.fromJson(s, POIJsonEntity.class);
                        Set<Map.Entry<String, List<String>>> entrySet = poiJsonEntity.groups.entrySet();
                        Iterator<Map.Entry<String, List<String>>> iterator = entrySet.iterator();
                        while(iterator.hasNext()){
                            PoiTask poiTask = new PoiTask();
                            Map.Entry<String, List<String>> next = iterator.next();
                            String name = next.getKey();
                            List<String> value = next.getValue();
                            poiTask.setName(name);

//                            poiTask.setPois(value);
                            pois.add(poiTask);
                        }
                        if (pois.size() == 0){
                            return ;
                        }
                        poiTaskList.clear();
                        poiTaskList.addAll(pois);
                    }*/
//                });
//        return poiTaskList;
    }


    private void setPoiJson(List<PoiTask> poiTasks) {
        final POIJson poiJson = new POIJson();
        poiJson.setVersion("1.0.0");
        poiJson.setEncoding("utf-8");
        poiJson.setPoi_info(getPOIData());
        poiJson.setGroups(new HashMap<>());
        poiJson.getGroups().put("test",getGroups());


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
