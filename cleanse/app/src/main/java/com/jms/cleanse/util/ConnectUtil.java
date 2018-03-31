package com.jms.cleanse.util;



import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import robot.boocax.com.sdkmodule.TCP_CONN;

/**
 * Created by WangJun on 2018/3/20.
 */

public class ConnectUtil {

    public static void startUdp(){

        Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        TCP_CONN.isUDP = false;
                        TCP_CONN.getUDPs();
                    }
                });
    }


}
