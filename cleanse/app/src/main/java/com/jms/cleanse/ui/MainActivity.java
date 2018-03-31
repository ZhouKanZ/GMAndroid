package com.jms.cleanse.ui;

import android.os.Bundle;
import android.util.Log;


import com.jms.cleanse.R;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MainActivity extends RxAppCompatActivity {

    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.i(TAG, "onCreate: ");

//        EventBus.getDefault().register(this);
        // Specifically bind this until onPause()
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "Unsubscribing subscription from onCreate()");
                    }
                })
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "Started in onCreate(), running until onPause(): " + num);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        Observable.interval(1,TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "Unsubscribing subscription from onStart()");
                    }
                })
                .compose(this.<Long>bindToLifecycle())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "Started in onStart(), running until in onStop(): " + num);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        // `this.<Long>` is necessary if you're compiling on JDK7 or below.
        //
        // If you're using JDK8+, then you can safely remove it.
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "Unsubscribing subscription from onResume()");
                    }
                })
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "Started in onResume(), running until in onDestroy(): " + num);
                    }
                });
    }

}
